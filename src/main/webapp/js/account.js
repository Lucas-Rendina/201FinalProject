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

                    const courseCard = document.createElement('div');

                    courseCard.className = 'course-card';

                    courseCard.innerHTML = `

                        <h3>${course.courseCode}</h3>

                        <p><strong>Professor:</strong> ${course.professor}</p>

                        <p><strong>Time:</strong> ${course.stime}</p>

                        <p><strong>Contact:</strong> ${course.contact}</p>

                        <button class="remove-course-btn" data-coursecode="${course.courseCode}">Remove Course</button>

                    `;

                    coursesList.appendChild(courseCard);



                    // Add click handler for the remove button

                    const removeBtn = courseCard.querySelector('.remove-course-btn');

                    removeBtn.addEventListener('click', () => removeCourse(course.courseCode, courseCard));

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



function removeCourse(courseCode, courseCard) {

    if (confirm('Are you sure you want to remove this course?')) {

        fetch('../AccountServlet', {

            method: 'POST',

            headers: {

                'Content-Type': 'application/x-www-form-urlencoded',

            },

            body: `action=removeCourse&courseCode=${courseCode}`

        })

        .then(response => response.json())

        .then(data => {

            if (data.status === 'success') {

                courseCard.remove(); // Remove the card from the DOM

                

                // Check if there are any courses left

                const coursesList = document.getElementById('coursesList');

                if (coursesList.children.length === 0) {

                    coursesList.innerHTML = '<p class="no-courses">No courses enrolled yet.</p>';

                }

            } else {

                alert('Error removing course: ' + data.message);

            }

        })

        .catch(error => {

            console.error('Error:', error);

            alert('Error removing course. Please try again.');

        });

    }

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