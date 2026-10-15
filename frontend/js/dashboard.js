const DashboardApp = (function() {
    'use strict';

    const STORAGE_KEY = 'vehical_auth_data';
    const LOGIN_PAGE = '../login.html';

    const ROLE_LABELS = {
        POLICE: 'Police Dept',
        PUC_CENTER: 'PUC Center',
        INSURANCE_AGENCY: 'Insurance'
    };

    const ROLE_FULL_NAMES = {
        POLICE: 'Police Department',
        PUC_CENTER: 'PUC Testing Center',
        INSURANCE_AGENCY: 'Insurance Agency'
    };

    const DASHBOARD_PATHS = {
        POLICE: 'police-dashboard.html',
        PUC_CENTER: 'puc-dashboard.html',
        INSURANCE_AGENCY: 'insurance-dashboard.html'
    };

    const SIDEBAR_ICONS = {
        overview: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>',
        verifyVehicle: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>',
        issueChallan: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>',
        challanHistory: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>',
        anprScanner: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/></svg>',
        todayDuty: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y1="10"/></svg>',
        newTest: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>',
        issuePuc: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>',
        renewalReminders: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>',
        reports: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>',
        history: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 8v4l3 3"/><circle cx="12" cy="12" r="9"/><path d="M3 12h2"/></svg>',
        newPolicy: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="12" y1="18" x2="12" y2="12"/><line x1="9" y1="15" x2="15" y2="15"/></svg>',
        issuePolicy: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>',
        verifyPolicy: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>',
        renewals: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><polyline points="1 20 1 14 7 14"/><path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/></svg>',
        claimsDesk: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>',
        customers: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>'
    };

    const SIDEBAR_MENUS = {
        POLICE: [
            { key: 'overview', label: 'Overview', icon: SIDEBAR_ICONS.overview, href: 'police-dashboard.html' },
            { key: 'anprScanner', label: 'ANPR Scanner', icon: SIDEBAR_ICONS.anprScanner, href: 'anpr-scanner-new.html' },
            { key: 'challanHistory', label: 'Challan History', icon: SIDEBAR_ICONS.challanHistory, href: 'challan-history.html' },
            { key: 'todayDuty', label: 'Today Duty', icon: SIDEBAR_ICONS.todayDuty, href: 'police-dashboard.html' }
        ],
        PUC_CENTER: [
            { key: 'overview', label: 'Overview', icon: SIDEBAR_ICONS.overview, href: 'puc-dashboard.html' },
            { key: 'issuePuc', label: 'Issue PUC', icon: SIDEBAR_ICONS.issuePuc, href: 'issue-puc.html' },
            { key: 'history', label: 'History', icon: SIDEBAR_ICONS.history, href: 'puc-history.html' },
            { key: 'renewalReminders', label: 'Renewal Reminders', icon: SIDEBAR_ICONS.renewalReminders, href: 'send-reminders.html' }
        ],
        INSURANCE_AGENCY: [
            { key: 'overview', label: 'Overview', icon: SIDEBAR_ICONS.overview, href: 'insurance-dashboard.html' },
            { key: 'newPolicy', label: 'New Policy', icon: SIDEBAR_ICONS.newPolicy, href: 'new-policy.html' },
            { key: 'history', label: 'History', icon: SIDEBAR_ICONS.history, href: 'insurance-history.html' }
        ]
    };

    function getAuthData() {
        try {
            const raw = localStorage.getItem(STORAGE_KEY);
            if (!raw) return null;
            const data = JSON.parse(raw);
            if (data.expiresAt && Date.now() > data.expiresAt) {
                clearAuth();
                return null;
            }
            return data;
        } catch (e) {
            return null;
        }
    }

    function clearAuth() {
        localStorage.removeItem(STORAGE_KEY);
    }

    function requireRole(allowedRoles) {
        const auth = getAuthData();
        if (!auth || !auth.user) {
            window.location.href = LOGIN_PAGE;
            return null;
        }
        if (allowedRoles && !allowedRoles.includes(auth.user.role)) {
            window.location.href = LOGIN_PAGE;
            return null;
        }
        return auth;
    }

    function logout() {
        clearAuth();
        window.location.href = LOGIN_PAGE;
    }

    function handleSidebarLogoutClick(e) {
        e.preventDefault();
        if (confirm('Are you sure you want to logout?')) {
            logout();
        }
    }

    function toggleSidebar() {
        const sidebar = document.querySelector('.sidebar-nav');
        const overlay = document.querySelector('.sidebar-overlay');
        if (sidebar) sidebar.classList.toggle('open');
        if (overlay) overlay.classList.toggle('visible');
    }

    function closeSidebarOnOverlayClick() {
        const overlay = document.querySelector('.sidebar-overlay');
        if (!overlay || overlay.dataset.bound === '1') return;
        overlay.dataset.bound = '1';
        overlay.addEventListener('click', () => {
            const sidebar = document.querySelector('.sidebar-nav');
            if (sidebar) sidebar.classList.remove('open');
            overlay.classList.remove('visible');
        });
    }

    function attachSidebarToggle() {
        const navbarContent = document.querySelector('.navbar-content');
        if (!navbarContent || document.getElementById('sidebarToggleBtn')) return;
        const brand = navbarContent.querySelector('.navbar-brand');
        if (!brand) return;
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.id = 'sidebarToggleBtn';
        btn.className = 'sidebar-toggle';
        btn.setAttribute('aria-label', 'Open navigation menu');
        btn.innerHTML = '<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="18" x2="21" y2="18"/></svg>';
        btn.addEventListener('click', toggleSidebar);
        brand.insertAdjacentElement('afterend', btn);
    }

    function ensureAppShellLayout(auth, activeKey) {
        if (!document.getElementById('sidebarNav')) {
            const shell = document.createElement('div');
            shell.className = 'app-shell';
            shell.innerHTML = '<aside class="sidebar-nav" id="sidebarNav"></aside><div class="sidebar-overlay"></div><div class="main-content"></div>';
            const mainContent = shell.querySelector('.main-content');
            const navbar = document.querySelector('.top-navbar');
            const toMove = Array.from(document.body.children).filter(el => {
                return el.tagName !== 'SCRIPT'
                    && !el.classList.contains('top-navbar')
                    && !el.classList.contains('app-shell');
            });
            if (navbar) {
                navbar.after(shell);
            } else {
                document.body.prepend(shell);
            }
            toMove.forEach(el => mainContent.appendChild(el));
        }
        renderSidebar(auth, activeKey || 'overview');
        attachSidebarToggle();
    }

    function renderSidebar(auth, activeKey) {
        const sidebarMount = document.getElementById('sidebarNav');
        if (!sidebarMount) return;

        const user = auth.user;
        const role = user.role;
        const menu = SIDEBAR_MENUS[role] || [];
        const active = activeKey || 'overview';

        const initials = user.fullName
            .split(' ')
            .map(n => n[0])
            .slice(0, 2)
            .join('')
            .toUpperCase();

        const roleText = ROLE_LABELS[role] || role.replace('_', ' ');

        const menuItemsHtml = menu.map(item => {
            const isActive = item.key === active;
            return `
                <a href="${item.href}" class="sidebar-item ${isActive ? 'active' : ''}" data-menu-key="${item.key}">
                    ${item.icon}
                    <span>${item.label}</span>
                </a>
            `;
        }).join('');

        const sidebarHtml = `
            <div class="sidebar-user-card">
                <div class="sidebar-avatar">${initials}</div>
                <div class="sidebar-user-info">
                    <div class="sidebar-user-name">${user.fullName}</div>
                    <div class="sidebar-user-role">${roleText}</div>
                </div>
            </div>

            <div class="sidebar-menu">
                <div class="sidebar-section-label">Navigation</div>
                ${menuItemsHtml}
            </div>

            <div class="sidebar-footer">
                <button class="sidebar-logout" id="sidebarLogoutBtn">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                        <polyline points="16 17 21 12 16 7"/>
                        <line x1="21" y1="12" x2="9" y2="12"/>
                    </svg>
                    <span>Logout</span>
                </button>
            </div>
        `;

        sidebarMount.innerHTML = sidebarHtml;

        const logoutBtn = document.getElementById('sidebarLogoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', handleSidebarLogoutClick);
        }

        closeSidebarOnOverlayClick();
    }

    function renderUserHeader(auth, options) {
        const user = auth.user;
        const initials = user.fullName
            .split(' ')
            .map(n => n[0])
            .slice(0, 2)
            .join('')
            .toUpperCase();

        const orgText = user.department || user.centerName || user.agencyName || '';
        const roleText = ROLE_LABELS[user.role] || user.role.replace('_', ' ');

        const headerHTML = `
            <nav class="top-navbar">
                <div class="navbar-content">
                    <div class="navbar-brand">
                        <svg viewBox="0 0 100 100" class="navbar-emblem">
                            <circle cx="50" cy="50" r="45" fill="none" stroke="#E8C07D" stroke-width="2"/>
                            <circle cx="50" cy="50" r="38" fill="none" stroke="#E8C07D" stroke-width="1"/>
                            <path d="M50 15 L55 40 L80 40 L60 55 L68 82 L50 65 L32 82 L40 55 L20 40 L45 40 Z" fill="#E8C07D" opacity="0.9"/>
                        </svg>
                        <div class="navbar-titles">
                            <h1>Vehicle Verification Portal</h1>
                            <p>Government of India | MoRTH</p>
                        </div>
                    </div>
                    <div class="navbar-user">
                        <span class="role-badge">
                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
                            </svg>
                            ${roleText}
                        </span>
                        <div class="user-menu" title="Account Menu">
                            <div class="user-avatar">${initials}</div>
                            <div class="user-info">
                                <span class="name">${user.fullName}</span>
                                <span class="dept">${user.username}</span>
                            </div>
                        </div>
                        <button class="logout-btn" id="logoutBtn">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                                <polyline points="16 17 21 12 16 7"/>
                                <line x1="21" y1="12" x2="9" y2="12"/>
                            </svg>
                            Logout
                        </button>
                    </div>
                </div>
            </nav>
        `;

        document.body.insertAdjacentHTML('afterbegin', headerHTML);

        document.getElementById('logoutBtn').addEventListener('click', (e) => {
            e.preventDefault();
            if (confirm('Are you sure you want to logout?')) {
                logout();
            }
        });
    }

    function renderWelcome(role, orgTitle) {
        const auth = getAuthData();
        if (!auth) return '';
        const now = new Date();
        const dateStr = now.toLocaleDateString('en-IN', {
            weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
        });
        const timeStr = now.toLocaleTimeString('en-IN', {
            hour: '2-digit', minute: '2-digit'
        });

        const greeting = now.getHours() < 12 ? 'Good Morning' :
                        now.getHours() < 17 ? 'Good Afternoon' : 'Good Evening';

        const roleDisplayName = ROLE_FULL_NAMES[auth.user.role] || auth.user.role.replace('_', ' ');

        return `
            <section class="welcome-bar">
                <div>
                    <h2>${greeting}, ${auth.user.fullName} 👋</h2>
                    <p>${orgTitle} — ${roleDisplayName} Control Center</p>
                </div>
                <div class="timestamp">
                    <span class="date">${dateStr}</span>
                    <span>Last login: ${new Date(auth.loginTime).toLocaleString('en-IN')}</span>
                </div>
            </section>
        `;
    }

    function init(allowedRoles, orgTitle, activeMenuKey) {
        const auth = requireRole(allowedRoles);
        if (!auth) return null;

        renderUserHeader(auth);
        ensureAppShellLayout(auth, activeMenuKey || 'overview');
        const bar = document.getElementById('welcomeBar');
        if (bar) bar.outerHTML = renderWelcome(auth.user.role.replace('_', ' '), orgTitle);
        return auth;
    }

    function renderFunctionPage({ allowedRoles, pageTitle, pageIcon, pageDescription, dashboardLabel, activeMenuKey }) {
        const auth = requireRole(allowedRoles);
        if (!auth) return null;

        renderUserHeader(auth);
        ensureAppShellLayout(auth, activeMenuKey || 'overview');

        const roleDashboard = DASHBOARD_PATHS[auth.user.role] || 'police-dashboard.html';
        const label = dashboardLabel || (ROLE_FULL_NAMES[auth.user.role] + ' Dashboard');

        let iconHtml = '';
        if (pageIcon) {
            iconHtml = `<span class="func-page-icon">${pageIcon}</span>`;
        }

        let descHtml = '';
        if (pageDescription) {
            descHtml = `<p class="func-page-desc">${pageDescription}</p>`;
        }

        const header = `
            <section class="func-page-hero">
                <div class="func-breadcrumb">
                    <a href="${roleDashboard}" class="func-breadcrumb-link">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <line x1="19" y1="12" x2="5" y2="12"/>
                            <polyline points="12 19 5 12 12 5"/>
                        </svg>
                        Back to ${label}
                    </a>
                </div>
                <div class="func-page-heading">
                    <div class="func-page-title-wrap">
                        <h2 class="func-page-title">${pageTitle}</h2>
                        ${descHtml}
                    </div>
                </div>
            </section>
        `;

        const mount = document.getElementById('funcPageMount');
        if (mount) {
            mount.outerHTML = header;
        }

        const footerHtml = `
            <footer class="footer-bar">
                <span class="copyright-text">© 2026 Government of India · Ministry of Road Transport &amp; Highways · All Rights Reserved</span>
                <span class="version-info">System v2.1.0 · Parivahan Integration · Live</span>
            </footer>
        `;
        if (!document.querySelector('.footer-bar')) {
            document.body.insertAdjacentHTML('beforeend', footerHtml);
        }

        return auth;
    }

    function renderPublicServicePage({ pageTitle, pageIcon, pageDescription }) {
        let iconHtml = '';
        if (pageIcon) {
            iconHtml = `<span class="func-page-icon">${pageIcon}</span>`;
        }
        let descHtml = '';
        if (pageDescription) {
            descHtml = `<p class="func-page-desc">${pageDescription}</p>`;
        }

        const publicNav = `
            <nav class="top-navbar">
                <div class="navbar-content">
                    <div class="navbar-brand">
                        <svg viewBox="0 0 100 100" class="navbar-emblem">
                            <circle cx="50" cy="50" r="45" fill="none" stroke="#E8C07D" stroke-width="2"/>
                            <circle cx="50" cy="50" r="38" fill="none" stroke="#E8C07D" stroke-width="1"/>
                            <path d="M50 15 L55 40 L80 40 L60 55 L68 82 L50 65 L32 82 L40 55 L20 40 L45 40 Z" fill="#E8C07D" opacity="0.9"/>
                        </svg>
                        <div class="navbar-titles">
                            <h1>Vehicle Verification Portal</h1>
                            <p>Government of India | MoRTH</p>
                        </div>
                    </div>
                    <div class="navbar-user">
                        <a href="../index.html" class="logout-btn">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/>
                                <polyline points="10 17 15 12 10 7"/>
                                <line x1="15" y1="12" x2="3" y2="12"/>
                            </svg>
                            Login
                        </a>
                    </div>
                </div>
            </nav>
        `;
        document.body.insertAdjacentHTML('afterbegin', publicNav);

        const header = `
            <section class="func-page-hero">
                <div class="func-breadcrumb">
                    <a href="../index.html" class="func-breadcrumb-link">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <line x1="19" y1="12" x2="5" y2="12"/>
                            <polyline points="12 19 5 12 12 5"/>
                        </svg>
                        Back to RTO Portal
                    </a>
                </div>
                <div class="func-page-heading">
                    <div class="func-page-title-wrap">
                        <h2 class="func-page-title">${pageTitle}</h2>
                        ${descHtml}
                    </div>
                </div>
            </section>
        `;
        const mount = document.getElementById('funcPageMount');
        if (mount) mount.outerHTML = header;

        if (!document.querySelector('.footer-bar')) {
            document.body.insertAdjacentHTML('beforeend', `
                <footer class="footer-bar">
                    <span class="copyright-text">© 2026 Government of India · Ministry of Road Transport &amp; Highways · All Rights Reserved</span>
                    <span class="version-info">System v2.1.0 · Parivahan Portal · Live</span>
                </footer>
            `);
        }

        return true;
    }

    return {
        init,
        getAuthData,
        logout,
        requireRole,
        renderFunctionPage,
        renderPublicServicePage
    };
})();
