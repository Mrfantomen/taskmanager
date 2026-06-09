import { useState, useEffect } from "react"
import { apiFetch } from "../api"
import CategoryCombobox from "./CategoryCombobox"

function EditTaskForm({ task, onSave, onCancel }) {
  const [title, setTitle] = useState(task.title)
  const [description, setDescription] = useState(task.description || "")
  const [deadline, setDeadline] = useState(task.deadline || "")
  const [priority, setPriority] = useState(task.priority || "")
  const [selectedCategories, setSelectedCategories] = useState(
    task.categories ? task.categories.map((c) => c.id) : []
  )
  const [categories, setCategories] = useState(task.categories || [])
  const [allCategories, setAllCategories] = useState([])
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    apiFetch("/categories")
      .then((res) => res.json())
      .then((data) => setAllCategories(data))
      .catch(() => setAllCategories([]))
  }, [])

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
        setAllCategories((prev) => [...prev, newCat])
        setSelectedCategories((prev) => [...prev, newCat.id])
      }
    } catch {
      // tyst fel
    }
  }

  async function handleSave() {
    setLoading(true)
    setError("")

    const body = {
      title,
      description,
      completed: task.completed,
      deadline: deadline || null,
      priority: priority || null,
      categoryIds: selectedCategories,
    }

    try {
      const response = await apiFetch(`/tasks/${task.id}`, {
        method: "PUT",
        body: JSON.stringify(body),
      })

      if (response.ok) {
        const updated = await response.json()
        onSave(updated)
      } else {
        const data = await response.json()
        setError(data.message || "Could not save task")
      }
    } catch {
      setError("Could not connect to server")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-3">
      <input
        type="text"
        value={title}
        onChange={(e) => setTitle(e.target.value)}
        className="w-full border border-gray-300 rounded px-3 py-2 font-medium focus:outline-none focus:ring-2 focus:ring-blue-500"
      />

      <textarea
        value={description}
        onChange={(e) => setDescription(e.target.value)}
        placeholder="Description"
        className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        rows={2}
      />

      <div className="flex gap-3">
        <div className="flex-1">
          <label className="block text-xs font-medium text-gray-500 mb-1">Deadline</label>
          <input
            type="date"
            value={deadline}
            onChange={(e) => setDeadline(e.target.value)}
            className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <div className="flex-1">
          <label className="block text-xs font-medium text-gray-500 mb-1">Priority</label>
          <select
            value={priority}
            onChange={(e) => setPriority(e.target.value)}
            className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">None</option>
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
          </select>
        </div>
      </div>

      <div>
        <label className="block text-xs font-medium text-gray-500 mb-1">Categories</label>
        <CategoryCombobox
          categories={allCategories}
          selectedCategories={selectedCategories}
          onToggle={toggleCategory}
          onCreate={handleCreateCategory}
        />
      </div>

      {error && <p className="text-red-500 text-sm">{error}</p>}

      <div className="flex gap-2 pt-1">
        <button
          type="button"
          onClick={handleSave}
          disabled={loading}
          className="bg-blue-600 text-white px-4 py-2 rounded text-sm hover:bg-blue-700 disabled:opacity-50 transition-colors"
        >
          {loading ? "Saving..." : "Save"}
        </button>
        <button
          type="button"
          onClick={onCancel}
          className="bg-gray-200 text-gray-700 px-4 py-2 rounded text-sm hover:bg-gray-300 transition-colors"
        >
          Cancel
        </button>
      </div>
    </div>
  )
}

export default EditTaskForm