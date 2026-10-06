package com.example.demo.security;

import com.example.demo.courses.CourseLevel;
import com.example.demo.courses.CourseRequest;
import com.example.demo.lessons.LessonRequest;
import com.example.demo.students.LoginRequest;
import com.example.demo.students.StudentRegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security-focused integration tests (requirement 21 - "Testes de
 * seguranca"): unauthenticated video access, access without a subscription,
 * and the admin API key gate.
 */
@SpringBootTest
@AutoConfigureMockMvc 
@ActiveProfiles("test")
class AccessControlIntegrationTest {

    private static final String ADMIN_KEY = "test-admin-key"; // matches application-test.properties

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void creatingACourseWithoutTheAdminKeyIsRejected() throws Exception {
        String body = objectMapper.writeValueAsString(
                new CourseRequest("Curso sem chave", "desc", "Java", CourseLevel.BEGINNER, null, 60));

        mockMvc.perform(post("/admin/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void creatingACourseWithAnInvalidAdminKeyIsRejected() throws Exception {
        String body = objectMapper.writeValueAsString(
                new CourseRequest("Curso chave errada", "desc", "Java", CourseLevel.BEGINNER, null, 60));

        mockMvc.perform(post("/admin/courses")
                        .header("X-Admin-Api-Key", "chave-invalida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void unauthenticatedUserCannotWatchALessonVideo() throws Exception {
        String courseSlug = createCourseAsAdmin("Curso Publico " + System.nanoTime());
        String lessonSlug = createLessonAsAdmin(courseSlug, "Aula Um " + System.nanoTime());

        // Course page (description + lesson names) is public...
        mockMvc.perform(get("/courses/" + courseSlug))
                .andExpect(status().isOk());

        // ...but watching the actual video is not.
        mockMvc.perform(get("/courses/" + courseSlug + "/" + lessonSlug))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void authenticatedStudentWithoutActiveSubscriptionCannotWatchVideo() throws Exception {
        String courseSlug = createCourseAsAdmin("Curso Assinatura " + System.nanoTime());
        String lessonSlug = createLessonAsAdmin(courseSlug, "Aula Dois " + System.nanoTime());
        String token = registerAndLogin();

        mockMvc.perform(get("/courses/" + courseSlug + "/" + lessonSlug)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    private String registerAndLogin() throws Exception {
        String email = "aluno" + System.nanoTime() + "@example.com";
        String registerBody = objectMapper.writeValueAsString(
                new StudentRegisterRequest("Aluno Teste", email, "senhaForte123"));
        mockMvc.perform(post("/students/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        String loginBody = objectMapper.writeValueAsString(new LoginRequest(email, "senhaForte123"));
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(response, "$.token");
    }

    private String createCourseAsAdmin(String title) throws Exception {
        String body = objectMapper.writeValueAsString(
                new CourseRequest(title, "Descricao do curso.", "Java", CourseLevel.BEGINNER, null, 60));

        String response = mockMvc.perform(post("/admin/courses")
                        .header("X-Admin-Api-Key", ADMIN_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(response, "$.slug");
    }

    private String createLessonAsAdmin(String courseSlug, String title) throws Exception {
        Long courseId = resolveCourseId(courseSlug);
        String body = objectMapper.writeValueAsString(
                new LessonRequest(title, "Descricao da aula.", null, "https://videos.example.com/v1.mp4", 300, 1));

        String response = mockMvc.perform(post("/admin/courses/" + courseId + "/lessons")
                        .header("X-Admin-Api-Key", ADMIN_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(response, "$.slug");
    }

    private Long resolveCourseId(String courseSlug) throws Exception {
        String response = mockMvc.perform(get("/courses/" + courseSlug))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Number id = JsonPath.read(response, "$.id");
        return id.longValue();
    }
}
