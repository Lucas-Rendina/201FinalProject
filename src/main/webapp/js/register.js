
		function validateAndSubmit(event) {
			event.preventDefault();
			
			const username = document.getElementById('username').value;
			const email = document.getElementById('email').value;
			const password = document.getElementById('password').value;
			const confirmPassword = document.getElementById('confirmPassword').value;
			const passwordError = document.getElementById('passwordError');

			passwordError.style.display = 'none';

			if (password !== confirmPassword) {
				passwordError.style.display = 'block';
				return false;
			}

			const formData = {
				username: username,
				email: email,
				password: password,
				confirmPassword: confirmPassword
			};

			// AJAX
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
