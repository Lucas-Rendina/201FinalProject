document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const errorMessage = document.getElementById('error-message');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');

    // Submit form handler
    loginForm.addEventListener('submit', function(event) {
        event.preventDefault();
        
        const username = usernameInput.value.trim();
        const password = passwordInput.value.trim();
        
        // Reset error message
        errorMessage.textContent = '';
        
        // Basic validation
        if (!username || !password) {
            errorMessage.textContent = 'Please fill in all fields.';
            return false;
        }
        
        // Submit form using AJAX
        $.ajax({
            type: 'POST',
            url: '../LoginServlet',
            data: {
                username: username,
                password: password
            },
            success: function(response) {
                if (response.status === 'success') {
                    // Trigger auth status update before redirect
                    window.updateAuthStatus(true);
                    // Redirect to marketplace
                    window.location.href = response.redirect;
                } else {
                    errorMessage.textContent = response.message;
                }
            },
            error: function(xhr, status, error) {
                errorMessage.textContent = 'An error occurred. Please try again.';
                console.error('Error:', error);
            }
        });
    });

    // Clear error message when user starts typing
    usernameInput.addEventListener('input', function() {
        errorMessage.textContent = '';
    });

    passwordInput.addEventListener('input', function() {
        errorMessage.textContent = '';
    });

    // Also handle the submit button click
    const submitButton = loginForm.querySelector('button[type="submit"]');
    submitButton.addEventListener('click', function() {
        loginForm.dispatchEvent(new Event('submit'));
    });
});