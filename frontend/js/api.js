const API_BASE_URL = "http://localhost:8080/api";

async function apiRequest(endpoint, method = "GET", body = null, useAuth = true) {
    const headers = {
        "Content-Type": "application/json"
    };

    if (useAuth) {
        const token = localStorage.getItem("token");
        if (token) {
            headers["Authorization"] = `Bearer ${token}`;
        }
    }

    const config = {
        method,
        headers
    };

    if (body) {
        config.body = JSON.stringify(body);
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

    if (!response.ok) {
        const errorData = await response.json().catch(() => ({ message: "Erro desconhecido" }));
        throw new Error(errorData.message || `Erro ${response.status}`);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}