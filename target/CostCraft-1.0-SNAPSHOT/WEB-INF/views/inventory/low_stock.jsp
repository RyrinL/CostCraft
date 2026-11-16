<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:message key="page.low_stock" var="pageTitle"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
</jsp:include>

<div class="row g-3">
  <div class="col-md-7">
    <div class="cc-card">
      <h6 class="fw-bold mb-3"><fmt:message key="lowstock.below_threshold"/></h6>
      <table class="cc-table">
        <thead><tr>
          <th><fmt:message key="lowstock.col_material"/></th>
          <th class="text-end"><fmt:message key="lowstock.col_stock"/></th>
          <th class="text-end"><fmt:message key="lowstock.col_threshold"/></th>
          <th><fmt:message key="lowstock.col_action"/></th>
        </tr></thead>
        <tbody>
        <c:forEach var="m" items="${lowStockMaterials}">
          <tr>
            <td class="fw-medium">${m.name}</td>
            <td class="text-end money" style="color:var(--cc-danger);">
              <fmt:formatNumber value="${m.currentStock}" pattern="#,##0.##"/> ${m.unit}
            </td>
            <td class="text-end money text-muted">
              <fmt:formatNumber value="${m.lowStockThreshold}" pattern="#,##0.##"/> ${m.unit}
            </td>
            <td>
              <a href="${pageContext.request.contextPath}/inventory?materialId=${m.id}" class="btn btn-sm btn-cc-outline"><fmt:message key="lowstock.add_stock"/></a>
            </td>
          </tr>
        </c:forEach>
        <c:if test="${empty lowStockMaterials}">
          <tr><td colspan="4" class="text-center text-muted py-3"><fmt:message key="lowstock.all_ok"/></td></tr>
        </c:if>
        </tbody>
      </table>
    </div>
  </div>
  <div class="col-md-5">
    <div class="cc-card">
      <h6 class="fw-bold mb-3"><fmt:message key="lowstock.open_alerts"/></h6>
      <c:forEach var="a" items="${alerts}">
        <div class="d-flex justify-content-between align-items-center py-2 border-bottom" style="border-color:var(--cc-border)!important;">
          <div>
            <div class="fw-medium small">${a.materialName}</div>
            <div class="text-muted" style="font-size:0.75rem;">${a.alertType}</div>
          </div>
          <form method="post" action="${pageContext.request.contextPath}/low-stock">
            <input type="hidden" name="resolveAlertId" value="${a.id}"/>
            <button type="submit" class="btn btn-sm btn-cc-outline"><fmt:message key="btn.resolve"/></button>
          </form>
        </div>
      </c:forEach>
      <c:if test="${empty alerts}">
        <div class="text-muted text-center py-3"><fmt:message key="lowstock.no_alerts"/></div>
      </c:if>
    </div>
  </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
