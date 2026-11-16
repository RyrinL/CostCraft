<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="editing" value="${material != null}"/>
<fmt:message key="${editing ? 'page.material_edit' : 'page.material_new'}" var="pageTitle"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
</jsp:include>

<div class="cc-card" style="max-width:520px;">
  <form method="post" action="${pageContext.request.contextPath}/materials">
    <input type="hidden" name="action" value="${editing ? 'update' : 'create'}"/>
    <c:if test="${editing}"><input type="hidden" name="id" value="${material.id}"/></c:if>
    <%-- carry the list's active filter so saving returns to where you were --%>
    <input type="hidden" name="fSearch" value="${param.fSearch}"/>
    <input type="hidden" name="fCat" value="${param.fCat}"/>
    <input type="hidden" name="fStatus" value="${param.fStatus}"/>
    <input type="hidden" name="fSort" value="${param.fSort}"/>

    <%-- Name, category, unit — shown in both create and edit modes --%>
    <div class="row g-3 mb-3">
      <div class="col-12">
        <label class="cc-form-label"><fmt:message key="material.form.category"/></label>
        <select class="cc-form-control" name="categoryId" required>
          <c:forEach var="cat" items="${categories}">
            <option value="${cat.id}" ${editing && material.categoryId == cat.id ? 'selected' : ''}>
              <fmt:message key="category.${cat.name}"/>
            </option>
          </c:forEach>
        </select>
      </div>
      <div class="col-12">
        <label class="cc-form-label"><fmt:message key="material.form.name"/></label>
        <input class="cc-form-control" type="text" name="name"
               value="${material.name}" required>
      </div>
      <div class="col-6">
        <label class="cc-form-label"><fmt:message key="material.form.unit"/></label>
        <input class="cc-form-control" type="text" name="unit"
               value="${editing ? material.unit : 'gram'}">
      </div>
    </div>

    <div class="row g-3 mb-3">
      <div class="col-6">
        <label class="cc-form-label"><fmt:message key="material.form.pkg_size"/> (${editing ? material.unit : 'unit'})</label>
        <input class="cc-form-control" type="number" step="0.01" name="packageSize"
               value="${material.packageSize}" required>
      </div>
      <div class="col-6">
        <label class="cc-form-label"><fmt:message key="material.form.pkg_price"/></label>
        <input class="cc-form-control" type="number" step="1" name="packagePrice"
               value="${material.packagePrice}" required>
      </div>
      <div class="col-6">
        <label class="cc-form-label"><fmt:message key="material.form.threshold"/></label>
        <input class="cc-form-control" type="number" step="0.01" name="lowStockThreshold"
               value="${material.lowStockThreshold}">
      </div>
    </div>

    <c:if test="${editing}">
      <div class="cc-card mb-3" style="background:var(--cc-surface-tint);">
        <div class="small text-muted"><fmt:message key="material.form.hint"/></div>
        <div class="small fw-semibold mt-1"><fmt:message key="material.form.current_stock"/> ${material.currentStock} ${material.unit} <fmt:message key="material.form.manage_via"/></div>
      </div>
    </c:if>

    <div class="d-flex gap-2">
      <button type="submit" class="btn-cc"><fmt:message key="btn.save"/></button>
      <a href="${pageContext.request.contextPath}/materials?search=${param.fSearch}&categoryId=${param.fCat}&status=${param.fStatus}&sort=${param.fSort}" class="btn btn-cc-outline"><fmt:message key="btn.cancel"/></a>
    </div>
  </form>

  <c:if test="${editing}">
    <div class="cc-danger-zone">
      <div class="label"><fmt:message key="btn.danger_zone"/></div>
      <form method="post" action="${pageContext.request.contextPath}/materials">
        <input type="hidden" name="action" value="delete"/>
        <input type="hidden" name="id" value="${material.id}"/>
        <input type="hidden" name="fSearch" value="${param.fSearch}"/>
        <input type="hidden" name="fCat" value="${param.fCat}"/>
        <input type="hidden" name="fStatus" value="${param.fStatus}"/>
        <input type="hidden" name="fSort" value="${param.fSort}"/>
        <fmt:message key="btn.delete" var="delLabel"/>
        <button type="submit" class="btn btn-outline-danger btn-sm"
                onclick="return confirm('${delLabel} ${material.name}?')">
          <fmt:message key="btn.delete"/> ${material.name}
        </button>
      </form>
    </div>
  </c:if>
</div>

<jsp:include page="../layout/footer.jsp"/>
