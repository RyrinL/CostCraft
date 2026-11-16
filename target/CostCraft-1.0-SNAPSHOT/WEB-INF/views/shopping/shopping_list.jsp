<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="cc" uri="http://costcraft.com/functions" %>
<fmt:message key="page.shopping" var="pageTitle"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
</jsp:include>

<%-- ── Plan production (optional): how many batches of each product ── --%>
<div class="cc-card mb-3 no-print">
  <div class="d-flex justify-content-between align-items-center mb-2">
    <h6 class="fw-bold mb-0"><fmt:message key="shopping.plan_title"/></h6>
    <button type="button" class="btn btn-sm btn-cc-outline" onclick="window.print()">
      <i class="bi bi-printer"></i> <fmt:message key="shopping.print"/>
    </button>
  </div>
  <p class="text-muted small mb-3"><fmt:message key="shopping.plan_hint"/></p>
  <form method="get" action="${pageContext.request.contextPath}/shopping-list">
    <div class="row g-2">
      <c:forEach var="p" items="${products}">
        <div class="col-6 col-md-4 col-lg-3">
          <label class="cc-form-label">${p.name}</label>
          <input class="cc-form-control plan-batches" type="number" min="0" step="0.5"
                 name="batches_${p.id}" value="${planRaw[p.id]}" placeholder="0"
                 data-batchcost="${p.cachedUnitCost * p.yieldPerBatch}">
          <div class="small text-muted plan-sub mt-1">&nbsp;</div>
        </div>
      </c:forEach>
    </div>
    <div class="d-flex justify-content-between align-items-center mt-3 flex-wrap gap-2">
      <button type="submit" class="btn-cc">
        <i class="bi bi-list-check"></i> <fmt:message key="shopping.generate"/>
      </button>
      <div class="small">
        <span class="text-muted"><fmt:message key="shopping.plan_cost"/></span>
        <span id="planTotalValue" class="fw-bold money-primary">—</span>
      </div>
    </div>
  </form>
</div>

<script>
(function () {
  var cfg = { rate: parseFloat('${curRate}') || 1, sym: '${curSymbol}', dec: parseInt('${curDecimals}', 10) || 0 };
  function fmt(base) {
    var v = base * cfg.rate;
    return cfg.sym + v.toLocaleString(undefined, { minimumFractionDigits: cfg.dec, maximumFractionDigits: cfg.dec });
  }
  var inputs = Array.prototype.slice.call(document.querySelectorAll('.plan-batches'));
  var totalEl = document.getElementById('planTotalValue');
  function recompute() {
    var total = 0;
    inputs.forEach(function (inp) {
      var batches  = parseFloat(inp.value) || 0;
      var batchCost = parseFloat(inp.getAttribute('data-batchcost')) || 0;
      var sub = batches * batchCost;
      total += sub;
      var hint = inp.parentNode.querySelector('.plan-sub');
      if (hint) hint.innerHTML = batches > 0 ? ('≈ ' + fmt(sub)) : '&nbsp;';
    });
    if (totalEl) totalEl.textContent = total > 0 ? ('≈ ' + fmt(total)) : '—';
  }
  inputs.forEach(function (inp) { inp.addEventListener('input', recompute); });
  recompute();
}());
</script>

<%-- ── The list ── --%>
<div class="cc-card">
  <h6 class="fw-bold mb-3">
    <i class="bi bi-cart"></i> <fmt:message key="shopping.list_title"/>
  </h6>

  <c:choose>
    <c:when test="${empty items}">
      <div class="cc-empty">
        <i class="bi bi-check2-circle"></i>
        <div class="cc-empty-text"><fmt:message key="shopping.none"/></div>
      </div>
    </c:when>
    <c:otherwise>
      <table class="cc-table">
        <thead><tr>
          <th><fmt:message key="shopping.col_material"/></th>
          <c:if test="${hasPlan}"><th class="text-end"><fmt:message key="shopping.col_need"/></th></c:if>
          <th class="text-end"><fmt:message key="shopping.col_have"/></th>
          <th class="text-end"><fmt:message key="shopping.col_min"/></th>
          <th class="text-end"><fmt:message key="shopping.col_buy"/></th>
          <th class="text-end"><fmt:message key="shopping.col_cost"/></th>
          <th class="text-end no-print"><fmt:message key="shopping.col_got"/></th>
        </tr></thead>
        <tbody>
        <c:set var="total" value="0"/>
        <c:forEach var="item" items="${items}">
          <c:set var="total" value="${total + item.estCost}"/>
          <tr>
            <td class="fw-medium">
              ${item.materialName}
              <c:if test="${item.belowThreshold}">
                <span class="badge rounded-pill badge-low ms-1"><fmt:message key="shopping.below_badge"/></span>
              </c:if>
            </td>
            <c:if test="${hasPlan}">
              <td class="text-end money text-muted">
                <c:if test="${item.needForPlan > 0}"><fmt:formatNumber value="${item.needForPlan}" pattern="#,##0.##"/> ${item.unit}</c:if>
              </td>
            </c:if>
            <td class="text-end money text-muted"><fmt:formatNumber value="${item.have}" pattern="#,##0.##"/> ${item.unit}</td>
            <td class="text-end money text-muted"><fmt:formatNumber value="${item.threshold}" pattern="#,##0.##"/> ${item.unit}</td>
            <td class="text-end money money-primary fw-semibold"><fmt:formatNumber value="${item.toBuy}" pattern="#,##0.##"/> ${item.unit}</td>
            <td class="text-end money">${cc:money(item.estCost, sessionScope.user)}</td>
            <td class="text-end no-print">
              <input type="checkbox" class="cc-check" aria-label="Got it">
            </td>
          </tr>
        </c:forEach>
        </tbody>
        <tfoot>
          <tr>
            <td class="fw-bold"><fmt:message key="shopping.total"/></td>
            <c:if test="${hasPlan}"><td></td></c:if>
            <td></td><td></td><td></td>
            <td class="text-end money money-primary fw-bold">${cc:money(total, sessionScope.user)}</td>
            <td class="no-print"></td>
          </tr>
        </tfoot>
      </table>
    </c:otherwise>
  </c:choose>
</div>

<jsp:include page="../layout/footer.jsp"/>
