'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/lib/auth'

export default function LoginForm() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const router = useRouter()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')

    const result = await login({ username, password })

    if (result.success) {
      router.push('/dashboard')
    } else {
      setError(result.error)
    }
    setLoading(false)
  }

  return (
    <div className="w-full max-w-md mx-4 p-4 sm:p-6 bg-white rounded-lg shadow-md">
      <div className="text-center mb-6">
        <h1 className="text-xl sm:text-2xl font-bold text-gray-800">PAZ Admin Portal</h1>
        <p className="text-gray-600 mt-2 text-sm sm:text-base">Sign in to your account</p>
      </div>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-3 py-2 sm:px-4 sm:py-3 rounded mb-4 text-sm">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label htmlFor="username" className="block text-sm font-medium text-gray-700">
            Username
          </label>
          <input
            id="username"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 text-sm sm:text-base"
            placeholder="Enter your username"
          />
        </div>

        <div>
          <label htmlFor="password" className="block text-sm font-medium text-gray-700">
            Password
          </label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-primary-500 focus:border-primary-500 text-sm sm:text-base"
            placeholder="Enter your password"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50"
        >
          {loading ? 'Signing in...' : 'Sign in'}
        </button>
      </form>

      <div className="mt-6 text-center">
        <p className="text-xs sm:text-sm text-gray-600">
          Demo credentials: testuser / password123
        </p>
        <p className="text-xs sm:text-sm text-gray-600 mt-1">
          Or try: admin / password123
        </p>
      </div>
    </div>
  )
}
