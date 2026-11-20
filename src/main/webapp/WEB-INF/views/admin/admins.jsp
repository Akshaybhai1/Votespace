<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="title" value="Admins"/>
  <jsp:param name="mode" value="app"/>
  <jsp:param name="active" value="admins"/>
</jsp:include>

<div class="page-head"><div><h1>Admins</h1><p>People who can manage the whole election. Add a new admin by e-mail; a password is generated and e-mailed.</p></div></div>

<div class="row g-4">
  <div class="col-lg-7">
    <div class="card-soft">
      <h2 class="card-title-lg">Current admins</h2>
      <div class="table-responsive">
        <table class="table table-modern">
          <thead><tr><th>Admin</th><th>User ID (e-mail)</th><th class="text-end">Action</th></tr></thead>
          <tbody>
          <c:forEach var="a" items="${admins}">
            <tr>
              <td><div class="d-flex align-items-center gap-2"><img class="avatar avatar-sm" alt="" src="${ctx}${a.photoUrl}">
                <span class="fw-semibold"><c:out value="${a.fullName}"/></span>
                <c:if test="${a.id == currentUser.id}"><span class="badge text-bg-primary">You</span></c:if></div></td>
              <td><c:out value="${a.email}"/></td>
              <td class="text-end">
                <c:choose>
                  <c:when test="${a.id == currentUser.id}"><span class="text-muted small">-</span></c:when>
                  <c:otherwise>
                    <form class="d-inline" method="post" action="${ctx}/admin/admins/${a.id}/delete" data-confirm="Remove this admin? They will no longer be able to sign in.">
                      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                      <button class="btn btn-sm btn-outline-danger" type="submit"><i class="bi bi-trash"></i> Remove</button>
                    </form>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
  </div>

  <div class="col-lg-5">
    <div class="card-soft">
      <h2 class="card-title-lg">Add a new admin</h2>
      <form method="post" action="${ctx}/admin/admins/save">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <div class="mb-3"><label class="form-label" for="fullName">Full name *</label>
          <input class="form-control" id="fullName" name="fullName" maxlength="120" required></div>
        <div class="mb-3"><label class="form-label" for="email">E-mail * <span class="text-muted fw-normal">(their user ID)</span></label>
          <input class="form-control" id="email" name="email" type="email" maxlength="150" required></div>
        <p class="small text-secondary">A random password is e-mailed to them. They should change it with <strong>Change password</strong> after the first login.</p>
        <button class="btn btn-primary" type="submit"><i class="bi bi-person-plus"></i> Add admin</button>
      </form>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"><jsp:param name="mode" value="app"/></jsp:include>
