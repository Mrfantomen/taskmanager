import { useState, useEffect, useRef } from "react"

function CategoryCombobox({ categories, selectedCategories, onToggle, onCreate }) {
  const [input, setInput] = useState("")
  const [open, setOpen] = useState(false)
  const [newColor, setNewColor] = useState("#3b82f6")
  const [showColorPicker, setShowColorPicker] = useState(false)
  const ref = useRef(null)

  useEffect(() => {
    function handleClickOutside(e) {
      if (ref.current && !ref.current.contains(e.target)) {
        setOpen(false)
        setShowColorPicker(false)
      }
    }
    document.addEventListener("mousedown", handleClickOutside)
    return () => document.removeEventListener("mousedown", handleClickOutside)
  }, [])

  const filtered = categories.filter((cat) =>
    cat.name.toLowerCase().includes(input.toLowerCase())
  )

  const exactMatch = categories.some(
    (cat) => cat.name.toLowerCase() === input.toLowerCase().trim()
  )

  async function handleCreate() {
    if (!input.trim()) return
    await onCreate(input.trim(), newColor)
    setInput("")
    setNewColor("#3b82f6")
    setShowColorPicker(false)
  }

  return (
    <div ref={ref} className="relative">
      <div className="flex gap-2">
        <input
          type="text"
          value={input}
          onChange={(e) => {
            setInput(e.target.value)
            setOpen(true)
            setShowColorPicker(false)
          }}
          onFocus={() => setOpen(true)}
          placeholder="Search or create category..."
          className="w-full border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        {input.trim() && !exactMatch && (
          <button
            type="button"
            onClick={() => setShowColorPicker((prev) => !prev)}
            className="text-white px-3 py-2 rounded transition-colors whitespace-nowrap text-sm font-medium border-2 border-white shadow"
            style={{ backgroundColor: newColor }}
          >
            + Create
          </button>
        )}
      </div>

      {showColorPicker && input.trim() && !exactMatch && (
        <div className="absolute z-20 mt-1 bg-white border border-gray-200 rounded-lg shadow-lg p-4 w-full">
          <p className="text-sm font-medium text-gray-700 mb-3">
            Choose color for <span className="font-bold">"{input.trim()}"</span>
          </p>
          <div className="flex gap-2 flex-wrap mb-3">
            {[
              "#ef4444", "#f97316", "#eab308", "#22c55e",
              "#14b8a6", "#3b82f6", "#8b5cf6", "#ec4899",
              "#64748b", "#000000"
            ].map((color) => (
              <button
                key={color}
                type="button"
                onClick={() => setNewColor(color)}
                className={`w-8 h-8 rounded-full border-2 transition-transform hover:scale-110 ${
                  newColor === color ? "border-gray-800 scale-110" : "border-transparent"
                }`}
                style={{ backgroundColor: color }}
              />
            ))}
          </div>
          <div className="flex items-center gap-2 mb-3">
            <label className="text-sm text-gray-600">Custom:</label>
            <input
              type="color"
              value={newColor}
              onChange={(e) => setNewColor(e.target.value)}
              className="w-10 h-8 rounded cursor-pointer border border-gray-300"
            />
            <span className="text-sm text-gray-500 font-mono">{newColor}</span>
          </div>
          <button
            type="button"
            onClick={handleCreate}
            className="w-full text-white py-2 rounded text-sm font-medium hover:opacity-90 transition-opacity"
            style={{ backgroundColor: newColor }}
          >
            Create "{input.trim()}"
          </button>
        </div>
      )}

      {open && !showColorPicker && (
        <div className="absolute z-10 w-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg max-h-48 overflow-y-auto">
          {filtered.length === 0 && (
            <p className="px-3 py-2 text-sm text-gray-400">No categories found</p>
          )}
          {filtered.map((cat) => (
            <button
              key={cat.id}
              type="button"
              onClick={() => {
                onToggle(cat.id)
                setInput("")
                setOpen(false)
              }}
              className="w-full text-left px-3 py-2 text-sm hover:bg-gray-50 flex items-center justify-between"
            >
              <span className="flex items-center gap-2">
                <span
                  className="w-3 h-3 rounded-full"
                  style={{ backgroundColor: cat.color || "#94a3b8" }}
                />
                {cat.name}
              </span>
              {selectedCategories.includes(cat.id) && (
                <span className="text-blue-600">✓</span>
              )}
            </button>
          ))}
        </div>
      )}

      {selectedCategories.length > 0 && (
        <div className="flex flex-wrap gap-2 mt-2">
          {categories
            .filter((cat) => selectedCategories.includes(cat.id))
            .map((cat) => (
              <span
                key={cat.id}
                className="flex items-center gap-1 px-2 py-1 rounded-full text-xs text-white font-medium"
                style={{ backgroundColor: cat.color || "#94a3b8" }}
              >
                {cat.name}
                <button
                  type="button"
                  onClick={() => onToggle(cat.id)}
                  className="hover:opacity-75"
                >
                  ×
                </button>
              </span>
            ))}
        </div>
      )}
    </div>
  )
}

export default CategoryCombobox