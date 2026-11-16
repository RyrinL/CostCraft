<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:message key="page.stock_history" var="pageTitle"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
  <jsp:param name="success" value="${param.success}"/>
</jsp:include>

<div class="cc-card mb-3">
  <form class="d-flex gap-2 align-items-end flex-wrap" method="get" action="${pageContext.request.contextPath}/inventory/history">
    <div>
      <label class="cc-form-label"><fmt:message key="history.filter_material"/></label>
      <select class="cc-form-control" name="materialId" style="min-width:200px;">
        <option value=""><fmt:message key="history.all_materials"/></option>
        <c:forEach var="m" items="${materials}">
          <option value="${m.id}" ${param.materialId == m.id ? 'selected' : ''}>${m.name}</option>
        </c:forEach>
      </select>
    </div>
    <button type="submit" class="btn-cc"><fmt:message key="btn.filter"/></button>
  </form>
</div>

<div class="cc-card">
  <table class="cc-table">
    <thead><tr>
      <th><fmt:message key="history.col_date"/></th>
      <th><fmt:message key="history.col_material"/></th>
      <th><fmt:message key="history.col_type"/></th>
      <th class="text-end"><fmt:message key="history.col_change"/></th>
      <th><fmt:message key="history.col_note"/></th>
      <th><fmt:message key="history.col_by"/></th>
      <th></th>
    </tr></thead>
    <tbody>
    <c:forEach var="log" items="${logs}">
      <tr>
        <td class="small text-muted"><fmt:formatDate value="${log.createdAt}" pattern="dd MMM HH:mm" type="date"/></td>
        <td class="fw-medium">${log.materialName}</td>
        <td><span class="badge rounded-pill badge-worker">${log.reason}</span></td>
        <td class="text-end money ${log.changeQuantity >= 0 ? '' : 'text-danger'}">
          <fmt:formatNumber value="${log.changeQuantity}" pattern="+#,##0.##;-#,##0.##"/>
        </td>
        <td class="text-muted small">${log.note}</td>
        <td class="small">${log.username}</td>
        <td class="text-end">
          <c:if test="${log.reason != 'BATCH_CONSUMPTION'}">
            <form method="post" action="${pageContext.request.contextPath}/inventory/history" style="display:inline;">
              <input type="hidden" name="action" value="reverse"/>
              <input type="hidden" name="logId" value="${log.id}"/>
              <fmt:message key="history.reverse" var="reverseLabel"/>
              <button type="submit" class="btn btn-outline-secondary btn-sm"
                      onclick="return confirm('${reverseLabel} #${log.id}?')"
                      title="${reverseLabel}" style="font-size:0.7rem;padding:0.15rem 0.4rem;">⟲</button>
            </form>
          </c:if>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty logs}">
      <tr><td colspan="7" class="text-muted text-center py-3"><fmt:message key="history.none"/></td></tr>
    </c:if>
    </tbody>
  </table>
</div>

<jsp:include page="../layout/footer.jsp"/>
