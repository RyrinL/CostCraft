<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="path" value="${pageContext.request.servletPath}"/>
<nav class="cc-sidebar">
  <div class="cc-sidebar-brand"><i class="bi bi-cake2"></i> CostCraft</div>

  <div class="cc-sidebar-nav">
    <div class="nav-section"><fmt:message key="nav.overview"/></div>
    <a href="${ctx}/dashboard" class="nav-link ${path == '/dashboard' ? 'active' : ''}"><i class="bi bi-shop"></i> <fmt:message key="nav.dashboard"/></a>

    <div class="nav-section"><fmt:message key="nav.materials_section"/></div>
    <a href="${ctx}/materials" class="nav-link ${path == '/materials' ? 'active' : ''}"><i class="bi bi-basket3"></i> <fmt:message key="nav.materials"/></a>
    <a href="${ctx}/inventory" class="nav-link ${path == '/inventory' ? 'active' : ''}"><i class="bi bi-boxes"></i> <fmt:message key="nav.inventory"/></a>
    <a href="${ctx}/inventory/history" class="nav-link ${path == '/inventory/history' ? 'active' : ''}"><i class="bi bi-clock-history"></i> <fmt:message key="nav.stock_history"/></a>
    <a href="${ctx}/low-stock" class="nav-link ${path == '/low-stock' ? 'active' : ''}"><i class="bi bi-exclamation-triangle"></i> <fmt:message key="nav.low_stock"/></a>
    <a href="${ctx}/shopping-list" class="nav-link ${path == '/shopping-list' ? 'active' : ''}"><i class="bi bi-cart"></i> <fmt:message key="nav.shopping"/></a>

    <div class="nav-section"><fmt:message key="nav.products_section"/></div>
    <a href="${ctx}/products" class="nav-link ${path == '/products' ? 'active' : ''}"><i class="bi bi-bag"></i> <fmt:message key="nav.products"/></a>
    <a href="${ctx}/price-sensitivity" class="nav-link ${path == '/price-sensitivity' ? 'active' : ''}"><i class="bi bi-graph-up-arrow"></i> <fmt:message key="nav.price_sensitivity"/></a>

    <div class="nav-section"><fmt:message key="nav.production"/></div>
    <a href="${ctx}/batches?action=new" class="nav-link"><i class="bi bi-plus-circle"></i> <fmt:message key="nav.log_batch"/></a>
    <a href="${ctx}/batches" class="nav-link ${path == '/batches' ? 'active' : ''}"><i class="bi bi-archive"></i> <fmt:message key="nav.batch_history"/></a>
    <a href="${ctx}/replenish" class="nav-link ${path == '/replenish' ? 'active' : ''}"><i class="bi bi-search"></i> <fmt:message key="nav.replenish"/></a>

    <c:if test="${sessionScope.user.owner}">
      <div class="nav-section"><fmt:message key="nav.sales_section"/></div>
      <a href="${ctx}/sales" class="nav-link ${path == '/sales' ? 'active' : ''}"><i class="bi bi-cash-stack"></i> <fmt:message key="nav.sales"/></a>

      <div class="nav-section"><fmt:message key="nav.settings"/></div>
      <a href="${ctx}/overhead" class="nav-link ${path == '/overhead' ? 'active' : ''}"><i class="bi bi-sliders"></i> <fmt:message key="nav.overhead"/></a>
      <a href="${ctx}/users" class="nav-link ${path == '/users' ? 'active' : ''}"><i class="bi bi-people"></i> <fmt:message key="nav.users"/></a>
    </c:if>
  </div>

  <div class="cc-sidebar-footer">
    <div class="small fw-semibold text-truncate">${sessionScope.user.username}</div>
    <div class="small text-muted mb-2">${sessionScope.user.role}</div>
    <div class="d-flex gap-2">
      <a href="${ctx}/preferences" class="btn btn-cc-outline btn-sm flex-fill text-center"><i class="bi bi-gear"></i> <fmt:message key="nav.prefs"/></a>
      <a href="${ctx}/logout" class="btn btn-cc-outline btn-sm flex-fill text-center"><i class="bi bi-box-arrow-right"></i> <fmt:message key="nav.logout"/></a>
    </div>
  </div>
</nav>
