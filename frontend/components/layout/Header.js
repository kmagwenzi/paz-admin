'use client'

import { useState } from 'react'
import { useAuth } from '@/lib/auth'
import { Menu, X } from 'lucide-react'

export default function Header({ onMenuToggle }) {
  const { user, logout } = useAuth()
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false)

  const handleMenuToggle = () => {
    setIsMobileMenuOpen(!isMobileMenuOpen)
    onMenuToggle?.(!isMobileMenuOpen)
  }

  return (
    <header className="bg-white shadow-sm border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center">
            <button
              onClick={handleMenuToggle}
              className="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100"
            >
              {isMobileMenuOpen ? (
                <X className="h-6 w-6" />
              ) : (
                <Menu className="h-6 w-6" />
              )}
            </button>
            <h1 className="text-xl font-semibold text-gray-900 ml-2 lg:ml-0">PAZ Admin Portal</h1>
          </div>
          
          <div className="flex items-center space-x-4">
            <span className="hidden sm:block text-sm text-gray-600">
              Welcome, {user?.username || user?.email}
            </span>
            <button
              onClick={logout}
              className="bg-red-600 hover:bg-red-700 text-white px-3 py-2 rounded-md text-sm font-medium transition-colors"
            >
              Sign out
            </button>
          </div>
        </div>
      </div>
    </header>
  )
}