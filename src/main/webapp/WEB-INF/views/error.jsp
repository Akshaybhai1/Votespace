<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/layout/header.jsp">
  <jsp:param name="title" value="Something went wrong"/>
  <jsp:param name="mode" value="public"/>
  <jsp:param name="bodyClass" value="auth"/>
</jsp:include>

<div class="card-soft text-center" style="max-width:520px;width:100%;padding:44px 34px">
  <div class="display-3 fw-bold text-primary"><c:out value="${empty status ? 'Oops' : status}"/></div>
  <h1 class="h4 fw-bold mt-2">
    <c:choose>
      <c:when test="${status == 403}">You are not allowed to open this page</c:when>
      <c:when test="${status == 404}">Page not found</c:when>
      <c:otherwise>Something went wrong</c:otherwise>
    </c:choose>
  </h1>
  <p class="text-secondary">Please go back to your dashboard and try again.</p>
  <a class="btn btn-primary" href="${ctx}/dashboard"><i class="bi bi-house"></i> Back to dashboard</a>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"><jsp:param name="mode" value="public"/></jsp:include>
