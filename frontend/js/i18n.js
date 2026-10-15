// Internationalization (i18n) System
const translations = {
    en: {
        // Header
        govUtility: "GOVERNMENT OF INDIA | MINISTRY OF ROAD TRANSPORT & HIGHWAYS",
        skipMain: "Skip to main content",
        skipNav: "Skip to navigation",
        screenReader: "Screen Reader",
        english: "English",
        marathi: "मराठी",
        home: "Home",
        
        // Main Header
        ministryHindi: "सड़क परिवहन और राजमार्ग मंत्रालय",
        ministryEnglish: "Ministry of Road Transport & Highways",
        govText: "Government of India",
        portalName: "Smart Saarthi",
        portalSub: "PARIVAHAN SARATHI",
        
        // Hero Section
        loginTitle: "Login to Your Account",
        loginDigital: "Login with Digital Seva",
        loginSmart: "Login with Smart Saarthi e-Services",
        newUser: "New User? Register Here",
        forgotPwd: "Forgot Password?",
        
        // Login Page
        loginCardTitle: "Secure Login",
        username: "Username",
        password: "Password",
        loginBtn: "Login",
        selectRole: "Select Role",
        police: "Police",
        pucCenter: "PUC Center",
        insurance: "Insurance Agency",
        
        // Services
        vehicleReg: "Vehicle Registration",
        vehicleRegDesc: "Book new registration / Transfer of ownership",
        drivingLicense: "Driving License",
        dlDesc: "Apply for learner / permanent license",
        conductorLicense: "Conductor License",
        clDesc: "Apply for conductor license",
        permit: "Permit",
        permitDesc: "Apply for vehicle permit",
        challanPayment: "Challan Payment",
        challanDesc: "Pay traffic challan online",
        rtoAppointment: "RTO Appointment",
        rtoDesc: "Book RTO service slot",
        otherServices: "Other Services",
        otherDesc: "Duplicate, Reprint, Surrender etc.",
        
        // Quick Links
        registerVehicle: "Register New Vehicle",
        issuePUC: "Issue PUC Certificate",
        payChallan: "Pay Challan",
        checkRegStatus: "Check Vehicle Registration Status",
        checkDLStatus: "Check Driving License Status",
        verifyRC: "Verify RC / DL",
        findRTO: "Find RTO Office",
        userManual: "User Manual",
        faqs: "FAQs",
        
        // Footer
        copyright: "© 2025 Ministry of Road Transport & Highways. All Rights Reserved.",
        version: "Version 2.1.0"
    },
    
    mr: {
        // Header
        govUtility: "भारत सरकार | सडक परिवहन आणि राजमार्ग मंत्रालय",
        skipMain: "मुख्य सामग्रीकडे जा",
        skipNav: "नेव्हिगेशनकडे जा",
        screenReader: "स्क्रीन रीडर",
        english: "English",
        marathi: "मराठी",
        home: "मुख्यपृष्ठ",
        
        // Main Header
        ministryHindi: "सडक परिवहन आणि राजमार्ग मंत्रालय",
        ministryEnglish: "Ministry of Road Transport & Highways",
        govText: "भारत सरकार",
        portalName: "स्मार्टसारथी",
        portalSub: "परिवहन सारथी",
        
        // Hero Section
        loginTitle: "आपल्या खात्यात लॉग इन करा",
        loginDigital: "डिजिटल सेवासह लॉग इन करा",
        loginSmart: "स्मार्टसारथी ई-सेवांसह लॉग इन करा",
        newUser: "नवीन वापरकर्ता? येथे नोंदणी करा",
        forgotPwd: "पासवर्ड विसरलात?",
        
        // Login Page
        loginCardTitle: "सुरक्षित लॉग इन",
        username: "वापरकर्तानाव",
        password: "पासवर्ड",
        loginBtn: "लॉग इन करा",
        selectRole: "भूमिका निवडा",
        police: "पोलिस",
        pucCenter: "पीयूसी केंद्र",
        insurance: "विमा कंपनी",
        
        // Services
        vehicleReg: "वाहन नोंदणी",
        vehicleRegDesc: "नवीन नोंदणी बुक करा / मालकी हस्तांतरण",
        drivingLicense: "ड्रायव्हिंग लायसन्स",
        dlDesc: "शिकारू / कायम लायसन्ससाठी अर्ज करा",
        conductorLicense: "कंडक्टर लायसन्स",
        clDesc: "कंडक्टर लायसन्ससाठी अर्ज करा",
        permit: "परमिट",
        permitDesc: "वाहन परमिटसाठी अर्ज करा",
        challanPayment: "चलन भरण",
        challanDesc: "ऑनलाइन वाहतवीक चलन भरा",
        rtoAppointment: "आरटीओ अपॉइंटमेंट",
        rtoDesc: "आरटीओ सेवा स्लॉट बुक करा",
        otherServices: "इतर सेवा",
        otherDesc: "डुप्लिकेट, पुन्ःमुद्रण, सोपण इत्यादी",
        
        // Quick Links
        registerVehicle: "नवीन वाहन नोंदणी करा",
        issuePUC: "पीयूसी प्रमाणपत्र जारी करा",
        payChallan: "चलन भरा",
        checkRegStatus: "वाहन नोंदणी स्थिती तपासा",
        checkDLStatus: "ड्रायव्हिंग लायसन्स स्थिती तपासा",
        verifyRC: "आरसी / डीएल सत्यापित करा",
        findRTO: "आरटीओ कार्यालय शोधा",
        userManual: "वापरकर्ता मॅन्युअल",
        faqs: "सामान्य प्रश्न",
        
        // Footer
        copyright: "© 2025 सडक परिवहन आणि राजमार्ग मंत्रालय। सर्व हक्क सुरक्षित।",
        version: "आवृत्ती 2.1.0"
    }
};

// Current language
let currentLanguage = 'en';

// Set language
function setLanguage(lang) {
    currentLanguage = lang;
    localStorage.setItem('language', lang);
    updatePageLanguage();
    updateLanguageButtons();
}

// Get current language
function getLanguage() {
    return localStorage.getItem('language') || 'en';
}

// Translate text
function t(key) {
    return translations[currentLanguage][key] || translations['en'][key] || key;
}

// Update page language
function updatePageLanguage() {
    // Update all elements with data-i18n attribute
    const i18nElements = document.querySelectorAll('[data-i18n]');
    i18nElements.forEach(el => {
        const key = el.getAttribute('data-i18n');
        if (key && translations[currentLanguage][key]) {
            el.textContent = translations[currentLanguage][key];
        }
    });
    
    // Update utility bar
    const utilityText = document.querySelector('.utility-text');
    if (utilityText && !utilityText.hasAttribute('data-i18n')) {
        utilityText.textContent = t('govUtility');
    }
    
    // Update header
    const ministryHindi = document.querySelector('.ministry-hindi');
    if (ministryHindi) ministryHindi.textContent = t('ministryHindi');
    
    const ministryEnglish = document.querySelector('.ministry-english');
    if (ministryEnglish) ministryEnglish.textContent = t('ministryEnglish');
    
    const govText = document.querySelector('.gov-text');
    if (govText) govText.textContent = t('govText');
    
    const portalName = document.querySelector('.portal-name');
    if (portalName) portalName.textContent = t('portalName');
    
    const portalSub = document.querySelector('.portal-sub');
    if (portalSub) portalSub.textContent = t('portalSub');
    
    // Update hero section
    const loginTitle = document.querySelector('.login-panel-header h3');
    if (loginTitle) loginTitle.textContent = t('loginTitle');
    
    // Update service cards
    const serviceCards = document.querySelectorAll('.service-title');
    const serviceDescs = document.querySelectorAll('.service-desc');
    
    if (serviceCards.length > 0) {
        const serviceKeys = ['vehicleReg', 'drivingLicense', 'conductorLicense', 'permit', 'challanPayment', 'rtoAppointment', 'otherServices'];
        serviceCards.forEach((card, index) => {
            if (serviceKeys[index]) {
                card.textContent = t(serviceKeys[index]);
            }
        });
    }
    
    if (serviceDescs.length > 0) {
        const descKeys = ['vehicleRegDesc', 'dlDesc', 'clDesc', 'permitDesc', 'challanDesc', 'rtoDesc', 'otherDesc'];
        serviceDescs.forEach((desc, index) => {
            if (descKeys[index]) {
                desc.textContent = t(descKeys[index]);
            }
        });
    }
    
    // Update quick links
    const quickLinks = document.querySelectorAll('.links-list a');
    const linkKeys = ['registerVehicle', 'issuePUC', 'payChallan', 'checkRegStatus', 'checkDLStatus', 'verifyRC', 'findRTO', 'userManual', 'faqs'];
    quickLinks.forEach((link, index) => {
        if (linkKeys[index]) {
            const icon = link.textContent.match(/[🚗🎫💳]/)?.[0] || '';
            link.textContent = icon + ' ' + t(linkKeys[index]);
        }
    });
    
    // Update login links
    const loginLinks = document.querySelectorAll('.login-link');
    if (loginLinks.length > 0) {
        loginLinks.forEach(link => {
            if (link.textContent.includes('Register')) {
                link.textContent = '🚗 ' + t('registerVehicle');
            } else if (link.textContent.includes('Forgot')) {
                link.textContent = t('forgotPwd');
            } else if (link.textContent.includes('New User')) {
                link.textContent = t('newUser');
            }
        });
    }
    
    // Update login page specific elements
    const roleBtns = document.querySelectorAll('.role-quick-btn[data-i18n]');
    if (roleBtns.length > 0) {
        roleBtns.forEach(btn => {
            const key = btn.getAttribute('data-i18n');
            if (key && translations[currentLanguage][key]) {
                btn.textContent = translations[currentLanguage][key];
            }
        });
    }
    
    // Update login form labels (without data-i18n)
    const usernameLabel = document.querySelector('label[for="username"]');
    if (usernameLabel && !usernameLabel.hasAttribute('data-i18n')) {
        usernameLabel.textContent = t('username');
    }
    
    const passwordLabel = document.querySelector('label[for="password"]');
    if (passwordLabel && !passwordLabel.hasAttribute('data-i18n')) {
        passwordLabel.textContent = t('password');
    }
    
    // Update login button (already has data-i18n, will be handled by the loop above)
    const loginBtn = document.getElementById('loginButton');
    if (loginBtn && !loginBtn.hasAttribute('data-i18n')) {
        loginBtn.textContent = t('loginBtn');
    }
    
    // Update document direction for RTL languages
    document.documentElement.lang = currentLanguage;
    document.documentElement.dir = currentLanguage === 'hi' || currentLanguage === 'mr' ? 'ltr' : 'ltr';
}

// Update language button states
function updateLanguageButtons() {
    const langLinks = document.querySelectorAll('.utility-link');
    langLinks.forEach(link => {
        link.classList.remove('active');
        if (link.textContent.toLowerCase().includes(currentLanguage) || 
            (currentLanguage === 'en' && link.textContent === 'English') ||
            (currentLanguage === 'mr' && link.textContent === 'मराठी')) {
            link.classList.add('active');
        }
    });
    
    // Update dropdown select
    const langSelect = document.getElementById('langSelect');
    if (langSelect) {
        langSelect.value = currentLanguage;
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    const savedLang = getLanguage();
    setLanguage(savedLang);
    
    // Add language switch event listeners
    const langLinks = document.querySelectorAll('.utility-link[data-lang]');
    langLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const lang = this.getAttribute('data-lang');
            if (lang === 'en' || lang === 'mr') {
                setLanguage(lang);
            }
        });
    });
    
    // Also handle dropdown select
    const langSelect = document.getElementById('langSelect');
    if (langSelect) {
        langSelect.addEventListener('change', function() {
            const lang = this.value;
            if (lang === 'en' || lang === 'mr') {
                setLanguage(lang);
            }
        });
    }
});
