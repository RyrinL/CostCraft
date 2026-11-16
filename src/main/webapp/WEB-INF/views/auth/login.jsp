<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Login — CostCraft</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body style="background:var(--cc-bg);display:flex;align-items:center;justify-content:center;min-height:100vh;">
<div style="width:100%;max-width:380px;padding:1rem;">
  <div class="cc-card">
    <div class="text-center mb-4">
      <div style="font-size:2.5rem;">🧁</div>
      <h1 style="font-size:1.4rem;font-weight:700;color:var(--cc-primary);">CostCraft</h1>
      <div class="text-muted small">Recipe Cost & Inventory</div>
    </div>

    <c:if test="${not empty error}">
      <div class="cc-alert-danger mb-3">${error}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
      <div class="mb-3">
        <label class="cc-form-label" for="username">Username</label>
        <input class="cc-form-control" type="text" id="username" name="username"
               autocomplete="username" required autofocus>
      </div>
      <div class="mb-4">
        <label class="cc-form-label" for="password">Password</label>
        <input class="cc-form-control" type="password" id="password" name="password"
               autocomplete="current-password" required>
      </div>
      <button type="submit" class="btn-cc w-100">Sign In</button>
    </form>

    <%-- DEV ONLY: skip-login for testing. --%>
        <div class="mt-3 pt-3" style="border-top:1px dashed var(--cc-border-strong);">
          <div class="text-muted small text-center mb-2">Testing shortcuts</div>
          <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/login?dev=owner" class="btn btn-cc-outline btn-sm flex-fill text-center">Skip as Owner</a>
            <a href="${pageContext.request.contextPath}/login?dev=worker" class="btn btn-cc-outline btn-sm flex-fill text-center">Skip as Worker</a>
          </div>
        </div>
  </div>
</div>
</body>
</html>
