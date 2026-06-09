import { useState, useEffect, useRef } from "react"
import { apiFetch } from "../api"
import CategoryCombobox from "./CategoryCombobox"


function CreateTaskForm({ onTaskCreated }) {
  const [title, setTitle] = useState("")
  const [description, setDescription] = useState("")
  const [deadline, setDeadline] = useState("")
  const [priority, setPriority] = useState("")
  const [selectedCategories, setSelectedCategories] = useState([])
  const [categories, setCategories] = useState([])
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)
  const [open, setOpen] = useState(false)

  useEffect(() => {
    if (open) {
      apiFetch("/categories")
        .then((res) => res.json())
        .then((data) => setCategories(data))
        .catch(() => setCategories([]))
    }
  }, [open])

  function toggleCategory(id) {
    setSelectedCategories((prev) =>
      prev.includes(id) ? prev.filter((c) => c !== id) : [...prev, id]
    )
  }

 async function handleCreateCategory(name, color) {
  try {
    const response = await apiFetch("/categories", {
      method: "POST",
      body: JSON.stringify({ name, color }),
    })
    if (response.ok) {
      const newCat = await response.json()
      setCategories((prev) => [...prev, newCat])
      setSelectedCategories((prev) => [...prev, newCat.id])
    }
  } catch {
  }
}
  async function handleSubmit(e) {
    e.preventDefault()
    setLoading(true)
    setError("")

    const body = {
      title,
      description,
      completed: false,
      deadline: deadline || null,
      priority: priority || null,
      categoryIds: selectedCategories,
    }

    try {
      const response = await apiFetch("/tasks", {
        method: "POST",
        body: JSON.stringify(body),
      })

      if (response.ok) {
        const newTask = await response.json()
        onTaskCreated(newTask)
        setTitle("")
        setDescription("")
        setDeadline("")
        setPriority("")
        setSelectedCategories([])
        setOpen(false)
      } else {
        const data = await response.json()
        setError(data.message || "Could not create task")
      }
    } catch {
      setError("Could not connect to server")
    } finally {
      setLoading(false)
    }
  }

  if (!open) {
    return (
      <button
        onClick={() => setOpen(true)}
        className="mb-6 bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition-colors"
      >
        + New Task
      </button>
    )
  }

  return (
    <div className="bg-white rounded-lg shadow p-6 mb-6">
      <h2 className="text-lg font-bold text-gray-800 mb-4">New Task</h2>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Title <span className="text-red-500">*</span>
          </label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Description
          </label>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            rows={2}
          />
        </div>

        <div className="flex gap-4">
          <div className="flex-1">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Deadline
            </label>
            <input
              type="date"
              value={deadline}
              onChange={(e) => setDeadline(e.target.value)}
              className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div className="flex-1">
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Priority
            </label>
            <select
              value={priority}
              onChange={(e) => setPriority(e.target.value)}
              className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">None</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Categories
          </label>
          <CategoryCombobox
            categories={categories}
            selectedCategories={selectedCategories}
            onToggle={toggleCategory}
            onCreate={handleCreateCategory}
          />
        </div>

        {error && <p className="text-red-500 text-sm">{error}</p>}

        <div className="flex gap-3">
          <button
            type="submit"
            disabled={loading}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50 transition-colors"
          >
            {loading ? "Creating..." : "Create Task"}
          </button>
          <button
            type="button"
            onClick={() => setOpen(false)}
            className="bg-gray-200 text-gray-700 px-4 py-2 rounded hover:bg-gray-300 transition-colors"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  )
}

export default CreateTaskForm