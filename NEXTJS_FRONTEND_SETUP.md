# ⚛️ Next.js Frontend Setup with Basic Routing

## Project Structure and Configuration

### Next.js 14 App Directory Structure

```
frontend/
├── app/
│   ├── globals.css
│   ├── layout.js
│   ├── page.js
│   ├── login/
│   │   └── page.js
│   ├── tasks/
│   │   ├── new/
│   │   │   └── page.js
│   │   ├── [id]/
│   │   │   └── page.js
│   │   └── page.js
│   ├── prints/
│   │   ├── new/
│   │   │   └── page.js
│   │   ├── [id]/
│   │   │   └── page.js
│   │   └── page.js
│   ├── classes/
│   │   ├── new/
│   │   │   └── page.js
│   │   ├── [id]/
│   │   │   └── page.js
│   │   └── page.js
│   └── api/
│       └── auth/
│           └── route.js
├── components/
│   ├── ui/
│   │   ├── ZimDatePicker.js
│   │   ├── PrintBtn.js
│   │   ├── LoadShedBanner.js
│   │   └── EcoCashTotal.js
│   ├── layout/
│   │   ├── Header.js
│   │   ├── Sidebar.js
│   │   └── Footer.js
│   ├── auth/
│   │   ├── LoginForm.js
│   │   └── ProtectedRoute.js
│   └── common/
│       ├── LoadingSpinner.js
│       └── ErrorBoundary.js
├── lib/
│   ├── api.js
│   ├── auth.js
│   ├── constants.js
│   └── utils.js
├── styles/
│   └── tailwind.css
├── public/
│   ├── images/
│   └── icons/
├── next.config.js
├── tailwind.config.js
├── postcss.config.js
└── package.json
```

### Package.json Dependencies

```json
{
  "name": "paz-admin-frontend",
  "version": "0.1.0",
  "private": true,
  "scripts": {
    "dev": "next dev",
    "build": "next build",
    "start": "next start",
    "lint": "next lint"
  },
  "dependencies": {
    "next": "14.0.0",
    "react": "^18.0.0",
    "react-dom": "^18.0.0",
    "axios": "^1.6.0",
    "js-cookie": "^3.0.5",
    "react-hook-form": "^7.48.0",
    "react-datepicker": "^4.25.0",
    "qrcode.react": "^3.1.0"
  },
  "devDependencies": {
    "autoprefixer": "^10.4.16",
    "postcss": "^8.4.31",
    "tailwindcss": "^3.3.5",
    "eslint": "^8.53.0",
    "eslint-config-next": "14.0.0"
  }
}
```

### Tailwind CSS Configuration

**tailwind.config.js:**
```javascript
/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './pages/**/*.{js,ts,jsx,tsx,mdx}',
    './components/**/*.{js,ts,jsx,tsx,mdx}',
    './app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#f0f9ff',
          500: '#0ea5e9',
          600: '#0284c7',
          700: '#0369a1',
        },
        secondary: {
          500: '#64748b',
          600: '#475569',
        },
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
```

### Next.js Configuration

**next.config.js:**
```javascript
/** @type {import('next').NextConfig} */
const nextConfig = {
  experimental: {
    appDir: true,
  },
  env: {
    API_URL: process.env.API_URL || 'http://localhost:8080/api',
  },
  async redirects() {
    return [
      {
        source: '/',
        destination: '/dashboard',
        permanent: false,
      },
    ]
  },
}

module.exports = nextConfig
```

### Routing Implementation

**app/layout.js:**
```javascript
import { Inter } from 'next/font/google'
import './globals.css'
import { AuthProvider } from '@/lib/auth'

const inter = Inter({ subsets: ['latin'] })

export const metadata = {
  title: 'PAZ Admin Portal',
  description: 'Digital administration system for Prison Alliance Zimbabwe',
}

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <AuthProvider>
          {children}
        </AuthProvider>
      </body>
    </html>
  )
}
```

**app/page.js (Control Panel):**
```javascript
import { redirect } from 'next/navigation'

export default function HomePage() {
  redirect('/dashboard')
}
```

**app/login/page.js:**
```javascript
'use client'

import LoginForm from '@/components/auth/LoginForm'

export default function LoginPage() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <LoginForm />
    </div>
  )
}
```

### Key UI Components Implementation

**components/ui/ZimDatePicker.js:**
```javascript
'use client'

import DatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css'

export default function ZimDatePicker({ value, onChange, disabled }) {
  const isPastSunday = (date) => {
    const day = date.getDay()
    return day === 0 && date < new Date()
  }

  return (
    <DatePicker
      selected={value}
      onChange={onChange}
      filterDate={isPastSunday}
      dateFormat="dd/MM/yyyy"
      className="border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-primary-500"
      disabled={disabled}
    />
  )
}
```

**components/ui/PrintBtn.js:**
```javascript
'use client'

export default function PrintBtn({ blobUrl, label = 'Print' }) {
  const handlePrint = () => {
    if (blobUrl) {
      const printWindow = window.open(blobUrl, '_blank')
      printWindow?.addEventListener('load', () => {
        printWindow.print()
      })
    }
  }

  return (
    <button
      onClick={handlePrint}
      className="bg-primary-600 text-white px-4 py-2 rounded hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-primary-500"
    >
      {label}
    </button>
  )
}
```

**components/ui/LoadShedBanner.js:**
```javascript
'use client'

import { useState, useEffect } from 'react'

const loadSheddingStages = {
  0: { message: 'No load shedding', color: 'bg-green-100 text-green-800' },
  1: { message: 'Stage 1 - Minimal impact', color: 'bg-yellow-100 text-yellow-800' },
  2: { message: 'Stage 2 - Moderate impact', color: 'bg-orange-100 text-orange-800' },
  3: { message: 'Stage 3 - Auto-save every 30s', color: 'bg-red-100 text-red-800' },
  4: { message: 'Stage 4+ - Severe impact', color: 'bg-red-200 text-red-900' },
}

export default function LoadShedBanner() {
  const [stage, setStage] = useState(0)

  useEffect(() => {
    // Simulate stage detection - in real app, this would come from an API
    const interval = setInterval(() => {
      const randomStage = Math.floor(Math.random() * 5)
      setStage(randomStage)
    }, 30000)

    return () => clearInterval(interval)
  }, [])

  if (stage === 0) return null

  return (
    <div className={`px-4 py-2 text-center ${loadSheddingStages[stage].color}`}>
      Grid at Stage {stage} – {loadSheddingStages[stage].message}
    </div>
  )
}
```

**components/ui/EcoCashTotal.js:**
```javascript
'use client'

import { useState, useEffect } from 'react'

export default function EcoCashTotal({ usdAmount }) {
  const [exchangeRate, setExchangeRate] = useState(0)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchExchangeRate = async () => {
      try {
        // Simulate API call to get RBZ mid-rate
        const response = await fetch('/api/exchange-rate')
        const data = await response.json()
        setExchangeRate(data.midRate * 1.15) // 15% buffer
      } catch (error) {
        console.error('Failed to fetch exchange rate:', error)
        setExchangeRate(12000) // Fallback rate
      } finally {
        setLoading(false)
      }
    }

    fetchExchangeRate()
  }, [])

  if (loading) {
    return <div className="text-gray-600">Loading exchange rate...</div>
  }

  const zwlAmount = usdAmount * exchangeRate

  return (
    <div className="bg-gray-50 p-3 rounded">
      <div className="text-sm text-gray-600">USD: ${usdAmount.toFixed(2)}</div>
      <div className="text-lg font-semibold">
        ZWL: {zwlAmount.toLocaleString('en-ZW')}
      </div>
      <div className="text-xs text-gray-500">
        Rate: 1 USD = {exchangeRate.toLocaleString('en-ZW')} ZWL (incl. 15% buffer)
      </div>
    </div>
  )
}
```

### Authentication Setup

**lib/auth.js:**
```javascript
'use client'

import { useState, useEffect } from 'react'
import Cookies from 'js-cookie'

export const useAuth = () => {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = Cookies.get('accessToken')
    if (token) {
      // Verify token and get user info
      verifyToken(token)
    } else {
      setLoading(false)
    }
  }, [])

  const verifyToken = async (token) => {
    try {
      const response = await fetch('/api/auth/verify', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      if (response.ok) {
        const userData = await response.json()
        setUser(userData)
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
      const response = await fetch('/api/auth/login', {
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
        return { success: false, error: 'Invalid credentials' }
      }
    } catch (error) {
      return { success: false, error: 'Login failed' }
    }
  }

  const logout = () => {
    Cookies.remove('accessToken')
    Cookies.remove('refreshToken')
    setUser(null)
  }

  return { user, loading, login, logout }
}
```

### API Integration

**lib/api.js:**
```javascript
import axios from 'axios'
import Cookies from 'js-cookie'

const api = axios.create({
  baseURL: process.env.API_URL || 'http://localhost:8080/api',
})

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = Cookies.get('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor to handle token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true

      try {
        const refreshToken = Cookies.get('refreshToken')
        const response = await axios.post('/api/auth/refresh', { refreshToken })
        
        const { accessToken } = response.data
        Cookies.set('accessToken', accessToken)
        
        originalRequest.headers.Authorization = `Bearer ${accessToken}`
        return api(originalRequest)
      } catch (refreshError) {
        Cookies.remove('accessToken')
        Cookies.remove('refreshToken')
        window.location.href = '/login'
        return Promise.reject(refreshError)
      }
    }

    return Promise.reject(error)
  }
)

export default api
```

### Next Steps for Implementation

1. **Create Page Components**: Implement all route pages based on the PRD requirements
2. **Add Form Handling**: Implement react-hook-form for all form submissions
3. **Setup State Management**: Consider using Zustand or Context API for global state
4. **Implement Error Boundaries**: Add proper error handling throughout the app
5. **Add Loading States**: Implement loading indicators for better UX
6. **Test Responsive Design**: Ensure mobile compatibility
7. **Implement SEO**: Add meta tags and structured data
8. **Add PWA Support**: Configure next-pwa for offline capability

This setup provides a solid foundation for the PAZ Admin Portal frontend with proper routing, authentication, and key components as specified in the PRD.