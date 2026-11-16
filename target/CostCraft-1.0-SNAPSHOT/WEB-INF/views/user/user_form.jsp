<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="editing" value="${editUser != null}"/>
<fmt:message key="${editing ? 'page.user_edit' : 'page.user_new'}" var="pageTitle"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
</jsp:include>

<div class="cc-card" style="max-width:480px;">
  <form method="post" action="${pageContext.request.contextPath}/users">
    <input type="hidden" name="action" value="${editing ? 'update' : 'create'}"/>
    <c:if test="${editing}"><input type="hidden" name="id" value="${editUser.id}"/></c:if>

    <div class="mb-3">
      <label class="cc-form-label"><fmt:message key="user.form.username"/></label>
      <input class="cc-form-control" type="text" name="username" value="${editUser.username}" required>
    </div>
    <c:if test="${not editing}">
    <div class="mb-3">
      <label class="cc-form-label"><fmt:message key="user.form.password"/></label>
      <input class="cc-form-control" type="password" name="password" required>
    </div>
    </c:if>
    <div class="mb-3">
      <label class="cc-form-label"><fmt:message key="user.form.role"/></label>
      <select class="cc-form-control" name="role">
        <option value="OWNER" ${editUser.role == 'OWNER' ? 'selected' : ''}>OWNER</option>
        <option value="WORKER" ${editUser.role == 'WORKER' ? 'selected' : ''}>WORKER</option>
      </select>
    </div>
    <div class="mb-3">
      <label class="cc-form-label"><fmt:message key="user.form.email"/></label>
      <input class="cc-form-control" type="email" name="email" value="${editUser.email}">
    </div>
    <c:if test="${editing}">
    <div class="mb-3">
      <label class="cc-form-label"><fmt:message key="user.form.active"/></label>
      <select class="cc-form-control" name="active">
        <option value="true" ${editUser.active ? 'selected' : ''}><fmt:message key="status.active"/></option>
        <option value="false" ${!editUser.active ? 'selected' : ''}><fmt:message key="status.inactive"/></option>
      </select>
    </div>
    </c:if>

    <div class="d-flex gap-2">
      <button type="submit" class="btn-cc"><fmt:message key="btn.save"/></button>
      <a href="${pageContext.request.contextPath}/users" class="btn btn-cc-outline"><fmt:message key="btn.cancel"/></a>
    </div>
  </form>

  <c:if test="${editing}">
  <hr style="border-color:var(--cc-border);margin:1.5rem 0;">
  <h6 class="fw-bold mb-3"><fmt:message key="user.form.change_password"/></h6>
  <form method="post" action="${pageContext.request.contextPath}/users">
    <input type="hidden" name="action" value="password"/>
    <input type="hidden" name="id" value="${editUser.id}"/>
    <div class="mb-3">
      <label class="cc-form-label"><fmt:message key="user.form.new_password"/></label>
      <input class="cc-form-control" type="password" name="newPassword" required>
    </div>
    <button type="submit" class="btn-cc"><fmt:message key="btn.update_password"/></button>
  </form>
  </c:if>
</div>

<jsp:include page="../layout/footer.jsp"/>
