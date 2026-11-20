<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="title" value="My ballot"/>
  <jsp:param name="mode" value="app"/>
  <jsp:param name="active" value="dashboard"/>
  <jsp:param name="charts" value="true"/>
</jsp:include>

<div class="hero-banner mb-4">
  <div class="row align-items-center g-3">
    <div class="col-lg-8">
      <span class="status-pill mb-2"><i class="bi ${status.icon}"></i> <c:out value="${status.label}"/></span>
      <h1 class="mt-2 mb-1"><c:out value="${election.title}"/></h1>
      <div class="sub">Hello <strong><c:out value="${currentUser.fullName}"/></strong> - opens <c:out value="${election.startDisplay}"/>, closes <c:out value="${election.endDisplay}"/>.</div>
    </div>
    <div class="col-lg-4 text-lg-end">
      <div class="countdown" data-start="${election.startMillis}" data-end="${election.endMillis}" data-now="${serverNow}">
        <span class="cd-label"></span><span class="cd-time"></span>
      </div>
    </div>
  </div>
</div>

<c:choose>
  <c:when test="${hasVoted}">
    <div class="card-soft mb-4 border-success" style="border-width:2px">
      <div class="d-flex align-items-center gap-3 flex-wrap">
        <div class="stat-icon bg-g3"><i class="bi bi-patch-check-fill"></i></div>
        <div class="flex-grow-1">
          <h2 class="h5 fw-bold mb-1">Your vote is recorded</h2>
          <div class="text-secondary">You voted for <strong><c:out value="${vote.candidate.user.fullName}"/></strong>
            (<c:out value="${empty vote.candidate.party ? 'Independent' : vote.candidate.party}"/>) on <c:out value="${vote.castAtDisplay}"/>. Each voter can vote only once.</div>
        </div>
        <img class="avatar avatar-md" alt="" src="${ctx}${vote.candidate.user.photoUrl}">
      </div>
    </div>
  </c:when>
  <c:when test="${status.name() == 'UPCOMING'}">
    <div class="alert alert-info"><i class="bi bi-hourglass-split"></i> Voting has not started yet. It opens on <strong><c:out value="${election.startDisplay}"/></strong>. This page will unlock automatically.</div>
  </c:when>
  <c:when test="${status.name() == 'ENDED'}">
    <div class="alert alert-secondary"><i class="bi bi-flag-fill"></i> Voting has closed. You did not cast a vote in this election.</div>
  </c:when>
  <c:when test="${status.name() == 'NOT_SCHEDULED'}">
    <div class="alert alert-secondary"><i class="bi bi-calendar-x"></i> The admin has not announced the election date yet.</div>
  </c:when>
</c:choose>

<div class="d-flex justify-content-between align-items-center mb-3">
  <h2 class="h4 fw-bold mb-0"><c:out value="${canVote ? 'Choose your candidate' : 'Candidates'}"/></h2>
  <c:if test="${canVote}"><span class="badge text-bg-warning">You can vote only once</span></c:if>
</div>

<c:choose>
  <c:when test="${empty candidates}">
    <div class="card-soft mb-4"><div class="empty-state"><i class="bi bi-person-badge"></i><p class="mb-0">No candidates have been added yet.</p></div></div>
  </c:when>
  <c:otherwise>
    <div class="row g-4 mb-4">
      <c:forEach var="c" items="${candidates}">
        <div class="col-md-6 col-xl-4">
          <div class="card-soft cand-card ${hasVoted and vote.candidate.id == c.id ? 'picked' : ''}">
            <img class="avatar avatar-lg" alt="" src="${ctx}${c.user.photoUrl}">
            <h3 class="h5 fw-bold mt-3 mb-0"><c:out value="${c.user.fullName}"/></h3>
            <span class="party"><c:out value="${empty c.party ? 'Independent' : c.party}"/></span>
            <div class="manifesto"><c:out value="${empty c.manifesto ? 'No manifesto added yet.' : c.manifesto}"/></div>
            <c:choose>
              <c:when test="${canVote}">
                <button class="btn btn-primary w-100" type="button" data-bs-toggle="modal" data-bs-target="#voteModal"
                        data-id="${c.id}" data-name="<c:out value='${c.user.fullName}'/>" data-photo="${ctx}${c.user.photoUrl}">
                  <i class="bi bi-check2-square"></i> Vote
                </button>
              </c:when>
              <c:when test="${hasVoted and vote.candidate.id == c.id}">
                <span class="badge text-bg-success px-3 py-2 mt-auto"><i class="bi bi-check2-circle"></i> Your choice</span>
              </c:when>
            </c:choose>
          </div>
        </div>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>

<div class="row g-4">
  <div class="col-lg-7">
    <div class="card-soft h-100">
      <div class="d-flex justify-content-between align-items-center mb-2">
        <h2 class="card-title-lg mb-0">Live results</h2>
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
      <div class="d-flex justify-content-around text-center mt-3">
        <div><div class="stat-label">Votes cast</div><div class="fw-bold fs-5" data-result="totalVotes"><c:out value="${summary.totalVotes}"/></div></div>
        <div><div class="stat-label">Turnout</div><div class="fw-bold fs-5"><span data-result="turnoutPercent"><c:out value="${summary.turnoutPercent}"/></span>%</div></div>
      </div>
    </div>
  </div>
</div>

<c:if test="${canVote}">
<div class="modal fade" id="voteModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" method="post" action="${ctx}/voter/vote" style="border-radius:20px">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
      <input type="hidden" name="candidateId" id="voteCandidateId">
      <div class="modal-body text-center p-4">
        <img id="voteCandidatePhoto" class="avatar avatar-lg mb-3" alt="" src="${ctx}/images/avatar.svg">
        <h2 class="h5 fw-bold">Confirm your vote</h2>
        <p class="text-secondary mb-1">You are about to vote for</p>
        <p class="fs-5 fw-bold text-primary" id="voteCandidateName"></p>
        <p class="small text-danger mb-4"><i class="bi bi-exclamation-triangle"></i> You can vote only once and cannot change it later.</p>
        <div class="d-flex gap-2 justify-content-center">
          <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary"><i class="bi bi-check2-circle"></i> Yes, cast my vote</button>
        </div>
      </div>
    </form>
  </div>
</div>
</c:if>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"><jsp:param name="mode" value="app"/></jsp:include>
