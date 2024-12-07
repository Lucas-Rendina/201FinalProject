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
    fetch('../AccountServlet?action=checkLogin')
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
    fetch('../AccountServlet?action=getUserInfo')
        .then(response => response.json())
        .then(data => {
            document.getElementById('displayUsername').textContent = data.username;
            document.getElementById('displayEmail').textContent = data.email;
        })
        .catch(error => console.error('Error:', error));
}

function loadEnrolledCourses() {
    console.log('Loading enrolled courses...');
    fetch('../AccountServlet?action=getEnrolledCourses')
        .then(response => response.json())
        .then(data => {
            console.log('Received courses data:', data);
            const coursesList = document.getElementById('coursesList');
            coursesList.innerHTML = '';
            
            if (data.courses && data.courses.length > 0) {
                data.courses.forEach(course => {
                    console.log('Processing course:', course);
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
            } else {
                console.log('No courses found');
                coursesList.innerHTML = '<p class="no-courses">No courses enrolled yet.</p>';
            }
        })
        .catch(error => {
            console.error('Error loading courses:', error);
            const coursesList = document.getElementById('coursesList');
            if (coursesList) {
                coursesList.innerHTML = '<p class="error-message">Error loading courses. Please try again later.</p>';
            }
        });
}

function handleSignOut() {
    fetch('../AccountServlet?action=signout', { method: 'POST' })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'success') {
                window.location.href = 'index.html';
            }
        })
        .catch(error => console.error('Error:', error));
}