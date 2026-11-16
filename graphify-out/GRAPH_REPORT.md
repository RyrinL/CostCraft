# Graph Report - H:/NXU/JEE/CostCraft  (2026-06-17)

## Corpus Check
- Corpus is ~22,627 words - fits in a single context window. You may not need a graph.

## Summary
- 1042 nodes · 2182 edges · 34 communities (32 shown, 2 thin omitted)
- Extraction: 82% EXTRACTED · 18% INFERRED · 0% AMBIGUOUS · INFERRED: 395 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_User Settings & Account|User Settings & Account]]
- [[_COMMUNITY_Product Catalog Servlet|Product Catalog Servlet]]
- [[_COMMUNITY_Overhead Cost Calculation|Overhead Cost Calculation]]
- [[_COMMUNITY_Stock Movement Commands|Stock Movement Commands]]
- [[_COMMUNITY_Overhead Config Servlet|Overhead Config Servlet]]
- [[_COMMUNITY_Production Batch Recording|Production Batch Recording]]
- [[_COMMUNITY_Material Replenishment|Material Replenishment]]
- [[_COMMUNITY_Bill of Materials DAO|Bill of Materials DAO]]
- [[_COMMUNITY_Cost Breakdown DTOs|Cost Breakdown DTOs]]
- [[_COMMUNITY_Currency Format Strategy|Currency Format Strategy]]
- [[_COMMUNITY_Cost Decorator Chain|Cost Decorator Chain]]
- [[_COMMUNITY_Inventory Audit Log|Inventory Audit Log]]
- [[_COMMUNITY_Low-Stock Alert DAO|Low-Stock Alert DAO]]
- [[_COMMUNITY_Batch Line Items DAO|Batch Line Items DAO]]
- [[_COMMUNITY_Material & User Admin|Material & User Admin]]
- [[_COMMUNITY_Inventory Movement Servlets|Inventory Movement Servlets]]
- [[_COMMUNITY_Currency Rate Data|Currency Rate Data]]
- [[_COMMUNITY_Material Price Observer|Material Price Observer]]
- [[_COMMUNITY_DB Schema Tables|DB Schema Tables]]
- [[_COMMUNITY_Request Filter Chain|Request Filter Chain]]
- [[_COMMUNITY_Dashboard & Services|Dashboard & Services]]
- [[_COMMUNITY_Product Service Layer|Product Service Layer]]
- [[_COMMUNITY_BOM & Cost Detail Servlets|BOM & Cost Detail Servlets]]
- [[_COMMUNITY_Low Stock Alert Servlet|Low Stock Alert Servlet]]
- [[_COMMUNITY_User Authentication Service|User Authentication Service]]
- [[_COMMUNITY_Material Category Entity|Material Category Entity]]
- [[_COMMUNITY_Exception Hierarchy|Exception Hierarchy]]
- [[_COMMUNITY_Batch Context DTO|Batch Context DTO]]
- [[_COMMUNITY_Currency Rate Service|Currency Rate Service]]
- [[_COMMUNITY_Login Servlet|Login Servlet]]
- [[_COMMUNITY_Logout Servlet|Logout Servlet]]
- [[_COMMUNITY_Core Design & Docs|Core Design & Docs]]
- [[_COMMUNITY_CI Hook Scripts|CI Hook Scripts]]

## God Nodes (most connected - your core abstractions)
1. `Material` - 31 edges
2. `Product` - 30 edges
3. `User` - 28 edges
4. `InventoryLog` - 26 edges
5. `ProductionBatch` - 26 edges
6. `OverheadConfig` - 25 edges
7. `ProductService` - 25 edges
8. `MaterialService` - 22 edges
9. `BomItem` - 21 edges
10. `Alert` - 20 edges

## Surprising Connections (you probably didn't know these)
- `BatchService` --references--> `Design Patterns (8 required)`  [EXTRACTED]
  src/main/java/com/costcraft/service/BatchService.java → CLAUDE.md
- `InventoryService` --references--> `Design Patterns (8 required)`  [EXTRACTED]
  src/main/java/com/costcraft/service/InventoryService.java → CLAUDE.md
- `MaterialService` --references--> `Design Patterns (8 required)`  [EXTRACTED]
  src/main/java/com/costcraft/service/MaterialService.java → CLAUDE.md
- `Four-Stage Cost Methodology` --references--> `UnitCostService`  [EXTRACTED]
  CLAUDE.md → src/main/java/com/costcraft/service/UnitCostService.java
- `DBUtil` --references--> `Design Patterns (8 required)`  [EXTRACTED]
  src/main/java/com/costcraft/util/DBUtil.java → CLAUDE.md

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Dashboard Aggregation from Four Services** — controller_dashboardservlet_dashboardservlet, service_productservice_productservice, service_materialservice_materialservice, service_alertservice_alertservice, service_batchservice_batchservice [EXTRACTED 1.00]
- **UnitCostBreakdown DTO Shared Across Controllers** — service_productservice_productservice, dto_unitcostbreakdown_unitcostbreakdown, controller_batchservlet_batchservlet, controller_pricesensitivityservlet_pricesensitivityservlet, controller_unitcostdetailservlet_unitcostdetailservlet [EXTRACTED 1.00]
- **Direct DAO Bypass Pattern** — controller_materialservlet_materialservlet, controller_replenishservlet_replenishservlet, dao_materialcategorydao_materialcategorydao, dao_bomitemdao_bomitemdao, dao_materialdao_materialdao, util_dbutil_dbutil [INFERRED 0.95]
- **Unit Cost Calculation Pipeline** — dao_bomitemdao_bomitemdao, dao_overheadconfigdao_overheadconfigdao, dto_batchcontext_batchcontext, dto_costline_costline, dto_unitcostbreakdown_unitcostbreakdown, dao_productdao_productdao [INFERRED 0.95]
- **Inventory Stock Alert Lifecycle** — dao_materialdao_materialdao, dao_inventorylogdao_inventorylogdao, dao_alertdao_alertdao, entity_inventorylog_inventorylog, entity_alert_alert [INFERRED 0.85]
- **Production Batch Recording Flow** — dao_productionbatchdao_productionbatchdao, dao_batchitemdao_batchitemdao, dao_inventorylogdao_inventorylogdao, dao_materialdao_materialdao, dto_batchcontext_batchcontext [INFERRED 0.85]
- **Stock Movement Command Pattern Implementations** — command_adjustmentcommand_adjustmentcommand, command_initialstockcommand_initialstockcommand, command_purchasecommand_purchasecommand, command_wastecommand_wastecommand, command_stockmovementcommand_stockmovementcommand [EXTRACTED 1.00]
- **Servlet Filter Chain for Request Processing** — filter_characterencodingfilter_characterencodingfilter, filter_localefilter_localefilter, filter_loginfilter_loginfilter, filter_rolefilter_rolefilter [INFERRED 0.85]
- **Cost Calculation Decorator Chain** — decorator_unitcostcalculation_unitcostcalculation, decorator_abstractcostdecorator_abstractcostdecorator, decorator_directmaterialscost_directmaterialscost [EXTRACTED 1.00]
- **Overhead Cost Decoration Flow** — decorator_directothercostdecorator_directothercostdecorator, decorator_percentageoverheaddecorator_percentageoverheaddecorator, factory_overheadstrategyfactory_overheadstrategyfactory, strategy_perbatchfixedstrategy_perbatchfixedstrategy, strategy_perunitfixedstrategy_perunitfixedstrategy, strategy_dailyallocatedstrategy_dailyallocatedstrategy, strategy_percentageofdirectstrategy_percentageofdirectstrategy, strategy_overheadallocationstrategy_overheadallocationstrategy [EXTRACTED 1.00]
- **Currency Format Strategy Selection** — factory_currencystrategyfactory_currencystrategyfactory, strategy_currencyformatstrategy_currencyformatstrategy, strategy_chineseyuanstrategy_chineseyuanstrategy, strategy_eurostrategy_eurostrategy, strategy_usdollarstrategy_usdollarstrategy, strategy_indonesianrupiahstrategy_indonesianrupiahstrategy [EXTRACTED 1.00]
- **Material Price Change Observer Chain** — observer_materialpricesubject_materialpricesubject, observer_priceobserver_priceobserver, observer_productcostobserver_productcostobserver [EXTRACTED 1.00]
- **BatchService recordBatch transaction boundary** — service_batchservice_batchservice, schema_table_production_batch, schema_table_batch_item, schema_table_material, schema_table_inventory_log, schema_table_alert [EXTRACTED 1.00]
- **Material price update Observer cascade** — service_materialservice_materialservice, schema_table_material, schema_table_product, service_unitcostservice_unitcostservice [EXTRACTED 1.00]
- **Stock movement audit and alert invariant** — service_inventoryservice_inventoryservice, schema_table_material, schema_table_inventory_log, schema_table_alert [EXTRACTED 1.00]

## Communities (34 total, 2 thin omitted)

### Community 0 - "User Settings & Account"
Cohesion: 0.05
Nodes (26): PreferencesServlet, UserDao, User, LocaleFilter, ServletContextEvent, ServletContextListener, HttpServletRequest, HttpServletResponse (+18 more)

### Community 1 - "Product Catalog Servlet"
Cohesion: 0.07
Nodes (19): ProductServlet, ProductDao, Product, BigDecimal, HttpServletRequest, HttpServletResponse, Override, Product (+11 more)

### Community 2 - "Overhead Cost Calculation"
Cohesion: 0.05
Nodes (35): PercentageOverheadDecorator, OverheadStrategyFactory, OverheadAllocationStrategy, BatchContext, BigDecimal, CostLine, List, OverheadConfig (+27 more)

### Community 3 - "Stock Movement Commands"
Cohesion: 0.05
Nodes (36): AdjustmentCommand, InitialStockCommand, PurchaseCommand, StockMovementCommand, WasteCommand, BigDecimal, Connection, InventoryLogDao (+28 more)

### Community 4 - "Overhead Config Servlet"
Cohesion: 0.08
Nodes (20): OverheadServlet, OverheadConfigDao, OverheadConfig, BigDecimal, HttpServletRequest, HttpServletResponse, OverheadConfig, Override (+12 more)

### Community 5 - "Production Batch Recording"
Cohesion: 0.08
Nodes (18): BatchServlet, ProductionBatchDao, ProductionBatch, HttpServletRequest, HttpServletResponse, Override, String, BigDecimal (+10 more)

### Community 6 - "Material Replenishment"
Cohesion: 0.09
Nodes (18): ReplenishLine, ReplenishServlet, MaterialDao, Material, HttpServletRequest, HttpServletResponse, Override, BigDecimal (+10 more)

### Community 7 - "Bill of Materials DAO"
Cohesion: 0.08
Nodes (16): BomItemDao, DirectMaterialsCost, BomItem, BomItem, Connection, List, Long, ResultSet (+8 more)

### Community 8 - "Cost Breakdown DTOs"
Cohesion: 0.06
Nodes (23): CostLine, UnitCostBreakdown, PriceObserver, ProductCostObserver, PriceObserver, ProductDao, BigDecimal, String (+15 more)

### Community 9 - "Currency Format Strategy"
Cohesion: 0.06
Nodes (27): CurrencyFormatStrategy, CurrencyStrategyFactory, CurrencyFormatStrategy, String, BigDecimal, Override, String, BigDecimal (+19 more)

### Community 10 - "Cost Decorator Chain"
Cohesion: 0.08
Nodes (24): AbstractCostDecorator, DirectOtherCostDecorator, UnitCostCalculation, UnitCostFinalizer, BigDecimal, CostLine, List, Override (+16 more)

### Community 11 - "Inventory Audit Log"
Cohesion: 0.09
Nodes (13): InventoryLogDao, InventoryLog, BigDecimal, Connection, InventoryLog, List, Long, ResultSet (+5 more)

### Community 12 - "Low-Stock Alert DAO"
Cohesion: 0.10
Nodes (11): AlertDao, Alert, Alert, Connection, List, Long, ResultSet, String (+3 more)

### Community 13 - "Batch Line Items DAO"
Cohesion: 0.10
Nodes (11): BatchItemDao, BatchItem, BatchItem, BigDecimal, Connection, List, Long, ResultSet (+3 more)

### Community 14 - "Material & User Admin"
Cohesion: 0.11
Nodes (17): MaterialServlet, UserManageServlet, MaterialCategoryDao, MaterialCategory, BigDecimal, HttpServletRequest, HttpServletResponse, Material (+9 more)

### Community 15 - "Inventory Movement Servlets"
Cohesion: 0.15
Nodes (15): InventoryHistoryServlet, InventoryServlet, InventoryService, HttpServletRequest, HttpServletResponse, Override, HttpServletRequest, HttpServletResponse (+7 more)

### Community 16 - "Currency Rate Data"
Cohesion: 0.13
Nodes (10): CurrencyRateDao, CurrencyRate, Connection, CurrencyRate, List, ResultSet, String, BigDecimal (+2 more)

### Community 17 - "Material Price Observer"
Cohesion: 0.17
Nodes (9): MaterialPriceSubject, MaterialService, Connection, PriceObserver, BigDecimal, List, Long, Material (+1 more)

### Community 18 - "DB Schema Tables"
Cohesion: 0.16
Nodes (15): Map, alert, batch_item, inventory_log, material, material_category, production_batch, user (+7 more)

### Community 19 - "Request Filter Chain"
Cohesion: 0.09
Nodes (16): Filter, CharacterEncodingFilter, LoginFilter, RoleFilter, FilterChain, Override, ServletRequest, ServletResponse (+8 more)

### Community 20 - "Dashboard & Services"
Cohesion: 0.17
Nodes (9): DashboardServlet, AlertService, OverheadService, Long, List, Long, OverheadConfig, Connection (+1 more)

### Community 21 - "Product Service Layer"
Cohesion: 0.23
Nodes (7): ProductService, BomItem, Connection, List, Long, Product, UnitCostBreakdown

### Community 22 - "BOM & Cost Detail Servlets"
Cohesion: 0.13
Nodes (13): BomServlet, PriceSensitivityServlet, UnitCostDetailServlet, HttpServlet, HttpServletRequest, HttpServletResponse, Override, HttpServletRequest (+5 more)

### Community 23 - "Low Stock Alert Servlet"
Cohesion: 0.17
Nodes (9): LowStockServlet, HttpServletRequest, HttpServletResponse, Override, HttpServletRequest, HttpServletResponse, Override, Alert (+1 more)

### Community 24 - "User Authentication Service"
Cohesion: 0.35
Nodes (5): UserService, List, Long, String, User

### Community 25 - "Material Category Entity"
Cohesion: 0.22
Nodes (3): MaterialCategory, Long, String

### Community 26 - "Exception Hierarchy"
Cohesion: 0.22
Nodes (6): ServiceException, ValidationException, RuntimeException, String, String, Throwable

### Community 28 - "Currency Rate Service"
Cohesion: 0.36
Nodes (5): currency_rate, CurrencyService, CurrencyRate, List, String

### Community 29 - "Login Servlet"
Cohesion: 0.43
Nodes (4): LoginServlet, HttpServletRequest, HttpServletResponse, Override

### Community 30 - "Logout Servlet"
Cohesion: 0.48
Nodes (4): LogoutServlet, HttpServletRequest, HttpServletResponse, Override

### Community 31 - "Core Design & Docs"
Cohesion: 0.38
Nodes (7): CostCraft Project Spec, Design Patterns (8 required), Four-Stage Cost Methodology, bom_item, overhead_config, product, UnitCostService

## Knowledge Gaps
- **80 isolated node(s):** `recordToolUse.sh script`, `String`, `HttpServletRequest`, `HttpServletResponse`, `Override` (+75 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **2 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Material` connect `Material Replenishment` to `Stock Movement Commands`, `Bill of Materials DAO`, `Material & User Admin`, `Material Price Observer`, `Material Category Entity`?**
  _High betweenness centrality (0.152) - this node is a cross-community bridge._
- **Why does `ReplenishServlet` connect `Material Replenishment` to `Bill of Materials DAO`, `Material & User Admin`, `Dashboard & Services`, `Product Service Layer`, `BOM & Cost Detail Servlets`?**
  _High betweenness centrality (0.142) - this node is a cross-community bridge._
- **Why does `BomItemDao` connect `Bill of Materials DAO` to `Production Batch Recording`, `Material Replenishment`?**
  _High betweenness centrality (0.124) - this node is a cross-community bridge._
- **Are the 6 inferred relationships involving `Material` (e.g. with `AdjustmentCommand` and `InitialStockCommand`) actually correct?**
  _`Material` has 6 INFERRED edges - model-reasoned connections that need verification._
- **Are the 2 inferred relationships involving `Product` (e.g. with `OverheadConfig` and `ProductionBatch`) actually correct?**
  _`Product` has 2 INFERRED edges - model-reasoned connections that need verification._
- **Are the 2 inferred relationships involving `ProductionBatch` (e.g. with `Product` and `User`) actually correct?**
  _`ProductionBatch` has 2 INFERRED edges - model-reasoned connections that need verification._
- **What connects `recordToolUse.sh script`, `String`, `HttpServletRequest` to the rest of the system?**
  _80 weakly-connected nodes found - possible documentation gaps or missing edges._