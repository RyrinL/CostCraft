<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="cc" uri="http://costcraft.com/functions" %>
<fmt:message key="page.price_sensitivity" var="pageTitle"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
</jsp:include>

<div class="cc-card mb-3" style="max-width:400px;">
  <form method="get" action="${pageContext.request.contextPath}/price-sensitivity" class="d-flex gap-2 align-items-end">
    <div class="flex-fill">
      <label class="cc-form-label"><fmt:message key="sensitivity.select_product"/></label>
      <select class="cc-form-control" name="productId">
        <option value=""><fmt:message key="sensitivity.choose"/></option>
        <c:forEach var="p" items="${products}">
          <option value="${p.id}" ${selectedProductId == p.id ? 'selected' : ''}>${p.name}</option>
        </c:forEach>
      </select>
    </div>
    <button type="submit" class="btn-cc"><fmt:message key="btn.view"/></button>
  </form>
</div>

<c:if test="${breakdown != null}">
<div class="row g-3">
  <div class="col-md-4">
    <div class="cc-metric">
      <div class="label"><fmt:message key="sensitivity.unit_cost"/></div>
      <div class="value money">${cc:money(breakdown.unitCost, sessionScope.user)}</div>
    </div>
  </div>
  <div class="col-md-4">
    <div class="cc-metric">
      <div class="label"><fmt:message key="sensitivity.price_30"/></div>
      <div class="value money money-primary">${cc:money(breakdown.priceLower, sessionScope.user)}</div>
    </div>
  </div>
  <div class="col-md-4">
    <div class="cc-metric">
      <div class="label"><fmt:message key="sensitivity.price_50"/></div>
      <div class="value money money-primary">${cc:money(breakdown.priceUpper, sessionScope.user)}</div>
    </div>
  </div>
</div>

<div class="cc-card mt-3" style="max-width:480px;">
  <h6 class="fw-bold mb-3"><fmt:message key="sensitivity.table_title"/></h6>
  <table class="cc-table">
    <thead><tr>
      <th><fmt:message key="sensitivity.col_margin"/></th>
      <th class="text-end"><fmt:message key="sensitivity.col_price"/></th>
    </tr></thead>
    <tbody>
    <c:forEach var="entry" items="${sensitivity}">
      <tr>
        <td>${entry.key}</td>
        <td class="text-end money money-primary">${cc:money(entry.value, sessionScope.user)}</td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
</div>
</c:if>

<jsp:include page="../layout/footer.jsp"/>
