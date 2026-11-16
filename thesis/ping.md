# CostCraft Thesis Revision Notes (Based on Updated ERD and Supervisor Comments)

---

# Abstract

Small bakery businesses frequently rely on spreadsheets to calculate production costs and manage inventory. Although spreadsheets provide flexibility, they often require manual updates, lack inventory integration, and cannot automatically propagate ingredient cost changes across products. These limitations may lead to inaccurate pricing decisions, inefficient inventory control, and difficulty maintaining historical production records.

This project develops **CostCraft**, a web-based production cost and inventory management system designed specifically for small bakery operations. The system was developed using Java EE technologies including Servlets, JSP, JSTL, JDBC, and MySQL within a five-layer architecture. CostCraft integrates material management, recipe-based production costing, overhead allocation, production batch recording, sales tracking, audit logging, and multi-currency support into a single platform.

The system applies a four-stage cost calculation methodology consisting of direct material costs, direct other costs, percentage-based overhead costs, and final unit-cost calculation. Inventory consumption is automatically recorded during production activities, while historical cost snapshots preserve the accuracy of past transactions. Role-based access control separates owner and worker responsibilities to improve operational security.

The implementation demonstrates how a lightweight web application can automate bakery production costing and inventory management while maintaining data integrity and traceability. Functional testing shows that the implemented modules satisfy the identified requirements and provide a practical replacement for spreadsheet-based workflows.

**Keywords:** Bakery Management System, Inventory Management, Production Costing, Java EE, JDBC, Cost Calculation, Recipe-Based Costing

---

# 1.1 Project Background and Motivation

## Background

Bakery production is inherently recipe-driven. Every finished product requires a specific combination of raw materials consumed in measured quantities. The production cost of a product depends not only on ingredient prices but also on operational expenses such as packaging, utilities, labor, and other overhead costs. As ingredient prices fluctuate over time, the cost of producing each product also changes, making accurate cost tracking essential for maintaining profitability.

Many small bakery operators still rely on spreadsheets to manage production costs. While spreadsheets are capable of performing basic calculations, they often require manual updates and lack integration with inventory management. Changes to ingredient prices may not automatically propagate across all affected products, resulting in inconsistent costing information and increased administrative effort.

Furthermore, inventory tracking is frequently performed separately from cost calculations, making it difficult to determine material consumption, monitor stock levels, and maintain historical production records. These limitations highlight the need for a dedicated system capable of integrating recipe management, cost calculation, inventory tracking, and production recording within a unified platform.

## Motivation

The motivation for this project originates from the needs of a real small-bakery operator who currently relies on a Google Sheets–based "Kalkulator HPP" (Harga Pokok Produksi) to calculate production costs. Although the spreadsheet successfully models ingredient-based costing, several practical limitations were identified during observation and use.

First, ingredient price changes do not automatically update all related product costs. Second, operational overhead such as electricity, labor, and packaging costs are not systematically incorporated into calculations. Third, inventory levels are not automatically reduced when production occurs. Fourth, historical cost information is not preserved when prices change. Finally, the spreadsheet lacks an audit trail capable of recording stock movements and production activities.

These limitations motivated the development of CostCraft as an integrated solution that combines production costing, inventory management, audit logging, and sales tracking while remaining simple enough for adoption by a single bakery operator.

---

# 2.1 Requirement Analysis

## 2.1.1 Functional Requirements

Functional requirements describe the services and operations that CostCraft must provide to support bakery production activities, inventory management, cost calculation, and sales recording. Based on user requirements and workflow analysis, the system must provide the following functionalities:

* **Authentication and Role Management:** Users must log in before accessing system functions. The owner can create worker accounts and assign roles. Owner-only modules such as user management, overhead configuration, and sales management must be protected from unauthorized access.

* **Material and Inventory Management:** The system must maintain a categorized material catalog, calculate per-unit material costs, track stock levels, record inventory movements, generate low-stock alerts, and assist purchase planning.

* **Product, Recipe, and Cost Management:** Users must be able to create products, define bills of materials (BOM), configure overhead costs, calculate production costs, and analyze pricing recommendations.

* **Production Batch Management:** The system must record production batches, deduct consumed ingredients automatically, preserve cost snapshots, generate inventory logs, and maintain batch history records.

* **Sales Management:** Users must be able to record customer orders, calculate revenue and profit, and maintain sales history.

---

## 2.1.2 Non-Functional Requirements

Non-functional requirements define the quality attributes and operational characteristics of the system. These requirements ensure that CostCraft remains reliable, secure, maintainable, and user-friendly.

* **Correctness:** Monetary and quantity calculations must maintain high precision to prevent rounding errors.

* **Security:** Passwords must be stored securely, database operations must prevent SQL injection attacks, and role-based access control must restrict unauthorized actions.

* **Data Integrity:** Multi-table operations must execute atomically so that partial updates cannot occur when failures happen during transactions.

* **Usability:** The interface must support both desktop and mobile devices and provide multilingual support for English and Indonesian users.

* **Maintainability:** The application architecture must separate presentation, business logic, and data access concerns to simplify future maintenance and enhancement.

---

# 2.2 Analysis of Similar Systems

To identify the unique contribution of CostCraft, three reference systems were analyzed: **Keruyun**, **Toast POS**, and the end-user's existing **Kalkulator HPP spreadsheet**.

Keruyun is a cloud-based food and beverage management platform widely used in China. It provides point-of-sale functionality, member management, supply-chain management, and business reporting. However, product costs are primarily maintained as manually entered values and are not automatically derived from recipe-level ingredient consumption. Additionally, overhead allocation is not a core component of its costing model.

Toast POS is a cloud-based restaurant management platform offering point-of-sale operations, online ordering, inventory management, payroll processing, and business analytics. Toast supports recipe-based inventory depletion, making it closer to the production-oriented approach adopted in CostCraft. However, its extensive configuration requirements and subscription-based pricing model make it less suitable for small bakery operators with relatively simple operational needs.

The most relevant reference is the bakery operator's existing Kalkulator HPP spreadsheet. The spreadsheet already contains categorized ingredients, recipe structures, and basic production-cost calculations. These concepts are directly adopted in CostCraft. However, the spreadsheet lacks overhead allocation, automatic cost propagation, inventory integration, historical cost preservation, and audit logging. Addressing these limitations forms the primary contribution of this project.

---

# 2.3 Use-Case Analysis

CostCraft involves two primary actors: **Owner** and **Worker**.

The Owner possesses full administrative control over the system, including user management, material management, product management, overhead configuration, production monitoring, inventory management, and sales recording. The Worker focuses on operational activities such as inventory recording, material monitoring, and production batch logging.

All use cases require successful authentication before access is granted. Login therefore serves as a prerequisite for every functional operation within the system. This design ensures that all actions can be associated with a specific user, improving accountability and supporting audit-trail functionality.

---

# 2.4 Overall Architecture

CostCraft adopts a layered architecture consisting of five logical layers: Presentation Layer, Controller Layer, Service Layer, Data Access Layer, and Database Layer.

The Presentation Layer provides user interfaces implemented using JSP, JSTL, EL, Bootstrap, and custom CSS. User requests are received by the Controller Layer, which consists of Jakarta Servlets responsible for request processing and navigation control.

Business rules and application logic are implemented within the Service Layer. This layer coordinates operations involving multiple entities and manages transactional workflows. The Data Access Layer handles communication with the database through JDBC and PreparedStatement objects. Finally, persistent data is stored in a MySQL database.

This layered approach improves maintainability by clearly separating user-interface concerns, business logic, and data access responsibilities.

> **Recommendation for Figure 5:** Replace the current request-response architecture diagram with a simple layered architecture diagram:
>
> Browser
> ↓
> Presentation Layer (JSP)
> ↓
> Controller Layer (Servlet)
> ↓
> Service Layer
> ↓
> DAO Layer
> ↓
> MySQL Database

This is more commonly accepted in academic reports.

---

# 2.5 Database Design

The CostCraft database consists of thirteen tables designed to support user management, inventory management, production recording, sales tracking, overhead configuration, and currency management.

The **user** table stores account information and role assignments. Materials are organized using **material_category** and **material** tables. Product definitions are stored in the **product** table, while recipe compositions are maintained through **bom_item** records.

Production activities are represented by **production_batch** and **batch_item** tables. Inventory movements are recorded in **inventory_log**, while stock shortages generate entries in the **alert** table. Sales transactions are maintained through **sales_order** and **sales_order_item** tables.

The **overhead_config** table stores configurable direct and percentage-based overhead allocations, allowing flexible cost calculation. Meanwhile, the **currency_rate** table supports multi-currency display through exchange-rate conversion and currency formatting.

Historical accuracy is preserved through snapshot fields such as **unit_cost_snapshot** and **recorded_unit_cost**, ensuring that previous transactions remain unchanged even when material costs are updated later.

**Figure 6:** Updated Entity-Relationship Diagram (Based on Revised ERD)

---

# 3.1 Database Connectivity

CostCraft uses a centralized database utility component to establish connections with the MySQL database. Connection parameters are stored externally within a configuration file, allowing deployment-specific settings to be modified without changing application source code.

All database operations are performed through the DAO layer, ensuring that database access remains separated from business logic. This approach improves maintainability, readability, and consistency throughout the application.

---

# 3.8 Testing Summary

**Recommendation:** Delete this section entirely.

Add the following sentence to Chapter 4 instead:

> Functional testing was conducted on all major modules, including authentication, inventory management, production logging, cost calculation, and sales recording. All planned test cases passed successfully, demonstrating that the system satisfies the identified functional requirements.

---

# 4 Summary (Revised, ≤ 3 Paragraphs)

CostCraft was developed as a web-based bakery production cost and inventory management system that integrates recipe management, inventory tracking, production recording, sales management, and overhead-aware cost calculation. The system replaces spreadsheet-based workflows with a centralized platform capable of automatically propagating cost changes, preserving historical records, and maintaining inventory traceability through audit logs.

The implementation demonstrates the practical application of Java EE technologies, including Servlets, JSP, JDBC, and MySQL, within a layered architecture. Role-based access control, transactional operations, and historical cost snapshots ensure data integrity while supporting the operational requirements of small bakery businesses.

Functional testing confirmed that all major system modules operate according to the identified requirements. Overall, CostCraft successfully achieves its objective of providing an integrated, lightweight, and practical solution for bakery production cost and inventory management.
