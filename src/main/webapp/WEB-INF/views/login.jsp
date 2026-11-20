<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="title" value="Sign in"/>
  <jsp:param name="mode" value="public"/>
  <jsp:param name="bodyClass" value="auth"/>
</jsp:include>

<div class="auth-card">
  <div class="auth-side">
    <a class="text-white text-decoration-none fw-bold fs-5" href="${ctx}/"><i class="bi bi-check2-square"></i> VoteSpace</a>
    <div>
      <h2>Welcome back</h2>
      <p class="opacity-75">Sign in to reach your dashboard.</p>
      <ul>
        <li><i class="bi bi-shield-check"></i> Admins manage voters, candidates and the election</li>
        <li><i class="bi bi-person-badge"></i> Candidates follow their votes</li>
        <li><i class="bi bi-check2-circle"></i> Voters cast one secure vote</li>
      </ul>
    </div>
    <div class="small opacity-75">Your login details were sent to your e-mail by the admin.</div>
  </div>

  <div class="auth-form">
    <h1 class="h3 fw-bold">Sign in</h1>
    <p class="text-secondary mb-4">Use your e-mail and password.</p>

    <c:if test="${param.error != null}"><div class="alert alert-danger py-2"><i class="bi bi-x-octagon-fill"></i> Wrong e-mail or password.</div></c:if>
    <c:if test="${param.logout != null}"><div class="alert alert-success py-2"><i class="bi bi-check-circle-fill"></i> You have been signed out.</div></c:if>

    <form action="${ctx}/login" method="post" autocomplete="on">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
      <div class="mb-3">
        <label class="form-label" for="username">E-mail</label>
        <input class="form-control" id="username" name="username" type="email" placeholder="you@example.com" required autofocus>
      </div>
      <div class="mb-4">
        <label class="form-label" for="password">Password</label>
        <input class="form-control" id="password" name="password" type="password" placeholder="Your password" required>
      </div>
      <button class="btn btn-primary w-100 py-2" type="submit">Sign in <i class="bi bi-arrow-right"></i></button>
    </form>
    <p class="text-center small text-secondary mt-4 mb-0">Forgot your password? Ask the election admin to reset it.</p>
  </div>
  <p class="text-center small mt-2 mb-0">
  First time using VoteSpace?
  <a href="${ctx}/setup">Create the first admin account</a>
</p>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"><jsp:param name="mode" value="public"/></jsp:include>
