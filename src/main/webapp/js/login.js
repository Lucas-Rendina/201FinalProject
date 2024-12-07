document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const errorMessage = document.getElementById('error-message');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');

<<<<<<< Updated upstream
    form.addEventListener('submit', function (event) {
        event.preventDefault(); // Prevent the default form submission
        
        // Clear any existing messages
        let existingMessage = document.querySelector('.response-message');
        if (existingMessage) {
            existingMessage.remove();
        }

        // Send form data via AJAX
        fetch('LoginServlet', {
            method: 'POST',
            body: new FormData(form)
        })
        .then(response => response.json())
        .then(data => {
            const messageElement = document.createElement('p');
            messageElement.classList.add('response-message');

            if (data.status === "success") {
                messageElement.textContent = data.message;
                messageElement.style.color = 'green';
                // Optionally redirect to another page after successful login:
                // setTimeout(() => window.location.href = 'account.html', 2000);
            } else {
                messageElement.textContent = data.message;
                messageElement.style.color = 'red';
            }

            form.insertAdjacentElement('afterend', messageElement);
        })
        .catch(error => {
            console.error('Error:', error);
=======
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
>>>>>>> Stashed changes
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