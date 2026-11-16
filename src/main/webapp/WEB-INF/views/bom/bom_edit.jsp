<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="cc" uri="http://costcraft.com/functions" %>
<fmt:message key="bom.label" var="bomLabel"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${bomLabel} — ${product.name}"/>
  <jsp:param name="success" value="${param.success}"/>
</jsp:include>

<div class="row g-3">
  <div class="col-md-8">
    <div class="cc-card">
      <h6 class="fw-bold mb-3"><fmt:message key="bom.title"/></h6>
      <table class="cc-table">
        <thead><tr>
          <th><fmt:message key="bom.col_material"/></th>
          <th class="text-end"><fmt:message key="bom.col_qty"/></th>
          <th class="text-end"><fmt:message key="bom.col_unit_cost"/></th>
          <th class="text-end"><fmt:message key="bom.col_line_cost"/></th>
          <th></th>
        </tr></thead>
        <tbody>
        <c:forEach var="bi" items="${bomItems}">
          <tr>
            <td class="fw-medium">${bi.materialName}</td>
            <td class="text-end">
              <form method="post" action="${pageContext.request.contextPath}/bom" class="d-flex justify-content-end gap-1">
                <input type="hidden" name="action" value="update"/>
                <input type="hidden" name="productId" value="${product.id}"/>
                <input type="hidden" name="id" value="${bi.id}"/>
                <input class="cc-form-control text-end" type="number" step="0.01" name="gramQuantity"
                       value="${bi.gramQuantity}" style="width:100px;min-height:36px;font-size:0.875rem;">
                <span class="small text-muted align-self-center">${bi.materialUnit}</span>
                <button type="submit" class="btn btn-sm btn-cc-outline">✓</button>
              </form>
            </td>
            <td class="text-end money text-muted">${cc:money(bi.materialUnitCost, sessionScope.user)}</td>
            <td class="text-end money money-primary">${cc:money(bi.lineCost, sessionScope.user)}</td>
            <td class="text-end">
              <form method="post" action="${pageContext.request.contextPath}/bom">
                <input type="hidden" name="action" value="delete"/>
                <input type="hidden" name="productId" value="${product.id}"/>
                <input type="hidden" name="id" value="${bi.id}"/>
                <button type="submit" class="btn btn-sm" style="color:var(--cc-danger);background:none;border:none;" title="Remove">✕</button>
              </form>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty bomItems}">
          <tr><td colspan="5" class="text-muted text-center py-3"><fmt:message key="bom.none"/></td></tr>
        </c:if>
        </tbody>
      </table>
    </div>
  </div>
  <div class="col-md-4">
    <div class="cc-card">
      <h6 class="fw-bold mb-3"><fmt:message key="bom.add_ingredient"/></h6>
      <form method="post" action="${pageContext.request.contextPath}/bom">
        <input type="hidden" name="action" value="add"/>
        <input type="hidden" name="productId" value="${product.id}"/>
        <div class="mb-3">
          <label class="cc-form-label"><fmt:message key="bom.col_material"/></label>
          <select class="cc-form-control" name="materialId" required>
            <option value=""><fmt:message key="bom.select"/></option>
            <c:forEach var="m" items="${materials}">
              <option value="${m.id}">${m.name}</option>
            </c:forEach>
          </select>
        </div>
        <div class="mb-3">
          <label class="cc-form-label"><fmt:message key="bom.qty_label"/></label>
          <input class="cc-form-control" type="number" step="0.01" name="gramQuantity" min="0.01" required>
        </div>
        <button type="submit" class="btn-cc w-100"><fmt:message key="btn.add"/></button>
      </form>
    </div>

    <div class="cc-card mt-3" style="background:var(--cc-surface-tint);">
      <div class="small fw-semibold mb-1"><fmt:message key="bom.product_info"/></div>
      <div class="small text-muted"><fmt:message key="bom.yield"/> <strong>${product.yieldPerBatch} ${product.yieldUnit}</strong></div>
      <div class="small text-muted"><fmt:message key="bom.days_per_batch"/> <strong>${product.daysPerBatch}</strong></div>
      <div class="mt-2">
        <a href="${pageContext.request.contextPath}/unit-cost?productId=${product.id}" class="btn btn-cc btn-sm w-100"><fmt:message key="bom.view_unit_cost"/></a>
      </div>
    </div>
  </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
