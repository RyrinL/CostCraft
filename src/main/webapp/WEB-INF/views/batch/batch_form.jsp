<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="cc" uri="http://costcraft.com/functions" %>
<fmt:message key="page.batch_log" var="pageTitle"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
</jsp:include>

<div class="row g-3">
  <div class="col-lg-5">
    <div class="cc-card">
      <h6 class="fw-bold mb-3"><fmt:message key="batch.details"/></h6>
      <form method="post" action="${pageContext.request.contextPath}/batches" id="batchForm">
        <div class="mb-3">
          <label class="cc-form-label"><fmt:message key="batch.product"/></label>
          <select class="cc-form-control" name="productId" id="productSelect"
                  onchange="this.form.submit()" required>
            <option value=""><fmt:message key="batch.select_product"/></option>
            <c:forEach var="p" items="${products}">
              <option value="${p.id}" ${product != null && product.id == p.id ? 'selected' : ''}>${p.name}</option>
            </c:forEach>
          </select>
        </div>

        <c:if test="${product != null}">
        <div class="mb-3">
          <label class="cc-form-label"><fmt:message key="batch.multiplier"/></label>
          <input class="cc-form-control" type="number" step="0.5" name="multiplier" value="1" min="0.5" required>
        </div>
        <div class="mb-3">
          <label class="cc-form-label"><fmt:message key="batch.notes"/></label>
          <textarea class="cc-form-control" name="notes" rows="2" style="min-height:auto;height:80px;"></textarea>
        </div>

        <c:if test="${not empty bomItems}">
        <h6 class="fw-semibold mb-2 mt-3"><fmt:message key="batch.override_title"/> <span class="text-muted fw-normal small"><fmt:message key="batch.override_hint"/></span></h6>
        <c:forEach var="bi" items="${bomItems}">
        <div class="mb-2">
          <label class="cc-form-label">${bi.materialName} (default: <fmt:formatNumber value="${bi.gramQuantity}" pattern="#,##0.##"/> ${bi.materialUnit})</label>
          <input class="cc-form-control" type="number" step="0.01" name="override_${bi.id}"
                 placeholder="${bi.gramQuantity}">
        </div>
        </c:forEach>
        </c:if>

        <button type="submit" class="btn-cc w-100 mt-2"><fmt:message key="batch.record_btn"/></button>
        </c:if>
      </form>
    </div>
  </div>

  <c:if test="${breakdown != null}">
  <div class="col-lg-7">
    <div class="cc-card">
      <h6 class="fw-bold mb-3">${breakdown.productName}</h6>
      <div class="row g-2 mb-3">
        <div class="col-6">
          <div class="cc-metric">
            <div class="label"><fmt:message key="batch.unit_cost"/></div>
            <div class="value money">${cc:money(breakdown.unitCost, sessionScope.user)}</div>
          </div>
        </div>
        <div class="col-6">
          <div class="cc-metric">
            <div class="label"><fmt:message key="batch.yield_per_batch"/></div>
            <div class="value">${breakdown.yieldPerBatch} ${product.yieldUnit}</div>
          </div>
        </div>
      </div>
      <table class="cc-table">
        <thead><tr>
          <th><fmt:message key="batch.col_ingredient"/></th>
          <th class="text-end"><fmt:message key="batch.col_default_qty"/></th>
          <th class="text-end"><fmt:message key="batch.col_line_cost"/></th>
        </tr></thead>
        <tbody>
        <c:forEach var="bi" items="${bomItems}">
          <tr>
            <td>${bi.materialName}</td>
            <td class="text-end"><fmt:formatNumber value="${bi.gramQuantity}" pattern="#,##0.##"/> ${bi.materialUnit}</td>
            <td class="text-end money">${cc:money(bi.lineCost, sessionScope.user)}</td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
  </c:if>
</div>

<script>
document.getElementById('productSelect').addEventListener('change', function() {
  window.location = '${pageContext.request.contextPath}/batches?action=new&productId=' + this.value;
});
document.getElementById('batchForm').onsubmit = function() {
  document.getElementById('productSelect').removeAttribute('onchange');
};
</script>

<jsp:include page="../layout/footer.jsp"/>
