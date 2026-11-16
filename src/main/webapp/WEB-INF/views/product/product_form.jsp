<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="editing" value="${product != null}"/>
<fmt:message key="${editing ? 'page.product_edit' : 'page.product_new'}" var="pageTitle"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
</jsp:include>

<div class="cc-card" style="max-width:520px;">
  <form method="post" action="${pageContext.request.contextPath}/products">
    <input type="hidden" name="action" value="${editing ? 'update' : 'create'}"/>
    <c:if test="${editing}"><input type="hidden" name="id" value="${product.id}"/></c:if>

    <div class="mb-3">
      <label class="cc-form-label"><fmt:message key="product.form.name"/></label>
      <input class="cc-form-control" type="text" name="name" value="${product.name}" required>
    </div>
    <div class="row g-3 mb-3">
      <div class="col-6">
        <label class="cc-form-label"><fmt:message key="product.form.yield"/></label>
        <input class="cc-form-control" type="number" step="0.01" name="yieldPerBatch"
               value="${product.yieldPerBatch}" required>
      </div>
      <div class="col-6">
        <label class="cc-form-label"><fmt:message key="product.form.yield_unit"/></label>
        <c:set var="yu" value="${editing ? product.yieldUnit : 'pcs'}"/>
        <select class="cc-form-control" name="yieldUnit">
          <option value="pcs"  ${yu == 'pcs'  ? 'selected' : ''}><fmt:message key="yield.pcs"/></option>
          <option value="jar"  ${yu == 'jar'  ? 'selected' : ''}><fmt:message key="yield.jar"/></option>
          <option value="box"  ${yu == 'box'  ? 'selected' : ''}><fmt:message key="yield.box"/></option>
          <option value="pack" ${yu == 'pack' ? 'selected' : ''}><fmt:message key="yield.pack"/></option>
          <option value="bag"  ${yu == 'bag'  ? 'selected' : ''}><fmt:message key="yield.bag"/></option>
          <option value="tray" ${yu == 'tray' ? 'selected' : ''}><fmt:message key="yield.tray"/></option>
          <option value="loaf" ${yu == 'loaf' ? 'selected' : ''}><fmt:message key="yield.loaf"/></option>
          <option value="roll" ${yu == 'roll' ? 'selected' : ''}><fmt:message key="yield.roll"/></option>
        </select>
      </div>
      <div class="col-6">
        <label class="cc-form-label"><fmt:message key="product.form.days"/></label>
        <input class="cc-form-control" type="number" step="0.01" name="daysPerBatch"
               value="${editing ? product.daysPerBatch : '0.5'}">
      </div>
      <div class="col-6">
        <label class="cc-form-label"><fmt:message key="product.form.margin_lower"/></label>
        <input class="cc-form-control" type="number" step="0.01" name="marginLowerPct"
               value="${editing ? product.marginLowerPct : '0.30'}">
      </div>
      <div class="col-6">
        <label class="cc-form-label"><fmt:message key="product.form.margin_upper"/></label>
        <input class="cc-form-control" type="number" step="0.01" name="marginUpperPct"
               value="${editing ? product.marginUpperPct : '0.50'}">
      </div>
    </div>
    <c:if test="${editing}">
    <div class="mb-3">
      <label class="cc-form-label"><fmt:message key="product.form.active"/></label>
      <select class="cc-form-control" name="active">
        <option value="true" ${product.active ? 'selected' : ''}><fmt:message key="status.active"/></option>
        <option value="false" ${!product.active ? 'selected' : ''}><fmt:message key="status.inactive"/></option>
      </select>
    </div>
    </c:if>
    <div class="d-flex gap-2">
      <button type="submit" class="btn-cc"><fmt:message key="btn.save"/></button>
      <a href="${pageContext.request.contextPath}/products" class="btn btn-cc-outline"><fmt:message key="btn.cancel"/></a>
      <c:if test="${editing}">
        <a href="${pageContext.request.contextPath}/bom?productId=${product.id}" class="btn btn-cc-outline ms-auto"><fmt:message key="product.form.edit_bom"/></a>
      </c:if>
    </div>
  </form>

  <c:if test="${editing}">
    <div class="cc-danger-zone">
      <div class="label"><fmt:message key="btn.danger_zone"/></div>
      <form method="post" action="${pageContext.request.contextPath}/products">
        <input type="hidden" name="action" value="delete"/>
        <input type="hidden" name="id" value="${product.id}"/>
        <fmt:message key="btn.delete" var="delLabel"/>
        <button type="submit" class="btn btn-outline-danger btn-sm"
                onclick="return confirm('${delLabel} ${product.name}?')">
          <fmt:message key="btn.delete"/> ${product.name}
        </button>
      </form>
    </div>
  </c:if>
</div>

<jsp:include page="../layout/footer.jsp"/>
