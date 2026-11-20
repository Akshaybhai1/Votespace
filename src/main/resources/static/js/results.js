/* VoteSpace - live results charts (Chart.js). Refreshes every 10 seconds. */
(function () {
  'use strict';

  var PALETTE = ['#4f46e5', '#06b6d4', '#f59e0b', '#ef4444', '#10b981', '#8b5cf6', '#ec4899', '#14b8a6', '#f97316', '#64748b'];
  var barChart = null, donutChart = null;

  function esc(s) {
    return String(s == null ? '' : s).replace(/[&<>"']/g, function (c) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
  }

  function color(c) { return PALETTE[c.id % PALETTE.length]; }

  function setText(data) {
    document.querySelectorAll('[data-result]').forEach(function (el) {
      var key = el.getAttribute('data-result');
      if (data[key] !== undefined) { el.textContent = data[key] + (key === 'turnoutPercent' ? '%' : ''); }
    });
    var stamp = document.getElementById('lastUpdated');
    if (stamp) { stamp.textContent = new Date().toLocaleTimeString(); }
  }

  function drawCharts(data) {
    var barEl = document.getElementById('barChart');
    var donutEl = document.getElementById('donutChart');
    var list = data.candidates;
    var labels = list.map(function (c) { return c.name; });
    var votes = list.map(function (c) { return c.votes; });
    var colors = list.map(color);

    var empty = document.getElementById('noCandidates');
    if (empty) { empty.classList.toggle('d-none', list.length > 0); }
    var noVotes = document.getElementById('noVotes');
    if (noVotes) { noVotes.classList.toggle('d-none', !(list.length > 0 && data.totalVotes === 0)); }

    if (barEl) {
      if (!barChart) {
        barChart = new Chart(barEl, {
          type: 'bar',
          data: { labels: labels, datasets: [{ label: 'Votes', data: votes, backgroundColor: colors, borderRadius: 10, maxBarThickness: 64 }] },
          options: {
            responsive: true, maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true, ticks: { precision: 0 }, grid: { color: '#eef2f7' } }, x: { grid: { display: false } } }
          }
        });
      } else {
        barChart.data.labels = labels;
        barChart.data.datasets[0].data = votes;
        barChart.data.datasets[0].backgroundColor = colors;
        barChart.update();
      }
    }

    if (donutEl) {
      if (!donutChart) {
        donutChart = new Chart(donutEl, {
          type: 'doughnut',
          data: { labels: labels, datasets: [{ data: votes, backgroundColor: colors, borderWidth: 3, borderColor: '#fff' }] },
          options: { responsive: true, maintainAspectRatio: false, cutout: '64%', plugins: { legend: { position: 'bottom' } } }
        });
      } else {
        donutChart.data.labels = labels;
        donutChart.data.datasets[0].data = votes;
        donutChart.data.datasets[0].backgroundColor = colors;
        donutChart.update();
      }
    }
  }

  function drawTable(data) {
    var body = document.getElementById('resultsBody');
    if (!body) { return; }
    body.innerHTML = data.candidates.map(function (c) {
      return '<tr>' +
        '<td class="fw-bold">#' + c.rank + '</td>' +
        '<td><div class="d-flex align-items-center gap-2">' +
          '<img class="avatar avatar-sm" alt="" src="' + esc(window.APP.ctx + c.photoUrl) + '">' +
          '<div><div class="fw-semibold">' + esc(c.name) + '</div><div class="small text-muted">' + esc(c.party) + '</div></div>' +
        '</div></td>' +
        '<td style="min-width:160px"><div class="progress progress-thin"><div class="progress-bar" style="width:' + c.percent + '%"></div></div></td>' +
        '<td class="text-end fw-bold text-nowrap">' + c.votes + ' <span class="text-muted small fw-normal">(' + c.percent + '%)</span></td>' +
      '</tr>';
    }).join('');
  }

  function load() {
    fetch(window.APP.ctx + '/api/results', { headers: { 'Accept': 'application/json' }, credentials: 'same-origin' })
      .then(function (r) { if (!r.ok) { throw new Error('bad response'); } return r.json(); })
      .then(function (data) { setText(data); drawCharts(data); drawTable(data); })
      .catch(function () { /* session expired or server busy - try again next time */ });
  }

  document.addEventListener('DOMContentLoaded', function () {
    if (!document.getElementById('barChart') && !document.getElementById('donutChart') && !document.querySelector('[data-result]')) { return; }
    load();
    setInterval(load, 10000);
  });
})();
