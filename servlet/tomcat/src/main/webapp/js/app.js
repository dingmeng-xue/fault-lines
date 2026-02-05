/**
 * Legacy Application JavaScript
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
        
        showSessionWarning();
    });
    
    function showSessionWarning() {
        console.log('Session management active');
    }
    
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
    
    window.LegacyApp = {
        loadUsers: loadUsers
    };
    
})();
