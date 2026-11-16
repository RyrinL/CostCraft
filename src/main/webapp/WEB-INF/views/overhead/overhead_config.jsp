<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="cc" uri="http://costcraft.com/functions" %>
<fmt:message key="page.overhead" var="pageTitle"/>
<jsp:include page="../layout/header.jsp">
  <jsp:param name="pageTitle" value="${pageTitle}"/>
  <jsp:param name="success" value="${param.success}"/>
</jsp:include>

<div class="row g-3">
  <div class="col-lg-7">
    <div class="cc-card">
      <h6 class="fw-bold mb-3"><fmt:message key="overhead.stage2_title"/></h6>
      <table class="cc-table">
        <thead><tr>
          <th><fmt:message key="overhead.col_name"/></th>
          <th><fmt:message key="overhead.col_rule"/></th>
          <th class="text-end"><fmt:message key="overhead.col_amount"/></th>
          <th><fmt:message key="overhead.col_active"/></th>
          <th></th>
        </tr></thead>
        <tbody>
        <c:forEach var="c" items="${overheadConfigs}">
          <c:if test="${c.costStage == 'DIRECT_OTHER'}">
          <tr>
            <td class="fw-medium">${c.name}</td>
            <td class="small text-muted">${c.allocationRule}</td>
            <td class="text-end money">
              <c:choose>
                <c:when test="${c.fixedAmount != null}">${cc:money(c.fixedAmount, sessionScope.user)}</c:when>
                <c:when test="${c.dailyAmount != null}">${cc:money(c.dailyAmount, sessionScope.user)}<fmt:message key="overhead.per_day"/></c:when>
              </c:choose>
            </td>
            <td>
              <c:choose>
                <c:when test="${c.active}"><span class="badge rounded-pill badge-ok"><fmt:message key="btn.yes"/></span></c:when>
                <c:otherwise><span class="badge rounded-pill badge-low"><fmt:message key="btn.no"/></span></c:otherwise>
              </c:choose>
            </td>
            <td><a href="?action=edit&id=${c.id}" class="btn btn-sm btn-cc-outline"><fmt:message key="btn.edit"/></a></td>
          </tr>
          </c:if>
        </c:forEach>
        </tbody>
      </table>
    </div>
    <div class="cc-card mt-3">
      <h6 class="fw-bold mb-3"><fmt:message key="overhead.stage3_title"/></h6>
      <table class="cc-table">
        <thead><tr>
          <th><fmt:message key="overhead.col_name"/></th>
          <th class="text-end"><fmt:message key="overhead.col_pct"/></th>
          <th><fmt:message key="overhead.col_active"/></th>
          <th></th>
        </tr></thead>
        <tbody>
        <c:forEach var="c" items="${overheadConfigs}">
          <c:if test="${c.costStage == 'PERCENTAGE_OVERHEAD'}">
          <tr>
            <td class="fw-medium">${c.name}</td>
            <td class="text-end money"><fmt:formatNumber value="${c.percentage * 100}" pattern="#,##0.##"/>%</td>
            <td>
              <c:choose>
                <c:when test="${c.active}"><span class="badge rounded-pill badge-ok"><fmt:message key="btn.yes"/></span></c:when>
                <c:otherwise><span class="badge rounded-pill badge-low"><fmt:message key="btn.no"/></span></c:otherwise>
              </c:choose>
            </td>
            <td><a href="?action=edit&id=${c.id}" class="btn btn-sm btn-cc-outline"><fmt:message key="btn.edit"/></a></td>
          </tr>
          </c:if>
        </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
  <div class="col-lg-5">
    <div class="cc-card">
      <h6 class="fw-bold mb-3"><c:choose><c:when test="${config != null}"><fmt:message key="overhead.edit_config"/></c:when><c:otherwise><fmt:message key="overhead.new_config"/></c:otherwise></c:choose></h6>
      <form method="post" action="${pageContext.request.contextPath}/overhead">
        <input type="hidden" name="action" value="${config != null ? 'update' : 'create'}"/>
        <c:if test="${config != null}"><input type="hidden" name="id" value="${config.id}"/></c:if>

        <div class="mb-3">
          <label class="cc-form-label"><fmt:message key="overhead.col_name"/></label>
          <input class="cc-form-control" type="text" name="name" value="${config.name}" required>
        </div>
        <div class="row g-2 mb-3">
          <div class="col-6">
            <label class="cc-form-label"><fmt:message key="overhead.cost_stage"/></label>
            <select class="cc-form-control" name="costStage" required>
              <option value="DIRECT_OTHER" ${config.costStage == 'DIRECT_OTHER' ? 'selected' : ''}><fmt:message key="overhead.direct_other"/></option>
              <option value="PERCENTAGE_OVERHEAD" ${config.costStage == 'PERCENTAGE_OVERHEAD' ? 'selected' : ''}><fmt:message key="overhead.pct_overhead"/></option>
            </select>
          </div>
          <div class="col-6">
            <label class="cc-form-label"><fmt:message key="overhead.alloc_rule"/></label>
            <select class="cc-form-control" name="allocationRule" required>
              <option value="PER_BATCH_FIXED" ${config.allocationRule == 'PER_BATCH_FIXED' ? 'selected' : ''}><fmt:message key="overhead.per_batch"/></option>
              <option value="PER_UNIT_FIXED" ${config.allocationRule == 'PER_UNIT_FIXED' ? 'selected' : ''}><fmt:message key="overhead.per_unit"/></option>
              <option value="DAILY_ALLOCATED" ${config.allocationRule == 'DAILY_ALLOCATED' ? 'selected' : ''}><fmt:message key="overhead.daily"/></option>
              <option value="PERCENTAGE_OF_DIRECT" ${config.allocationRule == 'PERCENTAGE_OF_DIRECT' ? 'selected' : ''}><fmt:message key="overhead.pct_of_direct"/></option>
            </select>
          </div>
        </div>
        <div class="row g-2 mb-3">
          <div class="col-6">
            <label class="cc-form-label"><fmt:message key="overhead.fixed_amount"/></label>
            <input class="cc-form-control" type="number" step="1" name="fixedAmount" value="${config.fixedAmount}">
          </div>
          <div class="col-6">
            <label class="cc-form-label"><fmt:message key="overhead.daily_amount"/></label>
            <input class="cc-form-control" type="number" step="1" name="dailyAmount" value="${config.dailyAmount}">
          </div>
          <div class="col-6">
            <label class="cc-form-label"><fmt:message key="overhead.days_month"/></label>
            <input class="cc-form-control" type="number" name="daysPerMonth" value="${config != null ? config.daysPerMonth : 30}">
          </div>
          <div class="col-6">
            <label class="cc-form-label"><fmt:message key="overhead.percentage"/></label>
            <input class="cc-form-control" type="number" step="0.001" name="percentage" value="${config.percentage}">
          </div>
        </div>
        <div class="mb-3">
          <label class="cc-form-label"><fmt:message key="overhead.col_active"/></label>
          <select class="cc-form-control" name="active">
            <option value="true" ${(config == null || config.active) ? 'selected' : ''}><fmt:message key="btn.yes"/></option>
            <option value="false" ${(config != null && !config.active) ? 'selected' : ''}><fmt:message key="btn.no"/></option>
          </select>
        </div>
        <button type="submit" class="btn-cc"><fmt:message key="btn.save"/></button>
      </form>

      <c:if test="${config != null}">
        <div class="cc-danger-zone">
          <div class="label"><fmt:message key="btn.danger_zone"/></div>
          <form method="post" action="${pageContext.request.contextPath}/overhead">
            <input type="hidden" name="action" value="delete"/>
            <input type="hidden" name="id" value="${config.id}"/>
            <fmt:message key="btn.delete" var="delLabel"/>
            <button type="submit" class="btn btn-outline-danger btn-sm"
                    onclick="return confirm('${delLabel} ${config.name}?')">
              <fmt:message key="btn.delete"/> ${config.name}
            </button>
          </form>
        </div>
      </c:if>
    </div>
  </div>
</div>

<jsp:include page="../layout/footer.jsp"/>
