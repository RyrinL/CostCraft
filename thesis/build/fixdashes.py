# Replace em-dashes with context-appropriate punctuation (no em-dashes left).
import re, glob, os

ROOT = os.path.join(os.path.dirname(__file__), '..')
bj = os.path.join(ROOT, 'build', 'build.js')
t = open(bj, encoding='utf-8').read()

pairs = [
    ('overhead — electricity, gas, labour', 'overhead: electricity, gas, labour'),
    ('this course — Servlet, JSP, JSTL, EL, sessions, filters and the five-tier architecture — with no external APIs',
     'this course (Servlet, JSP, JSTL, EL, sessions, filters and the five-tier architecture) with no external APIs'),
    ('(Harga Pokok Produksi — cost of goods produced)', '(Harga Pokok Produksi, cost of goods produced)'),
    ('Production Batch Logging — the four core modules.', 'Production Batch Logging: the four core modules.'),
    ('Its five gaps — no overhead, no propagation, no inventory, no historical cost, no audit trail — define the project’s contribution.',
     'Its five gaps (no overhead, no propagation, no inventory, no historical cost, no audit trail) define the project’s contribution.'),
    ('the operational tasks — recording stock movements, logging production batches, and viewing materials and recipes — but cannot manage users',
     'the operational tasks (recording stock movements, logging production batches, and viewing materials and recipes) but cannot manage users'),
    ('enforced by discipline — no SQL or business logic', 'enforced by discipline: no SQL or business logic'),
    ('preserved by snapshot columns — unit_cost_snapshot on batch_item and sales_order_item — so a record',
     'preserved by snapshot columns (unit_cost_snapshot on batch_item and sales_order_item), so a record'),
    ('Stage 1 — Direct Materials:', 'Stage 1, Direct Materials:'),
    ('Stage 2 — Direct Other Costs:', 'Stage 2, Direct Other Costs:'),
    ('Stage 3 — Percentage Overhead:', 'Stage 3, Percentage Overhead:'),
    ('Stage 4 — Unit Cost and Price:', 'Stage 4, Unit Cost and Price:'),
    ('matching inventory_log entry — the invariant', 'matching inventory_log entry, the invariant'),
    ('Listing 1. DBUtil — single shared connection pool.', 'Listing 1. DBUtil: single shared connection pool.'),
    ('Listing 2. LoginServlet — session login and PRG.', 'Listing 2. LoginServlet: session login and PRG.'),
    ('Listing 3. RoleFilter — owner-only authorization.', 'Listing 3. RoleFilter: owner-only authorization.'),
    ('Listing 5. UnitCostService — the four-stage unit-cost computation.', 'Listing 5. UnitCostService: the four-stage unit-cost computation.'),
    ('Listing 6. BatchService.recordBatch — the multi-table JDBC transaction.', 'Listing 6. BatchService.recordBatch: the multi-table JDBC transaction.'),
    ('Listing 7. SalesService.recordOrder — revenue, cost and profit with cost snapshots.', 'Listing 7. SalesService.recordOrder: revenue, cost and profit with cost snapshots.'),
    ('(reduced navigation — no Sales, Overhead or Users)', '(reduced navigation: no Sales, Overhead or Users)'),
    ('the core course foundations — request handling, session authentication, role-based authorization filters, the DAO pattern with PreparedStatement, and multi-table transactional integrity — while solving',
     'the core course foundations (request handling, session authentication, role-based authorization filters, the DAO pattern with PreparedStatement, and multi-table transactional integrity) while solving'),
    ('future work is multi-tenancy — a tenant table, a tenant key on every data table, tenant-scoped queries and a sign-up flow — which would turn CostCraft',
     'future work is multi-tenancy: a tenant table, a tenant key on every data table, tenant-scoped queries and a sign-up flow, which would turn CostCraft'),
    ("'CostCraft — J2EE Thesis", "'CostCraft · J2EE Thesis"),
]
for a, b in pairs:
    t = t.replace(a, b)

# catch-all: any remaining spaced em-dash -> comma; any stray em-dash -> hyphen
t = t.replace(' — ', ', ').replace('—', '-')
open(bj, 'w', encoding='utf-8').write(t)
print('build.js em-dashes left:', t.count('—'))

# diagrams: em-dash in titles -> colon
for f in glob.glob(os.path.join(ROOT, 'diagrams', '*.svg')):
    s = open(f, encoding='utf-8').read()
    s2 = s.replace(' — ', ': ').replace('—', '-')
    if s2 != s:
        open(f, 'w', encoding='utf-8').write(s2)
    print(os.path.basename(f), 'em-dashes left:', s2.count('—'))
