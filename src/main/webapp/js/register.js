
        function validateAndSubmit(event) {
            event.preventDefault();
            
            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            const passwordError = document.getElementById('passwordError');

            // Reset error message
            passwordError.style.display = 'none';

            // Check if passwords match
            if (password !== confirmPassword) {
                passwordError.style.display = 'block';
                return false;
            }

            // Create form data
            const formData = {
                username: username,
                email: email,
                password: password,
                confirmPassword: confirmPassword
            };

            // Submit form using AJAX
            $.ajax({
                type: 'POST',
                url: '../RegisterServlet',
                data: formData,
                success: function(response) {
                    if (response.status === 'success') {
                        alert('Registration successful! You will be redirected to login.');
                        window.location.href = 'login.html';
                    } else {
                        alert('Registration failed: ' + response.message);
                    }
                },
                error: function(xhr, status, error) {
                    alert('An error occurred: ' + error);
                }
            });

            return false;
        }

        // Real-time password match validation
        document.getElementById('confirmPassword').addEventListener('input', function() {
            const password = document.getElementById('password').value;
            const confirmPassword = this.value;
            const passwordError = document.getElementById('passwordError');

            if (password !== confirmPassword) {
                passwordError.style.display = 'block';
            } else {
                passwordError.style.display = 'none';
            }
        });
