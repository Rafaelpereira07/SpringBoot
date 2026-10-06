package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

/**
 * Makes the commercial price of each plan configurable per environment
 * (see requirement: "Os valores comerciais dos planos devem ser
 * configuraveis"), instead of hardcoding prices in Java or relying solely
 * on the seeded database row.
 */
@ConfigurationProperties(prefix = "app.plans")
public class PlanPricingProperties {

    private Mensal mensal = new Mensal();
    private Anual anual = new Anual();
    private Vitalicio vitalicio = new Vitalicio();

    public Mensal getMensal() {
        return mensal;
    }

    public void setMensal(Mensal mensal) {
        this.mensal = mensal;
    }

    public Anual getAnual() {
        return anual;
    }

    public void setAnual(Anual anual) {
        this.anual = anual;
    }

    public Vitalicio getVitalicio() {
        return vitalicio;
    }

    public void setVitalicio(Vitalicio vitalicio) {
        this.vitalicio = vitalicio;
    }

    public static class Mensal {
        private BigDecimal price = new BigDecimal("29.90");

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    public static class Anual {
        private BigDecimal price = new BigDecimal("249.90");

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    public static class Vitalicio {
        private BigDecimal price = new BigDecimal("699.90");

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
}
