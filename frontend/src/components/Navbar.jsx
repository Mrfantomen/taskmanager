function Navbar({ user, onLogout }) {
  async function handleLogout() {
    await fetch("http://localhost:8080/auth/logout", {
      method: "POST",
      credentials: "include",
    })
    onLogout()
  }

  return (
    <nav className="bg-white shadow-sm px-6 py-4 flex justify-between items-center">
      <h1 className="text-xl font-bold text-blue-600">Task Manager</h1>
      <div className="flex items-center gap-4">
        {user && (
          <span className="text-sm text-gray-600">
            Logged in as <span className="font-medium text-gray-800">{user.username}</span>
            {user.role === "ADMIN" && (
              <span className="ml-2 bg-purple-100 text-purple-700 text-xs px-2 py-1 rounded-full font-medium">
                ADMIN
              </span>
            )}
          </span>
        )}
        <button
          onClick={handleLogout}
          className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600 transition-colors text-sm"
        >
          Log out
        </button>
      </div>
    </nav>
  )
}

export default Navbar