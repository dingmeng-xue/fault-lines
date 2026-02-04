/**
 * Legacy Application JavaScript
 * Challenge: Static resources location
 * In traditional Tomcat: /js/, /css/, /images/
 * In Spring Boot: /static/js/, /static/css/, /static/images/
 */

(function() {
    'use strict';
    
    console.log('Legacy Servlet Application - JavaScript loaded');
    
    // Form validation
    document.addEventListener('DOMContentLoaded', function() {
        const loginForm = document.querySelector('form[action*="login"]');
        
        if (loginForm) {
            loginForm.addEventListener('submit', function(e) {
                const username = document.getElementById('username');
                const password = document.getElementById('password');
                
                if (!username.value.trim()) {
                    alert('Please enter a username');
                    e.preventDefault();
                    username.focus();
                    return false;
                }
                
                if (!password.value) {
                    alert('Please enter a password');
                    e.preventDefault();
                    password.focus();
                    return false;
                }
                
                console.log('Login form submitted');
            });
        }
        
        // Show session warning before timeout
        // Challenge: Session management in Spring Boot should be stateless
        showSessionWarning();
    });
    
    function showSessionWarning() {
        // In a real app, this would warn before session timeout
        console.log('Session management active - Challenge: Move to stateless JWT');
    }
    
    // AJAX example (using old XMLHttpRequest, not fetch API)
    // Challenge: Modern apps use fetch() or axios
    function loadUsers() {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', '/legacy-app/users/list', true);
        
        xhr.onload = function() {
            if (xhr.status === 200) {
                console.log('Users loaded:', xhr.responseText);
            }
        };
        
        xhr.send();
    }
    
    // Expose to global scope (old pattern)
    // Challenge: Use ES6 modules in modern apps
    window.LegacyApp = {
        loadUsers: loadUsers
    };
    
})();
