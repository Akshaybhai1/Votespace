/* VoteSpace - small UI helpers (no framework needed) */
(function () {
  'use strict';

  function pad(n) { return String(n).padStart(2, '0'); }

  function initSidebar() {
    var toggle = document.getElementById('sidebarToggle');
    var sidebar = document.getElementById('sidebar');
    var backdrop = document.getElementById('sidebarBackdrop');
    if (!toggle || !sidebar) { return; }
    function close() { sidebar.classList.remove('open'); backdrop.classList.remove('show'); }
    toggle.addEventListener('click', function () {
      var open = sidebar.classList.toggle('open');
      backdrop.classList.toggle('show', open);
    });
    backdrop.addEventListener('click', close);
  }

  function initConfirmForms() {
    document.querySelectorAll('form[data-confirm]').forEach(function (form) {
      form.addEventListener('submit', function (e) {
        if (!window.confirm(form.getAttribute('data-confirm'))) { e.preventDefault(); }
      });
    });
  }

  function initPhotoPreview() {
    document.querySelectorAll('input[type=file][data-preview]').forEach(function (input) {
      var img = document.getElementById(input.getAttribute('data-preview'));
      input.addEventListener('change', function () {
        var file = input.files && input.files[0];
        if (!file) { return; }
        if (file.size > 5 * 1024 * 1024) {
          window.alert('This image is larger than 5 MB. Please choose a smaller one.');
          input.value = '';
          return;
        }
        if (img) { img.src = URL.createObjectURL(file); }
      });
    });
  }

  function initLiveFilter() {
    document.querySelectorAll('input[data-filter]').forEach(function (input) {
      var items = document.querySelectorAll(input.getAttribute('data-filter'));
      input.addEventListener('input', function () {
        var q = input.value.trim().toLowerCase();
        items.forEach(function (el) {
          el.style.display = el.textContent.toLowerCase().indexOf(q) === -1 ? 'none' : '';
        });
      });
    });
  }

  function initVoteModal() {
    var modal = document.getElementById('voteModal');
    if (!modal) { return; }
    modal.addEventListener('show.bs.modal', function (event) {
      var btn = event.relatedTarget;
      if (!btn) { return; }
      document.getElementById('voteCandidateId').value = btn.getAttribute('data-id');
      document.getElementById('voteCandidateName').textContent = btn.getAttribute('data-name');
      var img = document.getElementById('voteCandidatePhoto');
      if (img) { img.src = btn.getAttribute('data-photo'); }
    });
  }

  function initCountdowns() {
    document.querySelectorAll('.countdown').forEach(function (el) {
      var start = Number(el.getAttribute('data-start')) || 0;
      var end = Number(el.getAttribute('data-end')) || 0;
      var offset = (Number(el.getAttribute('data-now')) || Date.now()) - Date.now();
      var label = el.querySelector('.cd-label');
      var time = el.querySelector('.cd-time');
      var reloading = false;

      if (!start || !end) {
        label.textContent = 'Schedule not announced yet';
        time.textContent = '';
        return;
      }

      function tick() {
        var now = Date.now() + offset;
        var target;
        if (now < start) { label.textContent = 'Voting opens in'; target = start; }
        else if (now < end) { label.textContent = 'Voting closes in'; target = end; }
        else { label.textContent = 'Voting has closed'; time.textContent = ''; return true; }

        var ms = target - now;
        var s = Math.max(0, Math.ceil(ms / 1000));
        var d = Math.floor(s / 86400), h = Math.floor((s % 86400) / 3600), m = Math.floor((s % 3600) / 60), sec = s % 60;
        time.textContent = (d > 0 ? d + 'd ' : '') + pad(h) + 'h ' + pad(m) + 'm ' + pad(sec) + 's';
        if (ms <= 0 && !reloading) { reloading = true; setTimeout(function () { window.location.reload(); }, 800); }
        return false;
      }

      if (!tick()) {
        var timer = setInterval(function () { if (tick()) { clearInterval(timer); } }, 1000);
      }
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    initSidebar();
    initConfirmForms();
    initPhotoPreview();
    initLiveFilter();
    initVoteModal();
    initCountdowns();
  });
})();
