document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('login-form');

    form.addEventListener('submit', function (event) {
        event.preventDefault(); // Prevent the default form submission
        const formData = new FormData(form);
        // Clear any existing messages
        let existingMessage = document.querySelector('.response-message');
        if (existingMessage) {
            existingMessage.remove();
        }
        // Send form data via AJAX
        fetch('../LoginServlet', {
            method: 'POST',
            body: new URLSearchParams(formData)
        })
        .then(response => response.json())
        .then(data => {
            const messageElement = document.createElement('p');
            messageElement.classList.add('response-message');

            if (data.status === "success") {
                localStorage.setItem('username', username);
                messageElement.textContent = data.message;
                messageElement.style.color = 'green';
                window.location.href = "../html/index.html";
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
