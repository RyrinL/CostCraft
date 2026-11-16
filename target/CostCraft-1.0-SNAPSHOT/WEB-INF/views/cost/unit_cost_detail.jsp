<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="cc" uri="http://costcraft.com/functions" %>
<fmt:message key="unitcost.label" var="unitcostLabel"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${unitcostLabel} — ${breakdown.productName}"/>
</jsp:include>

<div class="row g-3 mb-4">
  <div class="col-6 col-md-3">
    <div class="cc-metric">
      <div class="label"><fmt:message key="unitcost.direct_materials"/></div>
      <div class="value money">${cc:money(breakdown.stage1Materials, sessionScope.user)}</div>
      <div class="sub"><fmt:message key="unitcost.stage1"/></div>
    </div>
  </div>
  <div class="col-6 col-md-3">
    <div class="cc-metric">
      <div class="label"><fmt:message key="unitcost.other_direct"/></div>
      <div class="value money">${cc:money(breakdown.stage2OtherDirect, sessionScope.user)}</div>
      <div class="sub"><fmt:message key="unitcost.stage2"/></div>
    </div>
  </div>
  <div class="col-6 col-md-3">
    <div class="cc-metric">
      <div class="label"><fmt:message key="unitcost.overhead"/></div>
      <div class="value money">${cc:money(breakdown.stage3Overhead, sessionScope.user)}</div>
      <div class="sub"><fmt:message key="unitcost.stage3"/></div>
    </div>
  </div>
  <div class="col-6 col-md-3">
    <div class="cc-metric" style="border-color:var(--cc-primary);">
      <div class="label"><fmt:message key="unitcost.hpp"/></div>
      <div class="value money money-primary">${cc:money(breakdown.unitCost, sessionScope.user)}</div>
      <div class="sub">÷ ${breakdown.yieldPerBatch} ${product.yieldUnit}/batch</div>
    </div>
  </div>
</div>

<div class="row g-3">
  <div class="col-md-6">
    <div class="cc-card">
      <h6 class="fw-bold mb-3"><fmt:message key="unitcost.breakdown_title"/></h6>
      <table class="cc-table">
        <thead><tr>
          <th><fmt:message key="unitcost.col_item"/></th>
          <th><fmt:message key="unitcost.col_stage"/></th>
          <th class="text-end"><fmt:message key="unitcost.col_amount"/></th>
        </tr></thead>
        <tbody>
        <c:forEach var="line" items="${breakdown.breakdown}">
          <tr>
            <td class="fw-medium">${line.label}</td>
            <td><span class="badge rounded-pill badge-worker" style="font-size:0.65rem;">${line.stage}</span></td>
            <td class="text-end money">${cc:money(line.amount, sessionScope.user)}</td>
          </tr>
        </c:forEach>
        <tr style="border-top:2px solid var(--cc-border-strong);">
          <td colspan="2" class="fw-bold"><fmt:message key="unitcost.total_direct"/></td>
          <td class="text-end money fw-bold">${cc:money(breakdown.totalDirect, sessionScope.user)}</td>
        </tr>
        <tr>
          <td colspan="2" class="fw-bold"><fmt:message key="unitcost.total_overhead"/></td>
          <td class="text-end money fw-bold">${cc:money(breakdown.stage3Overhead, sessionScope.user)}</td>
        </tr>
        <tr style="background:var(--cc-surface-tint);">
          <td colspan="2" class="fw-bold" style="color:var(--cc-primary);"><fmt:message key="unitcost.hpp"/></td>
          <td class="text-end money fw-bold money-primary">${cc:money(breakdown.unitCost, sessionScope.user)}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
  <div class="col-md-6">
    <div class="cc-card">
      <h6 class="fw-bold mb-3"><fmt:message key="unitcost.selling_price_title"/></h6>
      <table class="cc-table">
        <thead><tr>
          <th><fmt:message key="unitcost.col_margin"/></th>
          <th class="text-end"><fmt:message key="unitcost.col_price"/></th>
        </tr></thead>
        <tbody>
        <tr>
          <td><fmt:message key="unitcost.lower"/> (<fmt:formatNumber value="${product.marginLowerPct * 100}" pattern="#,##0.#"/>%)</td>
          <td class="text-end money money-primary">${cc:money(breakdown.priceLower, sessionScope.user)}</td>
        </tr>
        <tr>
          <td><fmt:message key="unitcost.upper"/> (<fmt:formatNumber value="${product.marginUpperPct * 100}" pattern="#,##0.#"/>%)</td>
          <td class="text-end money money-primary">${cc:money(breakdown.priceUpper, sessionScope.user)}</td>
        </tr>
        </tbody>
      </table>
    </div>

    <div class="cc-card mt-3">
      <h6 class="fw-bold mb-2"><fmt:message key="unitcost.actions"/></h6>
      <div class="d-flex flex-column gap-2">
        <a href="${pageContext.request.contextPath}/batches?action=new&productId=${breakdown.productId}" class="btn-cc text-center text-decoration-none"><fmt:message key="unitcost.log_batch"/></a>
        <a href="${pageContext.request.contextPath}/bom?productId=${breakdown.productId}" class="btn btn-cc-outline text-center text-decoration-none"><fmt:message key="unitcost.edit_bom"/></a>
        <a href="${pageContext.request.contextPath}/price-sensitivity?productId=${breakdown.productId}" class="btn btn-cc-outline text-center text-decoration-none"><fmt:message key="unitcost.price_sensitivity"/></a>
        <a href="${pageContext.request.contextPath}/replenish?productId=${breakdown.productId}" class="btn btn-cc-outline text-center text-decoration-none"><fmt:message key="unitcost.stock_check"/></a>
        <button onclick="window.print()" class="btn btn-cc-outline text-start no-print"><fmt:message key="unitcost.print"/></button>
      </div>
    </div>
  </div>
</div>

<%-- Cost trend chart (hidden when no history) --%>
<c:if test="${not empty trendBatches}">
<div class="cc-card mt-3 no-print">
  <h6 class="fw-bold mb-3"><fmt:message key="unitcost.trend_title"/></h6>
  <canvas id="trendChart" height="120"></canvas>
</div>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<script>
(function() {
  var labels = [<c:forEach var="b" items="${trendBatches}" varStatus="s">"<fmt:formatDate value="${b.createdAt}" pattern="dd MMM"/>"<c:if test="${!s.last}">,</c:if></c:forEach>];
  var data   = [<c:forEach var="b" items="${trendBatches}" varStatus="s">${b.recordedUnitCost}<c:if test="${!s.last}">,</c:if></c:forEach>];
  new Chart(document.getElementById('trendChart'), {
    type: 'line',
    data: {
      labels: labels,
      datasets: [{
        label: 'Unit Cost (IDR)',
        data: data,
        borderColor: '#C8965A',
        backgroundColor: 'rgba(200,150,90,0.1)',
        pointBackgroundColor: '#C8965A',
        tension: 0.3,
        fill: true
      }]
    },
    options: {
      plugins: { legend: { display: false } },
      scales: {
        y: { ticks: { callback: function(v) { return 'Rp ' + v.toLocaleString('id-ID'); } } }
      }
    }
  });
}());
</script>
</c:if>

<jsp:include page="../layout/footer.jsp"/>
