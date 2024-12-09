document.addEventListener('DOMContentLoaded', function() {
	function updateAuthLink(isLoggedIn) {
		const authLink = document.getElementById('authLink');
		if (!authLink) return;

		if (isLoggedIn) {
			authLink.textContent = 'Account';
			authLink.href = 'account.html';
		} else {
			authLink.textContent = 'Login';
			authLink.href = 'login.html';
		}
	}

	fetch('../AccountServlet?action=checkLogin')
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.json();
		})
		.then(data => {
			updateAuthLink(data.loggedIn);
		})
		.catch(error => {
			console.error('Error checking login status:', error);
			updateAuthLink(false);
		});

	window.addEventListener('userLoggedIn', function() {
		updateAuthLink(true);
	});

	window.addEventListener('userLoggedOut', function() {
		updateAuthLink(false);
	});
});

window.updateAuthStatus = function(isLoggedIn) {
	const event = new CustomEvent(isLoggedIn ? 'userLoggedIn' : 'userLoggedOut');
	window.dispatchEvent(event);
};