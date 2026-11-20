<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="title" value="First-run setup"/>
  <jsp:param name="mode" value="public"/>
  <jsp:param name="bodyClass" value="auth"/>
</jsp:include>

<div class="card-soft" style="max-width:520px;width:100%;padding:36px">
  <h1 class="h3 fw-bold"><i class="bi bi-shield-lock-fill text-primary"></i> Create the admin account</h1>
  <p class="text-secondary">No admin exists yet. <c:if test="${needCode}">Enter the <strong>setup code</strong> given by the person who installed this app, then </c:if>choose your own admin e-mail and password.</p>

  <c:if test="${not empty error}"><div class="alert alert-danger py-2"><c:out value="${error}"/></div></c:if>

  <form method="post" action="${ctx}/setup">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <c:if test="${needCode}">
    <div class="mb-3"><label class="form-label" for="setupCode">Setup code</label>
      <input class="form-control" id="setupCode" name="setupCode" required autocomplete="off"></div>
    </c:if>
    <div class="mb-3"><label class="form-label" for="fullName">Your name</label>
      <input class="form-control" id="fullName" name="fullName" maxlength="120" required value="${fn:escapeXml(fullName)}"></div>
    <div class="mb-3"><label class="form-label" for="email">Admin e-mail (your username)</label>
      <input class="form-control" id="email" name="email" type="email" maxlength="150" required value="${fn:escapeXml(email)}"></div>
    <div class="mb-3"><label class="form-label" for="password">Password (min 8 characters)</label>
      <input class="form-control" id="password" name="password" type="password"  required></div>
    <div class="mb-4"><label class="form-label" for="confirm">Repeat password</label>
      <input class="form-control" id="confirm" name="confirm" type="password" required></div>
    <button class="btn btn-primary w-100 py-2" type="submit">Create admin <i class="bi bi-arrow-right"></i></button>
  </form>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"><jsp:param name="mode" value="public"/></jsp:include>