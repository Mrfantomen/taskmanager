const BASE_URL = "http://localhost:8080"

function getCsrfToken() {
  return document.cookie
    .split("; ")
    .find((row) => row.startsWith("XSRF-TOKEN="))
    ?.split("=")[1]
}

export async function apiFetch(path, options = {}) {
  const method = options.method || "GET"
  const headers = { ...options.headers }

  if (["POST", "PUT", "DELETE", "PATCH"].includes(method)) {
    const token = getCsrfToken()
    if (token) {
      headers["X-XSRF-TOKEN"] = token
    }
  }

  if (options.body && !headers["Content-Type"]) {
    headers["Content-Type"] = "application/json"
  }

  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers,
    credentials: "include",
  })

  return response
}