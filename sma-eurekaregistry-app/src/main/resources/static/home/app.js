// DOM Elements
const frame = document.getElementById('contentFrame');
const titleEl = document.getElementById('frameTitle');
const subEl = document.getElementById('frameSubtitle');
const openNewTabBtn = document.getElementById('openNewTabBtn');
const reloadBtn = document.getElementById('reloadBtn');
const navLinks = Array.from(document.querySelectorAll('.navlink'));

// =====================================================
// Logs Modal Setup
// =====================================================

// Create logs modal in main document
const logsModal = document.createElement('div');
logsModal.id = 'logsModal';
logsModal.style.cssText = `
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,.6);
  display: none;
  z-index: 9999;
  align-items: center;
  justify-content: center;
  padding: 20px;
`;
logsModal.innerHTML = `
  <div style="
    width: 90%; max-width: 900px; height: 80vh;
    background: #0b1020;
    border: 2px solid rgba(99,102,241,.5);
    border-radius: 12px;
    display: flex;
    flex-direction: column;
    box-shadow: 0 20px 60px rgba(0,0,0,.6);
  ">
    <!-- Header -->
    <div style="
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px;
      border-bottom: 1px solid rgba(99,102,241,.3);
      background: rgba(99,102,241,.08);
    ">
      <div style="display: flex; gap: 8px; align-items: center;">
        <span style="font-weight: 700; color: rgba(255,255,255,.92);">📋 Container App Logs</span>
        <span id="logsPodName" style="font-size: 12px; color: rgba(255,255,255,.58);">pod</span>
      </div>
      <div style="display: flex; gap: 8px;">
        <button id="logsRefreshBtn" style="
          padding: 6px 12px;
          border: 1px solid rgba(99,102,241,.3);
          background: rgba(99,102,241,.12);
          color: rgba(255,255,255,.92);
          border-radius: 6px;
          cursor: pointer;
          font-size: 12px;
          font-weight: 600;
        ">↻ Refresh</button>
        <button id="logsDownloadBtn" style="
          padding: 6px 12px;
          border: 1px solid rgba(99,102,241,.3);
          background: rgba(99,102,241,.12);
          color: rgba(255,255,255,.92);
          border-radius: 6px;
          cursor: pointer;
          font-size: 12px;
          font-weight: 600;
        ">⬇ Download</button>
        <button id="logsCloseBtn" style="
          padding: 6px 12px;
          border: 1px solid rgba(255,0,0,.3);
          background: rgba(255,0,0,.12);
          color: rgba(255,255,255,.92);
          border-radius: 6px;
          cursor: pointer;
          font-size: 12px;
          font-weight: 600;
        ">✕ Close</button>
      </div>
    </div>
    
    <!-- Logs Container -->
    <div id="logsContainer" style="
      flex: 1;
      overflow: auto;
      background: #0b1020;
      padding: 16px;
      font-family: 'Courier New', monospace;
      font-size: 12px;
      line-height: 1.6;
      color: rgba(16,185,129,.9);
      text-shadow: 0 0 10px rgba(16,185,129,.3);
    ">
      <div style="color: rgba(255,255,255,.58);">Loading logs...</div>
    </div>
    
    <!-- Status bar -->
    <div style="
      padding: 12px 16px;
      border-top: 1px solid rgba(99,102,241,.3);
      background: rgba(99,102,241,.08);
      font-size: 11px;
      color: rgba(255,255,255,.58);
      display: flex;
      justify-content: space-between;
    ">
      <span id="logsStatus">Ready</span>
      <span id="logsLineCount">Lines: 0</span>
    </div>
  </div>
`;
document.body.appendChild(logsModal);

// Logs modal event listeners
document.getElementById('logsCloseBtn').addEventListener('click', () => {
  logsModal.style.display = 'none';
});

document.getElementById('logsRefreshBtn').addEventListener('click', async () => {
  const podName = document.getElementById('logsPodName').textContent;
  await fetchAndDisplayLogs(podName);
});

document.getElementById('logsDownloadBtn').addEventListener('click', () => {
  const logsContainer = document.getElementById('logsContainer');
  const logsText = logsContainer.textContent;
  const element = document.createElement('a');
  element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(logsText));
  element.setAttribute('download', `logs-${Date.now()}.txt`);
  element.style.display = 'none';
  document.body.appendChild(element);
  element.click();
  document.body.removeChild(element);
});

logsModal.addEventListener('click', (e) => {
  if (e.target === logsModal) {
    logsModal.style.display = 'none';
  }
});

// Embedded CSS for srcdoc iframes
const PAGE_CSS = `:root { --bg: #0b1020; --border: rgba(255, 255, 255, 0.12); --text: rgba(255, 255, 255, 0.92); --muted: rgba(255, 255, 255, 0.72); --muted-2: rgba(255, 255, 255, 0.58); --shadow: 0 12px 40px rgba(0, 0, 0, 0.35); --ring: 0 0 0 3px rgba(99, 102, 241, 0.35); --radius: 16px; --font: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Arial, "Apple Color Emoji", "Segoe UI Emoji"; } * { box-sizing: border-box; } html, body { height: 100%; } body { margin: 0; font-family: var(--font); color: var(--text); background: radial-gradient(1100px 600px at 20% -10%, rgba(99, 102, 241, 0.22), transparent 60%), radial-gradient(900px 500px at 90% 10%, rgba(16, 185, 129, 0.14), transparent 55%), radial-gradient(900px 500px at 40% 110%, rgba(244, 114, 182, 0.12), transparent 55%), var(--bg); } .homeWrap { height: 100%; display: grid; place-items: center; padding: 28px; background: radial-gradient(800px 500px at 30% 10%, rgba(99, 102, 241, 0.2), transparent 60%), radial-gradient(600px 420px at 85% 60%, rgba(16, 185, 129, 0.14), transparent 60%), #0b1020; color: rgba(255, 255, 255, 0.92); font-family: var(--font); } .homeCard { width: min(860px, 100%); border: 1px solid rgba(255, 255, 255, 0.12); border-radius: 18px; background: rgba(255, 255, 255, 0.05); box-shadow: 0 12px 40px rgba(0, 0, 0, 0.35); backdrop-filter: blur(10px); padding: 26px 24px; overflow: hidden; position: relative; } .homeTitle { margin: 0; font-size: 22px; letter-spacing: 0.2px; } .homeSub { margin: 8px 0 0 0; color: rgba(255, 255, 255, 0.72); font-size: 13px; line-height: 1.5; } .pulley-system { margin-top: 30px; display: flex; align-items: center; justify-content: space-around; gap: 40px; position: relative; height: 140px; } .pulley-group { display: flex; flex-direction: column; align-items: center; gap: 16px; } .pulley-container { position: relative; width: 80px; height: 80px; display: flex; align-items: center; justify-content: center; } .pulley-outer { position: absolute; width: 80px; height: 80px; border: 3px solid rgba(99, 102, 241, 0.6); border-radius: 50%; background: radial-gradient(circle at 30% 30%, rgba(99, 102, 241, 0.2), rgba(99, 102, 241, 0.05)); box-shadow: 0 0 20px rgba(99, 102, 241, 0.3), inset 0 0 20px rgba(99, 102, 241, 0.1); animation: spinPulley 3s linear infinite; } .pulley-outer::before { content: ""; position: absolute; inset: 12px; border: 2px solid rgba(255, 255, 255, 0.2); border-radius: 50%; } .pulley-outer::after { content: ""; position: absolute; inset: 20px; border-radius: 50%; background: repeating-conic-gradient(from 0deg, rgba(99, 102, 241, 0.3) 0deg 6deg, transparent 6deg 12deg); opacity: 0.6; } .pulley-inner { position: absolute; width: 50px; height: 50px; border: 2px solid rgba(16, 185, 129, 0.5); border-radius: 50%; background: radial-gradient(circle, rgba(16, 185, 129, 0.1), transparent); } .pulley-center { position: absolute; width: 16px; height: 16px; border-radius: 50%; background: linear-gradient(135deg, rgba(244, 114, 182, 0.8), rgba(99, 102, 241, 0.8)); box-shadow: 0 0 12px rgba(244, 114, 182, 0.6), inset 0 0 8px rgba(255, 255, 255, 0.3); z-index: 2; } .pulley-label { font-size: 12px; color: rgba(255, 255, 255, 0.6); letter-spacing: 1px; text-transform: uppercase; } .belt-system { flex: 1; max-width: 200px; height: 60px; display: flex; align-items: center; position: relative; } .belt-svg { width: 100%; height: 100%; filter: drop-shadow(0 0 10px rgba(99, 102, 241, 0.2)); } .status-indicator { display: flex; align-items: center; gap: 10px; margin-top: 20px; justify-content: center; color: rgba(255, 255, 255, 0.7); font-size: 12px; } .pulse-dot { width: 10px; height: 10px; border-radius: 50%; background: rgba(16, 185, 129, 0.9); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.4); animation: pulseGlow 2s ease-out infinite; } .status-text { letter-spacing: 0.5px; } @keyframes spinPulley { from { transform: rotate(0deg); } to { transform: rotate(360deg); } } @keyframes pulseGlow { 0% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7); } 70% { box-shadow: 0 0 0 15px rgba(16, 185, 129, 0); } 100% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); } } @media (max-width: 600px) { .pulley-system { gap: 20px; } .pulley-container { width: 60px; height: 60px; } .pulley-outer { width: 60px; height: 60px; } .pulley-inner { width: 38px; height: 38px; } }`;

// =====================================================
// Logs fetching
// =====================================================

async function fetchAndDisplayLogs(podName) {
  const logsContainer = document.getElementById('logsContainer');
  const logsStatus = document.getElementById('logsStatus');
  const logsLineCount = document.getElementById('logsLineCount');
  
  logsContainer.innerHTML = '<div style="color: rgba(255,255,255,.58);">Loading logs...</div>';
  logsStatus.textContent = 'Loading...';
  
  try {
    // Try fetching from backend API
    const response = await fetch(`/api/logs/${encodeURIComponent(podName)}`, {
      headers: { 'Accept': 'application/json' }
    });
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }
    
    const data = await response.json();
    const logs = data.logs || data.message || JSON.stringify(data, null, 2);
    
    // Display logs
    displayLogsInTerminal(logs, logsContainer, logsLineCount, logsStatus);
    logsStatus.textContent = '✓ Logs loaded';
  } catch (err) {
    // Fallback: show mock logs for demo
    const mockLogs = generateMockLogs(podName);
    displayLogsInTerminal(mockLogs, logsContainer, logsLineCount, logsStatus);
    logsStatus.textContent = '⚠ Using demo logs (configure backend API)';
  }
}

function displayLogsInTerminal(logs, container, lineCounter, statusEl) {
  const lines = logs.split('\n').filter(l => l.trim());
  
  // Create log lines with proper terminal styling
  const logHTML = lines.map((line, i) => {
    let styledLine = escapeHtml(line);
    
    // Color code different log levels
    if (line.includes('ERROR') || line.includes('Exception')) {
      styledLine = `<span style="color: rgba(244,114,182,.95); font-weight: 600;">${styledLine}</span>`;
    } else if (line.includes('WARN')) {
      styledLine = `<span style="color: rgba(251,191,36,.95);">${styledLine}</span>`;
    } else if (line.includes('INFO') || line.includes('Successfully')) {
      styledLine = `<span style="color: rgba(16,185,129,.95);">${styledLine}</span>`;
    }
    
    return styledLine;
  }).join('<br>');
  
  container.innerHTML = logHTML || '<span style="color: rgba(255,255,255,.58);">No logs available</span>';
  lineCounter.textContent = `Lines: ${lines.length}`;
}

function generateMockLogs(podName) {
  const now = new Date();
  return `[${now.toISOString()}] Application starting on pod ${podName}
[${now.toISOString()}] Initializing Spring Boot application
[${now.toISOString()}] Loading application properties
[${now.toISOString()}] Starting embedded Tomcat server
[${now.toISOString()}] Tomcat started on port 9091 (http)
[${now.toISOString()}] Started application in 4.521 seconds
[${now.toISOString()}] Spring Boot Health Check: UP
[${now.toISOString()}] Successfully connected to Eureka Server
[${now.toISOString()}] Registering service instance with Eureka
[${now.toISOString()}] Application ready to receive requests
[${now.toISOString()}] Database pool initialized with 10 connections
[${now.toISOString()}] Cache warmed up successfully
[${now.toISOString()}] Monitoring: CPU 25%, Memory 512MB/2GB

--- Demo logs shown (configure backend endpoint for real logs) ---
To see real Azure logs, create a REST endpoint:
  GET /api/logs/{podName}
  
Returns JSON: { "logs": "log content here" }`;
}

// =====================================================
// Template generators
// =====================================================

function getHomeTemplate() {
  return `
<!doctype html><html lang="en"><head>
<meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Home</title>
<style>${PAGE_CSS}</style>
</head>
<body>
<div class="homeWrap" role="region" aria-label="Welcome screen">
  <div class="homeCard">
    <h2 class="homeTitle">Welcome to Service Console</h2>
    <p class="homeSub">Pick any option from the left navigation.</p>
    
    <!-- Mechanical Pulley System -->
    <div class="pulley-system" aria-label="Mechanical pulley animation">
      <!-- Left Pulley -->
      <div class="pulley-group left">
        <div class="pulley-container">
          <div class="pulley-outer"></div>
          <div class="pulley-inner"></div>
          <div class="pulley-center"></div>
        </div>
        <div class="pulley-label">Input</div>
      </div>

      <!-- Belt System -->
      <div class="belt-system">
        <svg class="belt-svg" viewBox="0 0 200 60" preserveAspectRatio="none">
          <defs>
            <pattern id="belt-pattern" x="0" y="0" width="20" height="60" patternUnits="userSpaceOnUse">
              <rect x="0" y="0" width="20" height="60" fill="rgba(255,255,255,0.1)" stroke="rgba(255,255,255,0.2)" stroke-width="1"/>
            </pattern>
            <linearGradient id="belt-gradient" x1="0%" y1="0%" x2="100%" y2="0%">
              <stop offset="0%" style="stop-color:rgba(99,102,241,0.3);stop-opacity:1" />
              <stop offset="50%" style="stop-color:rgba(99,102,241,0.5);stop-opacity:1" />
              <stop offset="100%" style="stop-color:rgba(99,102,241,0.3);stop-opacity:1" />
            </linearGradient>
          </defs>
          <rect x="5" y="25" width="190" height="10" fill="url(#belt-gradient)" stroke="rgba(255,255,255,0.3)" stroke-width="0.5"/>
          <rect x="5" y="25" width="190" height="10" fill="url(#belt-pattern)"/>
        </svg>
      </div>

      <!-- Right Pulley -->
      <div class="pulley-group right">
        <div class="pulley-container">
          <div class="pulley-outer"></div>
          <div class="pulley-inner"></div>
          <div class="pulley-center"></div>
        </div>
        <div class="pulley-label">Output</div>
      </div>
    </div>

    <!-- Status Line -->
    <div class="status-indicator">
      <span class="pulse-dot" aria-hidden="true"></span>
      <span class="status-text">System ready • services initializing</span>
    </div>
  </div>
</div>
</body></html>`;
}

function getBlockedEmbedTemplate(url, title) {
  return `<!doctype html><html lang="en"><head>
<meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>${escapeHtml(title)}</title>
<style>${PAGE_CSS}</style>
</head>
<body>
  <div class="homeWrap">
    <div class="homeCard">
      <h2 class="homeTitle">${escapeHtml(title)} can't be shown inside this frame</h2>
      <p class="homeSub">
        Bitrix blocks embedding using Content Security Policy:
        <b style="color: rgba(255,255,255,.92)">frame-ancestors 'self'</b>.
        So the browser blocks it in an iframe.
      </p>
      <a class="toolbtn" href="${escapeAttr(url)}" target="_blank" rel="noopener noreferrer"
         style="display:inline-flex; margin-top:14px; text-decoration:none;">
        ↗ Open ${escapeHtml(title)} in a new tab
      </a>
      <div style="margin-top:10px; color: rgba(255,255,255,.58); font-size:12px;">
        Your left navigation stays available here.
      </div>
    </div>
  </div>
</body></html>`;
}

function getPodContainerApp(podName) {
  // Extract container app name from pod name: awitor01customermgmt--0000005-544b9cdf48-qxrvr
  // Returns: awitor01customermgmt
  if (!podName) return '';
  const parts = podName.split('--');
  return parts[0] || '';
}

function getEurekaStyledTemplate(apps) {
  const cards = apps.map(app => {
    const rows = app.instances.map(inst => {
      const urls = [
        inst.homePageUrl ? `<a href="${escapeAttr(inst.homePageUrl)}" target="_blank" rel="noopener noreferrer">Home</a>` : '',
        inst.statusPageUrl ? `<a href="${escapeAttr(inst.statusPageUrl)}" target="_blank" rel="noopener noreferrer">Status</a>` : '',
        inst.healthCheckUrl ? `<a href="${escapeAttr(inst.healthCheckUrl)}" target="_blank" rel="noopener noreferrer">Health</a>` : ''
      ].filter(Boolean).join(' • ');

      const metaKeys = Object.keys(inst.metadata || {});
      const meta = metaKeys.length
        ? `<div style="margin-top:8px; color: rgba(255,255,255,.58); font-size:12px;">
             <b style="color: rgba(255,255,255,.72);">Metadata:</b>
             ${metaKeys.slice(0, 10).map(k => `
               <span style="margin-right:10px; display:inline-block; margin-top:6px;">
                 <code style="background:rgba(255,255,255,.08);padding:2px 6px;border-radius:8px;border:1px solid rgba(255,255,255,.10);">${escapeHtml(k)}</code>
                 ${escapeHtml(inst.metadata[k])}
               </span>`).join('')}
             ${metaKeys.length > 10 ? `<div style="margin-top:6px;">(+${metaKeys.length - 10} more)</div>` : ''}
           </div>`
        : '';

      // Logs button
      const podName = inst.id || inst.instanceId || '';
      const logsButton = podName
        ? `<button class="toolbtn" style="border:1px solid rgba(99,102,241,.35); background:rgba(99,102,241,.12); margin-top:10px; font-size:11px; padding:8px 10px; cursor: pointer;" onclick="window.parent.openLogsViewer('${escapeAttr(podName)}')">
          📋 View Logs
        </button>`
        : '';

      return `
        <div style="border:1px solid rgba(255,255,255,.12); border-radius:14px; padding:14px; background: rgba(255,255,255,.04);">
          <div style="display:flex; gap:10px; align-items:center; flex-wrap:wrap;">
            <div style="font-weight:900; letter-spacing:.2px;">${escapeHtml(inst.id || inst.instanceId || '')}</div>
            <span class="pill" style="margin:0; font-size:12px;">${escapeHtml(inst.status || 'UNKNOWN')}</span>
            ${inst.host ? `<span class="pill" style="margin:0; font-size:12px;">${escapeHtml(inst.host)}</span>` : ''}
            ${inst.ip ? `<span class="pill" style="margin:0; font-size:12px;">${escapeHtml(inst.ip)}</span>` : ''}
            ${inst.port ? `<span class="pill" style="margin:0; font-size:12px;">Port: ${escapeHtml(inst.port)}</span>` : ''}
            ${inst.securePort ? `<span class="pill" style="margin:0; font-size:12px;">Secure: ${escapeHtml(inst.securePort)}</span>` : ''}
          </div>
          <div style="margin-top:8px; color: rgba(255,255,255,.72); font-size:13px; line-height:1.45;">
            ${urls || '<span style="color: rgba(255,255,255,.58);">No URLs exposed</span>'}
          </div>
          ${meta}
          ${logsButton}
        </div>
      `;
    }).join('');

    return `
      <div style="border:1px solid rgba(255,255,255,.12); border-radius:18px; padding:16px; background: rgba(255,255,255,.05);">
        <div style="display:flex; justify-content:space-between; align-items:center; gap:12px; flex-wrap:wrap;">
          <div style="font-size:16px; font-weight:1000; letter-spacing:.3px;">${escapeHtml(app.name)}</div>
          <div style="color: rgba(255,255,255,.72); font-size:12px;">Instances:
            <b style="color: rgba(255,255,255,.92);">${app.instances.length}</b>
          </div>
        </div>
        <div style="margin-top:12px; display:grid; gap:10px;">
          ${rows}
        </div>
      </div>
    `;
  }).join('');

  return `<!doctype html><html lang="en"><head>
<meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Eureka Apps (Styled)</title>
<style>${PAGE_CSS}</style>
</head>
<body>
  <div class="homeWrap" style="place-items:stretch; padding:18px;">
    <div class="homeCard" style="width:100%; padding:18px;">
      <div style="display:flex; align-items:flex-start; justify-content:space-between; gap:14px; flex-wrap:wrap;">
        <div>
          <h2 class="homeTitle" style="font-size:18px;">Eureka Apps (Styled)</h2>
          <p class="homeSub" style="margin-top:6px;">Fetched from <b>/eureka/apps</b> (JSON) and rendered nicely.</p>
        </div>
        <button class="toolbtn" style="border:none;" onclick="location.reload()">↻ Refresh</button>
      </div>

      <div style="margin-top:14px; display:grid; gap:14px;">
        ${cards || `<div style="color: rgba(255,255,255,.72);">No applications found.</div>`}
      </div>
    </div>
  </div>
</body></html>`;
}

// =====================================================
// Utility functions
// =====================================================

function escapeHtml(s) {
  return String(s ?? '').replace(/[&<>"']/g, c => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  }[c]));
}

function escapeAttr(s) {
  return escapeHtml(s);
}

function setActive(link) {
  navLinks.forEach(a => a.removeAttribute('aria-current'));
  link.setAttribute('aria-current', 'page');
}

function openLogsViewer(podName) {
  document.getElementById('logsModal').style.display = 'flex';
  document.getElementById('logsPodName').textContent = podName;
  fetchAndDisplayLogs(podName);
}

// =====================================================
// Load functions
// =====================================================

function loadHome(link) {
  frame.removeAttribute('src');
  frame.srcdoc = getHomeTemplate();
  titleEl.textContent = link.getAttribute('data-title') || 'Home';
  subEl.textContent = 'Welcome screen';
  setActive(link);
}

function loadUrl(link) {
  const href = link.getAttribute('href');
  const label = link.getAttribute('data-title') || link.textContent.trim();

  frame.removeAttribute('srcdoc');
  frame.src = href;

  titleEl.textContent = label;
  subEl.textContent = 'Loaded inside the frame';
  setActive(link);
}

function loadBlocked(link) {
  const href = link.getAttribute('href');
  const label = link.getAttribute('data-title') || link.textContent.trim();

  frame.removeAttribute('src');
  frame.srcdoc = getBlockedEmbedTemplate(href, label);

  titleEl.textContent = label;
  subEl.textContent = 'Opens in a new tab (embedding blocked)';
  setActive(link);
}

async function loadEurekaStyled(link) {
  const label = link.getAttribute('data-title') || link.textContent.trim();
  titleEl.textContent = label;
  subEl.textContent = 'Fetching /eureka/apps …';
  setActive(link);

  frame.removeAttribute('src');
  frame.srcdoc = `<!doctype html><html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
    <style>${PAGE_CSS}</style></head><body>
    <div class="homeWrap"><div class="homeCard">
      <h2 class="homeTitle">Loading Eureka Apps…</h2>
      <p class="homeSub">Fetching <b>/eureka/apps</b> (JSON) and formatting it.</p>
      <div class="machine"><div class="belt" aria-hidden="true"></div></div>
    </div></div></body></html>`;

  try {
    const res = await fetch('/eureka/apps', { headers: { 'Accept': 'application/json' } });
    if (!res.ok) throw new Error('HTTP ' + res.status);

    const data = await res.json();

    // Expected: { applications: { application: [ { name, instance: [...] } ] } }
    const appList = (data && data.applications && data.applications.application)
      ? data.applications.application
      : [];

    const normalized = appList.map(app => {
      const instancesRaw = Array.isArray(app.instance) ? app.instance : (app.instance ? [app.instance] : []);
      const instances = instancesRaw.map(inst => ({
        id: inst.instanceId || inst.id || '',
        status: inst.status || inst.overriddenStatus || 'UNKNOWN',
        host: inst.hostName || inst.host || '',
        ip: inst.ipAddr || inst.ip || '',
        port: (inst.port && (inst.port['$'] ?? inst.port)) || '',
        securePort: (inst.securePort && (inst.securePort['$'] ?? inst.securePort)) || '',
        homePageUrl: inst.homePageUrl || '',
        statusPageUrl: inst.statusPageUrl || '',
        healthCheckUrl: inst.healthCheckUrl || '',
        metadata: inst.metadata || {}
      }));
      return { name: app.name || 'UNKNOWN_APP', instances };
    });

    frame.removeAttribute('src');
    frame.srcdoc = getEurekaStyledTemplate(normalized);
    subEl.textContent = 'Loaded (styled)';
  } catch (err) {
    frame.removeAttribute('src');
    frame.srcdoc = `<!doctype html><html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
      <style>${PAGE_CSS}</style></head><body>
      <div class="homeWrap"><div class="homeCard">
        <h2 class="homeTitle">Could not load Eureka Apps</h2>
        <p class="homeSub">
          Tried <b>GET /eureka/apps</b> with <b>Accept: application/json</b>.
          Error: <b>${escapeHtml(String(err))}</b>
        </p>
        <p class="homeSub" style="margin-top:10px;">
          If your Eureka doesn't support JSON, use <b>Eureka Apps (Raw)</b>,
          or tell me what format you get (XML/HTML) and I'll adapt the parser.
        </p>
      </div></div></body></html>`;
    subEl.textContent = 'Failed to load';
  }
}

// =====================================================
// Event listeners
// =====================================================

navLinks.forEach(link => {
  link.addEventListener('click', function (e) {
    e.preventDefault();
    const mode = link.getAttribute('data-inframe') || 'url';

    if (mode === 'home' || link.getAttribute('href') === '#home') {
      loadHome(link);
    } else if (mode === 'blocked') {
      loadBlocked(link);
    } else if (mode === 'eurekaStyled') {
      loadEurekaStyled(link);
    } else {
      loadUrl(link);
    }
  });

  link.addEventListener('keydown', function (e) {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      link.click();
    }
  });
});

openNewTabBtn.addEventListener('click', function () {
  const url = frame.getAttribute('src');
  if (url) window.open(url, '_blank', 'noopener,noreferrer');
});

reloadBtn.addEventListener('click', function () {
  const active = navLinks.find(a => a.getAttribute('aria-current') === 'page');
  const mode = active ? (active.getAttribute('data-inframe') || 'url') : 'url';

  if (mode === 'home') {
    loadHome(active || navLinks[0]);
  } else if (mode === 'blocked') {
    loadBlocked(active);
  } else if (mode === 'eurekaStyled') {
    loadEurekaStyled(active);
  } else {
    try {
      frame.contentWindow.location.reload();
    } catch (e) {
      frame.src = frame.src;
    }
  }
});

// =====================================================
// Initialize
// =====================================================

// Load home page on startup
const homeLink = navLinks.find(a => (a.getAttribute('data-inframe') === 'home') || a.getAttribute('href') === '#home') || navLinks[0];
loadHome(homeLink);
