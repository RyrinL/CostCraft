<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:message key="page.users" var="pageTitle"/>
<fmt:message key="user.new" var="pageAction"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
  <jsp:param name="pageAction" value="${pageAction}"/>
  <jsp:param name="pageActionHref" value="${pageContext.request.contextPath}/users?action=new"/>
  <jsp:param name="success" value="${param.success}"/>
</jsp:include>

<div class="cc-card">
  <table class="cc-table">
    <thead>
      <tr>
        <th><fmt:message key="user.col_username"/></th>
        <th><fmt:message key="user.col_role"/></th>
        <th><fmt:message key="user.col_email"/></th>
        <th><fmt:message key="user.col_currency"/></th>
        <th><fmt:message key="user.col_status"/></th>
        <th></th>
      </tr>
    </thead>
    <tbody>
    <c:forEach var="u" items="${users}">
      <tr>
        <td class="fw-semibold">${u.username}</td>
        <td><span class="badge rounded-pill badge-${u.role.toLowerCase()}">${u.role}</span></td>
        <td class="text-muted">${u.email}</td>
        <td>${u.displayCurrency}</td>
        <td>
          <c:choose>
            <c:when test="${u.active}"><span class="badge rounded-pill badge-ok"><fmt:message key="status.active"/></span></c:when>
            <c:otherwise><span class="badge rounded-pill badge-low"><fmt:message key="status.inactive"/></span></c:otherwise>
          </c:choose>
        </td>
        <td class="text-end">
          <a href="${pageContext.request.contextPath}/users?action=edit&id=${u.id}" class="btn btn-sm btn-cc-outline"><fmt:message key="btn.edit"/></a>
        </td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
</div>

<jsp:include page="../layout/footer.jsp"/>
