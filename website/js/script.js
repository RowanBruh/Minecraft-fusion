document.addEventListener('DOMContentLoaded', function() {
    // Mobile Navigation
    const hamburger = document.querySelector('.hamburger');
    const navLinks = document.querySelector('.nav-links');
    const navLinksItems = document.querySelectorAll('.nav-links li');
    
    hamburger.addEventListener('click', () => {
        // Toggle Nav
        navLinks.classList.toggle('nav-active');
        
        // Animate Links
        navLinksItems.forEach((link, index) => {
            if (link.style.animation) {
                link.style.animation = '';
            } else {
                link.style.animation = `navLinkFade 0.5s ease forwards ${index / 7 + 0.3}s`;
            }
        });
        
        // Hamburger Animation
        hamburger.classList.toggle('toggle');
    });
    
    // Smooth Scrolling for Navigation
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();
            
            const targetId = this.getAttribute('href');
            const targetElement = document.querySelector(targetId);
            
            if (targetElement) {
                window.scrollTo({
                    top: targetElement.offsetTop,
                    behavior: 'smooth'
                });
                
                // Close mobile nav if open
                if (navLinks.classList.contains('nav-active')) {
                    navLinks.classList.remove('nav-active');
                    hamburger.classList.remove('toggle');
                    
                    navLinksItems.forEach(link => {
                        link.style.animation = '';
                    });
                }
            }
        });
    });
    
    // Parallax Effect for Hero Section
    window.addEventListener('scroll', function() {
        const hero = document.querySelector('.hero');
        const scrollPosition = window.pageYOffset;
        
        if (hero) {
            hero.style.backgroundPositionY = scrollPosition * 0.5 + 'px';
        }
    });
    
    // Animation on Scroll
    const animateOnScroll = function() {
        const elements = document.querySelectorAll('.feature-card, .item, .entity, .download-card');
        
        elements.forEach(element => {
            const elementPosition = element.getBoundingClientRect().top;
            const screenPosition = window.innerHeight / 1.3;
            
            if (elementPosition < screenPosition) {
                element.style.opacity = '1';
                element.style.transform = 'translateY(0)';
            }
        });
    };
    
    // Set initial styles for animation
    document.querySelectorAll('.feature-card, .item, .entity, .download-card').forEach(element => {
        element.style.opacity = '0';
        element.style.transform = 'translateY(20px)';
        element.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
    });
    
    // Run once on load and then on scroll
    animateOnScroll();
    window.addEventListener('scroll', animateOnScroll);
    
    // Download Button Alert (Would be replaced with actual download in production)
    const downloadBtn = document.querySelector('.download-btn');
    if (downloadBtn) {
        downloadBtn.addEventListener('click', function(e) {
            e.preventDefault();
            alert('This is a demo website. In a real implementation, this button would download the mod file.');
        });
    }
});