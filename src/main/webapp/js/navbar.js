document.addEventListener('DOMContentLoaded', function() {
    // Check login status and update navbar
    fetch('AccountServlet?action=checkLogin')
        .then(response => response.json())
        .then(data => {
            const accountLink = document.getElementById('accountLink');
            if (data.loggedIn) {
                accountLink.textContent = 'Account';
                accountLink.href = 'account.html';
            } else {
                accountLink.textContent = 'Login';
                accountLink.href = 'login.html';
            }
        })
        .catch(error => console.error('Error:', error));
});