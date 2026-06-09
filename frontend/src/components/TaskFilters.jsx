function TaskFilters({ filters, onChange }) {
  return (
    <div className="bg-white rounded-lg shadow p-4 mb-4 space-y-3">
      {/* Sökfält */}
      <input
        type="text"
        placeholder="Search tasks..."
        value={filters.search}
        onChange={(e) => onChange({ ...filters, search: e.target.value })}
        className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
      />

      <div className="flex flex-wrap gap-3">
        {/* Completed-filter */}
        <div className="flex rounded border border-gray-300 overflow-hidden text-sm">
          {[
            { label: "All", value: "all" },
            { label: "Active", value: "active" },
            { label: "Done", value: "done" },
          ].map((option) => (
            <button
              key={option.value}
              type="button"
              onClick={() => onChange({ ...filters, completed: option.value })}
              className={`px-3 py-2 transition-colors ${
                filters.completed === option.value
                  ? "bg-blue-600 text-white"
                  : "bg-white text-gray-600 hover:bg-gray-50"
              }`}
            >
              {option.label}
            </button>
          ))}
        </div>

        {/* Priority-filter */}
        <select
          value={filters.priority}
          onChange={(e) => onChange({ ...filters, priority: e.target.value })}
          className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="">All priorities</option>
          <option value="HIGH">High</option>
          <option value="MEDIUM">Medium</option>
          <option value="LOW">Low</option>
        </select>

        {/* Sortering */}
        <select
          value={filters.sortBy}
          onChange={(e) => onChange({ ...filters, sortBy: e.target.value })}
          className="border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="">Default order</option>
          <option value="title">Title (A-Z)</option>
          <option value="deadline">Deadline</option>
          <option value="priority">Priority</option>
        </select>
      </div>

      {/* Datumintervall */}
      <div className="flex gap-3">
        <div className="flex-1">
          <label className="block text-xs font-medium text-gray-500 mb-1">From</label>
          <input
            type="date"
            value={filters.dateFrom}
            onChange={(e) => onChange({ ...filters, dateFrom: e.target.value })}
            className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <div className="flex-1">
          <label className="block text-xs font-medium text-gray-500 mb-1">To</label>
          <input
            type="date"
            value={filters.dateTo}
            onChange={(e) => onChange({ ...filters, dateTo: e.target.value })}
            className="w-full border border-gray-300 rounded px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        {(filters.dateFrom || filters.dateTo || filters.search || filters.priority || filters.completed !== "all") && (
          <div className="flex items-end">
            <button
              type="button"
              onClick={() => onChange({
                search: "",
                completed: "all",
                priority: "",
                sortBy: "",
                dateFrom: "",
                dateTo: "",
              })}
              className="px-3 py-2 text-sm text-gray-500 hover:text-gray-700 border border-gray-300 rounded hover:bg-gray-50 transition-colors"
            >
              Clear
            </button>
          </div>
        )}
      </div>
    </div>
  )
}

export default TaskFilters