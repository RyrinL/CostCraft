<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="cc" uri="http://costcraft.com/functions" %>
<fmt:message key="page.materials" var="pageTitle"/>
<c:if test="${sessionScope.user.owner}"><fmt:message key="material.new" var="pageAction"/></c:if>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
  <jsp:param name="pageAction" value="${pageAction}"/>
  <jsp:param name="pageActionHref" value="${pageContext.request.contextPath}/materials?action=new"/>
  <jsp:param name="success" value="${param.success}"/>
</jsp:include>

<div class="cc-card mb-3">
  <form method="get" action="${pageContext.request.contextPath}/materials" class="d-flex gap-2 flex-wrap align-items-end">
    <input type="hidden" name="sort" value="${sort}">
    <div style="flex:1;min-width:160px;">
      <label class="cc-form-label"><fmt:message key="material.search_label"/></label>
      <input class="cc-form-control" type="text" name="search" value="${search}"
             data-live-search data-target="#materialsTable" autocomplete="off">
    </div>
    <div>
      <label class="cc-form-label"><fmt:message key="material.category_label"/></label>
      <select class="cc-form-control" name="categoryId">
        <option value=""><fmt:message key="material.all_categories"/></option>
        <c:forEach var="cat" items="${categories}">
            <option value="${cat.id}" ${categoryId == cat.id ? 'selected' : ''}><fmt:message key="category.${cat.name}"/></option>
        </c:forEach>
      </select>
    </div>
    <div>
      <label class="cc-form-label"><fmt:message key="material.status_label"/></label>
      <select class="cc-form-control" name="status">
        <option value=""     ${empty status      ? 'selected' : ''}><fmt:message key="material.status_all"/></option>
        <option value="ok"   ${'ok'  == status   ? 'selected' : ''}><fmt:message key="material.status_ok"/></option>
        <option value="low"  ${'low' == status   ? 'selected' : ''}><fmt:message key="material.status_low"/></option>
      </select>
    </div>
    <button type="submit" class="btn-cc"><fmt:message key="btn.filter"/></button>
    <c:if test="${not empty search or not empty status or not empty categoryId}">
      <a href="${pageContext.request.contextPath}/materials" class="btn btn-outline-secondary btn-sm align-self-end"><fmt:message key="btn.clear"/></a>
    </c:if>
  </form>
</div>

<div class="cc-card">
  <c:set var="filterQs" value="fSearch=${search}&fCat=${categoryId}&fStatus=${status}&fSort=${sort}"/>
  <table class="cc-table" id="materialsTable">
    <thead>
      <tr>
        <c:set var="base" value="${pageContext.request.contextPath}/materials?search=${search}&status=${status}&categoryId=${categoryId}"/>
        <th><a href="${base}&sort=${sort == 'name_asc' ? 'name_desc' : 'name_asc'}" style="color:inherit;text-decoration:none;">
          <fmt:message key="material.col_name"/> <c:choose><c:when test="${sort == 'name_asc'}">▲</c:when><c:when test="${sort == 'name_desc'}">▼</c:when><c:otherwise><span class="text-muted">⇅</span></c:otherwise></c:choose>
        </a></th>
        <th><a href="${base}&sort=${sort == 'category_asc' ? 'category_desc' : 'category_asc'}" style="color:inherit;text-decoration:none;">
          <fmt:message key="material.col_category"/> <c:choose><c:when test="${sort == 'category_asc'}">▲</c:when><c:when test="${sort == 'category_desc'}">▼</c:when><c:otherwise><span class="text-muted">⇅</span></c:otherwise></c:choose>
        </a></th>
        <th class="text-end"><a href="${base}&sort=${sort == 'pkg_size_asc' ? 'pkg_size_desc' : 'pkg_size_asc'}" style="color:inherit;text-decoration:none;">
          <fmt:message key="material.col_pkg_size"/> <c:choose><c:when test="${sort == 'pkg_size_asc'}">▲</c:when><c:when test="${sort == 'pkg_size_desc'}">▼</c:when><c:otherwise><span class="text-muted">⇅</span></c:otherwise></c:choose>
        </a></th>
        <th class="text-end"><a href="${base}&sort=${sort == 'pkg_price_asc' ? 'pkg_price_desc' : 'pkg_price_asc'}" style="color:inherit;text-decoration:none;">
          <fmt:message key="material.col_pkg_price"/> <c:choose><c:when test="${sort == 'pkg_price_asc'}">▲</c:when><c:when test="${sort == 'pkg_price_desc'}">▼</c:when><c:otherwise><span class="text-muted">⇅</span></c:otherwise></c:choose>
        </a></th>
        <th class="text-end"><a href="${base}&sort=${sort == 'stock_asc' ? 'stock_desc' : 'stock_asc'}" style="color:inherit;text-decoration:none;">
          <fmt:message key="material.col_stock"/> <c:choose><c:when test="${sort == 'stock_asc'}">▲</c:when><c:when test="${sort == 'stock_desc'}">▼</c:when><c:otherwise><span class="text-muted">⇅</span></c:otherwise></c:choose>
        </a></th>
        <th><fmt:message key="material.col_status"/></th>
        <th></th>
      </tr>
    </thead>
    <tbody>
    <c:forEach var="m" items="${materials}">
      <tr>
        <td class="fw-semibold">${m.name}</td>
        <td class="text-muted small"><fmt:message key="category.${m.categoryName}"/></td>
        <td class="text-end money"><fmt:formatNumber value="${m.packageSize}" pattern="#,##0.##"/> ${m.unit}</td>
        <td class="text-end money">${cc:money(m.packagePrice, sessionScope.user)}</td>
        <td class="text-end money"><fmt:formatNumber value="${m.currentStock}" pattern="#,##0.##"/> ${m.unit}</td>
        <td>
          <c:choose>
            <c:when test="${m.belowThreshold}"><span class="badge rounded-pill badge-low"><fmt:message key="status.low"/></span></c:when>
            <c:otherwise><span class="badge rounded-pill badge-ok"><fmt:message key="status.ok"/></span></c:otherwise>
          </c:choose>
        </td>
        <td class="text-end">
          <c:if test="${sessionScope.user.owner}">
            <a href="${pageContext.request.contextPath}/materials?action=edit&id=${m.id}&${filterQs}" class="btn btn-sm btn-cc-outline"><fmt:message key="btn.edit"/></a>
          </c:if>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty materials}">
      <tr><td colspan="7" class="text-muted text-center py-3"><fmt:message key="material.none"/></td></tr>
    </c:if>
    </tbody>
  </table>
</div>

<jsp:include page="../layout/footer.jsp"/>
