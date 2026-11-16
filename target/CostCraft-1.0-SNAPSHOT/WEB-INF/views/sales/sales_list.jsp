<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="cc" uri="http://costcraft.com/functions" %>
<fmt:message key="page.sales" var="pageTitle"/>
<fmt:message key="sales.new" var="pageAction"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
  <jsp:param name="pageAction" value="${pageAction}"/>
  <jsp:param name="pageActionHref" value="${pageContext.request.contextPath}/sales?action=new"/>
  <jsp:param name="success" value="${param.success}"/>
</jsp:include>

<%-- Summary metrics --%>
<div class="row g-3 mb-3">
  <div class="col-6 col-md-3">
    <div class="cc-metric"><i class="bi bi-receipt cc-metric-icon"></i>
      <div class="label"><fmt:message key="sales.orders"/></div>
      <div class="value">${summary.orderCount}</div>
    </div>
  </div>
  <div class="col-6 col-md-3">
    <div class="cc-metric"><i class="bi bi-cash-stack cc-metric-icon"></i>
      <div class="label"><fmt:message key="sales.revenue"/></div>
      <div class="value">${cc:money(summary.revenue, sessionScope.user)}</div>
    </div>
  </div>
  <div class="col-6 col-md-3">
    <div class="cc-metric"><i class="bi bi-bag cc-metric-icon"></i>
      <div class="label"><fmt:message key="sales.cost"/></div>
      <div class="value text-muted">${cc:money(summary.cost, sessionScope.user)}</div>
    </div>
  </div>
  <div class="col-6 col-md-3">
    <div class="cc-metric"><i class="bi bi-graph-up-arrow cc-metric-icon"></i>
      <div class="label"><fmt:message key="sales.profit"/></div>
      <div class="value" style="color:${summary.profit.signum() < 0 ? 'var(--cc-danger)' : 'var(--cc-success)'};">
        ${cc:money(summary.profit, sessionScope.user)}
      </div>
    </div>
  </div>
</div>

<%-- Date filter --%>
<div class="cc-card mb-3">
  <form method="get" action="${pageContext.request.contextPath}/sales" class="d-flex gap-2 flex-wrap align-items-end">
    <div><label class="cc-form-label"><fmt:message key="sales.from"/></label>
      <input class="cc-form-control" type="date" name="from" value="${from}"></div>
    <div><label class="cc-form-label"><fmt:message key="sales.to"/></label>
      <input class="cc-form-control" type="date" name="to" value="${to}"></div>
    <button type="submit" class="btn-cc"><fmt:message key="btn.filter"/></button>
    <c:if test="${not empty from or not empty to}">
      <a href="${pageContext.request.contextPath}/sales" class="btn btn-outline-secondary btn-sm align-self-end"><fmt:message key="btn.clear"/></a>
    </c:if>
  </form>
</div>

<div class="row g-3">
  <%-- Orders --%>
  <div class="col-lg-7">
    <div class="cc-card">
      <h6 class="fw-bold mb-3"><fmt:message key="sales.orders"/></h6>
      <table class="cc-table">
        <thead><tr>
          <th><fmt:message key="sales.col_date"/></th>
          <th><fmt:message key="sales.col_customer"/></th>
          <th class="text-end"><fmt:message key="sales.col_revenue"/></th>
          <th class="text-end"><fmt:message key="sales.col_profit"/></th>
          <th></th>
        </tr></thead>
        <tbody>
        <c:forEach var="o" items="${orders}">
          <tr>
            <td class="text-muted small">${o.orderDate}</td>
            <td class="fw-medium">${o.customerName}
              <span class="text-muted small">· ${o.itemCount}</span>
            </td>
            <td class="text-end money">${cc:money(o.recordedRevenue, sessionScope.user)}</td>
            <td class="text-end money" style="color:${o.profit.signum() < 0 ? 'var(--cc-danger)' : 'var(--cc-success)'};">${cc:money(o.profit, sessionScope.user)}</td>
            <td class="cc-cell-actions text-end">
              <a href="${pageContext.request.contextPath}/sales?action=view&id=${o.id}" class="btn btn-sm btn-cc-outline"><fmt:message key="sales.view"/></a>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty orders}">
          <tr><td colspan="5">
            <div class="cc-empty"><i class="bi bi-receipt"></i>
              <div class="cc-empty-text"><fmt:message key="sales.none"/></div>
            </div>
          </td></tr>
        </c:if>
        </tbody>
      </table>
    </div>
  </div>

  <%-- Profit by product --%>
  <div class="col-lg-5">
    <div class="cc-card">
      <h6 class="fw-bold mb-3"><fmt:message key="sales.by_product"/></h6>
      <table class="cc-table">
        <thead><tr>
          <th><fmt:message key="sales.col_product"/></th>
          <th class="text-end"><fmt:message key="sales.col_qty"/></th>
          <th class="text-end"><fmt:message key="sales.col_profit"/></th>
        </tr></thead>
        <tbody>
        <c:forEach var="r" items="${byProduct}">
          <tr>
            <td class="fw-medium">${r.productName}</td>
            <td class="text-end money"><fmt:formatNumber value="${r.quantity}" pattern="#,##0.##"/></td>
            <td class="text-end money" style="color:${r.lineProfit.signum() < 0 ? 'var(--cc-danger)' : 'var(--cc-success)'};">${cc:money(r.lineProfit, sessionScope.user)}</td>
          </tr>
        </c:forEach>
        <c:if test="${empty byProduct}">
          <tr><td colspan="3" class="text-muted text-center py-3"><fmt:message key="sales.none"/></td></tr>
        </c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
