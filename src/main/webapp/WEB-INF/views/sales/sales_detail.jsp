<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="cc" uri="http://costcraft.com/functions" %>
<fmt:message key="page.sales_detail" var="pageTitle"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
  <jsp:param name="success" value="${param.success}"/>
</jsp:include>

<c:if test="${order == null}">
  <div class="cc-alert-danger"><fmt:message key="sales.not_found"/></div>
</c:if>

<c:if test="${order != null}">
<div class="cc-card mb-3" style="max-width:760px;">
  <div class="d-flex justify-content-between align-items-start flex-wrap gap-2 mb-3">
    <div>
      <div class="fw-bold" style="font-size:1.1rem;">${order.customerName}</div>
      <div class="text-muted small">${order.orderDate} · <fmt:message key="sales.by"/> ${order.username}</div>
      <c:if test="${not empty order.note}"><div class="small mt-1">${order.note}</div></c:if>
    </div>
    <a href="${pageContext.request.contextPath}/sales" class="btn btn-cc-outline btn-sm">&larr; <fmt:message key="sales.back"/></a>
  </div>

  <table class="cc-table">
    <thead><tr>
      <th><fmt:message key="sales.col_product"/></th>
      <th class="text-end"><fmt:message key="sales.col_qty"/></th>
      <th class="text-end"><fmt:message key="sales.col_price"/></th>
      <th class="text-end"><fmt:message key="sales.col_revenue"/></th>
      <th class="text-end"><fmt:message key="sales.col_profit"/></th>
    </tr></thead>
    <tbody>
    <c:forEach var="it" items="${items}">
      <tr>
        <td class="fw-medium">${it.productName}</td>
        <td class="text-end money"><fmt:formatNumber value="${it.quantity}" pattern="#,##0.##"/></td>
        <td class="text-end money">${cc:money(it.unitPrice, sessionScope.user)}</td>
        <td class="text-end money">${cc:money(it.lineRevenue, sessionScope.user)}</td>
        <td class="text-end money" style="color:${it.lineProfit.signum() < 0 ? 'var(--cc-danger)' : 'var(--cc-success)'};">${cc:money(it.lineProfit, sessionScope.user)}</td>
      </tr>
    </c:forEach>
    </tbody>
    <tfoot>
      <tr>
        <td class="fw-bold"><fmt:message key="sales.total"/></td>
        <td></td><td></td>
        <td class="text-end money fw-bold">${cc:money(order.recordedRevenue, sessionScope.user)}</td>
        <td class="text-end money fw-bold" style="color:${order.profit.signum() < 0 ? 'var(--cc-danger)' : 'var(--cc-success)'};">${cc:money(order.profit, sessionScope.user)}</td>
      </tr>
    </tfoot>
  </table>

  <div class="cc-danger-zone">
    <div class="label"><fmt:message key="btn.danger_zone"/></div>
    <form method="post" action="${pageContext.request.contextPath}/sales">
      <input type="hidden" name="action" value="delete"/>
      <input type="hidden" name="id" value="${order.id}"/>
      <fmt:message key="sales.delete_confirm" var="delMsg"/>
      <button type="submit" class="btn btn-outline-danger btn-sm" onclick="return confirm('${delMsg}')">
        <fmt:message key="sales.delete"/>
      </button>
    </form>
  </div>
</div>
</c:if>

<jsp:include page="../layout/footer.jsp"/>
