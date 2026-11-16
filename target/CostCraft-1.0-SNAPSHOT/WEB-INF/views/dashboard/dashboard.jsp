<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="cc" uri="http://costcraft.com/functions" %>
<fmt:message key="page.dashboard" var="pageTitle"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
</jsp:include>

<div class="row g-3 mb-4">
  <div class="col-6 col-md-3">
    <div class="cc-metric">
      <i class="bi bi-bag cc-metric-icon"></i>
      <div class="label"><fmt:message key="dashboard.products"/></div>
      <div class="value">${products.size()}</div>
    </div>
  </div>
  <div class="col-6 col-md-3">
    <div class="cc-metric">
      <i class="bi bi-exclamation-triangle cc-metric-icon" style="color:var(--cc-warning);opacity:0.5;"></i>
      <div class="label"><fmt:message key="dashboard.low_stock"/></div>
      <div class="value" style="color:var(--cc-warning);">${lowStockMaterials.size()}</div>
    </div>
  </div>
  <div class="col-6 col-md-3">
    <div class="cc-metric">
      <i class="bi bi-bell cc-metric-icon" style="color:var(--cc-danger);opacity:0.5;"></i>
      <div class="label"><fmt:message key="dashboard.open_alerts"/></div>
      <div class="value" style="color:var(--cc-danger);">${alerts.size()}</div>
    </div>
  </div>
  <div class="col-6 col-md-3">
    <div class="cc-metric">
      <i class="bi bi-clock-history cc-metric-icon"></i>
      <div class="label"><fmt:message key="dashboard.recent_batches"/></div>
      <div class="value">${recentBatches.size()}</div>
    </div>
  </div>
</div>

<div class="row g-3">
  <div class="col-md-6">
    <div class="cc-card">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h6 class="fw-bold mb-0"><fmt:message key="dashboard.products"/></h6>
        <a href="${pageContext.request.contextPath}/products" class="small text-decoration-none" style="color:var(--cc-primary);"><fmt:message key="dashboard.view_all"/></a>
      </div>
      <table class="cc-table">
        <thead><tr>
          <th><fmt:message key="dashboard.col_name"/></th>
          <th class="text-end"><fmt:message key="dashboard.col_unit_cost"/></th>
          <th class="text-end"><fmt:message key="dashboard.col_price_range"/></th>
          <th></th>
        </tr></thead>
        <tbody>
        <%-- find the most expensive unit cost so bars are relative to it --%>
        <c:set var="maxUnitCost" value="0"/>
        <c:forEach var="p" items="${products}" end="2">
          <c:if test="${p.cachedUnitCost > maxUnitCost}"><c:set var="maxUnitCost" value="${p.cachedUnitCost}"/></c:if>
        </c:forEach>
        <c:forEach var="p" items="${products}" end="2">
          <tr>
            <td>
              <a href="${pageContext.request.contextPath}/unit-cost?productId=${p.id}" class="text-decoration-none fw-medium">${p.name}</a>
              <c:set var="costPct" value="${maxUnitCost > 0 ? (p.cachedUnitCost * 100 / maxUnitCost) : 0}"/>
              <div class="cc-bar-track mt-1" style="max-width:140px;cursor:help;"
                   data-bs-toggle="tooltip"
                   title="${cc:money(p.cachedUnitCost, sessionScope.user)} &middot; <fmt:formatNumber value='${costPct}' maxFractionDigits='0'/>%">
                <div class="cc-bar-fill" style="width:${costPct}%;"></div>
              </div>
            </td>
            <td class="text-end money">${cc:money(p.cachedUnitCost, sessionScope.user)}</td>
            <td class="text-end money small text-muted">${cc:money(p.cachedPriceLower, sessionScope.user)} – ${cc:money(p.cachedPriceUpper, sessionScope.user)}</td>
            <td class="cc-cell-actions text-end">
              <a href="${pageContext.request.contextPath}/batches?action=new&productId=${p.id}"
                 class="btn btn-cc-outline btn-sm text-decoration-none" style="font-size:0.7rem;padding:0.2rem 0.5rem;">
                <fmt:message key="dashboard.quick_log"/>
              </a>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty products}">
          <tr><td colspan="4">
            <div class="cc-empty">
              <i class="bi bi-bag"></i>
              <div class="cc-empty-text"><fmt:message key="dashboard.no_products"/></div>
            </div>
          </td></tr>
        </c:if>
        </tbody>
      </table>
      <c:if test="${products.size() > 3}">
        <a href="${pageContext.request.contextPath}/products" class="d-block text-center small mt-2 text-decoration-none" style="color:var(--cc-text-muted);">
          +${products.size() - 3} <fmt:message key="dashboard.more_products"/> &rarr;
        </a>
      </c:if>
    </div>
  </div>
  <div class="col-md-6">
    <c:if test="${not empty alerts}">
    <div class="cc-card mb-3" style="border-color:var(--cc-warning);">
      <h6 class="fw-bold mb-2" style="color:var(--cc-warning);"><fmt:message key="dashboard.alerts_title"/></h6>
      <c:forEach var="a" items="${alerts}">
        <div class="d-flex justify-content-between align-items-center py-1 border-bottom" style="border-color:var(--cc-border)!important;">
          <span class="small">${a.materialName}</span>
          <a href="${pageContext.request.contextPath}/low-stock" class="small" style="color:var(--cc-primary);"><fmt:message key="btn.resolve"/></a>
        </div>
      </c:forEach>
    </div>
    </c:if>
    <div class="cc-card">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h6 class="fw-bold mb-0"><fmt:message key="dashboard.recent_batches"/></h6>
        <a href="${pageContext.request.contextPath}/batches" class="small text-decoration-none" style="color:var(--cc-primary);"><fmt:message key="dashboard.view_all"/></a>
      </div>
      <table class="cc-table">
        <thead><tr>
          <th><fmt:message key="dashboard.col_product"/></th>
          <th><fmt:message key="dashboard.col_units"/></th>
          <th><fmt:message key="dashboard.col_by"/></th>
          <th class="text-end"><fmt:message key="dashboard.col_date"/></th>
        </tr></thead>
        <tbody>
        <c:forEach var="b" items="${recentBatches}">
          <tr>
            <td class="fw-medium">${b.productName}</td>
            <td><fmt:formatNumber value="${b.unitsProduced}" pattern="#,##0.##"/></td>
            <td class="text-muted">${b.workerUsername}</td>
            <td class="text-end small text-muted">
              <fmt:formatDate value="${b.createdAt}" pattern="dd MMM" type="date"/>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty recentBatches}">
          <tr><td colspan="4">
            <div class="cc-empty">
              <i class="bi bi-clock-history"></i>
              <div class="cc-empty-text"><fmt:message key="dashboard.no_batches"/></div>
            </div>
          </td></tr>
        </c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
