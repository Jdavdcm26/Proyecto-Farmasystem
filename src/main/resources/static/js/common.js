// ── Utilidades compartidas ────────────────────────────────────────────────

// Stub defensivo: si el CDN de lucide falla (timeout, bloqueo, etc.) evita
// que las llamadas a lucide.createIcons() rompan el resto del script.
window.lucide = window.lucide || { createIcons: () => {} };

// ── Tema (claro/oscuro) ──────────────────────────────────────────────────
// El tema se aplica en el <head> mediante un script inline para evitar
// parpadeo; aquí solo exponemos el toggle.
function toggleTheme() {
  const current = document.documentElement.dataset.theme || 'dark';
  const next = current === 'dark' ? 'light' : 'dark';
  document.documentElement.dataset.theme = next;
  try { localStorage.setItem('theme', next); } catch (_) {}
}

// ── Sidebar desplegable (admin) ──────────────────────────────────────────
function toggleSidebar() {
  const app = document.querySelector('.app');
  const sb  = document.querySelector('.sidebar');
  if (!app || !sb) return;
  const collapsed = app.classList.toggle('sidebar-collapsed');
  sb.classList.toggle('collapsed', collapsed);
  try { localStorage.setItem('sidebar-collapsed', collapsed ? '1' : '0'); } catch (_) {}
}

// Restaurar estado del sidebar al cargar la página
(function applySidebarState() {
  try {
    if (localStorage.getItem('sidebar-collapsed') !== '1') return;
  } catch (_) { return; }
  // Esperar al DOM listo si aún no está.
  const apply = () => {
    document.querySelector('.app')?.classList.add('sidebar-collapsed');
    document.querySelector('.sidebar')?.classList.add('collapsed');
  };
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', apply, { once: true });
  } else {
    apply();
  }
})();

const $ = (s, r = document) => r.querySelector(s);
const $$ = (s, r = document) => Array.from(r.querySelectorAll(s));

const fmtMoney = (n) => new Intl.NumberFormat('es-CO', {
  style: 'currency', currency: 'COP', maximumFractionDigits: 0
}).format(Number(n || 0));

const fmtDate = (d) => {
  if (!d) return '—';
  const dt = new Date(d);
  return dt.toLocaleDateString('es-CO', { year: 'numeric', month: 'short', day: '2-digit' });
};
const fmtDateTime = (d) => {
  if (!d) return '—';
  const dt = new Date(d);
  return dt.toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' });
};

function toast(msg, type = 'success') {
  let host = $('.toast-host');
  if (!host) {
    host = document.createElement('div');
    host.className = 'toast-host';
    document.body.appendChild(host);
  }
  const t = document.createElement('div');
  t.className = `toast ${type}`;
  const icon = type === 'error' ? 'circle-alert' : type === 'warn' ? 'triangle-alert' : 'circle-check';
  t.innerHTML = `<i data-lucide="${icon}"></i><span>${msg}</span>`;
  host.appendChild(t);
  if (window.lucide) lucide.createIcons();
  setTimeout(() => t.remove(), 3500);
}

async function api(url, opts = {}) {
  const res = await fetch(url, {
    headers: { 'Content-Type': 'application/json', ...(opts.headers || {}) },
    ...opts
  });
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  if (!res.ok) {
    const msg = (data && data.error) || 'Error en la petición';
    throw new Error(msg);
  }
  return data;
}

async function requireSession(expectedRole) {
  try {
    const s = await api('/api/auth/verificar-sesion');
    if (!s.autenticado) throw new Error('no-session');
    if (expectedRole && s.rol !== expectedRole) throw new Error('wrong-role');
    return s;
  } catch (e) {
    window.location.href = '/login';
    throw e;
  }
}

async function logout() {
  try { await api('/api/auth/logout', { method: 'POST' }); } catch {}
  window.location.href = '/login';
}

function paintUserChip(session) {
  const chip = $('#userChip');
  if (!chip) return;
  const initials = (session.nombreCompleto || session.username || '?')
    .split(' ').map(s => s[0]).slice(0, 2).join('').toUpperCase();
  chip.innerHTML = `
    <div class="avatar">${initials}</div>
    <div class="who"><b>${session.nombreCompleto || session.username}</b><small>${session.rol}</small></div>
  `;
}

function openModal(id) { $(`#${id}`).classList.add('open'); }
function closeModal(id) { $(`#${id}`).classList.remove('open'); }
