document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('login-form');

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
        });
    });
});
