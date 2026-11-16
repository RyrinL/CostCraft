# CostCraft — Web Demo Script

Spoken script for a live walkthrough (~8 minutes). Story arc: **cost problem → solution → production → profit**.

---

## Opening (no screen yet)

> "This is CostCraft — a cost and production management system I built for a small bakery.
> The bakery owner was using a spreadsheet to calculate unit cost, but it only counted ingredients.
> Overhead — electricity, labour, packaging, risk — was invisible. She was pricing by feel and losing margin she couldn't see.
> CostCraft fixes that. Let me show you."

---

## 1. Login

*Navigate to the login page.*

> "The system has two roles. **Owner** — full access to pricing, products, and sales. And **Worker** — kitchen staff who log batches and check stock, but can't see financials."

*Log in as `admin`.*

> "Every page sits behind a login filter. Owner-only pages have a second role filter on top. The filter chain runs before any servlet processes the request."

---

## 2. Dashboard

*Land on the owner dashboard.*

> "The dashboard gives a quick read: how many products are active, how many materials are low on stock, any open alerts, and recent production batches."

*Point to the product cost bars.*

> "These bars rank products by unit cost — at a glance you can see which product is most expensive to make."

---

## 3. Materials

*Go to Materials.*

> "About 45 raw materials. Each one has a package price and a package size — the system derives the per-gram cost automatically."

*Click into one material.*

> "If the supplier raises the price of butter, I update it here. The system propagates that change to every product that uses butter — via the Observer pattern. No manual recalculation."

---

## 4. Inventory

*Go to Inventory, record a purchase.*

> "Stock is tracked here. Let me record a purchase."

*Submit the form, then open Inventory History.*

> "Every single stock change — purchase, batch consumption, adjustment, waste — writes a log row. That's an invariant in the code: the service layer enforces it, the DAO has no bypass.
> Cost and stock can never silently drift apart."

---

## 5. Unit Cost — HPP *(the centrepiece)*

*Go to Products, click a product, open Unit Cost.*

> "This is the core problem CostCraft solves."

*Walk through the four stages on screen.*

> "Stage 1: direct materials — every ingredient in the recipe multiplied by its per-gram cost.
> Stage 2: direct other — packaging per batch, electricity allocated by days.
> Stage 3: percentage overhead — labour at 25%, operational at 20%, marketing at 20%, risk buffer at 10%. All applied to the Stage 1+2 total.
> Stage 4: divide by yield to get unit cost, then apply margin to get a suggested price range."

*Point at the numbers.*

> "Ingredients alone say this product costs around Rp 30,000. After real overhead it's closer to Rp 43,000. That gap is what was invisible in the spreadsheet — and it's exactly where margin disappears."

---

## 6. Overhead Configuration

*Go to Overhead Config.*

> "All the overhead parameters live in one place. Change the electricity cost here and every product recalculates. That's the propagation her spreadsheet never had."

---

## 7. Production Batch *(the technical centrepiece)*

*Go to Log Batch, pick a product and multiplier, submit.*

> "Watch what happens when I log one batch."

*Batch recorded — open batch detail.*

> "That single submit just wrote to **five database tables inside one transaction**: the batch record, each ingredient line with a cost snapshot, the stock deductions, the inventory audit log, and any low-stock alerts.
> It's all-or-nothing. If anything fails — network, constraint, anything — the whole thing rolls back. No partial batch, no stock deducted without a record.
> And it **snapshots the unit cost at this moment**, so this batch will always show today's cost even if prices change tomorrow."

> "This is the data-integrity heart of the system."

---

## 8. Low Stock & Shopping List

*Go to Low Stock.*

> "After that batch, some materials dropped below their threshold. They show here as alerts.
> One click on '+ Stock' jumps straight to the inventory form pre-filled for that material."

*Go to Shopping List, enter a batch plan.*

> "The shopping list answers the practical question: what do I need to buy before the next bake?
> Enter how many batches you're planning — it sums the ingredients, subtracts what's already in stock, and gives a buy list with an estimated cost."

---

## 9. Sales

*Go to Sales, create a new order, add two product lines.*

> "The bakery bakes for events and orders. I add products and quantities — revenue, cost, and profit update live as I type."

*Save, return to list.*

> "The order list shows total profit and which products are earning the most. This closes the loop: from raw material cost, through production, to actual recorded profit."

---

## 10. Worker Role & Mobile

*Log out, log in as a Worker (or switch accounts).*

> "A Worker sees a reduced menu — no unit costs, no sales, no overhead config. Safe to hand to kitchen staff."

*Resize the browser narrow.*

> "The layout is mobile-first. Tables become card stacks, the sidebar collapses. The batch logging form is designed to be used on a phone in the kitchen."

*Go to Preferences.*

> "Language switches between English and Indonesian. Money can display in IDR, USD, EUR, or CNY — the conversion happens at render time, everything is stored in rupiah."

---

## Close

> "From raw material to recorded sale — CostCraft is the full cost-and-production loop the spreadsheet never gave her. Overhead-aware, inventory-linked, and transactionally safe.
> It's running on Tomcat and MySQL and a real bakery operator can use it today."

---

## Tips

- Pre-load a product with a full recipe and some stock before the demo so batch logging and sales work without setup visible.
- **Do not skip** Unit Cost (step 5) or Production Batch (step 7) — these are the two moments that answer "what problem does this solve?" and "what's technically interesting?"
- If asked *"is this live?"* — yes, Tomcat + MySQL, tunnelled via ngrok for remote viewing.
- If asked *"why not Spring Boot?"* — plain Servlet keeps the transaction boundary in `BatchService.recordBatch()` visible as code. `@Transactional` would erase the design pattern that's the centrepiece of the coursework.
