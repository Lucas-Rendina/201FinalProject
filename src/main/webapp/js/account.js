document.addEventListener('DOMContentLoaded', function() {
    // Check if user is logged in
    checkLoginStatus();
    
    // Load user information
    loadUserInfo();
    
    // Load enrolled courses
    loadEnrolledCourses();
    
    // Setup sign out button
    document.getElementById('signoutBtn').addEventListener('click', handleSignOut);
});

function checkLoginStatus() {
    fetch('AccountServlet?action=checkLogin')
        .then(response => response.json())
        .then(data => {
            if (!data.loggedIn) {
                window.location.href = 'login.html';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            window.location.href = 'login.html';
        });
}

function loadUserInfo() {
    fetch('AccountServlet?action=getUserInfo')
        .then(response => response.json())
        .then(data => {
            document.getElementById('displayUsername').textContent = data.username;
            document.getElementById('displayEmail').textContent = data.email;
        })
        .catch(error => console.error('Error:', error));
}

function loadEnrolledCourses() {
    fetch('AccountServlet?action=getEnrolledCourses')
        .then(response => response.json())
        .then(data => {
            const coursesList = document.getElementById('coursesList');
            coursesList.innerHTML = '';
            
            data.courses.forEach(course => {
                const courseCard = document.createElement('div');
                courseCard.className = 'course-card';
                courseCard.innerHTML = `
                    <h3>${course.courseCode}</h3>
                    <p><strong>Professor:</strong> ${course.professor}</p>
                    <p><strong>Time:</strong> ${course.stime}</p>
                    <p><strong>Contact:</strong> ${course.contact}</p>
                `;
                coursesList.appendChild(courseCard);
            });
        })
        .catch(error => console.error('Error:', error));
}

function handleSignOut() {
    fetch('AccountServlet?action=signout', { method: 'POST' })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'success') {
                window.location.href = 'index.html';
            }
        })
        .catch(error => console.error('Error:', error));
}