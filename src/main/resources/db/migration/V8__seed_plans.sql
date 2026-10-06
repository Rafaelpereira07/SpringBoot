-- Seed the three mandatory plans. Prices are illustrative placeholders;
-- the authoritative price shown to users is resolved at runtime from
-- app.plans.* properties (see PlanPricingProperties) so it stays configurable
-- without a migration. These rows only need to exist with correct
-- type/duration semantics.
INSERT INTO plans (name, type, description, duration_days, price) VALUES
 ('Mensal',    'MENSAL',    'Acesso a todos os cursos por 30 dias.', 30,   29.90),
 ('Anual',     'ANUAL',     'Acesso a todos os cursos por 365 dias.', 365, 249.90),
 ('Vitalicio', 'VITALICIO', 'Acesso a todos os cursos sem expiracao.', NULL, 699.90);
