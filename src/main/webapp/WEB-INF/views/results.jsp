<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="title" value="Live results"/>
  <jsp:param name="mode" value="app"/>
  <jsp:param name="active" value="results"/>
  <jsp:param name="charts" value="true"/>
</jsp:include>

<div class="page-head">
  <div><h1>Live results</h1><p><c:out value="${election.title}"/> - refreshes automatically every 10 seconds. Last update: <span id="lastUpdated">-</span></p></div>
  <span class="badge text-bg-${status.badge} fs-6"><i class="bi ${status.icon}"></i> <c:out value="${status.label}"/></span>
</div>

<div class="row g-3 mb-4">
  <div class="col-md-4"><div class="card-soft stat-card">
    <div class="stat-icon bg-g3"><i class="bi bi-check2-all"></i></div>
    <div><div class="stat-label">Votes cast</div><div class="stat-value" data-result="totalVotes"><c:out value="${summary.totalVotes}"/></div></div>
  </div></div>
  <div class="col-md-4"><div class="card-soft stat-card">
    <div class="stat-icon bg-g1"><i class="bi bi-people-fill"></i></div>
    <div><div class="stat-label">Registered voters</div><div class="stat-value" data-result="totalVoters"><c:out value="${summary.totalVoters}"/></div></div>
  </div></div>
  <div class="col-md-4"><div class="card-soft stat-card">
    <div class="stat-icon bg-g4"><i class="bi bi-pie-chart-fill"></i></div>
    <div><div class="stat-label">Turnout</div><div class="stat-value"><span data-result="turnoutPercent"><c:out value="${summary.turnoutPercent}"/></span>%</div></div>
  </div></div>
</div>

<div class="row g-4 mb-4">
  <div class="col-lg-7"><div class="card-soft h-100">
    <h2 class="card-title-lg">Votes per candidate</h2>
    <div class="chart-box"><canvas id="barChart"></canvas></div>
    <div id="noCandidates" class="d-none text-center text-muted small">No candidates yet.</div>
    <div id="noVotes" class="d-none text-center text-muted small mt-2">No votes have been cast yet.</div>
  </div></div>
  <div class="col-lg-5"><div class="card-soft h-100">
    <h2 class="card-title-lg">Vote share</h2>
    <div class="chart-box"><canvas id="donutChart"></canvas></div>
  </div></div>
</div>

<div class="card-soft">
  <h2 class="card-title-lg">Leaderboard</h2>
  <div class="table-responsive">
    <table class="table table-modern">
      <thead><tr><th>Rank</th><th>Candidate</th><th>Share</th><th class="text-end">Votes</th></tr></thead>
      <tbody id="resultsBody"><tr><td colspan="4" class="text-center text-muted py-4">Loading...</td></tr></tbody>
    </table>
  </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"><jsp:param name="mode" value="app"/></jsp:include>
