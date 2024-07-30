const token = searchParam('token')

if (token) {
    localStorage.setItem("access_token", token)
}

function searchParam(key) {
    return new URLSearchParams(location.search).get(key);
}

function checkAuth() {
    const token = localStorage.getItem("access_token");
    const loginBtn = document.getElementById("login-btn");
    const logoutBtn = document.getElementById("logout-btn");

    if (token) {
        loginBtn.style.display = "none";
        logoutBtn.style.display = "block";
    } else {
        loginBtn.style.display = "block";
        logoutBtn.style.display = "none";
    }
}

function logout() {
    localStorage.removeItem("access_token");
    location.href = '/logout'; // Redirect to login page after logout
}

// Make functions available globally
window.checkAuth = checkAuth;
window.logout = logout;

document.addEventListener("DOMContentLoaded", function() {
    if (typeof window.checkAuth === 'function') {
        window.checkAuth(); // Call checkAuth from token.js
    }
});