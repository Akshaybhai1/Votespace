<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
	<jsp:param name="title" value="Voters" />
	<jsp:param name="mode" value="app" />
	<jsp:param name="active" value="voters" />
</jsp:include>

<div class="page-head">
	<div>
		<h1>Voters</h1>
		<p>Register, edit and remove voters. Login details are e-mailed
			automatically.</p>
	</div>
	<a class="btn btn-primary" href="${ctx}/admin/voters/new"><i
		class="bi bi-person-plus"></i> Register voter</a>
</div>

<div class="card-soft">
	<c:choose>
		<c:when test="${empty voters}">
			<div class="empty-state">
				<i class="bi bi-people"></i>
				<p class="mt-2">No voters registered yet.</p>
				<a class="btn btn-primary" href="${ctx}/admin/voters/new">Register
					the first voter</a>
			</div>
		</c:when>
		<c:otherwise>
			<div
				class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
				<div class="search-box w-100">
					<i class="bi bi-search"></i><input class="form-control"
						type="search" placeholder="Search voters..."
						data-filter="#voterRows tr">
				</div>
			</div>
			<div class="table-responsive">
				<table class="table table-modern">
					<thead>
						<tr>
							<th>Voter</th>
							<th>E-mail</th>
							<th>Phone</th>
							<th>Status</th>
							<th class="text-end">Actions</th>
						</tr>
					</thead>
					<tbody id="voterRows">
						<c:forEach var="v" items="${voters}">
							<tr>
								<td><div class="d-flex align-items-center gap-2">
										<img class="avatar avatar-sm" alt="" src="${ctx}${v.photoUrl}"><span
											class="fw-semibold"><c:out value="${v.fullName}" /></span>
									</div></td>
								<td><c:out value="${v.email}" /></td>
								<td><c:out value="${empty v.phone ? '-' : v.phone}" /></td>
								<td><c:choose>
										<c:when test="${votedIds.contains(v.id)}">
											<span class="badge text-bg-success"><i
												class="bi bi-check2"></i> Voted</span>
										</c:when>
										<c:otherwise>
											<span class="badge text-bg-light border">Not voted</span>
										</c:otherwise>
									</c:choose></td>
								<td class="text-end text-nowrap"><a
									class="btn btn-sm btn-outline-primary"
									href="${ctx}/admin/voters/${v.id}/edit" title="Edit"><i
										class="bi bi-pencil-square"></i></a>
									<form class="d-inline" method="post"
										action="${ctx}/admin/voters/${v.id}/make-admin"
										data-confirm="Make this voter an admin? They will no longer have voter access.">
										<input type="hidden" name="${_csrf.parameterName}"
											value="${_csrf.token}">
										<button class="btn btn-sm btn-outline-warning" type="submit"
											title="Make admin">
											<i class="bi bi-shield-lock"></i>
										</button>
									</form>
									<form class="d-inline" method="post"
										action="${ctx}/admin/voters/${v.id}/reset-password"
										data-confirm="Generate a new password and e-mail it to this voter?">
										<input type="hidden" name="${_csrf.parameterName}"
											value="${_csrf.token}">
										<button class="btn btn-sm btn-outline-secondary" type="submit"
											title="Reset password">
											<i class="bi bi-key"></i>
										</button>
									</form>
									<form class="d-inline" method="post"
										action="${ctx}/admin/voters/${v.id}/delete"
										data-confirm="Delete this voter? Their vote (if any) will be removed too.">
										<input type="hidden" name="${_csrf.parameterName}"
											value="${_csrf.token}">
										<button class="btn btn-sm btn-outline-danger" type="submit"
											title="Delete">
											<i class="bi bi-trash"></i>
										</button>
									</form></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</c:otherwise>
	</c:choose>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"><jsp:param
		name="mode" value="app" /></jsp:include>
