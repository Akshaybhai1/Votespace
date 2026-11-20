<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="title" value="Change password"/>
  <jsp:param name="mode" value="app"/>
  <jsp:param name="active" value="password"/>
</jsp:include>

<div class="page-head"><div><h1>Change password</h1><p>Choose a new password that only you know.</p></div></div>

<div class="card-soft" style="max-width:520px">
  <form method="post" action="${ctx}/account/password">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <div class="mb-3"><label class="form-label" for="current">Current password</label>
      <input class="form-control" id="current" name="current" type="password" required></div>
    <div class="mb-3"><label class="form-label" for="newPassword">New password (min 8 characters)</label>
      <input class="form-control" id="newPassword" name="newPassword" type="password"  required></div>
    <div class="mb-4"><label class="form-label" for="confirm">Repeat new password</label>
      <input class="form-control" id="confirm" name="confirm" type="password"  required></div>
    <button class="btn btn-primary" type="submit"><i class="bi bi-check2-circle"></i> Save new password</button>
  </form>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"><jsp:param name="mode" value="app"/></jsp:include>
