<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="title" value="Admin dashboard"/>
  <jsp:param name="mode" value="app"/>
  <jsp:param name="active" value="dashboard"/>
  <jsp:param name="charts" value="true"/>
</jsp:include>

<div class="page-head">
  <div>
    <h1>Admin dashboard</h1>
    <p>Welcome back, <c:out value="${currentUser.fullName}"/>. Here is the state of the election.</p>
  </div>
  <div class="d-flex gap-2 flex-wrap">
    <a class="btn btn-outline-primary" href="${ctx}/admin/voters/new"><i class="bi bi-person-plus"></i> Register voter</a>
    <a class="btn btn-primary" href="${ctx}/admin/candidates/new"><i class="bi bi-person-badge"></i> Add candidate</a>
  </div>
</div>

<div class="row g-3 mb-4">
  <div class="col-6 col-xl-3"><div class="card-soft stat-card">
    <div class="stat-icon bg-g1"><i class="bi bi-people-fill"></i></div>
    <div><div class="stat-label">Registered voters</div><div class="stat-value" data-result="totalVoters"><c:out value="${summary.totalVoters}"/></div></div>
  </div></div>
  <div class="col-6 col-xl-3"><div class="card-soft stat-card">
    <div class="stat-icon bg-g2"><i class="bi bi-person-badge-fill"></i></div>
    <div><div class="stat-label">Candidates</div><div class="stat-value"><c:out value="${candidateCount}"/></div></div>
  </div></div>
  <div class="col-6 col-xl-3"><div class="card-soft stat-card">
    <div class="stat-icon bg-g3"><i class="bi bi-check2-all"></i></div>
    <div><div class="stat-label">Votes cast</div><div class="stat-value" data-result="totalVotes"><c:out value="${summary.totalVotes}"/></div></div>
  </div></div>
  <div class="col-6 col-xl-3"><div class="card-soft stat-card">
    <div class="stat-icon bg-g4"><i class="bi bi-pie-chart-fill"></i></div>
    <div><div class="stat-label">Turnout</div><div class="stat-value"><span data-result="turnoutPercent"><c:out value="${summary.turnoutPercent}"/></span>%</div></div>
  </div></div>
</div>

<div class="row g-4 mb-4">
  <div class="col-lg-8">
    <div class="card-soft h-100">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <h2 class="card-title-lg mb-0">Live results</h2>
        <span class="small text-muted">Updated <span id="lastUpdated">-</span></span>
      </div>
      <div class="chart-box"><canvas id="barChart"></canvas></div>
      <div id="noCandidates" class="empty-state d-none"><i class="bi bi-person-plus"></i><p class="mb-0">Add candidates to see the graph.</p></div>
      <div id="noVotes" class="text-center text-muted small d-none">No votes have been cast yet.</div>
    </div>
  </div>
  <div class="col-lg-4">
    <div class="card-soft h-100">
      <div class="d-flex justify-content-between align-items-start mb-3">
        <h2 class="card-title-lg mb-0">Election</h2>
        <span class="badge text-bg-${status.badge}"><i class="bi ${status.icon}"></i> <c:out value="${status.label}"/></span>
      </div>
      <div class="fw-bold fs-5 mb-3"><c:out value="${election.title}"/></div>
      <div class="small text-muted">Opens</div><div class="fw-semibold mb-2"><c:out value="${election.startDisplay}"/></div>
      <div class="small text-muted">Closes</div><div class="fw-semibold mb-3"><c:out value="${election.endDisplay}"/></div>
      <div class="countdown mb-4" data-start="${election.startMillis}" data-end="${election.endMillis}" data-now="${serverNow}">
        <span class="cd-label"></span><span class="cd-time"></span>
      </div>
      <a class="btn btn-primary w-100" href="${ctx}/admin/election"><i class="bi bi-calendar-event"></i> Set date &amp; time</a>
    </div>
  </div>
</div>

<div class="card-soft">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h2 class="card-title-lg mb-0">Recently registered voters</h2>
    <a class="btn btn-sm btn-outline-primary" href="${ctx}/admin/voters">View all</a>
  </div>
  <c:choose>
    <c:when test="${empty recentVoters}">
      <div class="empty-state"><i class="bi bi-people"></i><p class="mb-0">No voters yet. Register your first voter.</p></div>
    </c:when>
    <c:otherwise>
      <div class="table-responsive">
        <table class="table table-modern">
          <thead><tr><th>Voter</th><th>E-mail</th><th>Registered</th></tr></thead>
          <tbody>
          <c:forEach var="v" items="${recentVoters}">
            <tr>
              <td><div class="d-flex align-items-center gap-2"><img class="avatar avatar-sm" alt="" src="${ctx}${v.photoUrl}"><span class="fw-semibold"><c:out value="${v.fullName}"/></span></div></td>
              <td><c:out value="${v.email}"/></td>
              <td class="text-muted small"><c:out value="${v.createdAt.toLocalDate()}"/></td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
    </c:otherwise>
  </c:choose>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"><jsp:param name="mode" value="app"/></jsp:include>
