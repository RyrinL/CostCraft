<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="cc" uri="http://costcraft.com/functions" %>
<fmt:message key="page.batch_history" var="pageTitle"/>
<fmt:message key="batch.log_btn" var="pageAction"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
  <jsp:param name="pageAction" value="${pageAction}"/>
  <jsp:param name="pageActionHref" value="${pageContext.request.contextPath}/batches?action=new"/>
  <jsp:param name="success" value="${param.success}"/>
</jsp:include>

<%-- Filter bar --%>
<div class="cc-card mb-3">
  <form class="row g-2 align-items-end" method="get" action="${pageContext.request.contextPath}/batches">
    <div class="col-12 col-md-3">
      <label class="cc-form-label"><fmt:message key="batch.filter_product"/></label>
      <select class="cc-form-control" name="productId">
        <option value=""><fmt:message key="batch.all_products"/></option>
        <c:forEach var="p" items="${products}">
          <option value="${p.id}" ${productId == p.id ? 'selected' : ''}>${p.name}</option>
        </c:forEach>
      </select>
    </div>
    <div class="col-6 col-md-2">
      <label class="cc-form-label"><fmt:message key="batch.filter_date_from"/></label>
      <input class="cc-form-control" type="date" name="dateFrom" value="${dateFrom}">
    </div>
    <div class="col-6 col-md-2">
      <label class="cc-form-label"><fmt:message key="batch.filter_date_to"/></label>
      <input class="cc-form-control" type="date" name="dateTo" value="${dateTo}">
    </div>
    <div class="col-12 col-md-2">
      <label class="cc-form-label"><fmt:message key="batch.filter_worker"/></label>
      <input class="cc-form-control" type="text" name="worker" value="${worker}" placeholder="username">
    </div>
    <div class="col-12 col-md-3 d-flex gap-2">
      <button type="submit" class="btn-cc flex-fill"><fmt:message key="btn.filter"/></button>
      <a href="${pageContext.request.contextPath}/batches" class="btn btn-cc-outline flex-fill text-center text-decoration-none"><fmt:message key="btn.clear"/></a>
      <a href="${pageContext.request.contextPath}/batches?action=export&productId=${productId}&dateFrom=${dateFrom}&dateTo=${dateTo}&worker=${worker}"
         class="btn btn-cc-outline flex-fill text-center text-decoration-none">CSV</a>
    </div>
  </form>
</div>

<div class="cc-card">
  <table class="cc-table">
    <thead>
      <tr>
        <th><fmt:message key="batch.col_date"/></th>
        <th><fmt:message key="batch.col_product"/></th>
        <th class="text-end"><fmt:message key="batch.col_multiplier"/></th>
        <th class="text-end"><fmt:message key="batch.col_units"/></th>
        <th class="text-end"><fmt:message key="batch.col_unit_cost"/></th>
        <th class="text-end"><fmt:message key="batch.col_total"/></th>
        <th><fmt:message key="batch.col_by"/></th>
        <th><fmt:message key="batch.col_notes"/></th>
      </tr>
    </thead>
    <tbody>
    <c:forEach var="b" items="${batches}">
      <tr>
        <td class="small text-muted"><fmt:formatDate value="${b.createdAt}" pattern="dd MMM yyyy" type="date"/></td>
        <td class="fw-semibold">${b.productName}</td>
        <td class="text-end"><fmt:formatNumber value="${b.multiplier}" pattern="#,##0.##"/>×</td>
        <td class="text-end money"><fmt:formatNumber value="${b.unitsProduced}" pattern="#,##0.##"/></td>
        <td class="text-end money">${cc:money(b.recordedUnitCost, sessionScope.user)}</td>
        <td class="text-end money money-primary">${cc:money(b.recordedTotalCost, sessionScope.user)}</td>
        <td class="small">${b.workerUsername}</td>
        <td class="small text-muted">${b.notes}</td>
      </tr>
    </c:forEach>
    <c:if test="${empty batches}">
      <tr><td colspan="8" class="text-muted text-center py-3"><fmt:message key="batch.none"/></td></tr>
    </c:if>
    </tbody>
  </table>
</div>

<jsp:include page="../layout/footer.jsp"/>
