async function apiRequest(url, method = "GET", data = null, isAuth = false) {
    const headers = {
        "Content-Type": "application/json"
    };

    if (isAuth) {
        const token = localStorage.getItem("token");
        headers["Authorization"] = "Bearer " + token;
    }

    const response = await fetch(BASE_URL + url, {
        method,
        headers,
        body: data ? JSON.stringify(data) : null
    });

    return response.json();
}