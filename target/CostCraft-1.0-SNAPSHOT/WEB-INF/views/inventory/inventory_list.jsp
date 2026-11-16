<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:message key="page.inventory" var="pageTitle"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
  <jsp:param name="success" value="${param.success}"/>
</jsp:include>

<div class="row g-3">
  <div class="col-lg-4">
    <div class="cc-card">
      <h6 class="fw-bold mb-3"><fmt:message key="inventory.record_movement"/></h6>
      <form method="post" action="${pageContext.request.contextPath}/inventory">
        <div class="mb-3">
          <label class="cc-form-label"><fmt:message key="inventory.material"/></label>
          <select class="cc-form-control" name="materialId" required>
            <option value=""><fmt:message key="inventory.select_material"/></option>
            <c:forEach var="m" items="${materials}">
              <option value="${m.id}" ${m.id == selectedMaterialId ? 'selected' : ''}>${m.name} (${m.currentStock} ${m.unit})</option>
            </c:forEach>
          </select>
        </div>
        <div class="mb-3">
          <label class="cc-form-label"><fmt:message key="inventory.movement_type"/></label>
          <select class="cc-form-control" name="movement" required>
            <option value="PURCHASE"><fmt:message key="inventory.purchase"/></option>
            <option value="ADJUSTMENT_ADD"><fmt:message key="inventory.adj_add"/></option>
            <option value="ADJUSTMENT_SUB"><fmt:message key="inventory.adj_sub"/></option>
            <option value="WASTE"><fmt:message key="inventory.waste"/></option>
            <option value="INITIAL_STOCK"><fmt:message key="inventory.initial"/></option>
          </select>
        </div>
        <div class="mb-3">
          <label class="cc-form-label"><fmt:message key="inventory.qty"/></label>
          <input class="cc-form-control" type="number" step="0.01" name="quantity" min="0.01" required
                 ${not empty selectedMaterialId ? 'autofocus' : ''}>
        </div>
        <div class="mb-3">
          <label class="cc-form-label"><fmt:message key="inventory.note"/></label>
          <input class="cc-form-control" type="text" name="note">
        </div>
        <button type="submit" class="btn-cc w-100"><fmt:message key="btn.record"/></button>
      </form>
    </div>
  </div>
  <div class="col-lg-8">
    <div class="cc-card">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h6 class="fw-bold mb-0"><fmt:message key="inventory.current_stock"/></h6>
        <a href="${pageContext.request.contextPath}/inventory/history" class="small text-decoration-none" style="color:var(--cc-primary);"><fmt:message key="inventory.full_history"/></a>
      </div>
      <form method="get" action="${pageContext.request.contextPath}/inventory" class="d-flex gap-2 mb-3 flex-wrap">
        <input class="cc-form-control" type="text" name="search" placeholder="${inventory.search_placeholder}"
               value="${search}" style="flex:1;min-width:150px;"
               data-live-search data-target="#inventoryTable" autocomplete="off">
        <input type="hidden" name="sort" value="${sort}">
        <button type="submit" class="btn-cc"><fmt:message key="btn.search"/></button>
        <c:if test="${not empty search}">
          <a href="${pageContext.request.contextPath}/inventory" class="btn btn-outline-secondary btn-sm align-self-center"><fmt:message key="btn.clear"/></a>
        </c:if>
      </form>
      <table class="cc-table" id="inventoryTable">
        <thead><tr>
          <th>
            <c:choose>
              <c:when test="${sort == 'name_asc'}">
                <fmt:message key="inventory.col_name"/> <a href="${pageContext.request.contextPath}/inventory?search=${search}&sort=name_desc" style="color:var(--cc-primary);text-decoration:none;" title="Z→A">↑</a>
              </c:when>
              <c:otherwise>
                <fmt:message key="inventory.col_name"/> <a href="${pageContext.request.contextPath}/inventory?search=${search}&sort=name_asc" style="color:var(--cc-primary);text-decoration:none;" title="A→Z">↓</a>
              </c:otherwise>
            </c:choose>
          </th>
          <th><fmt:message key="inventory.col_category"/></th>
          <th class="text-end"><fmt:message key="inventory.col_stock"/></th>
          <th><fmt:message key="inventory.col_status"/></th>
        </tr></thead>
        <tbody>
        <c:forEach var="m" items="${materials}">
          <tr>
            <td class="fw-medium">${m.name}</td>
            <td class="text-muted small"><fmt:message key="category.${m.categoryName}"/></td>
            <td class="text-end money"><fmt:formatNumber value="${m.currentStock}" pattern="#,##0.##"/> ${m.unit}</td>
            <td>
              <c:choose>
                <c:when test="${m.belowThreshold}"><span class="badge rounded-pill badge-low"><fmt:message key="status.low"/></span></c:when>
                <c:otherwise><span class="badge rounded-pill badge-ok"><fmt:message key="status.ok"/></span></c:otherwise>
              </c:choose>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
