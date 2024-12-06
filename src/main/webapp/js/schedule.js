document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("add-course-form");
    const scheduleGrid = document.getElementById("schedule-grid").getElementsByTagName("tbody")[0];

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const formData = new FormData(form);
        const selectedDays = Array.from(formData.getAll("days[]"));
        
        if(selectedDays.length === 0) {
            alert("Please select at least one day");
            return;
        }

        const courseData = {
            courseCode: formData.get("course_code"),
            professor: formData.get("professor"),
            startTime: formData.get("start_time"),
            endTime: formData.get("end_time"),
            days: selectedDays.join(","),
            price: formData.get("price") || "0"
        };

        try {
            // Use relative path - the servlet will handle the full URL
            const response = await fetch("api/sell-course", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: new URLSearchParams({
                    courseCode: courseData.courseCode,
                    professor: courseData.professor,
                    startTime: courseData.startTime,
                    endTime: courseData.endTime,
                    days: courseData.days,
                    price: courseData.price
                }).toString()
            });

            const text = await response.text();
            
            if (response.ok) {
                alert("Course added successfully!");
                updateScheduleGrid(courseData);
                form.reset();
            } else {
                console.error("Server error response:", text);
                alert(`Failed to add course: ${text}`);
            }
        } catch (error) {
            console.error("Error:", error);
            alert("Error connecting to server. Please try again.");
        }
    });

    // Rest of your existing JavaScript code remains the same
});
