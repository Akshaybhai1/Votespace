<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" scope="request"/>
<c:set var="isApp" value="${param.mode == 'app'}" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><c:out value="${empty param.title ? 'VoteSpace' : param.title}"/> | VoteSpace</title>
<link rel="icon" href="${ctx}/images/avatar.svg">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
<link href="${ctx}/css/style.css" rel="stylesheet">
<script>window.APP = { ctx: '<c:out value="${ctx}"/>' };</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<c:if test="${param.charts == 'true'}">
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.3/dist/chart.umd.min.js"></script>
<script src="${ctx}/js/results.js"></script>
</c:if>
<script src="${ctx}/js/app.js"></script>
</head>
<body class="${isApp ? 'app-body' : param.bodyClass}">
<c:if test="${isApp}">
<div class="sidebar-backdrop" id="sidebarBackdrop"></div>
<aside class="sidebar" id="sidebar">
  <a class="brand" href="${ctx}/dashboard"><i class="bi bi-check2-square"></i><span>VoteSpace</span></a>

  <nav class="nav flex-column gap-1">
    <div class="nav-label">Menu</div>
    <c:choose>
      <c:when test="${currentUser.admin}">
        <a class="nav-link ${param.active == 'dashboard' ? 'active' : ''}" href="${ctx}/admin/dashboard"><i class="bi bi-speedometer2"></i> Dashboard</a>
        <a class="nav-link ${param.active == 'voters' ? 'active' : ''}" href="${ctx}/admin/voters"><i class="bi bi-people"></i> Voters</a>
        <a class="nav-link ${param.active == 'candidates' ? 'active' : ''}" href="${ctx}/admin/candidates"><i class="bi bi-person-badge"></i> Candidates</a>
        <a class="nav-link ${param.active == 'election' ? 'active' : ''}" href="${ctx}/admin/election"><i class="bi bi-calendar-event"></i> Election schedule</a>
      </c:when>
      <c:when test="${currentUser.candidate}">
        <a class="nav-link ${param.active == 'dashboard' ? 'active' : ''}" href="${ctx}/candidate/dashboard"><i class="bi bi-person-circle"></i> My dashboard</a>
      </c:when>
      <c:otherwise>
        <a class="nav-link ${param.active == 'dashboard' ? 'active' : ''}" href="${ctx}/voter/dashboard"><i class="bi bi-ui-checks-grid"></i> My ballot</a>
      </c:otherwise>
    </c:choose>
    <a class="nav-link ${param.active == 'results' ? 'active' : ''}" href="${ctx}/results"><i class="bi bi-bar-chart-line"></i> Live results</a>
  </nav>

  <div class="sidebar-user">
    <div class="d-flex align-items-center gap-2 mb-3">
      <img class="avatar avatar-sm" alt="" src="${ctx}${currentUser.photoUrl}">
      <div class="overflow-hidden">
        <div class="name text-truncate"><c:out value="${currentUser.fullName}"/></div>
        <div class="role"><c:out value="${currentUser.role}"/></div>
      </div>
    </div>
    <form action="${ctx}/logout" method="post">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
      <button class="btn btn-sm btn-light w-100" type="submit"><i class="bi bi-box-arrow-right"></i> Sign out</button>
    </form>
  </div>
</aside>

<div class="app-content">
  <header class="topbar">
    <button class="btn btn-outline-primary" id="sidebarToggle" type="button" aria-label="Menu"><i class="bi bi-list"></i></button>
    <strong><c:out value="${param.title}"/></strong>
  </header>
  <main class="app-main">
    <c:if test="${not empty success}"><div class="alert alert-success d-flex gap-2"><i class="bi bi-check-circle-fill"></i><div><c:out value="${success}"/></div></div></c:if>
    <c:if test="${not empty warning}"><div class="alert alert-warning d-flex gap-2"><i class="bi bi-exclamation-triangle-fill"></i><div><c:out value="${warning}"/></div></div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger d-flex gap-2"><i class="bi bi-x-octagon-fill"></i><div><c:out value="${error}"/></div></div></c:if>
</c:if>
