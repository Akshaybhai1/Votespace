<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="title" value="Home"/>
  <jsp:param name="mode" value="public"/>
  <jsp:param name="bodyClass" value="landing"/>
</jsp:include>

<section class="landing-hero">
  <div class="container">
    <div class="navbar-lite d-flex justify-content-between align-items-center">
      <a class="brand" href="${ctx}/"><i class="bi bi-check2-square"></i> VoteSpace</a>
      <a class="btn btn-light" href="${ctx}/login"><i class="bi bi-box-arrow-in-right"></i> Sign in</a>
    </div>

    <div class="row align-items-center g-5 mt-3">
      <div class="col-lg-7">
        <span class="badge rounded-pill text-bg-light mb-3 px-3 py-2"><i class="bi bi-shield-lock-fill text-primary"></i> Secure online voting</span>
        <h1>Your voice. <span>One vote.</span> Total transparency.</h1>
        <p class="lead mt-3">VoteSpace lets registered voters choose their candidate online, once, and watch the live results as the election unfolds.</p>
        <div class="d-flex flex-wrap gap-3 mt-4">
          <a class="btn btn-primary btn-lg" href="${ctx}/login">Go to my dashboard <i class="bi bi-arrow-right"></i></a>
        </div>
      </div>
      <div class="col-lg-5">
        <div class="glass">
          <div class="d-flex justify-content-between align-items-start mb-3">
            <div>
              <div class="small text-uppercase opacity-75 fw-semibold">Current election</div>
              <h2 class="h4 fw-bold mb-0"><c:out value="${election.title}"/></h2>
            </div>
            <span class="status-pill"><i class="bi ${status.icon}"></i> <c:out value="${status.label}"/></span>
          </div>
          <div class="mb-3">
            <div class="small opacity-75">Opens</div><div class="fw-semibold"><c:out value="${election.startDisplay}"/></div>
            <div class="small opacity-75 mt-2">Closes</div><div class="fw-semibold"><c:out value="${election.endDisplay}"/></div>
          </div>
          <div class="countdown" data-start="${election.startMillis}" data-end="${election.endMillis}" data-now="${serverNow}">
            <span class="cd-label"></span><span class="cd-time"></span>
          </div>
        </div>
      </div>
    </div>
  </div>
</section>

<section class="landing-section">
  <div class="container">
    <div class="text-center mb-5">
      <h2 class="fw-bold">Built for fair elections</h2>
      <p class="text-secondary">Everything the admin, candidates and voters need - in one place.</p>
    </div>
    <div class="row g-4">
      <div class="col-md-6 col-lg-3"><div class="feature"><div class="ico bg-g1"><i class="bi bi-lock-fill"></i></div><h3 class="h6 fw-bold">Login required</h3><p class="text-secondary small mb-0">Powered by Spring Security. Every role gets its own protected dashboard.</p></div></div>
      <div class="col-md-6 col-lg-3"><div class="feature"><div class="ico bg-g3"><i class="bi bi-1-circle-fill"></i></div><h3 class="h6 fw-bold">One person, one vote</h3><p class="text-secondary small mb-0">The system blocks double voting, even if the button is clicked twice.</p></div></div>
      <div class="col-md-6 col-lg-3"><div class="feature"><div class="ico bg-g2"><i class="bi bi-envelope-check-fill"></i></div><h3 class="h6 fw-bold">Login by e-mail</h3><p class="text-secondary small mb-0">New voters and candidates receive their login details by e-mail.</p></div></div>
      <div class="col-md-6 col-lg-3"><div class="feature"><div class="ico bg-g4"><i class="bi bi-bar-chart-fill"></i></div><h3 class="h6 fw-bold">Live graphs</h3><p class="text-secondary small mb-0">Everyone can follow the results in real time with clear charts.</p></div></div>
    </div>
  </div>
</section>

<footer class="landing-foot">&copy; VoteSpace - Spring Boot voting application</footer>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"><jsp:param name="mode" value="public"/></jsp:include>
