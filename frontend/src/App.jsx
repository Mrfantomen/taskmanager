import { useState } from "react"
import LoginPage from "./components/LoginPage"
import TaskList from "./components/TaskList"
import Navbar from "./components/Navbar"

function App() {
  const [user, setUser] = useState(null)

  async function handleLogin() {
    const response = await fetch("http://localhost:8080/auth/me", {
      credentials: "include",
    })
    const data = await response.json()
    setUser(data)
  }

  function handleLogout() {
    setUser(null)
  }

  if (!user) {
    return <LoginPage onLogin={handleLogin} />
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <Navbar user={user} onLogout={handleLogout} />
      <div className="p-8">
        <TaskList />
      </div>
    </div>
  )
}

export default App