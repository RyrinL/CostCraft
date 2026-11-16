<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:message key="page.sales_new" var="pageTitle"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
</jsp:include>

<div class="cc-card" style="max-width:780px;">
  <form method="post" action="${pageContext.request.contextPath}/sales">
    <input type="hidden" name="action" value="create"/>

    <div class="row g-3 mb-3">
      <div class="col-12 col-md-6">
        <label class="cc-form-label"><fmt:message key="sales.customer"/></label>
        <input class="cc-form-control" type="text" name="customerName" value="${param.customerName}" required autofocus>
      </div>
      <div class="col-6 col-md-3">
        <label class="cc-form-label"><fmt:message key="sales.date"/></label>
        <input class="cc-form-control" type="date" name="orderDate" value="${not empty param.orderDate ? param.orderDate : today}" required>
      </div>
      <div class="col-12">
        <label class="cc-form-label"><fmt:message key="sales.note"/></label>
        <input class="cc-form-control" type="text" name="note" value="${param.note}">
      </div>
    </div>

    <label class="cc-form-label"><fmt:message key="sales.items"/></label>
    <fmt:message key="sales.col_qty" var="qtyLabel"/>
    <fmt:message key="sales.col_price" var="priceLabel"/>

    <%-- Column headers (hidden on phones — inputs carry placeholders there instead) --%>
    <div class="row g-2 mb-1 d-none d-md-flex">
      <div class="col-md-5"><div class="cc-form-label mb-0"><fmt:message key="sales.col_product"/></div></div>
      <div class="col-md-2"><div class="cc-form-label mb-0">${qtyLabel}</div></div>
      <div class="col-md-3"><div class="cc-form-label mb-0">${priceLabel}</div></div>
      <div class="col-md-2"></div>
    </div>

    <div id="lines">
      <div class="sales-line row g-2 align-items-end mb-2">
        <div class="col-12 col-md-5">
          <select class="cc-form-control line-product" name="productId">
            <option value=""><fmt:message key="sales.select_product"/></option>
            <c:forEach var="p" items="${products}">
              <option value="${p.id}" data-cost="${p.cachedUnitCost}" data-price="${p.cachedPriceLower}">${p.name}</option>
            </c:forEach>
          </select>
        </div>
        <div class="col-4 col-md-2">
          <input class="cc-form-control line-qty" type="text" inputmode="numeric" name="quantity" placeholder="${qtyLabel}" aria-label="${qtyLabel}">
        </div>
        <div class="col-5 col-md-3">
          <input class="cc-form-control line-price" type="text" inputmode="numeric" name="unitPrice" placeholder="${priceLabel}" aria-label="${priceLabel}">
        </div>
        <div class="col-3 col-md-2 d-flex align-items-center justify-content-between gap-2">
          <span class="small text-muted line-profit">&nbsp;</span>
          <button type="button" class="btn btn-sm btn-outline-danger line-remove" aria-label="Remove">&times;</button>
        </div>
      </div>
    </div>

    <button type="button" id="addLine" class="btn btn-cc-outline btn-sm mt-1">
      <i class="bi bi-plus-lg"></i> <fmt:message key="sales.add_line"/>
    </button>

    <div class="d-flex flex-wrap justify-content-end gap-4 mt-3 pt-3" style="border-top:1px solid var(--cc-border);">
      <div class="small"><span class="text-muted"><fmt:message key="sales.revenue"/>:</span> <span id="tRevenue" class="fw-semibold money">—</span></div>
      <div class="small"><span class="text-muted"><fmt:message key="sales.cost"/>:</span> <span id="tCost" class="text-muted money">—</span></div>
      <div class="small"><span class="text-muted"><fmt:message key="sales.profit"/>:</span> <span id="tProfit" class="fw-bold money">—</span></div>
    </div>

    <div class="d-flex gap-2 mt-3">
      <button type="submit" class="btn-cc"><fmt:message key="sales.save"/></button>
      <a href="${pageContext.request.contextPath}/sales" class="btn btn-cc-outline"><fmt:message key="btn.cancel"/></a>
    </div>
  </form>
</div>

<script>
(function () {
  var cfg = { rate: parseFloat('${curRate}') || 1, sym: '${curSymbol}', dec: parseInt('${curDecimals}', 10) || 0 };
  function fmt(base) {
    return cfg.sym + (base * cfg.rate).toLocaleString(undefined, { minimumFractionDigits: cfg.dec, maximumFractionDigits: cfg.dec });
  }
  // digits-only value -> grouped string ("36178" -> "36,178"); and the reverse
  function digits(s) { return (s || '').replace(/[^\d]/g, ''); }
  function group(s) { var d = digits(s); return d ? parseInt(d, 10).toLocaleString('en-US') : ''; }
  function num(s) { var d = digits(s); return d ? parseInt(d, 10) : 0; }

  var linesEl = document.getElementById('lines');
  var form = linesEl.closest('form');

  function recompute() {
    var rev = 0, cost = 0;
    linesEl.querySelectorAll('.sales-line').forEach(function (row) {
      var sel = row.querySelector('.line-product');
      var opt = sel.options[sel.selectedIndex];
      var unitCost = opt ? (parseFloat(opt.getAttribute('data-cost')) || 0) : 0;
      var qty = num(row.querySelector('.line-qty').value);
      var price = num(row.querySelector('.line-price').value);
      var lineRev = qty * price, lineCost = qty * unitCost;
      rev += lineRev; cost += lineCost;
      var pEl = row.querySelector('.line-profit');
      pEl.innerHTML = (qty > 0 && price > 0) ? fmt(lineRev - lineCost) : '&nbsp;';
    });
    document.getElementById('tRevenue').textContent = fmt(rev);
    document.getElementById('tCost').textContent = fmt(cost);
    document.getElementById('tProfit').textContent = fmt(rev - cost);
  }

  // reformat an input with thousands separators (caret stays at the end)
  function formatField(el) { el.value = group(el.value); }

  function wire(row) {
    var sel = row.querySelector('.line-product');
    var qtyEl = row.querySelector('.line-qty');
    var priceEl = row.querySelector('.line-price');
    sel.addEventListener('change', function () {
      var opt = sel.options[sel.selectedIndex];
      var suggested = opt ? parseFloat(opt.getAttribute('data-price')) : NaN;
      // Update the suggested price on product change — but never overwrite a price the user typed.
      if (priceEl.value === '' || priceEl.dataset.auto === '1') {
        if (suggested && !isNaN(suggested)) {
          priceEl.value = group(String(Math.round(suggested)));
          priceEl.dataset.auto = '1';
        } else {
          priceEl.value = '';
          delete priceEl.dataset.auto;
        }
      }
      recompute();
    });
    qtyEl.addEventListener('input', function () { formatField(qtyEl); recompute(); });
    priceEl.addEventListener('input', function () {
      delete priceEl.dataset.auto;   // user took over — stop auto-filling
      formatField(priceEl);
      recompute();
    });
    row.querySelector('.line-remove').addEventListener('click', function () {
      if (linesEl.querySelectorAll('.sales-line').length > 1) { row.remove(); recompute(); }
    });
  }

  document.getElementById('addLine').addEventListener('click', function () {
    var clone = linesEl.querySelector('.sales-line').cloneNode(true);
    clone.querySelector('.line-product').selectedIndex = 0;
    clone.querySelector('.line-qty').value = '';
    clone.querySelector('.line-price').value = '';
    clone.querySelector('.line-profit').innerHTML = '&nbsp;';
    linesEl.appendChild(clone);
    wire(clone);
  });

  // Strip separators before submit so the server receives plain numbers.
  if (form) form.addEventListener('submit', function () {
    linesEl.querySelectorAll('.line-qty, .line-price').forEach(function (el) { el.value = digits(el.value); });
  });

  // Format any values already present (e.g. after a validation re-render).
  linesEl.querySelectorAll('.line-qty, .line-price').forEach(formatField);
  linesEl.querySelectorAll('.sales-line').forEach(wire);
  recompute();
}());
</script>

<jsp:include page="../layout/footer.jsp"/>
