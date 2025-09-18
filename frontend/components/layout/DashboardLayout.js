'use client'

import { useState } from 'react'
import Header from './Header'
import Sidebar from './Sidebar'

export default function DashboardLayout({ children }) {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false)

  const handleMenuToggle = (isOpen) => {
    setIsSidebarOpen(isOpen)
  }

  return (
    <div className="h-screen flex flex-col">
      <Header onMenuToggle={handleMenuToggle} />
      <div className="flex flex-1 overflow-hidden">
        {/* Sidebar for desktop and mobile */}
        <div className={`
          fixed inset-y-0 left-0 z-50 w-64 bg-white border-r border-gray-200 transform transition-transform duration-300 ease-in-out
          lg:static lg:translate-x-0
          ${isSidebarOpen ? 'translate-x-0' : '-translate-x-full'}
        `}>
          <Sidebar />
        </div>
        
        {/* Overlay for mobile */}
        {isSidebarOpen && (
          <div
            className="fixed inset-0 bg-black bg-opacity-50 z-40 lg:hidden"
            onClick={() => setIsSidebarOpen(false)}
          />
        )}

        <main className="flex-1 overflow-auto bg-gray-50 lg:ml-0">
          <div className="p-4 sm:p-6">
            {children}
          </div>
        </main>
      </div>
    </div>
  )
}