# Online Complaint Management System — Java Backend

Backend for the uploaded HTML/CSS/JavaScript Resolve Complaint Portal. It uses **Java 25, Spring Boot REST API, Spring Data JPA, and SQLite**. Complaints and notifications persist in `ocms.db`.

## Requirements
- JDK 25
- Maven 3.9+
- Internet on first run so Maven can download dependencies

## Run in VS Code / terminal
1. Extract this ZIP.
2. Open the `ocms-backend` folder in VS Code.
3. Run `mvn spring-boot:run` in the terminal.
4. Backend starts at `http://localhost:8080`. SQLite database `ocms.db` is created automatically and sample data is seeded on first run.

## REST API
| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/complaints` | List complaints; optional `?status=Resolved&search=water` |
| GET | `/api/complaints/CMP-1048` | Complaint details |
| POST | `/api/complaints` | Submit a complaint; JSON body below |
| PATCH | `/api/complaints/CMP-1048/status` | Update status/assignment/remarks (admin workflow) |
| GET | `/api/stats` | Dashboard totals |
| GET | `/api/notifications` | List notifications |
| PATCH | `/api/notifications/1/read` | Mark one notification read |
| PATCH | `/api/notifications/read-all` | Mark all notifications read |

### POST `/api/complaints` body
```json
{
  "category": "Water Supply",
  "title": "Low water pressure",
  "description": "Water pressure is low on our street.",
  "location": "North Street"
}
```
The backend generates a `CMP-####` reference, calculates priority using the same rule-based logic as the frontend, assigns a team, stores the complaint, and creates a notification.

### PATCH status body
```json
{
  "status": "In Progress",
  "assigned": "Water Department",
  "remarks": "Inspection scheduled for tomorrow."
}
```
Allowed statuses: `Submitted`, `Under Review`, `Assigned`, `In Progress`, `Resolved`, `Closed`, `Escalated`.

## Important frontend integration note
Your current `script.js` explicitly uses browser `localStorage`; it will **not automatically call this backend**. Replace its localStorage reads/writes and submit handler with `fetch()` calls to these endpoints to connect the UI. Keep the frontend and backend running separately, and open the frontend using VS Code Live Server (or another local web server). Do not open the frontend with `file://` if browser CORS restrictions cause issues.

## Notes
- The starter backend is suitable for a college project/demo.
- It does not include login/roles yet; add authentication and authorization before deploying publicly.
- SQLite file is stored in the backend working directory.
