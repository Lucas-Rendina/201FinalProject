document.addEventListener('DOMContentLoaded', function() {
    console.log('Account page loaded');

    // Initialize account functionality
    initializeAccount();
});

function initializeAccount() {
    // Check if user is logged in first
    checkLoginStatus();

    // Setup sign out button
    const signoutBtn = document.getElementById('signoutBtn');
    if (signoutBtn) {
        signoutBtn.addEventListener('click', handleSignOut);
    } else {
        console.error('Sign out button not found');
    }
}

function checkLoginStatus() {
    console.log('Checking login status...');
    fetch('../AccountServlet?action=checkLogin')
        .then(response => {
            console.log('Response received:', response);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('Login status:', data);
            if (!data.loggedIn) {
                console.log('User not logged in, redirecting to login page');
                window.location.href = 'login.html';
            } else {
                console.log('User is logged in, loading account data');
                // Only load the rest of the account data if logged in
                loadUserInfo();
                loadEnrolledCourses();
            }
        })
        .catch(error => {
            console.error('Error checking login status:', error);
            window.location.href = 'login.html';
        });
}

function loadUserInfo() {
    console.log('Loading user info...');
    fetch('../AccountServlet?action=getUserInfo')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('User info received:', data);
            const usernameElement = document.getElementById('displayUsername');
            const emailElement = document.getElementById('displayEmail');
            
            if (usernameElement && data.username) {
                usernameElement.textContent = data.username;
            }
            if (emailElement && data.email) {
                emailElement.textContent = data.email;
            }
        })
        .catch(error => {
            console.error('Error loading user info:', error);
            // Handle error - maybe show an error message to user
        });
}

function loadEnrolledCourses() {
    console.log('Loading enrolled courses...');
    fetch('../AccountServlet?action=getEnrolledCourses')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('Courses data received:', data);
            const coursesList = document.getElementById('coursesList');
            if (!coursesList) {
                console.error('Courses list element not found');
                return;
            }

            coursesList.innerHTML = '';
            
            if (data.courses && data.courses.length > 0) {
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
            } else {
                // Show a message if no courses are enrolled
                coursesList.innerHTML = '<p class="no-courses">No courses enrolled yet.</p>';
            }
        })
        .catch(error => {
            console.error('Error loading enrolled courses:', error);
            const coursesList = document.getElementById('coursesList');
            if (coursesList) {
                coursesList.innerHTML = '<p class="error-message">Error loading courses. Please try again later.</p>';
            }
        });
}

function handleSignOut() {
    console.log('Handling sign out...');
    fetch('../AccountServlet?action=signout', { 
        method: 'POST'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('Sign out response:', data);
            if (data.status === 'success') {
                // Update navbar before redirecting
                if (window.updateAuthStatus) {
                    window.updateAuthStatus(false);
                }
                // Redirect to index page
                window.location.href = 'index.html';
            } else {
                console.error('Sign out failed:', data.message);
            }
        })
        .catch(error => {
            console.error('Error during sign out:', error);
            // Maybe show an error message to the user
            alert('Error signing out. Please try again.');
        });
}