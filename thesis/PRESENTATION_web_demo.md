# CostCraft — Live Web Demo Cards

Cue cards for demonstrating the running app. Open with **Skip as Owner** (or log in as `admin`).
Order the demo as a story: *cost → inventory → production → sales*. Each card = one screen.

---

## Card 1 — Login & Roles
**Show:** the login page, then log in.
**Say:**
- "CostCraft is a single-bakery tool. Two roles: **Owner** (full control) and **Worker** (kitchen staff)."
- "Every page is behind a login filter; owner-only areas are protected by a role filter."
**Do:** log in as Owner → land on the dashboard.

---

## Card 2 — Dashboard
**Show:** the owner dashboard.
**Say:**
- "At-a-glance: product count, low-stock count, open alerts, recent batches."
- "The bars under each product rank how expensive each is to make."
**Point to:** the 4 metric cards + the product cost bars.

---

## Card 3 — Materials & Inventory (the audit invariant)
**Show:** Materials list → Inventory → record a purchase → Inventory History.
**Say:**
- "~45 raw materials, each with package price and a **derived per-gram cost**."
- "Watch: I record a stock purchase…" → record one → open History.
- "**Every** stock change writes a matching log row. There's no way to change stock without an audit trail."
**Key line:** "Cost and stock can never silently drift apart."

---

## Card 4 — Unit Cost (HPP) — THE CENTREPIECE
**Show:** Products → click a product → Unit Cost detail.
**Say:**
- "This is the problem the bakery actually has. The spreadsheet only counts ingredients."
- "CostCraft computes cost in **four stages**: Stage 1 direct materials, Stage 2 packaging/utilities, Stage 3 percentage overhead (labour, marketing…), Stage 4 = unit cost ÷ yield, plus a suggested price range."
**Key line:** "Ingredients say it costs Rp 30k; after real overhead it's Rp 43k. That gap is invisible in a spreadsheet."

---

## Card 5 — Overhead Configuration
**Show:** Settings → Overhead Config.
**Say:**
- "Overhead is configured in one place — packaging per batch, electricity, and the percentage costs."
- "Change it here and every product's cost updates. That's the propagation the spreadsheet lacks."

---

## Card 6 — Production Batch Logging — THE TECHNICAL SHOWPIECE
**Show:** Log Batch → pick a product, set multiplier → submit → batch detail + history.
**Say:**
- "Logging one batch writes to **five tables in a single database transaction**: the batch, each ingredient line, the stock deductions, the audit log, and any low-stock alerts."
- "It's all-or-nothing — if anything fails, the whole thing rolls back. No half-recorded batch."
- "And it **snapshots the cost** at this moment, so March's batch always shows March's cost."
**Key line:** "This is the data-integrity heart of the system."

---

## Card 7 — Low Stock & Shopping List
**Show:** Low Stock → click **+ Stock** (jumps to inventory pre-filled) → then Shopping List with a plan.
**Say:**
- "Low stock flags what's running out; one click jumps to restock it."
- "The shopping list answers 'what do I buy?' — enter how many batches you'll bake, it sums the ingredients, subtracts stock, and gives a printable buy-list with an estimated cost."

---

## Card 8 — Sales / Order Book (profit)
**Show:** Sales → New Order → add 2 product lines (watch live profit) → save → list.
**Say:**
- "She bakes for events. Record an order, and it computes **revenue, cost and profit** live as I type."
- "The list shows total profit and which products earn the most."
**Key line:** "This closes the loop: cost → price → actual profit."

---

## Card 9 — Roles, Mobile & Multi-currency
**Show:** Skip as Worker (reduced menu) → resize narrow (card-stacks) → Preferences → switch language/currency.
**Say:**
- "A Worker sees a reduced menu — no pricing, no sales — safe to hand to staff."
- "Mobile-first: tables become cards, sidebar collapses — built for a phone in the kitchen."
- "Bilingual English/Indonesian, and money can display in any currency."

---

## Card 10 — Wrap
**Say:**
- "From raw material to recorded sale, CostCraft is the full cost-and-production loop the spreadsheet never gave her — overhead-aware, inventory-linked, and transactionally safe."
- "It's deployed and a real bakery operator can use it today."

---

### Demo tips
- Have a product with a recipe and some stock ready so the batch/sale demos work.
- If asked "is this live?" — yes, on Tomcat + MySQL; can be exposed via a tunnel for remote viewing.
- Keep Card 4 (HPP) and Card 6 (transaction) as the two you must not skip.
