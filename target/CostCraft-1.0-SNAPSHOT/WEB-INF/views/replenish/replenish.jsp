<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:message key="replenish.title" var="pageTitle"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
</jsp:include>

<div class="cc-card mb-3" style="max-width:540px;">
  <form class="row g-2 align-items-end" method="get" action="${pageContext.request.contextPath}/replenish">
    <div class="col-12 col-md-6">
      <label class="cc-form-label"><fmt:message key="replenish.select_product"/></label>
      <select class="cc-form-control" name="productId" required>
        <option value=""><fmt:message key="replenish.choose"/></option>
        <c:forEach var="p" items="${products}">
          <option value="${p.id}" ${productId == p.id ? 'selected' : ''}>${p.name}</option>
        </c:forEach>
      </select>
    </div>
    <div class="col-6 col-md-3">
      <label class="cc-form-label"><fmt:message key="replenish.multiplier"/></label>
      <input class="cc-form-control" type="number" step="0.5" min="0.5" name="multiplier"
             value="${not empty multiplier ? multiplier : '1'}">
    </div>
    <div class="col-6 col-md-3">
      <button type="submit" class="btn-cc w-100"><fmt:message key="replenish.check"/></button>
    </div>
  </form>
</div>

<c:if test="${not empty lines}">
  <div class="cc-card" style="max-width:640px;">
    <h6 class="fw-bold mb-3">
      ${product.name} — ×<fmt:formatNumber value="${multiplier}" pattern="#,##0.##"/> batch
    </h6>

    <%-- Summary badge --%>
    <c:set var="allOk" value="true"/>
    <c:forEach var="l" items="${lines}">
      <c:if test="${!l.sufficient}"><c:set var="allOk" value="false"/></c:if>
    </c:forEach>
    <c:choose>
      <c:when test="${allOk}">
        <div class="cc-alert-success mb-3"><fmt:message key="replenish.all_ok"/></div>
      </c:when>
      <c:otherwise>
        <div class="cc-alert-danger mb-3"><fmt:message key="replenish.has_shortfall"/></div>
      </c:otherwise>
    </c:choose>

    <table class="cc-table">
      <thead><tr>
        <th><fmt:message key="replenish.col_material"/></th>
        <th class="text-end"><fmt:message key="replenish.col_required"/></th>
        <th class="text-end"><fmt:message key="replenish.col_available"/></th>
        <th><fmt:message key="replenish.col_status"/></th>
      </tr></thead>
      <tbody>
      <c:forEach var="l" items="${lines}">
        <tr>
          <td class="fw-medium">${l.materialName}</td>
          <td class="text-end money"><fmt:formatNumber value="${l.required}" pattern="#,##0.##"/> ${l.unit}</td>
          <td class="text-end money"><fmt:formatNumber value="${l.available}" pattern="#,##0.##"/> ${l.unit}</td>
          <td>
            <c:choose>
              <c:when test="${l.sufficient}">
                <span class="badge rounded-pill" style="background:var(--cc-success);color:#fff;"><fmt:message key="replenish.ok"/></span>
              </c:when>
              <c:otherwise>
                <span class="badge rounded-pill" style="background:var(--cc-danger);color:#fff;"><fmt:message key="replenish.short"/></span>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>

    <div class="mt-3">
      <a href="${pageContext.request.contextPath}/batches?action=new&productId=${productId}"
         class="btn-cc text-decoration-none"><fmt:message key="replenish.log_batch"/></a>
    </div>
  </div>
</c:if>

<jsp:include page="../layout/footer.jsp"/>
