const $ = (id) => document.getElementById(id);
const API_BASE = "http://localhost:8080/api";
let complaints = [];
let notifications = [];

async function api(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
    ...options
  });
  if (!response.ok) {
    let message = `Request failed (${response.status})`;
    try { const body = await response.json(); message = body.message || body.error || message; } catch {}
    throw new Error(message);
  }
  if (response.status === 204) return null;
  return response.json();
}
async function loadData() {
  try {
    [complaints, notifications] = await Promise.all([
      api("/complaints"), api("/notifications")
    ]);
    render();
  } catch (error) {
    toast("Cannot connect to backend. Start the Java server on port 8080.");
    console.error(error);
  }
}
function save() { /* Data is saved by the Java backend in SQLite. */ }
function toast(message) {
  $("toastText").textContent = message;
  $("toast").classList.add("show");
  clearTimeout(toast.timer);
  toast.timer = setTimeout(() => $("toast").classList.remove("show"), 2800);
}
function dateLabel(date) {
  const d = new Date(date + "T12:00:00");
  return d.toLocaleDateString("en-IN", {day:"2-digit", month:"short", year:"numeric"});
}
function statusClass(status) {
  return "status-" + status.toLowerCase().replaceAll(" ", "-");
}
function priorityClass(priority) {
  return "priority-" + priority.toLowerCase();
}
function statusBadge(status) {
  return `<span class="status ${statusClass(status)}">${status}</span>`;
}
function priorityBadge(priority) {
  return `<span class="priority ${priorityClass(priority)}">${priority}</span>`;
}
function complaintCell(c) {
  return `<div class="complaint-id">${c.id}</div><div class="complaint-name" title="${escapeHTML(c.title)}">${escapeHTML(c.title)}</div>`;
}
function escapeHTML(value) {
  return String(value).replace(/[&<>"']/g, ch => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[ch]));
}
function renderRecent() {
  $("recentRows").innerHTML = complaints.slice(0,5).map(c => `<tr>
    <td>${complaintCell(c)}</td><td>${escapeHTML(c.category)}</td><td>${dateLabel(c.date)}</td>
    <td>${statusBadge(c.status)}</td><td><button class="text-btn" data-detail="${c.id}">View</button></td></tr>`).join("");
}
function renderAll() {
  const query = ($("searchInput")?.value || "").toLowerCase().trim();
  const filter = $("statusFilter")?.value || "All";
  const filtered = complaints.filter(c => (filter === "All" || c.status === filter) &&
    [c.id,c.title,c.category,c.status].some(x => x.toLowerCase().includes(query)));
  $("allRows").innerHTML = filtered.map(c => `<tr>
    <td>${complaintCell(c)}</td><td>${escapeHTML(c.category)}</td><td>${dateLabel(c.date)}</td>
    <td>${priorityBadge(c.priority)}</td><td>${statusBadge(c.status)}</td><td>${escapeHTML(c.assigned)}</td></tr>`).join("") ||
    `<tr><td colspan="6" style="text-align:center;padding:30px">No complaints match your search.</td></tr>`;
  $("resultCount").textContent = `Showing ${filtered.length} of ${complaints.length} complaints`;
}
function renderStats() {
  $("totalStat").textContent = String(complaints.length).padStart(2,"0");
  $("progressStat").textContent = String(complaints.filter(c => ["Submitted","Under Review","Assigned","In Progress"].includes(c.status)).length).padStart(2,"0");
  $("resolvedStat").textContent = String(complaints.filter(c => ["Resolved","Closed"].includes(c.status)).length).padStart(2,"0");
  $("attentionStat").textContent = String(complaints.filter(c => c.status === "Escalated").length).padStart(2,"0");
  $("notificationCount").textContent = notifications.filter(n => !n.read).length;
  $("notificationCount").style.display = notifications.some(n => !n.read) ? "" : "none";
}
function renderNotifications() {
  $("notificationList").innerHTML = notifications.map((n,i) => `<article class="notification-item ${n.read ? "read" : ""}">
    <span class="notification-symbol ${n.type === "success" ? "green" : n.type === "warning" ? "amber" : ""}">${n.type === "success" ? "✓" : n.type === "warning" ? "!" : "♧"}</span>
    <div class="notification-body"><strong>${escapeHTML(n.title)}</strong><p>${escapeHTML(n.message)}</p><small>${escapeHTML(n.time || (n.createdAt ? new Date(n.createdAt).toLocaleString("en-IN", {dateStyle:"medium",timeStyle:"short"}) : ""))}</small></div>
    <span class="unread-dot" title="${n.read ? "Read" : "Unread"}"></span>
    </article>`).join("") || `<p style="padding:24px;color:#8a90a4">You’re all caught up. No notifications yet.</p>`;
}
function render() { renderStats(); renderRecent(); renderAll(); renderNotifications(); }
function navigate(page) {
  document.querySelectorAll(".page").forEach(p => p.classList.toggle("active", p.id === `page-${page}`));
  document.querySelectorAll(".nav-item").forEach(b => b.classList.toggle("active", b.dataset.page === page));
  const names = {dashboard:"Dashboard",complaints:"My Complaints",new:"Submit a Complaint",notifications:"Notifications"};
  $("breadcrumbTitle").textContent = names[page] || "Dashboard";
  if (page === "complaints") renderAll();
  if (page === "notifications") renderNotifications();
  window.scrollTo({top:0,behavior:"smooth"});
}
function addNotification(title,message,type="info") {
  notifications.unshift({title,message,time:new Date().toLocaleString("en-IN",{dateStyle:"medium",timeStyle:"short"}),type,read:false});
}
function scorePriority(category, description) {
  const weights = {"Electricity":4,"Water Supply":3,"Sanitation":3,"Road Maintenance":2,"Billing Issues":2,"Other":1};
  const text = description.toLowerCase();
  let score = weights[category] || 1;
  if (["danger","accident","fire","shock","overflow","no supply"].some(w => text.includes(w))) score += 3;
  if (text.includes("again") || text.includes("repeated")) score += 1;
  return {score,priority:score >= 6 ? "High" : score >= 3 ? "Medium" : "Low"};
}
function showDetail(id) {
  const c = complaints.find(x => x.id === id);
  if (!c) return;
  alert(`${c.id} — ${c.title}\n\nCategory: ${c.category}\nPriority: ${c.priority}\nStatus: ${c.status}\nAssigned to: ${c.assigned}\nSubmitted: ${dateLabel(c.date)}\nLocation: ${c.location || "Not provided"}\n\nDescription: ${c.description}\n\nLatest remarks: ${c.remarks || "No remarks yet"}`);
}
document.addEventListener("click", e => {
  const nav = e.target.closest("[data-page]");
  const go = e.target.closest("[data-go]");
  const detail = e.target.closest("[data-detail]");
  if (nav) navigate(nav.dataset.page);
  if (go) navigate(go.dataset.go);
  if (detail) showDetail(detail.dataset.detail);
});
$("complaintForm").addEventListener("submit", async e => {
  e.preventDefault();
  const category = $("category").value;
  const title = $("title").value.trim();
  const description = $("description").value.trim();
  const location = $("location").value.trim();
  if (!category || !title || !description) { toast("Please complete all required fields."); return; }
  try {
    const created = await api("/complaints", { method: "POST", body: JSON.stringify({category,title,description,location}) });
    $("complaintForm").reset();
    await loadData();
    navigate("complaints");
    toast(`Complaint submitted. Reference: ${created.id}`);
  } catch (error) { toast(error.message || "Unable to submit complaint."); }

});
$("searchInput").addEventListener("input", renderAll);
$("statusFilter").addEventListener("change", renderAll);
$("markReadBtn").addEventListener("click", async () => {
  try { await api("/notifications/read-all", {method:"PATCH"}); await loadData(); toast("All notifications marked as read."); }
  catch (error) { toast(error.message || "Unable to update notifications."); }
});
$("topNotifications").addEventListener("click", () => navigate("notifications"));
$("helpBtn").addEventListener("click", () => alert("Support desk\n\nFor this project demo, contact your system administrator or department officer."));
$("profileBtn").addEventListener("click", () => alert("Citizen account\nName: Sham Francis\nRole: User\n\nThis is a frontend demonstration profile."));
$("todayLabel").textContent = new Date().toLocaleDateString("en-IN",{weekday:"short",day:"2-digit",month:"short",year:"numeric"});
loadData();
