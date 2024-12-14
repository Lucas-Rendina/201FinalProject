
const CLIENT_ID = "739920793998-44jtfedt0dt7grf33ngq4j5jqhmuqr11.apps.googleusercontent.com";
const API_KEY = "AIzaSyAUuF8QEjNLc5sHsU7ywjWVVrhMBHzbhw8";
const DISCOVERY_DOCS = ["https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest"];
const SCOPES = "https://www.googleapis.com/auth/calendar.events";

let tokenClient;
let gapiInitialized = false;
let gisInitialized = false;

const authorizeButton = document.getElementById("authorize-button");
const signoutButton = document.getElementById("signout-button");
const content = document.getElementById("content");
const eventsList = document.getElementById("events-list");
const eventForm = document.getElementById("event-form");

function gapiLoaded() {
	console.log("Google API client library loaded.");
	gapi.load("client", async () => {
		await gapi.client.init({
			apiKey: API_KEY,
			discoveryDocs: DISCOVERY_DOCS,
		});
		console.log("Google API client initialized.");
		gapiInitialized = true;
		maybeEnableButtons();
	});
}

function gisLoaded() {
	console.log("Google Identity Services library loaded.");
	tokenClient = google.accounts.oauth2.initTokenClient({
		client_id: CLIENT_ID,
		scope: SCOPES,
		prompt: 'select_account', 
		callback: handleTokenResponse,
	});
	gisInitialized = true;
	maybeEnableButtons();
}

function maybeEnableButtons() {
	console.log("Checking if buttons can be enabled...");
	console.log("gapiInitialized:", gapiInitialized);
	console.log("gisInitialized:", gisInitialized);

	if (gapiInitialized && gisInitialized) {
		console.log("All libraries initialized. Enabling the authorize button.");
		authorizeButton.disabled = false;
	}
}

function handleTokenResponse(response) {
	if (response.error) {
		console.error("Error during token retrieval:", response.error);
		return;
	}
	console.log("Access token received:", response.access_token);
	content.style.display = "block";
	authorizeButton.style.display = "none";

	listUpcomingEvents();
}

authorizeButton.onclick = () => {
	console.log("Authorize button clicked.");
	tokenClient.requestAccessToken();
};

signoutButton.onclick = () => {
	console.log("Sign out button clicked.");
	google.accounts.oauth2.revoke(tokenClient.access_token, () => {
		console.log("Access token revoked.");
		content.style.display = "none";
		eventsList.innerHTML = "";
	});
};

function listUpcomingEvents() {
	gapi.client.calendar.events.list({
		calendarId: "primary",
		timeMin: new Date().toISOString(),
		showDeleted: false,
		singleEvents: true,
		maxResults: 10,
		orderBy: "startTime",
	}).then((response) => {
		const events = response.result.items;
		eventsList.innerHTML = "";
		if (events.length > 0) {
			events.forEach((event) => {
				const li = document.createElement("li");
				const start = event.start.dateTime || event.start.date;
				li.textContent = `${start} - ${event.summary}`;
				eventsList.appendChild(li);
			});
		} else {
			eventsList.textContent = "No upcoming events found.";
		}
	}).catch((error) => {
		console.error("Error listing events:", error);
	});
}

function createEvent(e) {
	e.preventDefault();

	const summary = document.getElementById("summary").value;
	const startTime = document.getElementById("start-time").value;
	const endTime = document.getElementById("end-time").value;

	if (!summary || !startTime || !endTime) {
		alert("Please fill out all event details.");
		return;
	}

	const event = {
		summary: summary,
		start: {
			dateTime: new Date(startTime).toISOString(),
			timeZone: "America/Los_Angeles",
		},
		end: {
			dateTime: new Date(endTime).toISOString(),
			timeZone: "America/Los_Angeles",
		},
	};

	console.log("Attempting to create event:", event);

	gapi.client.calendar.events.insert({
		calendarId: "primary",
		resource: event,
	}).then((response) => {
		console.log("Event created successfully:", response.result);
		alert("Event created: " + response.result.htmlLink);
		listUpcomingEvents();
	}).catch((error) => {
		console.error("Error creating event:", error);
		alert("Error creating event. Check console for details.");
	});
}

eventForm.addEventListener("submit", createEvent);
