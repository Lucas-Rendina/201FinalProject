		$(document).ready(function() {
			// Load courses on page load
			$.get("../MarketplaceServlet", function(data) {
				displayCourses(data);
			});

			$("#searchCourse").on("keyup", function() {
				let value = $(this).val().toLowerCase();
				$(".course-card").filter(function() {
					$(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
				});
			});

			function displayCourses(courses) {
				const container = $("#courseContainer");
				container.empty();
				
				courses.forEach(course => {
					const card = $(`
						<div class="course-card">
							<h3>${course.courseCode}</h3>
							<p class="professor">Professor: ${course.professor}</p>
							<p class="time">Time: ${course.stime}</p>
							<p class="contact">Contact: ${course.contact}</p>
							<button class="add-course" data-courseid="${course.courseCode}">Add Course</button>
						</div>
					`);
					
					container.append(card);
				});

				$(".add-course").click(function() {
					const courseCode = $(this).data("courseid");
					$.post("../MarketplaceServlet", {
						action: "add",
						courseCode: courseCode
					}, function(response) {
						if (response.status === "success") {
							alert("Course added successfully!");
						} else {
							alert("Error adding course: " + response.message);
						}
					});
				});
			}
		});