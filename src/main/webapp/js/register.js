document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('register-form');

    form.addEventListener('submit', function (event) {
        event.preventDefault(); // Prevent the default form submission

        // Clear any existing messages
        let existingMessage = document.querySelector('.response-message');
        if (existingMessage) {
            existingMessage.remove();
        }

        // Get form data
        const formData = new FormData(form);
        const password = formData.get('password');
        const confirmPassword = formData.get('confirmPassword');

        // Client-side validation for matching passwords
        if (password !== confirmPassword) {
            const messageElement = document.createElement('p');
            messageElement.classList.add('response-message');
            messageElement.textContent = "Passwords do not match. Please try again.";
            messageElement.style.color = 'red';
            form.insertAdjacentElement('afterend', messageElement);
            return; // Stop here if passwords don't match
        }

        // If passwords match, proceed with submission
        fetch('RegisterServlet', {
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
                // Optionally do something on success, like redirect
                // setTimeout(() => window.location.href = 'login.html', 2000);
            } else {
                messageElement.textContent = data.message;
                messageElement.style.color = 'red';
            }

            form.insertAdjacentElement('afterend', messageElement);
        })
        .catch(error => {
            console.error('Error:', error);
        });
    });
});
