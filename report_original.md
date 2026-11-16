J2EE Course Design Report
BakeFlow - A Bakery Production Cost and Inventory Management
System
A Recipe-Driven Web Platform for Tracking Bakery Recipes, Costs, and
Inventory
林志安
L25020027
IT 1
J2EE
Software Engineering
Nanjing Xiaozhuang University
2026
1

I. Project Topic Selection and Explanation
1. Introduction
This project, named BakeFlow, is a recipe-driven cost calculation and inventory management
system designed for small bakery operations. On the platform, a bakery owner (the system
administrator) and her staff (bakers) can manage an ingredient master list with package-level
pricing, define product recipes with precise gram-level ingredient quantities, compute the true
“cost of goods produced” (Harga Pokok Produksi, or HPP) including overhead allocation, log
production batches that automatically deduct ingredients from inventory, and review reports
on margins and stock levels. The platform is built using the Java J2EE technology stack with
a MySQL database. The goal is not to compete with large commercial restaurant POS systems,
but to provide a focused, recipe-aware costing and production tool for a single-bakery operator
who currently relies on spreadsheets to compute production cost and has no integrated way to
track ingredient stock.
2. Problem Statement
Small bakery operators face a recurring operational problem that mainstream commercial
software does not solve well. Every product they bake is the result of a fixed recipe consuming
measured quantities of raw ingredients, and the true cost of one finished jar of cookies or one
loaf of bread is the sum of those ingredient costs plus a share of overhead (electricity, gas,
labor, packaging, and amortized fixed costs). When an ingredient price changes, as it often
does, the cost of every product that uses that ingredient changes too, and pricing decisions need
to adjust accordingly. Without a system that captures this chain, operators are forced into one
of two unhappy positions: either under-price their goods and lose margin invisibly, or over-
price them and lose customers.
At the same time, the ways small bakeries currently calculate cost are poor. Most operators
manage cost through informal channels: handwritten ingredient notes, paper recipes pinned to
the wall, and Google Sheets spreadsheets shared between family members. These channels
share three weaknesses:
a. No automated propagation: when an ingredient price changes, every dependent product
cost must be manually recalculated, which is error-prone and rarely done thoroughly.
b. No overhead awareness: ingredient cost is visible, but electricity, labor, and amortized
fixed costs are usually absent from the calculation. The operator believes a product
yields a 60% margin when, after true overhead, it yields 40%.
c. No inventory linkage: cost calculation lives in one spreadsheet and stock tracking, when
it exists at all, lives in another. There is no automated deduction of ingredients when a
2

batch is produced, so stock levels drift from reality and restocking is reactive rather
than planned.
General commercial restaurant management platforms such as Keruyun (客如云) and Toast POS
partly address the inventory problem, but they are storefront-oriented, optimized for point-of-
sale throughput rather than for the producer side of a small bakery, and they do not model
recipe-level cost calculation. There is a clear gap for a platform that is recipe-driven, overhead-
aware, inventory-integrated, and scoped to a single-bakery operator. BakeFlow is designed to
fill exactly that gap.
3. Why This Topic Is Worth Building
a. It solves a real, recurring problem. The daily cycle of every small bakery involves
recipe-driven production, ingredient consumption, and pricing decisions. A system that
automates the cost-and-inventory chain directly serves operational decisions made
every day, on every batch, in every small bakery.
b. It is realistic to build. The system needs no external APIs or complex integrations.
Every core feature (user login, ingredient master data, recipe and HPP calculation,
production batch logging with transactional inventory deduction) can be built entirely
with technologies covered in this course: Servlet, JSP, JSTL, EL, JDBC, sessions,
filters, and the five-tier architecture.
c. It has genuine value. Beyond convenience, the platform makes implicit overhead costs
explicit, which is the most common blind spot in small-business F&B operations. By
surfacing true cost and supporting price-sensitivity analysis, the system helps the
operator make pricing decisions grounded in data rather than feel.
d. It is personally relevant. The intended end-user is a real small-bakery operator known
to the developer. The user has been requesting a software tool to support her business
operations for an extended period and has already supplied a working HPP spreadsheet
that documents her current process. This grounds the project in an authentic problem
context with a verifiable deployment target.
4. Research Orientation
This project sits at the intersection of solving a concrete operational problem for a real user and
demonstrating the full J2EE technology stack studied in this course. The orientation is two-
fold: first, to design a system that genuinely fits how a small bakery owner works (recipe-
driven thinking, gram-level precision, overhead awareness, and stock-aware production); and
second, to express that design through a clean, layered Java web architecture that shows
command of the fundamentals (Servlet, JSP, JDBC, sessions, filters, transactions). The aim is
a system that works well, makes sense as a product, and reflects real-world web-development
3

thinking, particularly in its treatment of transactional data integrity during production batch
logging.
4

II. Research and Analysis of Similar Projects
1. Overview
To understand what already exists in the bakery and F&B management space and where the
gaps are, three reference points were researched and analyzed: Keruyun (客如云), a leading
Chinese commercial F&B management platform; Toast POS, a leading international restaurant
management platform; and the end-user’s own existing tool, an HPP spreadsheet currently in
daily use. These three were chosen because together they represent the dominant patterns in
this space (large-scale commercial Chinese F&B SaaS, large-scale international restaurant
SaaS, and the informal artifact a small operator actually uses) and they show both what current
solutions offer and what they are still missing for a recipe-driven small-bakery context.
a. Keruyun (客如云)
Figure 1. Keruyun (客如云) F&B Management Platform Interface
What it is: Keruyun is one of China’s leading SaaS providers for the F&B industry, offering
an integrated platform that covers point-of-sale, member management, supply chain, and
operational reporting. Its target customers are storefront restaurants, cafes, and bakery chains.
Architecture (inferred): at its scale, Keruyun most likely runs on a microservices backend
behind a multi-device client (POS terminal, kitchen display, mobile app), with separate services
for orders, members, supply chain, and reporting, all following MVC principles internally.
Core features: real-time point-of-sale checkout with payment integration, SKU-level product
and pricing management, member and loyalty program management, supplier and procurement
workflows, multi-channel sales (in-store, delivery aggregator), and operational dashboards for
revenue and traffic.
5

Strength: highly polished storefront and POS workflows, with deep integration into Chinese
payment and delivery ecosystems.
Weakness for our purpose: Keruyun is optimized for retail throughput, not for the producer
side of a small bakery. Its costing functionality is limited to SKU-level cost-price fields entered
manually by the operator. It does not model recipe-level ingredient consumption, does not
derive product cost from ingredient cost automatically, and does not allocate overhead. The
producer’s actual operational opacity, the true cost of one jar of cookies, is left unsolved.
b. Toast POS
Figure 2. Toast POS Restaurant Management Interface
What it is: Toast POS is one of the largest cloud-based restaurant management platforms in
North America. It provides an integrated suite covering point-of-sale, online ordering,
inventory management, payroll, and analytics for restaurants of every size.
Architecture (inferred): a modern large-scale cloud architecture based on MVC principles,
with a service-oriented backend, a relational core for structured order and inventory data, and
an inventory module that supports recipe-based ingredient depletion as an enterprise add-on.
Core features: real-time POS, online and mobile ordering, recipe-level inventory tracking,
supplier and purchase order management, integrated payment processing, and mature reporting
on margins, food cost percentage, and waste.
Strength: its recipe-based inventory tracking is the clearest mainstream example of the
production-side costing model that this project adopts. It demonstrates that the recipe-driven
approach is operationally sound at industrial scale.
6

Weakness for our purpose: Toast is a complex enterprise product priced for full-service
restaurants. Its recipe-inventory features are typically locked behind higher-tier subscriptions
and require significant configuration. For a single small-bakery operator who currently uses a
spreadsheet, the cost, complexity, and configuration overhead are prohibitive.
c. The End-User’s Existing HPP Spreadsheet
Figure 3. The End-User’s Existing HPP Spreadsheet (Kalkulator HPP)
What it is: The end-user, a small bakery operator, currently manages cost calculation through
a Google Sheets-based HPP calculator titled "Kalkulator HPP Untuk Menentukan Harga Jual"
(Cost-of-Goods-Sold Calculator for Determining Selling Price). This is the most directly
relevant reference because it is the artifact the system is intended to replace.
Structure: the spreadsheet contains three coordinated sheets: a product pricing summary with
per-product HPP and a 3× markup recommendation; an ingredient master list categorized by
tepung, gula, lemak & butter, telur, and so on, with package size, package price, and derived
per-gram cost; and a per-product ingredient breakdown with gram quantities and line-item cost
totals.
Strengths: the ingredient categorization is coherent and is adopted directly in this project. The
per-gram cost derivation is the correct unit-cost abstraction. The recipe-as-line-items
representation maps cleanly to a relational model. The 3× markup convention is a standard
small-business heuristic and is preserved as the system’s default markup multiplier.
Weakness for our purpose: the user has explicitly identified the principal limitation in her
own words: the spreadsheet does not account for electricity, labor, or other overhead costs.
Beyond that, any change to a single ingredient price requires manual re-entry across multiple
dependent products; there is no inventory tracking; there is no historical cost preservation; and
there is no audit trail of changes. These five gaps define this project’s scope of contribution.
7

2. Summary and Comparison
The table below compares the three reference points against the goals of this project.
Table 1. Feature Comparison of Keruyun, Toast POS, HPP Spreadsheet, and BakeFlow
| Feature  | Keruyun  | Toast POS  |     | BakeFlow  |
| -------- | -------- | ---------- | --- | --------- |
HPP
Spreadsheet
| Ingredient master + per- | Limited  | Yes  | Yes  | Yes  |
| ------------------------ | -------- | ---- | ---- | ---- |
gram cost
| Recipe-driven cost   | No      | Yes           | Yes  | Yes  |
| -------------------- | ------- | ------------- | ---- | ---- |
| calculation          |         | (enterprise)  |      |      |
| Overhead allocation  | Manual  | Partial       | No   | Yes  |
(electricity, labor,
packaging)
|     | Yes  | Yes  | No  | Yes  |
| --- | ---- | ---- | --- | ---- |
Inventory and stock
movement
|     | No  | Yes  | No  | Yes  |
| --- | --- | ---- | --- | ---- |
Production batch logging
with transactional
deduction
| Historical cost preservation  | Partial  | Yes  | No   | Yes  |
| ----------------------------- | -------- | ---- | ---- | ---- |
| Audit trail of stock changes  | Yes      | Yes  | No   | Yes  |
| Lightweight for a single      | No       | No   | Yes  | Yes  |
small operator
|     | Yes  | Yes  | No  | Out of scope  |
| --- | ---- | ---- | --- | ------------- |
Real-time POS / customer-
facing channels
Key takeaway: each reference does something well. Keruyun has polished POS workflows
and Chinese payment integration; Toast POS demonstrates that recipe-driven inventory works
at scale; the user’s HPP spreadsheet captures the correct unit-cost abstraction at the right level
of granularity for a small bakery. But none of them brings together recipe-driven costing,
overhead allocation, inventory linkage, and transactional batch logging in a form that a single
small-bakery operator can actually adopt. That intersection (recipe-driven, overhead-aware,
inventory-integrated, and lightweight) is the gap BakeFlow targets.
3. Core Architecture and Module Patterns of Similar Projects
Beneath their differences, all of these platforms (including the user’s spreadsheet, when read
as a structured artifact) share the same layered structure that this project adopts: a view layer
for the user interface, a controller layer to handle requests, a service layer for business logic, a
data-access layer for the database, and the database itself. Each feature (users, ingredients,
recipes, batches) is divided into its own module, and within each module the same vertical
layering is applied.
8

Table 2. Five-Tier Architecture Layers and Technologies
Layer Responsibility Technologies in this project
View Renders the user interface and collects JSP, HTML, CSS
user input
Controller Receives requests, calls the service Servlet (@WebServlet)
layer, returns the response
Service Business logic, validation, rules, Plain Java classes
transaction boundaries
DAO Database queries and CRUD operations JDBC, PreparedStatement
only
Database Persistent data storage MySQL
Figure 4. Five-Tier MVC Architecture of BakeFlow
General functions shared by all similar platforms are user accounts, ingredient or product
master data management, and some form of transaction recording (sales or production).
Distinctive functions that separate stronger platforms are recipe-based cost derivation,
overhead-aware true-cost calculation, and transactional inventory linkage. The key design idea
9

taken from the research is that recipe-driven cost calculation is the deciding feature for a
producer-side bakery system, which is why this project treats Recipe & HPP Calculation as a
first-class module rather than an afterthought, and pairs it with a transactionally-safe
Production Batch Logging module that maintains the integrity of the cost-and-inventory chain
over time.
10

III. Detailed Design of Personal Project Modules and Functions
1. Overall Architecture and Technology Stack
BakeFlow adopts a five-tier MVC architecture built consistently on the J2EE fundamentals.
All four core modules are implemented with plain Servlet and JSP over a JDBC data-access
layer, sharing the same MySQL database and following the same layered pattern. Keeping a
single, uniform technology approach across every module makes the architecture easy to reason
about, keeps each feature in a predictable place, and demonstrates solid command of the core
J2EE stack from end to end.
a. View layer: JSP with JSTL and EL, plus HTML and CSS.
b. Controller layer: Jakarta Servlet (Tomcat 10), using @WebServlet with doGet/doPost.
c. Service layer: plain Java business-logic classes that hold validation, rules, and
transaction boundaries.
d. DAO layer: JDBC with PreparedStatement, accessed through a centralized
DBUtil/JdbcUtil class.
e. Database: MySQL 8.x, configured via an external db.properties file.
f. Cross-cutting: HttpSession for login state; a Filter for login interception on protected
pages; role-based access control filter for owner/baker separation.
g. Build tool: Maven (pom.xml manages all dependencies, including mysql-connector-j).
For every action the request flows JSP → Servlet → Service → DAO → MySQL and back up
the same chain (Figure 4). This single, uniform flow applies to all four modules, so every
feature follows the same predictable path through the architecture.
2. Technical Foundation Required
The project assumes a working command of the following J2EE foundations, all covered by
this course:
a. Configuring Servlets with @WebServlet and handling doGet/doPost.
b. Forwarding and redirecting between Servlets and JSP, and reading request parameters.
c. Displaying data in JSP using EL (${…}) and JSTL (c:forEach, c:if).
d. Maintaining login state with HttpSession, and using a Filter to intercept unauthenticated
requests.
e. Writing JDBC code with PreparedStatement to prevent SQL injection, behind a DAO
layer.
11

f. Centralising the database connection in a DBUtil/JdbcUtil class with external
configuration.
g. Controlling a JDBC transaction with setAutoCommit(false), commit(), and rollback()
across multiple table updates.
h. Designing a normalised relational schema with primary and foreign keys.
3. Scope Decision and Honest Justification
A complete bakery management system could include many more modules (multi-channel
sales, customer loyalty, supplier procurement, advanced demand forecasting, payment gateway
integration, mobile baker apps). Building all of them to a gradeable standard within the
available time is not realistic for an individual developer. This project therefore commits to
four core modules, with the rest documented as optional. The reasons:
a. Time limitation: the report and the working code are due within a short window, which
is not enough to build, test, and debug a large number of modules well.
b. Individual development: this is solo work, so total capacity is bounded by one
person’s hours.
c. Concurrent coursework: the project overlaps with other assignments, further limiting
time.
d. Quality over quantity: four well-built, properly-layered modules demonstrate more
competence than many rushed ones, and the four chosen modules form a complete cost-
and-production loop.
Why these four: User Authentication & Role Management, Ingredient & Inventory
Management, Recipe & HPP Calculation, and Production Batch Logging together cover the
entire core operational loop. An owner can register users, maintain an ingredient catalog with
stock levels, define products with their recipes and computed cost, and log production batches
that atomically deduct ingredients from stock. This is the most meaningful self-contained slice
of the system and demonstrates the Servlet/JSP fundamentals consistently across the full
production cycle.
12

Figure 5. Use Case Diagram
Figure 6. Sequence Diagram of the "Log Production Batch" Process
13

4. Selected Modules - Detailed Design
Each module below is described by its responsibility, core functions and implementation logic,
the operating effect the user observes, the key files involved, and the database tables touched.
a. Module 1 - User Authentication & Role Management Module (Servlet / JSP)
Handles all user identity operations: registration of bakery staff by the owner, login, login-state
management, login interception, role-based access control, and logout. Since every action in
the system requires a logged-in user and most actions are restricted by role, this is the entry
point for the whole platform.
Core functions and implementation logic:
• User Creation (Owner only) - the owner creates accounts for bakery staff (username,
password, role: owner or baker, email). UserService validates the inputs (non-empty
fields, password length, no duplicate username) before UserDao inserts the record.
Before insertion, the password is hashed using MD5 with a per-user salt (via Java’s
MessageDigest) so that no plaintext password is ever written to the database.
• User Login - UserService checks credentials; on success the user object (including the
role) is stored in HttpSession.
• Role-Based Access Control - a RoleFilter intercepts requests to role-restricted URL
patterns (for example, user management and overhead configuration are owner-only)
and rejects role-disallowed requests with a 403 response.
• Login Interception - a LoginFilter mapped to all protected paths checks every request
for a valid session and redirects unauthenticated requests to the login page.
• Logout - LogoutServlet calls session.invalidate() and redirects to the login page.
Operating effect (what the user sees): the owner opens login.jsp and signs in. The header
displays her name and role, and the navigation menu shows all owner-accessible pages
(ingredient master, recipe management, overhead configuration, user management, batch
logging, reports). A baker who signs in sees a reduced menu containing only the pages relevant
to her work (ingredient view, recipe view, batch logging). Any attempt to open a role-restricted
page from a baker account bounces back with an access-denied message. Any attempt to open
any protected page without logging in bounces back to login.
Key files: LoginServlet.java, LogoutServlet.java, UserManageServlet.java, LoginFilter.java,
RoleFilter.java, UserService.java, UserDao.java, User.java, login.jsp, user_manage.jsp,
DBUtil.java
Database: user (id, username, password_hash, salt, role, email, created_at, active)
b. Module 2 - Ingredient & Inventory Management Module (Servlet / JSP)
14

The foundation of the cost-and-stock chain. Manages the categorized ingredient catalog,
including package-level pricing, derived per-gram cost, current stock, low-stock thresholds,
and the audit-logged history of every stock movement. This module is the data backbone on
which Modules 3 and 4 depend.
Core functions and implementation logic:
• Ingredient CRUD - ingredients are organized into categories (Tepung, Gula, Lemak
& Butter, Telur, Pengembang & Pengemulsi, Perisa & Rempah, Garam & Asam,
Kacang & Topping, Lain-lain), mirroring the categorization in the user’s existing
spreadsheet. Each record stores package size, package price, unit of measure, low-stock
threshold, and current stock.
• Per-Gram Cost Derivation - on insert or update, IngredientService computes
price_per_gram = package_price / package_size and stores it as a cached column to
avoid recomputation on every read.
• Cascading Recomputation - when an ingredient price changes, IngredientService
triggers a recomputation pass over all products that include the ingredient in their
recipe, updating cached product-level HPP fields. This is performed inline within the
same transaction.
• Stock Movement Logging - all changes to current_stock route through
InventoryService, which enforces the invariant that every stock change is accompanied
by a matching inventory_log entry (purchase in, batch consumption out, manual
adjustment, waste write-off). The DAO does not expose a raw update path that bypasses
the log.
• Low-Stock Dashboard - a JSP page filters ingredients with current_stock below
low_stock_threshold and surfaces them for restocking decisions.
Operating effect (what the user sees): the owner opens the ingredient list and sees a grid of
ingredients grouped by category, with current stock, low-stock flag, and per-gram cost. She
edits the price of "Unsalted Butter" from 212 IDR/gram to 230 IDR/gram, and on save the HPP
of every product that uses butter is automatically recomputed. She enters a stock purchase for
1000g of flour; the inventory log records the movement with timestamp, reason, and her
username. A separate view shows the inventory history for any one ingredient as a
chronological ledger.
Key files: IngredientServlet.java, IngredientCategoryServlet.java, InventoryServlet.java,
InventoryHistoryServlet.java, IngredientService.java, InventoryService.java,
IngredientDao.java, InventoryLogDao.java, Ingredient.java, InventoryLog.java,
ingredient_list.jsp, ingredient_form.jsp, inventory_history.jsp, low_stock.jsp
15

Database: ingredient_category (id, name); ingredient (id, category_id, name, package_size,
package_price, price_per_gram, unit, current_stock, low_stock_threshold, created_at);
inventory_log (id, ingredient_id, change_quantity, reason, reference_type, reference_id,
user_id, created_at)
c. Module 3 - Recipe & HPP Calculation Module (Servlet / JSP)
The distinctive module, and the one that delivers the project’s core value. Manages product
recipes (the per-product set of ingredients with gram quantities) and computes HPP including
configurable overhead. This module replaces the user’s existing spreadsheet workflow with an
integrated, propagating, overhead-aware calculation.
Core functions and implementation logic:
• Product and Recipe CRUD - products (Nastar, Kastengle, Putri Salju, and so on) are
created with a recipe consisting of recipe_item rows, each linking the product to an
ingredient with a gram_quantity for one base unit (one adoman).
• Overhead Configuration - the owner configures overhead categories (electricity per
batch hour, gas per batch hour, labor per batch hour, packaging per jar, and amortized
fixed monthly costs allocated over typical monthly batch count). Each has a unit cost
and an allocation rule.
• HPP Computation - implemented as a pure Service method that takes a product
reference and returns a structured result: raw material cost (sum of
recipe_item.gram_quantity × ingredient.price_per_gram), the overhead breakdown line
by line, total HPP per adoman, total HPP per jar at a configurable jar gramasi, and a
suggested selling price at a configurable markup multiplier (default 3×).
• Price-Sensitivity View - a parameterized variant of the HPP computation accepts a
hypothetical price for one ingredient and returns the recomputed HPP for every
dependent product without committing the change. This answers questions like: "if
butter rises by 10%, what happens to my margins?"
Operating effect (what the user sees): the owner opens "Nastar" in the product list and sees
a breakdown that mirrors the structure of her existing spreadsheet (ingredient line items with
gram quantities, per-gram costs, and line totals), but with three new sections at the bottom: an
overhead breakdown showing the contribution of electricity, labor, and packaging; the resulting
true HPP per jar; and the suggested selling price. Changing the overhead configuration on a
separate page causes every product’s HPP to update on next view.
Key files: ProductServlet.java, RecipeServlet.java, OverheadServlet.java, HppServlet.java,
PriceSensitivityServlet.java, ProductService.java, RecipeService.java, HppService.java,
ProductDao.java, RecipeItemDao.java, OverheadConfigDao.java, Product.java,
16

RecipeItem.java, OverheadConfig.java, product_list.jsp, recipe_edit.jsp, hpp_detail.jsp,
overhead_config.jsp, price_sensitivity.jsp
Database: product (id, name, base_unit_label, jar_gramasi, markup_multiplier,
cached_hpp_per_adoman, cached_hpp_per_jar, created_at); recipe_item (id, product_id,
ingredient_id, gram_quantity); overhead_config (id, name, unit_cost, allocation_rule, active)
d. Module 4 - Production Batch Logging Module (Servlet / JSP)
The transactionally critical module, and the technical centerpiece of the project. Records a
production batch and atomically deducts the consumed ingredients from inventory across
multiple tables in a single JDBC transaction. This is the module where data consistency matters
most.
Core functions and implementation logic:
• Batch Entry - a baker selects a product, enters the multiplier (how many adoman were
produced), optionally overrides per-ingredient amounts to account for real bakery
practice (humidity adjustments, ingredient substitutions), and submits.
• Atomic Deduction - BatchService.recordBatch() executes inside an explicit JDBC
transaction. It inserts the production_batch row with a snapshot of the HPP at current
prices, inserts a batch_item row for each ingredient consumed (including per-ingredient
unit price at the moment of recording), updates ingredient.current_stock for each
consumed ingredient, and inserts an inventory_log entry for each stock movement. If
any step fails, the entire transaction is rolled back; no partial state is left in the database.
• Low-Stock Alert Generation - after deduction, the same transaction evaluates
low_stock_threshold for each affected ingredient and creates alert rows for any
threshold crossings, surfacing them on the owner’s dashboard.
• Historical Cost Preservation - the batch record snapshots the HPP at the time of
production rather than referencing live prices. A batch produced in March at March
prices does not appear to have cost more when reviewed in June after prices have
changed.
• Batch History - the owner can browse historical batches by date, product, or baker,
and open any batch to see its ingredient consumption and recorded cost.
Transaction handling: because logging a batch writes to four tables (insert production_batch,
insert batch_item rows, update ingredient stock, insert inventory_log rows) and possibly inserts
alert rows, all of these statements are wrapped in a single JDBC transaction. The Service layer
obtains a connection, calls setAutoCommit(false), performs every write, and commits only if
all succeed; if any step fails it calls rollback(), so a half-recorded batch or stock that has been
17

deducted without a matching batch record can never exist. This is the correct course-level way
to guarantee consistency, without introducing heavier specifications such as JTA or EJB.
Operating effect (what the user sees): a baker selects "Nastar" and enters multiplier 2 (two
adoman produced). She glances at the auto-calculated ingredient consumption (330g flour, 50g
milk powder, 200g margarin, and so on) and optionally edits any line. On submit, the system
shows a confirmation with the recorded HPP and any new low-stock alerts. The ingredient list
immediately reflects the deducted quantities. Switching to "Batch History" shows the new entry
at the top, with the HPP frozen at the prices in effect at the time of recording.
Key files: BatchServlet.java, BatchHistoryServlet.java, BatchDetailServlet.java,
BatchService.java, BatchDao.java, BatchItemDao.java, AlertDao.java, ProductionBatch.java,
BatchItem.java, batch_log.jsp, batch_confirm.jsp, batch_history.jsp, batch_detail.jsp
Database: production_batch (id, product_id, multiplier, recorded_hpp_per_adoman,
recorded_hpp_total, baker_id, notes, created_at); batch_item (id, batch_id, ingredient_id,
gram_quantity, unit_price_snapshot, line_cost); alert (id, ingredient_id, alert_type, message,
resolved, created_at)
5. Database Schema Design
The system uses nine related tables. The schema is normalised: a user can record many batches;
an ingredient can appear in many recipes and many batch items; a product is composed of many
recipe items; and every stock change generates an inventory log entry. The unit_price_snapshot
column on batch_item preserves historical cost regardless of later ingredient price changes.
Table 3. Database Schema Summary
Table Key Columns Purpose / Relationships
user id (PK), username, password_hash, salt, role, Owner and baker accounts.
email, active, created_at
ingredient_category id (PK), name Categorization (Tepung, Gula,
etc.).
ingredient id (PK), category_id (FK), name, Raw materials. category_id
package_size, package_price, references
price_per_gram, unit, current_stock, ingredient_category(id).
low_stock_threshold
product id (PK), name, base_unit_label, jar_gramasi, Finished products (Nastar,
markup_multiplier, cached_hpp_per_adoman, Kastengle, etc.).
cached_hpp_per_jar
recipe_item id (PK), product_id (FK), ingredient_id (FK), Join table. References
gram_quantity product(id) and ingredient(id).
18

| Table  | Key Columns  | Purpose / Relationships  |
| ------ | ------------ | ------------------------ |
overhead_config  id (PK), name, unit_cost, allocation_rule,  Owner-configured overhead
|     | active  | categories.  |
| --- | ------- | ------------ |
production_batch  id (PK), product_id (FK), multiplier,  A single recorded batch.
|     | recorded_hpp_per_adoman,  | baker_id references user(id).  |
| --- | ------------------------- | ------------------------------ |
recorded_hpp_total, baker_id (FK), notes,
created_at
batch_item
|     | id (PK), batch_id (FK), ingredient_id (FK),    | Per-ingredient consumption     |
| --- | ---------------------------------------------- | ------------------------------ |
|     | gram_quantity, unit_price_snapshot, line_cost  | record with historical price.  |
inventory_log
|     | id (PK), ingredient_id (FK), change_quantity,  | Audit trail of every stock  |
| --- | ---------------------------------------------- | --------------------------- |
|     | reason, reference_type, reference_id, user_id  | movement.                   |
(FK), created_at

Figure 7. Entity-Relationship Diagram of the Database Schema
6. Representative Logic Example
To make the implementation concrete, the following pseudocode shows the transactional Log
Production Batch operation in the Service layer, the most important business logic in the
system:
19

Connection conn = DBUtil.getConnection();
try {
conn.setAutoCommit(false);
// 1. Snapshot HPP at current prices
BigDecimal hppPerAdoman = hppService.compute(conn, productId);
BigDecimal hppTotal = hppPerAdoman.multiply(BigDecimal.valueOf(multiplier));
// 2. Insert the batch record
int batchId = batchDao.insertBatch(
conn, productId, multiplier, hppPerAdoman, hppTotal, bakerId, notes);
// 3. For each recipe item: insert batch_item, deduct stock, log movement
List<RecipeItem> recipe = recipeDao.findByProduct(conn, productId);
for (RecipeItem ri : recipe) {
BigDecimal qty = ri.getGramQuantity()
.multiply(BigDecimal.valueOf(multiplier));
BigDecimal priceSnap = ingredientDao.getPricePerGram(conn,
ri.getIngredientId());
batchItemDao.insert(conn, batchId, ri.getIngredientId(), qty, priceSnap);
ingredientDao.deductStock(conn, ri.getIngredientId(), qty);
inventoryLogDao.insert(
conn, ri.getIngredientId(), qty.negate(), "BATCH_CONSUMPTION",
"production_batch", batchId, bakerId);
// 4. Check threshold and generate alert if crossed
if (ingredientDao.isBelowThreshold(conn, ri.getIngredientId())) {
alertDao.create(conn, ri.getIngredientId(), "LOW_STOCK");
}
}
conn.commit();
return batchId;
} catch (SQLException e) {
conn.rollback();
throw new ServiceException("Batch logging failed", e);
} finally {
conn.setAutoCommit(true);
DBUtil.close(conn);
}
This shows the separation of concerns (the Servlet never touches a Connection), a single
transaction keeping the batch, its line items, the stock updates, the inventory log, and any
threshold alerts consistent, and centralised connection handling through DBUtil. It also
demonstrates historical cost preservation: the unit_price_snapshot stored on each batch_item
captures the ingredient price at the moment of recording, so a batch logged today will show
today’s cost even when reviewed months later after prices have changed.
7. Optional Extensions and Rationale
Three areas are deliberately kept optional and outside the committed four-module scope:
20

a. Reports & Analysis Module (optional stretch goal): a dedicated reporting module
could surface margin-per-product analysis, ingredient consumption trends, most/least
profitable products, and a more elaborate price-sensitivity dashboard. Although
individual reports are partially served by the HPP module’s existing views, a separate
Reports module would consolidate them and add time-series visualizations. This is
meaningful additional work and is therefore documented as the natural first extension
if time permits.
b. Sales Recording Module (optional, lightweight): a simple end-of-day sales entry
would let the owner record how many jars of each product sold that day, enabling sell-
through analysis and waste tracking. This is deliberately distinct from a real-time POS,
which would require payment integration, receipt printing, and counter hardware, all
outside the scope of a course design. Daily end-of-day entry retains analytical value
without the integration complexity.
c. Spring Boot Migration (optional, framework exploration): as a natural next step
beyond the core J2EE implementation, the Reports & Analysis module is a strong
candidate for migration to Spring Boot with Thymeleaf. Because reports are read-only
and self-contained, the module can be rebuilt as a standalone Spring Boot application
sharing the same MySQL database without touching any other module. This would
replace report Servlets with Spring @Controllers, replace report JSPs with Thymeleaf
templates, and use @GetMapping in place of doGet. The result would demonstrate the
modern framework approach alongside the J2EE fundamentals.
Keeping these as clearly-scoped optional extensions keeps the project realistic and achievable
within the deadline while still showing full awareness of what a complete bakery management
system requires.
8. Technology Coverage by Module
To confirm that the design exercises the full J2EE technology stack taught in this course, the
table below maps each module to the specific technologies it relies on. All four modules are
implemented consistently on the same Servlet/JSP stack, and together they cover the complete
set of J2EE fundamentals: Servlets, JSP, JSTL, EL, HttpSession, Filter, JDBC transactions,
and the DAO pattern, each demonstrated in the module where it naturally belongs.
Table 4. Technology Coverage by Module
Module Core J2EE Technologies Key Techniques Demonstrated
User Authentication & Servlet (doGet/doPost), Session-based login state; LoginFilter
Role Management @WebServlet, JSP, HttpSession, intercepting protected paths; RoleFilter
(Servlet/JSP) Filter enforcing owner-only routes; salted
password hashing; redirect after
login/logout
21

Module Core J2EE Technologies Key Techniques Demonstrated
Ingredient & Inventory Servlet, JSP, EL (${…}), JSTL Category-grouped listing; cached price-
Management (c:forEach, c:if), JDBC, per-gram derivation; cascading
(Servlet/JSP) PreparedStatement recomputation on price update; invariant
enforcement via Service layer (no DAO
bypass of audit log)
Recipe & HPP Servlet, JSP, JSTL, EL, JDBC, Join-table data model (recipe_item);
Calculation PreparedStatement pure-function HPP computation in
(Servlet/JSP) Service; parameterized price-sensitivity
calculation without committing changes;
configurable markup multiplier
Production Batch Servlet, HttpSession, JDBC, JDBC transaction (setAutoCommit /
Logging (Servlet/JSP) PreparedStatement, DBUtil commit / rollback) across four tables;
historical cost snapshotting; threshold-
crossing alert generation within the same
transaction; referential integrity via
foreign keys
All modules (cross- Five-Tier MVC architecture, Strict layer separation (no SQL in
cutting) MySQL, Maven, DBUtil/DAO Servlets, no logic in JSP); centralized
pattern connection handling; HTTP request–
response cycle throughout; external
db.properties configuration
22