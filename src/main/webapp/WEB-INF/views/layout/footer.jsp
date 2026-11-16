<%@ page contentType="text/html;charset=UTF-8" %>
  </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
(function () {
  var toggle  = document.getElementById('ccHamburger');
  var overlay = document.getElementById('ccOverlay');
  var sidebar = document.querySelector('.cc-sidebar');
  if (!toggle || !sidebar) return;

  function openSidebar() {
    sidebar.classList.add('open');
    overlay.classList.add('open');
  }

  function closeSidebar() {
    sidebar.classList.remove('open');
    overlay.classList.remove('open');
  }

  toggle.addEventListener('click', function () {
    if (sidebar.classList.contains('open')) { closeSidebar(); } else { openSidebar(); }
  });

  overlay.addEventListener('click', closeSidebar);

  sidebar.querySelectorAll('a.nav-link').forEach(function (link) {
    link.addEventListener('click', closeSidebar);
  });
}());

// ── Mobile card-stack tables: copy header text into each cell's data-label ──
(function () {
  document.querySelectorAll('.cc-table').forEach(function (table) {
    var heads = table.querySelectorAll('thead th');
    if (!heads.length) return;
    var labels = Array.prototype.map.call(heads, function (th) {
      return th.textContent.replace(/[▲▼⇅]/g, '').trim(); // strip sort arrows
    });
    table.querySelectorAll('tbody tr').forEach(function (row) {
      Array.prototype.forEach.call(row.children, function (td, i) {
        if (td.hasAttribute('colspan')) return;          // empty-state cell
        td.setAttribute('data-label', labels[i] || '');  // '' => label hidden via CSS
      });
    });
  });
}());

// ── Dismissible / auto-fading toasts ──
(function () {
  function dismiss(el) {
    el.classList.add('cc-toast-hiding');
    setTimeout(function () { el.remove(); }, 300);
  }
  document.querySelectorAll('.cc-toast').forEach(function (toast) {
    var btn = toast.querySelector('.cc-toast-close');
    if (btn) btn.addEventListener('click', function () { dismiss(toast); });
    if (toast.classList.contains('cc-alert-success')) {
      setTimeout(function () { if (toast.isConnected) dismiss(toast); }, 5000);
    }
  });
}());

// ── Submit buttons: busy state to prevent double-submit ──
(function () {
  document.querySelectorAll('form').forEach(function (form) {
    form.addEventListener('submit', function () {
      if (!form.checkValidity || form.checkValidity()) {
        var btn = form.querySelector('button[type=submit], button:not([type])');
        if (btn && !btn.disabled) {
          btn.disabled = true;
          btn.insertAdjacentHTML('afterbegin', '<span class="cc-spinner"></span>');
        }
      }
    });
  });
}());

// ── Bootstrap tooltips (cost bars etc.) ──
(function () {
  if (window.bootstrap && bootstrap.Tooltip) {
    document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(function (el) {
      new bootstrap.Tooltip(el);
    });
  }
}());

// ── Client-side pagination: long tables become page "slides" ──
(function () {
  var PAGE_SIZE = 10;
  document.querySelectorAll('.cc-table').forEach(function (table) {
    var tbody = table.querySelector('tbody');
    if (!tbody) return;
    // real data rows only (skip the empty-state colspan row)
    var rows = Array.prototype.filter.call(tbody.rows, function (r) {
      return !r.querySelector('td[colspan]');
    });
    if (rows.length <= PAGE_SIZE) return;

    var pageCount = Math.ceil(rows.length / PAGE_SIZE);
    var current = 1;

    var pager = document.createElement('div');
    pager.className = 'cc-pager';
    table.parentNode.insertBefore(pager, table.nextSibling);

    function btn(html, opts) {
      opts = opts || {};
      var b = document.createElement('button');
      b.type = 'button';
      b.className = 'cc-pager-btn' + (opts.active ? ' active' : '');
      b.innerHTML = html;
      if (opts.disabled) b.disabled = true;
      if (opts.go) b.addEventListener('click', function () { goTo(opts.go); });
      return b;
    }
    function gap() {
      var s = document.createElement('span');
      s.className = 'cc-pager-gap';
      s.textContent = '…';
      return s;
    }
    function pagesToShow() {
      var set = {};
      [1, pageCount, current, current - 1, current + 1].forEach(function (p) {
        if (p >= 1 && p <= pageCount) set[p] = true;
      });
      return Object.keys(set).map(Number).sort(function (a, b) { return a - b; });
    }
    function renderPager() {
      pager.innerHTML = '';
      pager.appendChild(btn('&lsaquo;', { disabled: current === 1, go: current - 1 }));
      var prev = 0;
      pagesToShow().forEach(function (p) {
        if (p - prev > 1) pager.appendChild(gap());
        pager.appendChild(btn(String(p), { active: p === current, go: p }));
        prev = p;
      });
      pager.appendChild(btn('&rsaquo;', { disabled: current === pageCount, go: current + 1 }));
    }
    function showPage(animate) {
      var start = (current - 1) * PAGE_SIZE, end = start + PAGE_SIZE;
      rows.forEach(function (r, i) {
        r.style.display = (i >= start && i < end) ? '' : 'none';
      });
      if (animate) {
        tbody.classList.remove('cc-paging-anim');
        void tbody.offsetWidth;        // reflow to restart the slide
        tbody.classList.add('cc-paging-anim');
      }
    }
    function goTo(p) {
      if (p < 1 || p > pageCount || p === current) return;
      current = p;
      showPage(true);
      renderPager();
      // scroll the table to the top of the viewport (especially helpful on mobile)
      var card = table.closest('.cc-card') || table;
      card.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }

    showPage(false);   // initial: keep the load-in row stagger
    renderPager();

    // Expose a small controller so live-search can pause/resume pagination.
    table.__ccPager = {
      hide: function () { pager.style.display = 'none'; },
      show: function () { pager.style.display = ''; },
      apply: function () { showPage(false); renderPager(); }
    };
  });
}());

// ── Live (instant) search: filter a table's rows as you type ──
(function () {
  document.querySelectorAll('[data-live-search]').forEach(function (input) {
    var table = document.querySelector(input.getAttribute('data-target'));
    if (!table) return;
    var tbody = table.querySelector('tbody');
    if (!tbody) return;
    var rows = Array.prototype.filter.call(tbody.rows, function (r) {
      return !r.querySelector('td[colspan]');
    });

    function run() {
      var q = input.value.trim().toLowerCase();
      var pager = table.__ccPager;
      if (!q) {                                   // cleared → restore normal view
        if (pager) { pager.show(); pager.apply(); }
        else rows.forEach(function (r) { r.style.display = ''; });
        return;
      }
      if (pager) pager.hide();                    // searching → show all matches, no paging
      rows.forEach(function (r) {
        r.style.display = (r.textContent.toLowerCase().indexOf(q) !== -1) ? '' : 'none';
      });
    }
    input.addEventListener('input', run);
  });
}());
</script>
</body>
</html>
