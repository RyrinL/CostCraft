<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="cc" uri="http://costcraft.com/functions" %>
<fmt:message key="page.products" var="pageTitle"/>
<c:if test="${sessionScope.user.owner}"><fmt:message key="product.new" var="pageAction"/></c:if>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
  <jsp:param name="pageAction" value="${pageAction}"/>
  <jsp:param name="pageActionHref" value="${pageContext.request.contextPath}/products?action=new"/>
  <jsp:param name="success" value="${param.success}"/>
</jsp:include>

<div class="cc-card">
  <table class="cc-table">
    <thead>
      <tr>
        <th><fmt:message key="product.col_product"/></th>
        <th class="text-end"><fmt:message key="product.col_yield"/></th>
        <th class="text-end"><fmt:message key="product.col_unit_cost"/></th>
        <th class="text-end"><fmt:message key="product.col_price_range"/></th>
        <th><fmt:message key="product.col_status"/></th>
        <th></th>
      </tr>
    </thead>
    <tbody>
    <c:forEach var="p" items="${products}">
      <tr>
        <td class="fw-semibold">${p.name}</td>
        <td class="text-end"><fmt:formatNumber value="${p.yieldPerBatch}" pattern="#,##0.##"/> ${p.yieldUnit}</td>
        <td class="text-end money money-primary">${cc:money(p.cachedUnitCost, sessionScope.user)}</td>
        <td class="text-end money text-muted small">
          ${cc:money(p.cachedPriceLower, sessionScope.user)} – ${cc:money(p.cachedPriceUpper, sessionScope.user)}
        </td>
        <td>
          <c:choose>
            <c:when test="${p.active}"><span class="badge rounded-pill badge-ok"><fmt:message key="status.active"/></span></c:when>
            <c:otherwise><span class="badge rounded-pill badge-low"><fmt:message key="status.inactive"/></span></c:otherwise>
          </c:choose>
        </td>
        <td class="text-end">
          <a href="${pageContext.request.contextPath}/unit-cost?productId=${p.id}" class="btn btn-sm btn-cc me-1"><fmt:message key="unitcost.label"/></a>
          <c:if test="${sessionScope.user.owner}">
            <a href="${pageContext.request.contextPath}/bom?productId=${p.id}" class="btn btn-sm btn-cc-outline me-1"><fmt:message key="bom.label"/></a>
            <a href="${pageContext.request.contextPath}/products?action=edit&id=${p.id}" class="btn btn-sm btn-cc-outline"><fmt:message key="btn.edit"/></a>
          </c:if>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty products}">
      <tr><td colspan="6" class="text-muted text-center py-3"><fmt:message key="product.none"/></td></tr>
    </c:if>
    </tbody>
  </table>
</div>

<jsp:include page="../layout/footer.jsp"/>
