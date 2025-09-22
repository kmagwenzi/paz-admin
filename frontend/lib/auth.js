'use client'

import { createContext, useContext, useState, useEffect } from 'react'
import Cookies from 'js-cookie'

const AuthContext = createContext({})

export const useAuth = () => {
  return useContext(AuthContext)
}

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = Cookies.get('accessToken')
    if (token) {
      verifyToken(token)
    } else {
      setLoading(false)
    }
  }, [])

  const verifyToken = async (token) => {
    try {
      // For mock implementation, we'll decode the token directly
      const payload = JSON.parse(Buffer.from(token, 'base64').toString())
      if (payload && payload.sub) {
        // Set user from token payload
        setUser({
          id: payload.sub,
          email: payload.email,
          username: payload.username,
          roles: payload.roles
        })
      } else {
        logout()
      }
    } catch (error) {
      console.error('Token verification failed:', error)
      logout()
    } finally {
      setLoading(false)
    }
  }

  const login = async (credentials) => {
    try {
      const response = await fetch('/api/auth/signin', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
      })

      if (response.ok) {
        const { accessToken, refreshToken, user } = await response.json()
        Cookies.set('accessToken', accessToken, { secure: true })
        Cookies.set('refreshToken', refreshToken, { secure: true })
        setUser(user)
        return { success: true }
      } else {
        const errorData = await response.json().catch(() => ({}))
        return { success: false, error: errorData.message || 'Invalid credentials' }
      }
    } catch (error) {
      console.error('Login error:', error)
      return { success: false, error: 'Login failed. Please check if the backend server is running.' }
    }
  }

  const logout = () => {
    Cookies.remove('accessToken')
    Cookies.remove('refreshToken')
    setUser(null)
  }

  const value = {
    user,
    loading,
    login,
    logout,
  }

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}