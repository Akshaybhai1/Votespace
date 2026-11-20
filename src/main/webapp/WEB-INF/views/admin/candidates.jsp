<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="title" value="Candidates"/>
  <jsp:param name="mode" value="app"/>
  <jsp:param name="active" value="candidates"/>
</jsp:include>

<div class="page-head">
  <div><h1>Candidates</h1><p>Add, edit and remove the people voters can choose from.</p></div>
  <a class="btn btn-primary" href="${ctx}/admin/candidates/new"><i class="bi bi-person-badge"></i> Add candidate</a>
</div>

<c:choose>
  <c:when test="${empty candidates}">
    <div class="card-soft"><div class="empty-state"><i class="bi bi-person-badge"></i><p class="mt-2">No candidates yet.</p>
      <a class="btn btn-primary" href="${ctx}/admin/candidates/new">Add the first candidate</a></div></div>
  </c:when>
  <c:otherwise>
    <div class="search-box mb-3"><i class="bi bi-search"></i><input class="form-control" type="search" placeholder="Search candidates..." data-filter="#candGrid > div"></div>
    <div class="row g-4" id="candGrid">
      <c:forEach var="c" items="${candidates}">
        <div class="col-md-6 col-xl-4">
          <div class="card-soft cand-card">
            <img class="avatar avatar-lg" alt="" src="${ctx}${c.user.photoUrl}">
            <h3 class="h5 fw-bold mt-3 mb-0"><c:out value="${c.user.fullName}"/></h3>
            <span class="party"><c:out value="${empty c.party ? 'Independent' : c.party}"/></span>
            <div class="small text-muted mb-2"><c:out value="${c.user.email}"/></div>
            <div class="manifesto"><c:out value="${empty c.manifesto ? 'No manifesto added yet.' : c.manifesto}"/></div>
            <div class="mb-3"><span class="badge text-bg-primary"><i class="bi bi-check2-square"></i> <c:out value="${empty voteCounts[c.id] ? 0 : voteCounts[c.id]}"/> votes</span></div>
            <div class="d-flex gap-2 justify-content-center">
              <a class="btn btn-sm btn-outline-primary" href="${ctx}/admin/candidates/${c.id}/edit"><i class="bi bi-pencil-square"></i> Edit</a>
              <form method="post" action="${ctx}/admin/candidates/${c.id}/reset-password" data-confirm="Generate a new password and e-mail it to this candidate?">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                <button class="btn btn-sm btn-outline-secondary" type="submit" title="Reset password"><i class="bi bi-key"></i></button>
              </form>
              <form method="post" action="${ctx}/admin/candidates/${c.id}/delete" data-confirm="Delete this candidate? Votes they received will be removed too.">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                <button class="btn btn-sm btn-outline-danger" type="submit" title="Delete"><i class="bi bi-trash"></i></button>
              </form>
            </div>
          </div>
        </div>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"><jsp:param name="mode" value="app"/></jsp:include>
