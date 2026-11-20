<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="title" value="Candidate dashboard"/>
  <jsp:param name="mode" value="app"/>
  <jsp:param name="active" value="dashboard"/>
  <jsp:param name="charts" value="true"/>
</jsp:include>

<div class="hero-banner mb-4">
  <div class="d-flex align-items-center gap-3 flex-wrap">
    <img class="avatar avatar-lg" alt="" src="${ctx}${candidate.user.photoUrl}">
    <div class="flex-grow-1">
      <span class="status-pill mb-2"><i class="bi ${status.icon}"></i> <c:out value="${status.label}"/></span>
      <h1 class="mb-1"><c:out value="${candidate.user.fullName}"/></h1>
      <div class="sub"><c:out value="${empty candidate.party ? 'Independent candidate' : candidate.party}"/> - <c:out value="${election.title}"/></div>
    </div>
    <div class="countdown" data-start="${election.startMillis}" data-end="${election.endMillis}" data-now="${serverNow}">
      <span class="cd-label"></span><span class="cd-time"></span>
    </div>
  </div>
</div>

<div class="row g-3 mb-4">
  <div class="col-6 col-xl-3"><div class="card-soft stat-card">
    <div class="stat-icon bg-g1"><i class="bi bi-check2-square"></i></div>
    <div><div class="stat-label">My votes</div><div class="stat-value"><c:out value="${empty mine ? 0 : mine.votes}"/></div></div>
  </div></div>
  <div class="col-6 col-xl-3"><div class="card-soft stat-card">
    <div class="stat-icon bg-g2"><i class="bi bi-percent"></i></div>
    <div><div class="stat-label">Vote share</div><div class="stat-value"><c:out value="${empty mine ? 0 : mine.percent}"/>%</div></div>
  </div></div>
  <div class="col-6 col-xl-3"><div class="card-soft stat-card">
    <div class="stat-icon bg-g3"><i class="bi bi-trophy-fill"></i></div>
    <div><div class="stat-label">My rank</div><div class="stat-value">#<c:out value="${empty mine ? '-' : mine.rank}"/> <span class="fs-6 text-muted fw-semibold">of <c:out value="${candidateCount}"/></span></div></div>
  </div></div>
  <div class="col-6 col-xl-3"><div class="card-soft stat-card">
    <div class="stat-icon bg-g4"><i class="bi bi-people-fill"></i></div>
    <div><div class="stat-label">Total votes cast</div><div class="stat-value" data-result="totalVotes"><c:out value="${summary.totalVotes}"/></div></div>
  </div></div>
</div>

<div class="row g-4 mb-4">
  <div class="col-lg-7">
    <div class="card-soft h-100">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <h2 class="card-title-lg mb-0">Live results - all candidates</h2>
        <span class="small text-muted">Updated <span id="lastUpdated">-</span></span>
      </div>
      <div class="chart-box"><canvas id="barChart"></canvas></div>
      <div id="noCandidates" class="d-none text-center text-muted small">No candidates yet.</div>
      <div id="noVotes" class="d-none text-center text-muted small mt-2">No votes have been cast yet.</div>
    </div>
  </div>
  <div class="col-lg-5">
    <div class="card-soft h-100">
      <h2 class="card-title-lg">Vote share</h2>
      <div class="chart-box"><canvas id="donutChart"></canvas></div>
    </div>
  </div>
</div>

<div class="card-soft" style="max-width:860px">
  <h2 class="card-title-lg">My profile</h2>
  <p class="text-secondary small">Voters see this information on their ballot. Your name and e-mail can only be changed by the admin.</p>
  <form method="post" action="${ctx}/candidate/profile" enctype="multipart/form-data">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <div class="d-flex align-items-center gap-3 mb-4">
      <img id="preview" class="photo-preview" alt="Photo preview" src="${ctx}${candidate.user.photoUrl}">
      <div>
        <label class="form-label" for="photo">Change photo</label>
        <input class="form-control" id="photo" name="photo" type="file" accept="image/png,image/jpeg,image/gif,image/webp" data-preview="preview">
      </div>
    </div>
    <div class="row g-3">
      <div class="col-md-6">
        <label class="form-label" for="party">Party / Group</label>
        <input class="form-control" id="party" name="party" maxlength="100" value="${fn:escapeXml(candidate.party)}">
      </div>
      <div class="col-md-6">
        <label class="form-label" for="phone">Phone</label>
        <input class="form-control" id="phone" name="phone" maxlength="20" value="${fn:escapeXml(candidate.user.phone)}">
      </div>
      <div class="col-12">
        <label class="form-label" for="manifesto">Manifesto <span class="text-muted fw-normal">(max 1000 characters)</span></label>
        <textarea class="form-control" id="manifesto" name="manifesto" rows="4" maxlength="1000"><c:out value="${candidate.manifesto}"/></textarea>
      </div>
    </div>
    <button class="btn btn-primary mt-4" type="submit"><i class="bi bi-check2-circle"></i> Save profile</button>
  </form>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"><jsp:param name="mode" value="app"/></jsp:include>
