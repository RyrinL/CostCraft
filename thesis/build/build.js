const fs = require('fs');
const path = require('path');
const {
  Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell, ImageRun,
  Footer, AlignmentType, LevelFormat, TableOfContents, HeadingLevel,
  BorderStyle, WidthType, ShadingType, PageNumber
} = require('docx');

const SS = path.join(__dirname, '..', 'SS');
const CONTENT_W = 9360;

function pngSize(p) { const b = fs.readFileSync(p); return { w: b.readUInt32BE(16), h: b.readUInt32BE(24) }; }

let figNum = 0;
function figure(file, maxWpx, caption) {
  figNum++;
  const maxImg = process.env.MAXIMG ? parseInt(process.env.MAXIMG, 10) : Infinity;
  if (process.env.NOIMG || figNum > maxImg) return [new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 160 }, children: [new TextRun({ text: `[Figure ${figNum}. ${caption}]`, italics: true, size: 18, color: '6B5D52' })] })];
  figNum--;
  const full = file.includes('/') ? path.join(__dirname, '..', file) : path.join(SS, file);
  const { w, h } = pngSize(full);
  const width = Math.min(maxWpx, w);
  const height = Math.round(width * h / w);
  figNum++;
  return [
    new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: 120, after: 40 },
      children: [new ImageRun({ type: 'png', data: fs.readFileSync(full), transformation: { width, height },
        altText: { title: caption, description: caption, name: 'fig' + figNum } })] }),
    new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: 160 },
      children: [new TextRun({ text: `Figure ${figNum}. ${caption}`, italics: true, size: 18, color: '6B5D52' })] })
  ];
}

const P = (t, o = {}) => new Paragraph({ spacing: { after: 120, line: 276 }, alignment: AlignmentType.JUSTIFIED, children: [new TextRun({ text: t, size: 22, ...o })] });
const Pr = (runs) => new Paragraph({ spacing: { after: 120, line: 276 }, alignment: AlignmentType.JUSTIFIED, children: runs });
const H1 = (t) => new Paragraph({ heading: HeadingLevel.HEADING_1, pageBreakBefore: true, children: [new TextRun(t)] });
const H2 = (t) => new Paragraph({ heading: HeadingLevel.HEADING_2, children: [new TextRun(t)] });
const H3 = (t) => new Paragraph({ heading: HeadingLevel.HEADING_3, children: [new TextRun(t)] });
const bullet = (t) => new Paragraph({ numbering: { reference: 'b', level: 0 }, spacing: { after: 60 }, children: [new TextRun({ text: t, size: 22 })] });
const num = (t) => new Paragraph({ numbering: { reference: 'n', level: 0 }, spacing: { after: 60 }, children: [new TextRun({ text: t, size: 22 })] });

// code block: single shaded cell, one paragraph per line, monospace
const cb = { style: BorderStyle.SINGLE, size: 1, color: 'DDD3C4' };
function code(lines, caption) {
  const out = [new Table({
    width: { size: CONTENT_W, type: WidthType.DXA }, columnWidths: [CONTENT_W],
    rows: [new TableRow({ children: [new TableCell({
      borders: { top: cb, bottom: cb, left: cb, right: cb },
      width: { size: CONTENT_W, type: WidthType.DXA },
      shading: { fill: 'F7F4EE', type: ShadingType.CLEAR },
      margins: { top: 100, bottom: 100, left: 140, right: 120 },
      children: lines.map(l => new Paragraph({ spacing: { after: 0, line: 240 },
        children: [new TextRun({ text: l === '' ? ' ' : l, font: 'Consolas', size: 16, color: '2E2520' })] }))
    })] })]
  })];
  if (caption) out.push(new Paragraph({ spacing: { before: 30, after: 160 }, children: [new TextRun({ text: caption, italics: true, size: 17, color: '6B5D52' })] }));
  else out.push(new Paragraph({ spacing: { after: 120 }, children: [] }));
  return out;
}

const cellBorder = { style: BorderStyle.SINGLE, size: 1, color: 'CCCCCC' };
const borders = { top: cellBorder, bottom: cellBorder, left: cellBorder, right: cellBorder };
function cell(text, w, o = {}) {
  return new TableCell({ borders, width: { size: w, type: WidthType.DXA },
    shading: o.head ? { fill: 'C8965A', type: ShadingType.CLEAR } : undefined,
    margins: { top: 60, bottom: 60, left: 110, right: 110 },
    // keepNext on every cell keeps all rows on one page (table moves as a block, never splits)
    children: (Array.isArray(text) ? text : [text]).map(t => new Paragraph({ alignment: o.center ? AlignmentType.CENTER : AlignmentType.LEFT, keepNext: true,
      children: [new TextRun({ text: t, size: 18, bold: !!o.head, color: o.head ? 'FFFFFF' : '2E2520' })] })) });
}
function table(widths, rows, caption) {
  const t = new Table({ width: { size: CONTENT_W, type: WidthType.DXA }, columnWidths: widths,
    rows: rows.map((r, i) => new TableRow({ tableHeader: i === 0, cantSplit: true, children: r.map((c, j) => cell(c, widths[j], { head: i === 0, center: i > 0 && j > 0 })) })) });
  const out = [];
  // Table caption ABOVE the table (academic convention) and bound to it (keepNext)
  // so the caption and table never end up on separate pages.
  if (caption) out.push(new Paragraph({ alignment: AlignmentType.CENTER, spacing: { before: 160, after: 60 }, keepNext: true, children: [new TextRun({ text: caption, italics: true, size: 18, color: '6B5D52' })] }));
  out.push(t);
  out.push(new Paragraph({ spacing: { after: 120 }, children: [] }));
  return out;
}

const children = [];

// ===== TITLE PAGE =====
const tp = (t, o) => new Paragraph({ alignment: AlignmentType.CENTER, spacing: { after: (o && o.after) || 120 }, children: [new TextRun({ size: 24, ...o, text: t })] });
children.push(
  new Paragraph({ spacing: { before: 1300, after: 200 }, alignment: AlignmentType.CENTER, children: [new TextRun({ text: 'J2EE Course Thesis', size: 28, bold: true, color: '6B5D52' })] }),
  tp('CostCraft', { size: 56, bold: true, after: 80 }),
  tp('A Recipe-Driven Bakery Production Cost and', { size: 26, bold: true, after: 40 }),
  tp('Inventory Management System', { size: 26, bold: true, after: 900 }),
  tp('Author:  林志安 (L25020027)', { after: 80 }),
  tp('Class:  IT 1     Major:  Software Engineering', { after: 80 }),
  tp('Course:  J2EE', { after: 80 }),
  tp('Nanjing Xiaozhuang University', { after: 80 }),
  tp('2026', {})
);

// ===== TOC =====
children.push(new Paragraph({ pageBreakBefore: true, heading: HeadingLevel.HEADING_1, children: [new TextRun('Table of Contents')] }));
children.push(new TableOfContents('Table of Contents', { hyperlink: true, headingStyleRange: '1-3' }));

// ============================================================
// ABSTRACT
// ============================================================
children.push(H1('Abstract'));
children.push(P('Small bakery businesses frequently rely on spreadsheets to calculate production costs and manage inventory. Although spreadsheets provide flexibility, they often require manual updates, lack inventory integration, and cannot automatically propagate ingredient cost changes across products. These limitations may lead to inaccurate pricing decisions, inefficient inventory control, and difficulty maintaining historical production records.'));
children.push(P('This project develops CostCraft, a web-based production cost and inventory management system designed specifically for small bakery operations. The system was developed using Java EE technologies including Servlets, JSP, JSTL, JDBC, and MySQL within a five-layer architecture. CostCraft integrates material management, recipe-based production costing, overhead allocation, production batch recording, sales tracking, audit logging, and multi-currency support into a single platform.'));
children.push(P('The system applies a four-stage cost calculation methodology consisting of direct material costs, direct other costs, percentage-based overhead costs, and final unit-cost calculation. Inventory consumption is automatically recorded during production activities, while historical cost snapshots preserve the accuracy of past transactions. Role-based access control separates owner and worker responsibilities to improve operational security.'));
children.push(P('The implementation demonstrates how a lightweight web application can automate bakery production costing and inventory management while maintaining data integrity and traceability. Functional testing shows that the implemented modules satisfy the identified requirements and provide a practical replacement for spreadsheet-based workflows.'));
children.push(Pr([new TextRun({ text: 'Keywords: ', bold: true, size: 22 }), new TextRun({ text: 'Bakery Management System, Inventory Management, Production Costing, Java EE, JDBC, Cost Calculation, Recipe-Based Costing.', size: 22 })]));

// ============================================================
// 1. INTRODUCTION
// ============================================================
children.push(H1('1  Introduction'));
children.push(H2('1.1  Project Background and Motivation'));
children.push(H3('Background'));
children.push(P('Bakery production is inherently recipe-driven. Every finished product requires a specific combination of raw materials consumed in measured quantities. The production cost of a product depends not only on ingredient prices but also on operational expenses such as packaging, utilities, labor, and other overhead costs. As ingredient prices fluctuate over time, the cost of producing each product also changes, making accurate cost tracking essential for maintaining profitability.'));
children.push(P('Many small bakery operators still rely on spreadsheets to manage production costs. While spreadsheets are capable of performing basic calculations, they often require manual updates and lack integration with inventory management. Changes to ingredient prices may not automatically propagate across all affected products, resulting in inconsistent costing information and increased administrative effort.'));
children.push(P('Furthermore, inventory tracking is frequently performed separately from cost calculations, making it difficult to determine material consumption, monitor stock levels, and maintain historical production records. These limitations highlight the need for a dedicated system capable of integrating recipe management, cost calculation, inventory tracking, and production recording within a unified platform.'));
children.push(H3('Motivation'));
children.push(P('The motivation for this project originates from the needs of a real small-bakery operator who currently relies on a Google Sheets-based "Kalkulator HPP" (Harga Pokok Produksi) to calculate production costs. Although the spreadsheet successfully models ingredient-based costing, several practical limitations were identified during observation and use.'));
children.push(P('First, ingredient price changes do not automatically update all related product costs. Second, operational overhead such as electricity, labor, and packaging costs are not systematically incorporated into calculations. Third, inventory levels are not automatically reduced when production occurs. Fourth, historical cost information is not preserved when prices change. Finally, the spreadsheet lacks an audit trail capable of recording stock movements and production activities.'));
children.push(P('These limitations motivated the development of CostCraft as an integrated solution that combines production costing, inventory management, audit logging, and sales tracking while remaining simple enough for adoption by a single bakery operator.'));
children.push(H2('1.2  Objectives and Scope'));
children.push(P('The objective is to replace the spreadsheet with an integrated web system that derives true unit cost (HPP) with explicit overhead, links cost to inventory so that producing a batch atomically deducts stock and records an audit trail, preserves historical cost, separates the owner and worker roles, and demonstrates command of the J2EE stack through a clean five-tier MVC implementation. The system commits to the following modules, which together form the complete cost-and-production loop, and which were implemented:'));
children.push(bullet('User Authentication & Role Management; Material & Inventory Management; Product, BOM & Unit-Cost (HPP) Calculation; and Production Batch Logging: the four core modules.'));
children.push(bullet('A Sales / Order Book that records orders for events and festivals and reports revenue and profit.'));
children.push(bullet('Cross-cutting capabilities: multi-currency display, English/Indonesian internationalisation, and a mobile-first responsive interface.'));
children.push(P('Deliberately out of scope are a customer-facing storefront, real-time point-of-sale with payment hardware, finished-goods inventory and multi-tenant hosting of several independent bakeries; CostCraft is designed as a single-operator tool. The remainder of this report is organised as the template requires: Section 2 presents the design analysis, Section 3 the design implementation with key code and screenshots, and Section 4 the summary.'));

// ============================================================
// 2. DESIGN ANALYSIS  (~10-15 pages)
// ============================================================
children.push(H1('2  Design Analysis'));

children.push(H2('2.1  Requirement Analysis'));
children.push(H3('2.1.1  Functional Requirements'));
children.push(P('Functional requirements describe the services and operations that CostCraft must provide to support bakery production activities, inventory management, cost calculation, and sales recording. Based on user requirements and workflow analysis, the system must provide the following functionalities:'));
children.push(bullet('Authentication and Role Management: Users must log in before accessing system functions. The owner can create worker accounts and assign roles. Owner-only modules such as user management, overhead configuration, and sales management must be protected from unauthorized access.'));
children.push(bullet('Material and Inventory Management: The system must maintain a categorized material catalog, calculate per-unit material costs, track stock levels, record inventory movements, generate low-stock alerts, and assist purchase planning.'));
children.push(bullet('Product, Recipe, and Cost Management: Users must be able to create products, define bills of materials (BOM), configure overhead costs, calculate production costs, and analyze pricing recommendations.'));
children.push(bullet('Production Batch Management: The system must record production batches, deduct consumed ingredients automatically, preserve cost snapshots, generate inventory logs, and maintain batch history records.'));
children.push(bullet('Sales Management: Users must be able to record customer orders, calculate revenue and profit, and maintain sales history.'));
children.push(H3('2.1.2  Non-functional Requirements'));
children.push(P('Non-functional requirements define the quality attributes and operational characteristics of the system. These requirements ensure that CostCraft remains reliable, secure, maintainable, and user-friendly.'));
children.push(bullet('Correctness: Monetary and quantity calculations must maintain high precision to prevent rounding errors.'));
children.push(bullet('Security: Passwords must be stored securely, database operations must prevent SQL injection attacks, and role-based access control must restrict unauthorized actions.'));
children.push(bullet('Data Integrity: Multi-table operations must execute atomically so that partial updates cannot occur when failures happen during transactions.'));
children.push(bullet('Usability: The interface must support both desktop and mobile devices and provide multilingual support for English and Indonesian users.'));
children.push(bullet('Maintainability: The application architecture must separate presentation, business logic, and data access concerns to simplify future maintenance and enhancement.'));

children.push(H2('2.2  Analysis of Similar Systems'));
children.push(P('To identify the unique contribution of CostCraft, three reference systems were analyzed: Keruyun, Toast POS, and the end-user’s existing Kalkulator HPP spreadsheet.'));
children.push(P('Keruyun is a cloud-based food and beverage management platform widely used in China. It provides point-of-sale functionality, member management, supply-chain management, and business reporting. However, product costs are primarily maintained as manually entered values and are not automatically derived from recipe-level ingredient consumption. Additionally, overhead allocation is not a core component of its costing model.'));
children.push(...figure('Keruyun.png', 520, 'Keruyun food and beverage management platform interface'));
children.push(P('Toast POS is a cloud-based restaurant management platform offering point-of-sale operations, online ordering, inventory management, payroll processing, and business analytics. Toast supports recipe-based inventory depletion, making it closer to the production-oriented approach adopted in CostCraft. However, its extensive configuration requirements and subscription-based pricing model make it less suitable for small bakery operators with relatively simple operational needs.'));
children.push(...figure('Toast_POS.png', 520, 'Toast POS restaurant management interface'));
children.push(P('The most relevant reference is the bakery operator’s existing Kalkulator HPP spreadsheet. The spreadsheet already contains categorized ingredients, recipe structures, and basic production-cost calculations. These concepts are directly adopted in CostCraft. However, the spreadsheet lacks overhead allocation, automatic cost propagation, inventory integration, historical cost preservation, and audit logging. Addressing these limitations forms the primary contribution of this project.'));
children.push(...figure('HPP_calc.png', 340, 'The end-user’s existing Kalkulator HPP spreadsheet'));
children.push(...table([3360, 1500, 1500, 1500, 1500], [
  ['Capability', 'Keruyun', 'Toast', 'HPP Sheet', 'CostCraft'],
  ['Ingredient master + per-gram cost', 'Limited', 'Yes', 'Yes', 'Yes'],
  ['Recipe-driven cost calculation', 'No', 'Yes', 'Yes', 'Yes'],
  ['Overhead allocation', 'Manual', 'Partial', 'No', 'Yes'],
  ['Inventory & stock movement', 'Yes', 'Yes', 'No', 'Yes'],
  ['Transactional batch logging', 'No', 'Yes', 'No', 'Yes'],
  ['Historical cost preservation', 'Partial', 'Yes', 'No', 'Yes'],
  ['Audit trail of stock changes', 'Yes', 'Yes', 'No', 'Yes'],
  ['Lightweight for one operator', 'No', 'No', 'Yes', 'Yes'],
], 'Table 1. Feature comparison against CostCraft'));
children.push(P('No existing solution combines recipe-driven costing, overhead allocation, inventory linkage and transactional batch logging in a form a single small-bakery operator can adopt. That intersection is the design target.'));

children.push(H2('2.3  Use-Case Analysis'));
children.push(P('CostCraft involves two primary actors: Owner and Worker. The Owner possesses full administrative control over the system, including user management, material management, product management, overhead configuration, production monitoring, inventory management, and sales recording. The Worker focuses on operational activities such as inventory recording, material monitoring, and production batch logging.'));
children.push(P('All use cases require successful authentication before access is granted. Login therefore serves as a prerequisite for every functional operation within the system. This design ensures that all actions can be associated with a specific user, improving accountability and supporting audit-trail functionality. Figure 4 summarises the use cases and the role split.'));
children.push(...figure('diagrams/usecase.png', 580, 'Use-case diagram (gold = owner-only, green = both roles)'));

children.push(H2('2.4  Overall Architecture'));
children.push(P('CostCraft adopts a layered architecture consisting of five logical layers: Presentation Layer, Controller Layer, Service Layer, Data Access Layer, and Database Layer.'));
children.push(P('The Presentation Layer provides user interfaces implemented using JSP, JSTL, EL, Bootstrap, and custom CSS. User requests are received by the Controller Layer, which consists of Jakarta Servlets responsible for request processing and navigation control.'));
children.push(P('Business rules and application logic are implemented within the Service Layer. This layer coordinates operations involving multiple entities and manages transactional workflows. The Data Access Layer handles communication with the database through JDBC and PreparedStatement objects. Finally, persistent data is stored in a MySQL database.'));
children.push(P('This layered approach improves maintainability by clearly separating user-interface concerns, business logic, and data access responsibilities. Figure 5 shows the overall layered architecture.'));
children.push(...figure('diagrams/architecture.png', 300, 'Layered architecture of CostCraft'));
children.push(P('The system was developed entirely with Java EE technologies. Table 2 summarises the technologies used in each layer of the implementation.'));
children.push(...table([3200, 6160], [
  ['Layer / concern', 'Technology'],
  ['Presentation', 'JSP, JSTL, EL, Bootstrap, custom CSS'],
  ['Controller', 'Jakarta Servlet (@WebServlet, doGet / doPost)'],
  ['Service', 'Plain Java business-logic classes; transaction control'],
  ['Data access', 'JDBC with PreparedStatement (DAO pattern)'],
  ['Database', 'MySQL 8 (InnoDB), external db.properties configuration'],
  ['Platform / build', 'Jakarta EE on Apache Tomcat 10; Apache Maven'],
  ['Security', 'Session-based login, login and role filters, salted password hashing'],
], 'Table 2. Technologies used in CostCraft'));

children.push(H2('2.5  Database Design'));
children.push(P('The CostCraft database consists of thirteen tables designed to support user management, inventory management, production recording, sales tracking, overhead configuration, and currency management.'));
children.push(P('The user table stores account information and role assignments. Materials are organized using material_category and material tables. Product definitions are stored in the product table, while recipe compositions are maintained through bom_item records.'));
children.push(P('Production activities are represented by production_batch and batch_item tables. Inventory movements are recorded in inventory_log, while stock shortages generate entries in the alert table. Sales transactions are maintained through sales_order and sales_order_item tables.'));
children.push(P('The overhead_config table stores configurable direct and percentage-based overhead allocations, allowing flexible cost calculation. Meanwhile, the currency_rate table supports multi-currency display through exchange-rate conversion and currency formatting.'));
children.push(P('Historical accuracy is preserved through snapshot fields such as unit_cost_snapshot and recorded_unit_cost, ensuring that previous transactions remain unchanged even when material costs are updated later. Figure 6 shows the entity-relationship diagram of the database.'));
children.push(...figure('diagrams/erd_user.png', 620, 'Entity-relationship diagram of the CostCraft database'));
children.push(...table([2600, 6760], [
  ['Table', 'Purpose'],
  ['user', 'Owner and worker accounts (salted password hash, role, display currency, language).'],
  ['material_category, material', 'Categorised raw materials with package price and derived per-gram unit cost.'],
  ['product, bom_item', 'Finished products and their gram-level recipes (bill of materials).'],
  ['overhead_config', 'Owner-configured Stage-2 and Stage-3 overhead items.'],
  ['production_batch, batch_item', 'A recorded batch and its per-ingredient consumption with cost snapshot.'],
  ['inventory_log, alert', 'Audit trail of every stock change; low-stock alerts.'],
  ['sales_order, sales_order_item', 'Orders and product lines with revenue, cost and profit.'],
  ['currency_rate', 'Conversion rate, symbol and decimals per display currency.'],
], 'Table 3. Database table summary'));

children.push(H2('2.6  Cost Calculation Methodology'));
children.push(P('The central design idea is that unit cost is computed in four explicit stages that are never collapsed, mirroring the operator’s spreadsheet while adding the overhead it omits:'));
children.push(num('Stage 1, Direct Materials: the sum over the bill of materials of gram_quantity × material unit cost.'));
children.push(num('Stage 2, Direct Other Costs: per-batch physical costs not in the recipe (jar, lid, baking paper, sticker, water, and a daily-allocated share of electricity).'));
children.push(num('Stage 3, Percentage Overhead: configurable percentages of total direct cost (operational, labour, marketing, risk buffer).'));
children.push(num('Stage 4, Unit Cost and Price: total cost divided by yield per batch gives the unit cost (HPP); applying lower and upper margin multipliers gives a suggested selling-price range.'));
children.push(P('Stages 1 and 2 sum to the total direct cost, which is the base for the Stage-3 percentages. Because overhead is configured in one place, changing it consistently updates every product’s cost. The breakdown is returned as a structured object so the view can show each stage separately.'));

// ============================================================
// 3. DESIGN IMPLEMENTATION
// ============================================================
children.push(H1('3  Design Implementation'));
children.push(P('This section shows how the design in Section 2 is realised, through key code excerpts and the running interface. It does not repeat the design rationale. The project is a Maven web application packaged as a WAR and deployed to Tomcat 10; classes are organised by responsibility into controller, service, dao, entity, dto, util and filter packages.'));

children.push(H2('3.1  Database Connectivity'));
children.push(P('CostCraft uses a centralized database utility component to establish connections with the MySQL database. Connection parameters are stored externally within a configuration file, allowing deployment-specific settings to be modified without changing application source code.'));
children.push(P('All database operations are performed through the DAO layer, ensuring that database access remains separated from business logic. This approach improves maintainability, readability, and consistency throughout the application. Listing 1 shows how a connection is obtained from the external configuration and used by a DAO.'));
children.push(...code([
  '// Database settings are read from an external db.properties file',
  'Properties props = new Properties();',
  'props.load(getClass().getResourceAsStream("/db.properties"));',
  '',
  'Connection conn = DriverManager.getConnection(',
  '        props.getProperty("db.url"),',
  '        props.getProperty("db.username"),',
  '        props.getProperty("db.password"));',
  '',
  '// DAOs always use PreparedStatement (prevents SQL injection)',
  'String sql = "SELECT * FROM material WHERE id = ?";',
  'try (PreparedStatement ps = conn.prepareStatement(sql)) {',
  '    ps.setLong(1, materialId);',
  '    ResultSet rs = ps.executeQuery();',
  '    // ... map the result row to a Material object',
  '}'
], 'Listing 1. Obtaining a database connection and querying through a DAO.'));

children.push(H2('3.2  Authentication and Access Control'));
children.push(P('Login verifies a salted hash, stores the user in the session, and redirects (Post/Redirect/Get).'));
children.push(...code([
  'protected void doPost(HttpServletRequest req, HttpServletResponse resp) {',
  '    User user = userService.login(req.getParameter("username"),',
  '                                  req.getParameter("password"));',
  '    if (user == null) {',
  '        req.setAttribute("error", "Invalid username or password.");',
  '        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp")',
  '           .forward(req, resp);',
  '        return;',
  '    }',
  '    HttpSession session = req.getSession(true);',
  '    session.setAttribute("user", user);              // login state',
  '    resp.sendRedirect(req.getContextPath() + "/dashboard");',
  '}'
], 'Listing 2. LoginServlet: session login and PRG.'));
children.push(P('A RoleFilter, mapped in web.xml to /users, /overhead and /sales, rejects non-owners with 403.'));
children.push(...code([
  'public class RoleFilter implements Filter {',
  '    public void doFilter(ServletRequest rq, ServletResponse rs,',
  '                         FilterChain chain) throws IOException, ServletException {',
  '        User u = (User)((HttpServletRequest) rq).getSession()',
  '                       .getAttribute("user");',
  '        if (u == null || !u.isOwner()) {',
  '            ((HttpServletResponse) rs)',
  '                .sendError(HttpServletResponse.SC_FORBIDDEN);',
  '            return;',
  '        }',
  '        chain.doFilter(rq, rs);                       // owner -> continue',
  '    }',
  '}'
], 'Listing 3. RoleFilter: owner-only authorization.'));
children.push(...figure('login.png', 340, 'Login page'));
children.push(...figure('dashboard-admin.png', 580, 'Owner dashboard (full navigation)'));
children.push(...figure('dashboard_worker.png', 580, 'Worker dashboard (reduced navigation: no Sales, Overhead or Users)'));
children.push(...figure('users.png', 580, 'User management (owner only)'));

children.push(H2('3.3  Material & Inventory Management'));
children.push(P('All persistence uses PreparedStatement behind the DAO layer. The DAO methods accept a Connection so the service can compose them in a transaction.'));
children.push(...code([
  'public Long insertBatch(Connection conn, Long productId, BigDecimal mult,',
  '        BigDecimal units, BigDecimal unitCost, BigDecimal total,',
  '        Long workerId, String notes) throws SQLException {',
  '    String sql = "INSERT INTO production_batch (product_id, multiplier,"',
  '       + " units_produced, recorded_unit_cost, recorded_total_cost,"',
  '       + " worker_id, notes) VALUES (?,?,?,?,?,?,?)";',
  '    try (PreparedStatement ps = conn.prepareStatement(sql,',
  '                 Statement.RETURN_GENERATED_KEYS)) {',
  '        ps.setLong(1, productId);     ps.setBigDecimal(2, mult);',
  '        ps.setBigDecimal(3, units);   ps.setBigDecimal(4, unitCost);',
  '        ps.setBigDecimal(5, total);   ps.setLong(6, workerId);',
  '        ps.setString(7, notes);       ps.executeUpdate();',
  '        try (ResultSet k = ps.getGeneratedKeys()) { k.next(); return k.getLong(1); }',
  '    }',
  '}'
], 'Listing 4. A DAO insert with PreparedStatement and a passed-in Connection.'));
children.push(...figure('materials.png', 580, 'Material catalogue with search, filter and sorting'));
children.push(...figure('materials_form.png', 340, 'Material create / edit form (owner only)'));
children.push(...figure('inventory.png', 580, 'Recording a stock movement'));
children.push(...figure('inventory_history.png', 580, 'Inventory movement history (audit ledger)'));
children.push(...figure('low_stock.png', 580, 'Low-stock list with one-click restock'));
children.push(...figure('shopping_list.png', 580, 'Shopping-list planner with estimated cost'));

children.push(H2('3.4  Product, BOM & Unit-Cost (HPP) Calculation'));
children.push(P('The four-stage methodology is implemented in UnitCostService.compute, which builds the calculation stage by stage and returns a structured breakdown.'));
children.push(...code([
  'public UnitCostBreakdown compute(Connection conn, Long productId) {',
  '    Product product = productDao.findById(conn, productId);',
  '    List<BomItem> bom = bomItemDao.findByProduct(conn, productId);',
  '',
  '    UnitCostCalculation calc = new DirectMaterialsCost(bom);   // Stage 1',
  '',
  '    for (OverheadConfig c :                                    // Stage 2',
  '         overheadConfigDao.findActiveByCostStage(conn, "DIRECT_OTHER"))',
  '        calc = new DirectOtherCostDecorator(calc, c, ctx);',
  '    BigDecimal totalDirect = calc.getTotalDirect();',
  '',
  '    for (OverheadConfig c :                                    // Stage 3',
  '         overheadConfigDao.findActiveByCostStage(conn, "PERCENTAGE_OVERHEAD"))',
  '        calc = new PercentageOverheadDecorator(calc, c, ctxWithDirect);',
  '',
  '    calc = new UnitCostFinalizer(calc, product.getYieldPerBatch()); // Stage 4',
  '    result.unitCost   = calc.getUnitCost();',
  '    result.priceLower = result.unitCost.multiply(ONE.add(marginLower));',
  '    result.priceUpper = result.unitCost.multiply(ONE.add(marginUpper));',
  '    return result;',
  '}'
], 'Listing 5. UnitCostService: the four-stage unit-cost computation.'));
children.push(...figure('products.png', 580, 'Product list with cost-comparison bars'));
children.push(...figure('BOM.png', 580, 'Editing a product’s bill of materials (recipe)'));
children.push(...figure('Unit_Cost.png', 580, 'Unit-cost (HPP) four-stage breakdown'));
children.push(...figure('overhead.png', 580, 'Overhead configuration (Stage 2 and Stage 3)'));
children.push(...figure('price_sensitivity.png', 580, 'Price-sensitivity analysis'));

children.push(H2('3.5  Production Batch Logging (Transaction)'));
children.push(P('The integrity centrepiece is one explicit JDBC transaction across several tables; it commits only on full success and rolls back on any failure, and snapshots the unit cost so history is preserved.'));
children.push(...code([
  'public long recordBatch(Long productId, BigDecimal multiplier,',
  '        Map<Long,BigDecimal> overrides, Long workerId, String notes) {',
  '    Connection conn = DBUtil.getInstance().getConnection();',
  '    try {',
  '        conn.setAutoCommit(false);                       // begin transaction',
  '        UnitCostBreakdown bd = unitCostService.compute(conn, productId);',
  '        BigDecimal units = product.getYieldPerBatch().multiply(multiplier);',
  '        long batchId = batchDao.insertBatch(conn, productId, multiplier,',
  '              units, bd.unitCost, bd.unitCost.multiply(units), workerId, notes);',
  '',
  '        for (BomItem bi : bomDao.findByProduct(conn, productId)) {',
  '            BigDecimal qty  = baseQty(bi, overrides).multiply(multiplier);',
  '            BigDecimal snap = materialDao.getUnitCost(conn, bi.getMaterialId());',
  '            batchItemDao.insert(conn, batchId, bi.getMaterialId(), qty, snap);',
  '            materialDao.deductStock(conn, bi.getMaterialId(), qty);',
  '            inventoryLogDao.insert(conn, bi.getMaterialId(), qty.negate(),',
  '                  "BATCH_CONSUMPTION", "production_batch", batchId, workerId);',
  '            if (materialDao.isBelowThreshold(conn, bi.getMaterialId()))',
  '                alertDao.create(conn, bi.getMaterialId(), "LOW_STOCK");',
  '        }',
  '        conn.commit();                                   // all-or-nothing',
  '        return batchId;',
  '    } catch (Exception e) {',
  '        conn.rollback();                                 // undo partial writes',
  '        throw new ServiceException("Batch logging failed", e);',
  '    } finally {',
  '        conn.setAutoCommit(true); conn.close();',
  '    }',
  '}'
], 'Listing 6. BatchService.recordBatch: the multi-table JDBC transaction.'));
children.push(...figure('batch_form.png', 580, 'Logging a production batch'));
children.push(...figure('batch_history.png', 580, 'Production batch history'));
children.push(...figure('stock_pre-flight.png', 360, 'Stock pre-flight check before a batch'));

children.push(H2('3.6  Sales / Order Book'));
children.push(P('An order and its lines are written in one transaction; each line snapshots the product’s unit cost so profit stays historically accurate.'));
children.push(...code([
  'conn.setAutoCommit(false);',
  'long orderId = orderDao.insert(conn, order);',
  'for (int i = 0; i < productIds.size(); i++) {',
  '    Product p = productDao.findById(conn, productIds.get(i));',
  '    BigDecimal costSnap = p.getCachedUnitCost();        // snapshot HPP',
  '    BigDecimal lineRev  = unitPrices.get(i).multiply(qty);',
  '    BigDecimal lineCost = costSnap.multiply(qty);',
  '    itemDao.insert(conn, item);                          // line + snapshots',
  '    totalRevenue = totalRevenue.add(lineRev);',
  '    totalCost    = totalCost.add(lineCost);',
  '}',
  'orderDao.updateTotals(conn, orderId, totalRevenue, totalCost);',
  'conn.commit();'
], 'Listing 7. SalesService.recordOrder: revenue, cost and profit with cost snapshots.'));
children.push(...figure('sales_list.png', 580, 'Sales list with revenue, cost and profit summary'));
children.push(...figure('sales_new.png', 520, 'New order with live profit preview'));
children.push(...figure('Order_detail.png', 520, 'Order detail with per-line profit'));

children.push(H2('3.7  Multi-Currency Display and Internationalisation'));
children.push(P('All money is stored in a base currency and converted at render time. A MoneyFormatter selects a per-currency formatting strategy by the user’s display currency, exposed to JSP as a custom EL function so any monetary value renders consistently.'));
children.push(...code([
  '// MoneyFormatter.java',
  'public static String format(BigDecimal valueInBase, User user) {',
  '    String code = (user != null) ? user.getDisplayCurrency() : "IDR";',
  '    CurrencyFormatStrategy s = CurrencyStrategyFactory.forCurrency(code);',
  '    return s.format(valueInBase);     // convert via rate_to_base + locale',
  '}',
  '',
  '<!-- cc-functions.tld: expose it to JSP EL -->',
  '<function>',
  '  <name>money</name>',
  '  <function-class>com.costcraft.util.MoneyFormatter</function-class>',
  '  <function-signature>java.lang.String',
  '     format(java.math.BigDecimal, com.costcraft.entity.User)</function-signature>',
  '</function>',
  '',
  '<%-- JSP usage --%>',
  '${cc:money(p.cachedUnitCost, sessionScope.user)}'
], 'Listing 8. Multi-currency rendering via MoneyFormatter and a custom EL function.'));
children.push(...figure('Pref.png', 580, 'User preferences (display currency and language)'));
children.push(...figure('indonesian.png', 580, 'The interface in Indonesian'));
children.push(...figure('mobile.png', 320, 'Mobile-first responsive layout'));

// ============================================================
// 4. SUMMARY
// ============================================================
children.push(H1('4  Summary'));
children.push(P('CostCraft was developed as a web-based bakery production cost and inventory management system that integrates recipe management, inventory tracking, production recording, sales management, and overhead-aware cost calculation. The system replaces spreadsheet-based workflows with a centralized platform capable of automatically propagating cost changes, preserving historical records, and maintaining inventory traceability through audit logs.'));
children.push(P('The implementation demonstrates the practical application of Java EE technologies, including Servlets, JSP, JDBC, and MySQL, within a layered architecture. Role-based access control, transactional operations, and historical cost snapshots ensure data integrity while supporting the operational requirements of small bakery businesses.'));
children.push(P('Functional testing confirmed that all major system modules operate according to the identified requirements. Overall, CostCraft successfully achieves its objective of providing an integrated, lightweight, and practical solution for bakery production cost and inventory management.'));

// ===== REFERENCES =====
children.push(H1('References'));
[
  'Jakarta EE. Jakarta Servlet Specification. Eclipse Foundation.',
  'Apache Tomcat 10 Documentation. The Apache Software Foundation.',
  'Oracle. JavaServer Pages (JSP) and JSTL Documentation.',
  'MySQL 8.0 Reference Manual. Oracle Corporation.',
  'Toast, Inc. Toast POS product documentation.',
  'Keruyun (客如云) F&B management platform.',
].forEach(r => children.push(new Paragraph({ numbering: { reference: 'refs', level: 0 }, spacing: { after: 80 }, children: [new TextRun({ text: r, size: 20 })] })));

// ============================================================
const doc = new Document({
  creator: 'Lin Zhi’an', title: 'CostCraft Thesis',
  styles: {
    default: { document: { run: { font: 'Arial', size: 22 } } },
    paragraphStyles: [
      { id: 'Heading1', name: 'Heading 1', basedOn: 'Normal', next: 'Normal', quickFormat: true, run: { size: 30, bold: true, font: 'Arial', color: '2E2520' }, paragraph: { spacing: { before: 240, after: 160 }, outlineLevel: 0 } },
      { id: 'Heading2', name: 'Heading 2', basedOn: 'Normal', next: 'Normal', quickFormat: true, run: { size: 25, bold: true, font: 'Arial', color: 'A87A45' }, paragraph: { spacing: { before: 200, after: 120 }, outlineLevel: 1 } },
      { id: 'Heading3', name: 'Heading 3', basedOn: 'Normal', next: 'Normal', quickFormat: true, run: { size: 22, bold: true, font: 'Arial', color: '2E2520' }, paragraph: { spacing: { before: 140, after: 80 }, outlineLevel: 2 } },
    ]
  },
  numbering: { config: [
    { reference: 'b', levels: [{ level: 0, format: LevelFormat.BULLET, text: '•', alignment: AlignmentType.LEFT, style: { paragraph: { indent: { left: 560, hanging: 280 } } } }] },
    { reference: 'n', levels: [{ level: 0, format: LevelFormat.DECIMAL, text: '%1.', alignment: AlignmentType.LEFT, style: { paragraph: { indent: { left: 560, hanging: 280 } } } }] },
    { reference: 'refs', levels: [{ level: 0, format: LevelFormat.DECIMAL, text: '[%1]', alignment: AlignmentType.LEFT, style: { paragraph: { indent: { left: 560, hanging: 360 } } } }] },
  ]},
  sections: [{
    properties: { page: { size: { width: 12240, height: 15840 }, margin: { top: 1440, right: 1440, bottom: 1440, left: 1440 } } },
    footers: { default: new Footer({ children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: 'CostCraft · J2EE Thesis   ·   ', size: 16, color: '9C8E84' }), new TextRun({ children: [PageNumber.CURRENT], size: 16, color: '9C8E84' })] })] }) },
    children
  }]
});

Packer.toBuffer(doc).then(buf => {
  const out = path.join(__dirname, '..', process.env.OUT || 'CostCraft_Thesis_raw.docx');
  fs.writeFileSync(out, buf);
  console.log('WROTE ' + out + '  (' + (buf.length / 1024).toFixed(0) + ' KB, ' + figNum + ' figures)');
});
