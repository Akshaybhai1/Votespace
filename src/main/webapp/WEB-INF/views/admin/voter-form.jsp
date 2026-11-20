<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="title" value="${editing ? 'Edit voter' : 'Register voter'}"/>
  <jsp:param name="mode" value="app"/>
  <jsp:param name="active" value="voters"/>
</jsp:include>

<div class="page-head">
  <div><h1><c:out value="${editing ? 'Edit voter' : 'Register voter'}"/></h1>
    <p><c:out value="${editing ? 'Update the voter details.' : 'A password is generated and e-mailed to the voter.'}"/></p></div>
  <a class="btn btn-outline-primary" href="${ctx}/admin/voters"><i class="bi bi-arrow-left"></i> Back</a>
</div>

<div class="card-soft" style="max-width:760px">
  <form method="post" action="${ctx}/admin/voters/save" enctype="multipart/form-data">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <c:if test="${editing}"><input type="hidden" name="id" value="${person.id}"></c:if>

    <div class="d-flex align-items-center gap-3 mb-4">
      <img id="preview" class="photo-preview" alt="Photo preview" src="${ctx}${person.photoUrl}">
      <div>
        <label class="form-label" for="photo">Photo</label>
        <input class="form-control" id="photo" name="photo" type="file" accept="image/png,image/jpeg,image/gif,image/webp" data-preview="preview">
        <div class="form-text">JPG, PNG, GIF or WEBP, up to 5 MB.</div>
      </div>
    </div>

    <div class="row g-3">
      <div class="col-md-6">
        <label class="form-label" for="fullName">Full name *</label>
        <input class="form-control" id="fullName" name="fullName" maxlength="120" required value="${fn:escapeXml(person.fullName)}">
      </div>
      <div class="col-md-6">
        <label class="form-label" for="email">E-mail * <span class="text-muted fw-normal">(used to sign in)</span></label>
        <input class="form-control" id="email" name="email" type="email" maxlength="150" required value="${fn:escapeXml(person.email)}">
      </div>
      <div class="col-md-6">
        <label class="form-label" for="phone">Phone</label>
        <input class="form-control" id="phone" name="phone" maxlength="20" value="${fn:escapeXml(person.phone)}">
      </div>
    </div>

    <div class="mt-4 d-flex gap-2">
      <button class="btn btn-primary" type="submit"><i class="bi bi-check2-circle"></i> <c:out value="${editing ? 'Save changes' : 'Register & send e-mail'}"/></button>
      <a class="btn btn-light" href="${ctx}/admin/voters">Cancel</a>
    </div>
  </form>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"><jsp:param name="mode" value="app"/></jsp:include>
