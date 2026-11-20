<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="title" value="Election schedule"/>
  <jsp:param name="mode" value="app"/>
  <jsp:param name="active" value="election"/>
</jsp:include>

<div class="page-head">
  <div><h1>Election schedule</h1><p>Voting is only possible between the start and end time you set here.</p></div>
  <span class="badge text-bg-${status.badge} fs-6"><i class="bi ${status.icon}"></i> <c:out value="${status.label}"/></span>
</div>

<div class="row g-4">
  <div class="col-lg-7">
    <div class="card-soft">
      <h2 class="card-title-lg">Date &amp; time</h2>
      <form method="post" action="${ctx}/admin/election">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <div class="mb-3">
          <label class="form-label" for="title">Election title *</label>
          <input class="form-control" id="title" name="title" maxlength="150" required value="${fn:escapeXml(election.title)}">
        </div>
        <div class="row g-3">
          <div class="col-md-6">
            <label class="form-label" for="startTime">Voting starts *</label>
            <input class="form-control" id="startTime" name="startTime" type="datetime-local" required value="${election.startInput}">
          </div>
          <div class="col-md-6">
            <label class="form-label" for="endTime">Voting ends *</label>
            <input class="form-control" id="endTime" name="endTime" type="datetime-local" required value="${election.endInput}">
          </div>
        </div>
        <div class="form-text mt-2">Times use the server time zone: <strong><c:out value="${zone}"/></strong>.</div>
        <div class="form-check mt-3">
          <input class="form-check-input" type="checkbox" id="notifyUsers" name="notifyUsers" value="true">
          <label class="form-check-label" for="notifyUsers">E-mail this schedule to all voters and candidates</label>
        </div>
        <button class="btn btn-primary mt-4" type="submit"><i class="bi bi-check2-circle"></i> Save schedule</button>
      </form>
    </div>
  </div>

  <div class="col-lg-5">
    <div class="card-soft mb-4">
      <h2 class="card-title-lg">Countdown</h2>
      <div class="countdown" data-start="${election.startMillis}" data-end="${election.endMillis}" data-now="${serverNow}">
        <span class="cd-label"></span><span class="cd-time"></span>
      </div>
    </div>
    <div class="card-soft danger-zone">
      <h2 class="card-title-lg text-danger">Danger zone</h2>
      <p class="small text-secondary">Clears every vote so a new round can start. Voters and candidates are kept. This cannot be undone.</p>
      <form method="post" action="${ctx}/admin/election/reset-votes" data-confirm="Delete ALL votes? This cannot be undone.">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <button class="btn btn-outline-danger" type="submit"><i class="bi bi-arrow-counterclockwise"></i> Reset all votes</button>
      </form>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"><jsp:param name="mode" value="app"/></jsp:include>
