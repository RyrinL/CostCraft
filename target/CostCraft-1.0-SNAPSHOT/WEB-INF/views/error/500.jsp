<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><title>500 — CostCraft</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head>
<body style="display:flex;align-items:center;justify-content:center;min-height:100vh;background:var(--cc-bg);">
<div class="cc-card text-center" style="max-width:400px;">
  <div style="font-size:3rem;">⚠️</div>
  <h2 class="mt-2">Server Error</h2>
  <p class="text-muted">Something went wrong. Please try again.</p>
  <p class="small text-muted">${pageContext.exception.message}</p>
  <a href="${pageContext.request.contextPath}/dashboard" class="btn-cc">Back to Dashboard</a>
</div>
</body></html>
