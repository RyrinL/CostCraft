# CostCraft — Thesis Report Outline

**Title:** CostCraft — A Recipe-Driven Bakery Production Cost and Inventory Management System
**Author:** 林志安 (L25020027), IT 1, Software Engineering, Nanjing Xiaozhuang University, 2026
**Stack:** Java J2EE (Servlet/JSP/JSTL/EL/JDBC), Tomcat 10, MySQL 8, Maven, five-tier MVC

---

## Chapters

1. **Abstract** (EN) — problem, approach, result in ~250 words.

2. **Introduction**
   - 1.1 Background & problem statement (from design report)
   - 1.2 Objectives
   - 1.3 Scope (4 core modules + Sales; out-of-scope list)
   - 1.4 Report structure

3. **Related Work / Requirement Analysis**
   - 2.1 Similar systems: Keruyun, Toast POS, the user's HPP spreadsheet  *(Figs 1–3, reuse)*
   - 2.2 Feature comparison table
   - 2.3 Functional & non-functional requirements
   - 2.4 Use-case analysis  *(Fig: use-case diagram — I generate)*

4. **System Design**
   - 3.1 Five-tier MVC architecture  *(Fig: architecture — I generate)*
   - 3.2 Technology stack & rationale
   - 3.3 Database design — 13 tables, normalized  *(Fig: ER diagram — I generate)*
   - 3.4 The four-stage cost (HPP) methodology
   - 3.5 Key process design: Log Production Batch  *(Fig: sequence diagram — I generate)*

5. **Implementation** (per module: responsibility, logic, key files, screenshots)
   - 4.1 User Authentication & Role Management
   - 4.2 Material & Inventory Management (+ low-stock, shopping list)
   - 4.3 Product, BOM & Unit-Cost (HPP) Calculation (+ overhead, price sensitivity)
   - 4.4 Production Batch Logging (the transactional centerpiece)
   - 4.5 Sales / Order Book (revenue & profit)
   - 4.6 Cross-cutting: multi-currency display, i18n (EN/ID), responsive mobile UI

6. **Testing**
   - 5.1 Test approach
   - 5.2 Functional test-case tables per module (steps / expected / actual / pass)
   - 5.3 Transaction & rollback test (batch logging integrity)

7. **Conclusion**
   - 6.1 Summary of work
   - 6.2 Limitations
     - **Single-tenant by design** — one bakery per deployment. Multiple OWNER accounts
       are co-managers of the *same* bakery, not separate businesses; all data is shared.
       (Consistent with the design report, which scoped multi-tenant SaaS out.)
     - Also: MD5+salt hashing (not bcrypt/argon2), no CSRF/MFA/rate-limiting, raw-material
       inventory only (no finished-goods stock), prices entered in base currency (IDR).
   - 6.3 Future work
     - **Multi-tenancy** — to host independent bakeries each seeing only their own data:
       add a `bakery` (tenant) table, a `bakery_id` FK on every data table, tenant-scoped
       queries in every DAO, and a sign-up flow creating a bakery + its first owner. This is
       the natural path to turning CostCraft into a product for many bakeries.
     - Reports & analytics module; Spring Boot + Thymeleaf migration; finished-goods stock
       + sales-driven deduction; stronger password hashing.

8. **References**
9. **Acknowledgements** (optional)
