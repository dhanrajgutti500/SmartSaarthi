// Government Portal JavaScript
// Smart Saarthi - Interactive functionality

document.addEventListener('DOMContentLoaded', function() {
    // Font size controls
    const fontButtons = document.querySelectorAll('.font-btn');
    const htmlElement = document.documentElement;
    
    // Base font size
    let currentFontSize = 100; // percentage
    
    fontButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const action = this.getAttribute('data-size');
            
            switch(action) {
                case 'increase':
                    currentFontSize = Math.min(currentFontSize + 10, 130);
                    break;
                case 'decrease':
                    currentFontSize = Math.max(currentFontSize - 10, 80);
                    break;
                case 'normal':
                    currentFontSize = 100;
                    break;
            }
            
            htmlElement.style.fontSize = currentFontSize + '%';
            
            // Update active state
            fontButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
        });
    });
    
    // Skip to main content functionality
    const skipLinks = document.querySelectorAll('a[href^="#"]');
    skipLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            const targetId = this.getAttribute('href').substring(1);
            const targetElement = document.getElementById(targetId);
            
            if (targetElement) {
                e.preventDefault();
                targetElement.setAttribute('tabindex', '-1');
                targetElement.focus();
                targetElement.scrollIntoView({ behavior: 'smooth' });
            }
        });
    });
    
    // Smooth scroll for navigation
    const navLinks = document.querySelectorAll('.nav-item');
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            // Remove active class from all links
            navLinks.forEach(l => l.classList.remove('active'));
            // Add active class to clicked link
            this.classList.add('active');
        });
    });
    
    // Service card hover effect enhancement
    const serviceCards = document.querySelectorAll('.service-card');
    serviceCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.borderColor = '#0a3d62';
        });

        card.addEventListener('mouseleave', function() {
            this.style.borderColor = '#e0e0e0';
        });
    });

    // Mobile dropdown functionality
    const dropdownItems = document.querySelectorAll('.nav-item.dropdown');
    const isMobile = window.innerWidth <= 1200;

    dropdownItems.forEach(dropdown => {
        const navLink = dropdown.querySelector('.nav-link');

        // Mobile: click to toggle
        if (isMobile) {
            navLink.addEventListener('click', function(e) {
                e.preventDefault();
                dropdown.classList.toggle('active');

                // Close other dropdowns
                dropdownItems.forEach(otherDropdown => {
                    if (otherDropdown !== dropdown) {
                        otherDropdown.classList.remove('active');
                    }
                });
            });
        }
    });

    // Handle window resize for dropdown behavior
    window.addEventListener('resize', function() {
        const newIsMobile = window.innerWidth <= 1200;

        if (newIsMobile !== isMobile) {
            // Reset dropdown states when switching between mobile/desktop
            dropdownItems.forEach(dropdown => {
                dropdown.classList.remove('active');
            });
        }
    });
    
    // Login button interactions
    const loginButtons = document.querySelectorAll('.login-btn');
    loginButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            // Add visual feedback
            this.style.transform = 'scale(0.98)';
            setTimeout(() => {
                this.style.transform = 'scale(1)';
            }, 100);
            
            // Store which login method was clicked for potential analytics
            const loginMethod = this.classList.contains('primary') ? 'digital-seva' : 'smartsarthi';
            sessionStorage.setItem('loginMethod', loginMethod);
        });
    });
    
    // Update visitor count (simulated)
    function updateVisitorCount() {
        const visitorElement = document.querySelector('.stat-value');
        if (visitorElement && visitorElement.textContent.includes(',')) {
            // Parse current count
            let count = parseInt(visitorElement.textContent.replace(/,/g, ''));
            // Increment by random amount (simulating real visits)
            count += Math.floor(Math.random() * 10);
            // Format with Indian number system
            visitorElement.textContent = count.toLocaleString('en-IN');
        }
    }
    
    // Update visitor count every 30 seconds
    setInterval(updateVisitorCount, 30000);
    
    // Accessibility: Add ARIA attributes dynamically
    const utilityBar = document.querySelector('.gov-utility-bar');
    if (utilityBar) {
        utilityBar.setAttribute('role', 'banner');
    }
    
    const mainNav = document.querySelector('.gov-navigation');
    if (mainNav) {
        mainNav.setAttribute('role', 'navigation');
        mainNav.setAttribute('aria-label', 'Main navigation');
    }
    
    const mainContent = document.querySelector('.main-content');
    if (mainContent) {
        mainContent.setAttribute('role', 'main');
    }
    
    // Current date update in footer
    function updateFooterDate() {
        const dateElement = document.querySelector('.stat-value');
        if (dateElement && dateElement.textContent.includes('Sep')) {
            const now = new Date();
            const options = { day: '2-digit', month: 'short', year: 'numeric' };
            const formattedDate = now.toLocaleDateString('en-IN', options);
            // Keep the visitor count element separate from date
        }
    }
    
    // Add keyboard navigation support
    document.addEventListener('keydown', function(e) {
        // Escape key to close any open modals or menus
        if (e.key === 'Escape') {
            // Add any modal closing logic here
        }
    });
    
    // Mobile three-dots menu toggle
    const mobileToggle = document.getElementById('mobileMenuToggle');
    const navMoreItems = document.getElementById('navMoreItems');

    if (mobileToggle && navMoreItems) {
        mobileToggle.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            navMoreItems.classList.toggle('open');
        });

        // Close the mobile menu when clicking outside of it
        document.addEventListener('click', function(e) {
            if (!navMoreItems.contains(e.target) && !mobileToggle.contains(e.target)) {
                navMoreItems.classList.remove('open');
            }
        });

        // Close mobile menu after clicking a final link (dropdown item or non-dropdown nav item)
        navMoreItems.querySelectorAll('.dropdown-item, .nav-item:not(.dropdown)').forEach(function(item) {
            item.addEventListener('click', function() {
                navMoreItems.classList.remove('open');
            });
        });
    }

    // Console log for debugging
    console.log('Smart Saarthi Government Portal loaded successfully');
});