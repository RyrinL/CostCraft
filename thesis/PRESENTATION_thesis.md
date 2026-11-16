# CostCraft — Thesis Presentation / Defense Cards

Cue cards for explaining the report and answering questions. ~10–12 minutes of talking points.

---

## Card 1 — The Problem (why this topic)
- Every bakery product is a **fixed recipe** of measured ingredients; true cost = ingredients **+ overhead** (electricity, labour, packaging, fixed costs).
- Small operators use spreadsheets with **five gaps**: no automatic propagation when prices change, no overhead awareness, no inventory link, no historical cost, no audit trail.
- **Result:** they price by feel and lose margin invisibly.
- **Personal:** the end-user is a real bakery operator with an existing cost spreadsheet to replace.

## Card 2 — The Gap (related work)
- **Keruyun / Toast POS:** storefront / point-of-sale tools — strong at retail, weak at the **producer side**; costing is manual or enterprise-locked.
- **Her spreadsheet:** correct unit-cost idea, but no overhead, propagation, inventory or history.
- **Nobody** combines recipe-driven costing + overhead + inventory + transactional logging, **lightweight** for one operator. That intersection is CostCraft.

## Card 3 — The Solution (one sentence)
- "CostCraft turns recipes into a true unit cost with explicit overhead, links that cost to inventory so producing a batch atomically deducts stock, and records production and sales — all on the J2EE stack."
- Four core modules + Sales + cross-cutting (multi-currency, i18n, responsive).

## Card 4 — Architecture (five-tier MVC)
- **JSP → Servlet → Service → DAO → MySQL**, strict layering.
- Rules: no SQL/logic in servlets, no logic in JSP, DAOs take a `Connection`, services own transactions.
- Cross-cutting **filter chain**: encoding → locale → login → role.
- One shared **HikariCP** connection pool behind a **singleton** `DBUtil`.

## Card 5 — Database (13 tables)
- Normalised; InnoDB (so we get **transactions + foreign keys**), utf8mb4.
- Hubs: `user`, `material`, `product`.
- **Historical cost** preserved by snapshot columns (`unit_cost_snapshot` on `batch_item` and `sales_order_item`).
- **Audit invariant:** every stock change ↔ one `inventory_log` row.

## Card 6 — The Four-Stage Cost (HPP) — core contribution
- Stage 1 **Direct Materials** (recipe × per-gram cost)
- Stage 2 **Direct Other** (packaging, daily-allocated electricity)
- Stage 3 **Percentage Overhead** (labour, marketing, risk… as % of direct)
- Stage 4 **Unit Cost** = total ÷ yield, then suggested price range.
- **Why it matters:** makes the overhead the spreadsheet hides **explicit**.

## Card 7 — The Transaction (technical centrepiece)
- Logging a batch writes **5 tables in one JDBC transaction**: `setAutoCommit(false)` → insert batch, insert items, deduct stock, write log, raise alerts → `commit()`; any failure → `rollback()`.
- The **servlet never touches a Connection** — the service owns the boundary.
- Guarantees no half-recorded batch and no stock deducted without a record.
- "The course-level way to guarantee consistency — without JTA or EJB."

## Card 8 — Implementation highlights
- Plain **Servlet (@WebServlet) + JSP/JSTL/EL + JDBC PreparedStatement** throughout.
- **Session** login state; **LoginFilter** + **RoleFilter** for auth/authz.
- **BigDecimal** everywhere for money (never float).
- **Custom EL function** `cc:money` renders every amount in the user's currency.
- ~13 tables, all four modules + sales, fully bilingual.

## Card 9 — Testing
- Functional black-box tests per module (login/roles, materials, inventory, HPP, batch, sales, currency) — all pass.
- **Integrity test:** injected a failure mid-transaction → full rollback, stock restored, no partial rows. Proves atomicity.

## Card 10 — Limitations & Future Work
- **Limitations:** single-tenant (one bakery); MD5+salt (not bcrypt); raw-material stock only; prices entered in base currency.
- **Future work:** **multi-tenancy** (tenant table + tenant-scoped queries) to serve many bakeries; a reports module (good Spring Boot + Thymeleaf candidate); finished-goods stock; stronger hashing.

---

## Card 11 — Likely Questions (with answers)
- **"Why MD5?"** — The course scope; per-user salt is used. I document bcrypt/Argon2 as the production upgrade.
- **"Why single-tenant?"** — It's one operator's tool by design; multi-tenancy is identified as the future-work path (tenant table + tenant-scoped queries).
- **"What's HikariCP / a connection pool?"** — Reuses a small set of DB connections instead of opening one per request — faster and bounded; held by the `DBUtil` singleton.
- **"Why InnoDB?"** — It gives transactions and foreign-key enforcement, which the batch transaction depends on.
- **"How do you keep cost accurate over time?"** — Each batch/sale **snapshots** the unit cost, so past records never change when prices change.
- **"What stops stock from drifting?"** — All stock changes route through the service, which always writes an `inventory_log` row; the DAO exposes no bypass.
- **"Where's the transaction?"** — `BatchService.recordBatch()`: one `setAutoCommit(false)` … `commit()/rollback()` across five tables.

## Card 12 — One-line close
- "CostCraft replaces an overhead-blind spreadsheet with an integrated, transactionally-safe system that takes a small bakery from raw material to recorded profit — built end-to-end on the J2EE fundamentals."
