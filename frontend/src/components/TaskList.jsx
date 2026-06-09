import { useState, useEffect } from "react"
import CreateTaskForm from "./CreateTaskForm"
import EditTaskForm from "./EditTaskForm"
import TaskFilters from "./TaskFilters"
import { apiFetch } from "../api"

const PRIORITY_ORDER = { HIGH: 0, MEDIUM: 1, LOW: 2, null: 3, undefined: 3 }

function TaskList() {
  const [tasks, setTasks] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [editingId, setEditingId] = useState(null)
  const [filters, setFilters] = useState({
    search: "",
    completed: "all",
    priority: "",
    sortBy: "",
    dateFrom: "",
    dateTo: "",
  })

  useEffect(() => {
    apiFetch("/tasks")
      .then((res) => {
        if (!res.ok) throw new Error("Failed to fetch tasks")
        return res.json()
      })
      .then((data) => {
        setTasks(data)
        setLoading(false)
      })
      .catch(() => {
        setError("Could not load tasks")
        setLoading(false)
      })
  }, [])

  function handleTaskCreated(newTask) {
    setTasks((prev) => [...prev, newTask])
  }

  function handleTaskSaved(updatedTask) {
    setTasks((prev) =>
      prev.map((t) => (t.id === updatedTask.id ? updatedTask : t))
    )
    setEditingId(null)
  }

  async function handleDelete(id) {
    if (!confirm("Delete this task?")) return
    const response = await apiFetch(`/tasks/${id}`, { method: "DELETE" })
    if (response.ok) {
      setTasks((prev) => prev.filter((t) => t.id !== id))
    }
  }

  async function handleToggleComplete(task) {
    const body = {
      title: task.title,
      description: task.description,
      completed: !task.completed,
      deadline: task.deadline || null,
      priority: task.priority || null,
      categoryIds: task.categories ? task.categories.map((c) => c.id) : [],
    }
    const response = await apiFetch(`/tasks/${task.id}`, {
      method: "PUT",
      body: JSON.stringify(body),
    })
    if (response.ok) {
      const updated = await response.json()
      setTasks((prev) => prev.map((t) => (t.id === updated.id ? updated : t)))
    }
  }

  // Filtrera och sortera tasks på frontend
  const filteredTasks = tasks
    .filter((task) => {
      if (filters.search && !task.title.toLowerCase().includes(filters.search.toLowerCase()))
        return false
      if (filters.completed === "active" && task.completed) return false
      if (filters.completed === "done" && !task.completed) return false
      if (filters.priority && task.priority !== filters.priority) return false
      if (filters.dateFrom && task.deadline && task.deadline < filters.dateFrom) return false
      if (filters.dateTo && task.deadline && task.deadline > filters.dateTo) return false
      return true
    })
    .sort((a, b) => {
      if (filters.sortBy === "title")
        return a.title.localeCompare(b.title)
      if (filters.sortBy === "deadline") {
        if (!a.deadline) return 1
        if (!b.deadline) return -1
        return a.deadline.localeCompare(b.deadline)
      }
      if (filters.sortBy === "priority")
        return PRIORITY_ORDER[a.priority] - PRIORITY_ORDER[b.priority]
      return 0
    })

  return (
    <div className="max-w-2xl mx-auto">
      <h2 className="text-xl font-bold text-gray-800 mb-4">My Tasks</h2>

      <CreateTaskForm onTaskCreated={handleTaskCreated} />

      <TaskFilters filters={filters} onChange={setFilters} />

      {loading && <p className="text-gray-500">Loading tasks...</p>}
      {error && <p className="text-red-500">{error}</p>}

      <ul className="space-y-3">
        {filteredTasks.map((task) => (
          <li key={task.id} className="bg-white rounded-lg shadow p-4">
            {editingId === task.id ? (
              <EditTaskForm
                task={task}
                onSave={handleTaskSaved}
                onCancel={() => setEditingId(null)}
              />
            ) : (
              <>
                <div className="flex justify-between items-start gap-2">
                  <div className="flex items-start gap-3 flex-1">
                    <input
                      type="checkbox"
                      checked={task.completed}
                      onChange={() => handleToggleComplete(task)}
                      className="mt-1 w-4 h-4 rounded cursor-pointer accent-blue-600"
                    />
                    <div className="flex-1">
                      <p className={`font-medium ${task.completed ? "line-through text-gray-400" : "text-gray-800"}`}>
                        {task.title}
                      </p>
                      {task.description && (
                        <p className="text-sm text-gray-500 mt-1">{task.description}</p>
                      )}
                      <div className="flex flex-wrap gap-2 mt-2">
                        {task.priority && (
                          <span className={`text-xs px-2 py-1 rounded-full font-medium ${
                            task.priority === "HIGH" ? "bg-red-100 text-red-700" :
                            task.priority === "MEDIUM" ? "bg-yellow-100 text-yellow-700" :
                            "bg-green-100 text-green-700"
                          }`}>
                            {task.priority}
                          </span>
                        )}
                        {task.deadline && (
                          <span className="text-xs text-gray-400 self-center">{task.deadline}</span>
                        )}
                        {task.categories && task.categories.map((cat) => (
                          <span
                            key={cat.id}
                            className="text-xs px-2 py-1 rounded-full text-white font-medium"
                            style={{ backgroundColor: cat.color || "#94a3b8" }}
                          >
                            {cat.name}
                          </span>
                        ))}
                      </div>
                    </div>
                  </div>
                  <div className="flex gap-2 shrink-0">
                    <button
                      onClick={() => setEditingId(task.id)}
                      className="text-sm text-blue-600 hover:text-blue-800"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => handleDelete(task.id)}
                      className="text-sm text-red-500 hover:text-red-700"
                    >
                      Delete
                    </button>
                  </div>
                </div>
              </>
            )}
          </li>
        ))}
      </ul>

      {!loading && filteredTasks.length === 0 && tasks.length > 0 && (
        <p className="text-gray-500 text-center mt-8">No tasks match your filters.</p>
      )}
      {!loading && tasks.length === 0 && (
        <p className="text-gray-500 text-center mt-8">No tasks yet.</p>
      )}
    </div>
  )
}

export default TaskList