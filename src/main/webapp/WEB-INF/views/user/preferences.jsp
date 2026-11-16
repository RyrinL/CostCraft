<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:message key="page.preferences" var="pageTitle"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
  <jsp:param name="success" value="${param.success}"/>
</jsp:include>

<div class="cc-card" style="max-width:420px;">
  <form method="post" action="${pageContext.request.contextPath}/preferences">

    <h6 class="fw-bold mb-3"><fmt:message key="prefs.currency_title"/></h6>
    <div class="mb-4">
      <label class="cc-form-label"><fmt:message key="prefs.currency_label"/></label>
      <select class="cc-form-control" name="displayCurrency">
        <c:forEach var="c" items="${currencies}">
          <option value="${c.code}" ${sessionScope.user.displayCurrency == c.code ? 'selected' : ''}>
            ${c.symbol} ${c.code} — ${c.name}
          </option>
        </c:forEach>
      </select>
    </div>

    <hr style="border-color:var(--cc-border);margin:1.5rem 0 1rem;">

    <h6 class="fw-bold mb-3"><fmt:message key="prefs.language_title"/></h6>
    <div class="mb-4">
      <label class="cc-form-label"><fmt:message key="prefs.language_label"/></label>
      <select class="cc-form-control" name="language">
        <option value="en" ${sessionScope.user.language == 'en' ? 'selected' : ''}><fmt:message key="prefs.lang_en"/></option>
        <option value="id" ${sessionScope.user.language == 'id' ? 'selected' : ''}><fmt:message key="prefs.lang_id"/></option>
      </select>
    </div>

    <button type="submit" class="btn-cc"><fmt:message key="btn.save"/></button>
  </form>
</div>

<jsp:include page="../layout/footer.jsp"/>
