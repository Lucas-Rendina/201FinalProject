function validateForm() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const errorMessage = document.getElementById('error-message');
    
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
    
    return false;
}

// Optional: Clear error message when user starts typing
document.getElementById('username').addEventListener('input', function() {
    document.getElementById('error-message').textContent = '';
});

document.getElementById('password').addEventListener('input', function() {
    document.getElementById('error-message').textContent = '';
});