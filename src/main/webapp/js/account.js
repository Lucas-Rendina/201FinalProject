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

            

            // Update courses list

            const coursesList = document.getElementById('coursesList');

            coursesList.innerHTML = '';

            

            // Update calendar view

            const calendarView = document.getElementById('calendarView');

            calendarView.innerHTML = '';

            

            if (data.courses && data.courses.length > 0) {

                // Create course cards

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

                

                // Create and populate calendar

                const calendar = createCalendarStructure();

                calendarView.appendChild(calendar);

                data.courses.forEach(course => {

                    addCourseToCalendar(course);

                });

            } else {

                console.log('No courses found');

                coursesList.innerHTML = '<p class="no-courses">No courses enrolled yet.</p>';

                calendarView.innerHTML = '<p class="no-courses">No courses to display in calendar.</p>';

            }

        })

        .catch(error => {

            console.error('Error loading courses:', error);

            const coursesList = document.getElementById('coursesList');

            const calendarView = document.getElementById('calendarView');

            if (coursesList && calendarView) {

                coursesList.innerHTML = '<p class="error-message">Error loading courses. Please try again later.</p>';

                calendarView.innerHTML = '<p class="error-message">Error loading calendar. Please try again later.</p>';

            }

        });

}



function createCalendarStructure() {

    const calendar = document.createElement('div');

    calendar.className = 'calendar-grid';

    

    // Create header row

    const header = document.createElement('div');

    header.className = 'calendar-header';

    const timeHeader = document.createElement('div');

    timeHeader.className = 'time-header';

    timeHeader.textContent = 'Time';

    header.appendChild(timeHeader);

    

    ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'].forEach(day => {

        const dayHeader = document.createElement('div');

        dayHeader.className = 'day-header';

        dayHeader.textContent = day;

        header.appendChild(dayHeader);

    });

    

    calendar.appendChild(header);

    

    // Create time slots (every 30 minutes)

    for (let hour = 8; hour <= 17; hour++) {

        for (let minute of [0, 30]) {

            const timeRow = document.createElement('div');

            timeRow.className = 'calendar-row';

            

            const timeCell = document.createElement('div');

            timeCell.className = 'time-cell';

            timeCell.textContent = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;

            timeRow.appendChild(timeCell);

            

            // Add empty cells for each day

            for (let day = 0; day < 5; day++) {

                const dayCell = document.createElement('div');

                dayCell.className = 'day-cell';

                dayCell.dataset.day = day;

                dayCell.dataset.hour = hour;

                dayCell.dataset.minute = minute;

                timeRow.appendChild(dayCell);

            }

            

            calendar.appendChild(timeRow);

        }

    }

    

    return calendar;

}



function addCourseToCalendar(course) {

    const [daysStr, time] = course.stime.split(' ');

    const [startTime, endTime] = time.split('-');

    

    // Convert time to minutes since 8:00

    const getMinutes = (timeStr) => {

        const [hours, minutes] = timeStr.split(':').map(Number);

        return (hours - 8) * 60 + minutes;

    };

    

    const startMinutes = getMinutes(startTime);

    const endMinutes = getMinutes(endTime);

    const duration = endMinutes - startMinutes;

    

    // Parse days string to handle two-character days (Th) correctly

    const getDays = (daysStr) => {

        const days = [];

        for (let i = 0; i < daysStr.length; i++) {

            if (daysStr[i] === 'T' && i + 1 < daysStr.length && daysStr[i + 1] === 'h') {

                days.push('Th');

                i++; // Skip the 'h'

            } else {

                days.push(daysStr[i]);

            }

        }

        return days;

    };

    

    // Map days to column indices

    const dayMap = {

        'M': 0,

        'T': 1,

        'W': 2,

        'Th': 3,

        'F': 4

    };

    

    // Generate a unique color for this course

    const courseColor = `var(--course-color-${course.courseCode.replace(/\s+/g, '-')})`;

    

    // Parse days and create course blocks

    const days = getDays(daysStr);

    days.forEach(day => {

        if (dayMap.hasOwnProperty(day)) {

            const courseElement = document.createElement('div');

            courseElement.className = 'course-block';

            courseElement.dataset.courseCode = course.courseCode;

            courseElement.innerHTML = `

                <h3>${course.courseCode}</h3>

                <p>${course.professor}</p>

                <p>${startTime}-${endTime}</p>

            `;

            

            courseElement.style.top = `${startMinutes}px`;

            courseElement.style.height = `${duration}px`;

            courseElement.style.setProperty('--course-color', courseColor);

            

            const dayColumn = document.querySelector(`.day-cell[data-day="${dayMap[day]}"]`);

            if (dayColumn) {

                dayColumn.appendChild(courseElement);

            }

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

                // Remove the course card

                courseCard.remove();

                

                // Reload the entire schedule

                fetch('../AccountServlet?action=getEnrolledCourses')

                    .then(response => response.json())

                    .then(data => {

                        const calendarView = document.getElementById('calendarView');

                        calendarView.innerHTML = '';

                        

                        if (data.courses && data.courses.length > 0) {

                            // Recreate and populate calendar

                            const calendar = createCalendarStructure();

                            calendarView.appendChild(calendar);

                            data.courses.forEach(course => {

                                addCourseToCalendar(course);

                            });

                        } else {

                            calendarView.innerHTML = '<p class="no-courses">No courses to display in calendar.</p>';

                        }

                    })

                    .catch(error => {

                        console.error('Error reloading schedule:', error);

                        calendarView.innerHTML = '<p class="error-message">Error loading calendar. Please try again later.</p>';

                    });

                

                // Check if there are any courses left in the list

                const remainingCourseCards = document.querySelectorAll('.course-card');

                if (remainingCourseCards.length === 0) {

                    const coursesList = document.getElementById('coursesList');

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