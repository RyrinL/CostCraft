<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="${sessionScope.user != null ? sessionScope.user.language : 'en'}">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${not empty param.pageTitle ? param.pageTitle.concat(' — CostCraft') : 'CostCraft'}</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=13">
</head>
<body>
<%-- Mobile top bar: the brand itself is the sidebar toggle --%>
<button class="cc-topbar" id="ccHamburger" aria-label="Open navigation">
  <i class="bi bi-list"></i> CostCraft
</button>
<%-- Overlay backdrop (mobile sidebar) --%>
<div class="cc-overlay" id="ccOverlay"></div>
<div class="cc-layout">
  <%@ include file="sidebar.jsp" %>
  <main class="cc-main">
    <div class="cc-page-header d-flex align-items-center justify-content-between flex-wrap gap-2 mb-3">
      <div>
        <h1>${param.pageTitle}</h1>
        <c:if test="${not empty param.pageSubtitle}">
          <div class="text-muted small">${param.pageSubtitle}</div>
        </c:if>
      </div>
      <c:if test="${not empty param.pageAction}">
        <a href="${param.pageActionHref}" class="btn btn-cc">${param.pageAction}</a>
      </c:if>
    </div>
    <c:if test="${not empty param.success}">
      <div class="cc-alert-success cc-toast mb-3" role="status">
        <i class="bi bi-check-circle-fill"></i>
        <span><fmt:message key="flash.saved"/></span>
        <button type="button" class="cc-toast-close" aria-label="Dismiss">&times;</button>
      </div>
    </c:if>
    <c:if test="${not empty error}">
      <div class="cc-alert-danger cc-toast mb-3" role="alert">
        <i class="bi bi-exclamation-triangle-fill"></i>
        <span>${error}</span>
        <button type="button" class="cc-toast-close" aria-label="Dismiss">&times;</button>
      </div>
    </c:if>
