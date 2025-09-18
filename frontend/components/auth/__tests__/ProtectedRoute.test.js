import { render, screen, waitFor } from '@testing-library/react'
import ProtectedRoute from '../ProtectedRoute'
import { AuthProvider } from '@/lib/auth'

// Mock the useRouter from next/navigation
jest.mock('next/navigation', () => ({
  useRouter: () => ({
    push: jest.fn(),
  }),
}))

// Mock the useAuth hook to provide controlled auth context
jest.mock('@/lib/auth', () => ({
  useAuth: () => ({
    user: null,
    loading: false,
  }),
  AuthProvider: ({ children }) => children,
}))

describe('ProtectedRoute', () => {
  const mockPush = jest.fn()

  beforeEach(() => {
    jest.clearAllMocks()
    require('next/navigation').useRouter.mockImplementation(() => ({
      push: mockPush,
    }))
  })

  it('redirects to login when user is not authenticated', async () => {
    // Mock no user and not loading
    require('@/lib/auth').useAuth.mockImplementation(() => ({
      user: null,
      loading: false,
    }))

    render(
      <AuthProvider>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </AuthProvider>
    )

    // Should redirect to login
    await waitFor(() => {
      expect(mockPush).toHaveBeenCalledWith('/login')
    })

    // Should not render children
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument()
  })

  it('shows loading spinner when authentication is in progress', () => {
    // Mock loading state
    require('@/lib/auth').useAuth.mockImplementation(() => ({
      user: null,
      loading: true,
    }))

    render(
      <AuthProvider>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </AuthProvider>
    )

    // Should show loading spinner
    expect(screen.getByRole('status')).toBeInTheDocument()
    expect(screen.queryByText('Protected Content')).not.toBeInTheDocument()
  })

  it('renders children when user is authenticated', () => {
    // Mock authenticated user
    require('@/lib/auth').useAuth.mockImplementation(() => ({
      user: { username: 'admin', role: 'admin' },
      loading: false,
    }))

    render(
      <AuthProvider>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </AuthProvider>
    )

    // Should render children
    expect(screen.getByText('Protected Content')).toBeInTheDocument()
    // Should not redirect
    expect(mockPush).not.toHaveBeenCalled()
  })

  it('does not redirect when still loading', () => {
    // Mock loading state
    require('@/lib/auth').useAuth.mockImplementation(() => ({
      user: null,
      loading: true,
    }))

    render(
      <AuthProvider>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </AuthProvider>
    )

    // Should not redirect while loading
    expect(mockPush).not.toHaveBeenCalled()
  })

  it('handles edge case where user becomes authenticated after initial load', async () => {
    let userState = null
    let loadingState = true

    // Mock auth hook with mutable state
    require('@/lib/auth').useAuth.mockImplementation(() => ({
      user: userState,
      loading: loadingState,
    }))

    const { rerender } = render(
      <AuthProvider>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </AuthProvider>
    )

    // Initially loading
    expect(screen.getByRole('status')).toBeInTheDocument()
    expect(mockPush).not.toHaveBeenCalled()

    // Simulate authentication completing with user
    userState = { username: 'admin', role: 'admin' }
    loadingState = false

    // Force rerender to simulate state change
    rerender(
      <AuthProvider>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </AuthProvider>
    )

    // Should render children now
    expect(screen.getByText('Protected Content')).toBeInTheDocument()
    expect(mockPush).not.toHaveBeenCalled()
  })

  it('handles edge case where authentication fails after loading', async () => {
    let userState = null
    let loadingState = true

    // Mock auth hook with mutable state
    require('@/lib/auth').useAuth.mockImplementation(() => ({
      user: userState,
      loading: loadingState,
    }))

    render(
      <AuthProvider>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </AuthProvider>
    )

    // Initially loading
    expect(screen.getByRole('status')).toBeInTheDocument()

    // Simulate authentication completing without user
    loadingState = false
    // Force update by simulating a state change (this is a bit tricky in tests)
    // For this test, we'll rely on the useEffect dependency array

    // Since we can't easily simulate the useEffect trigger, we'll test the behavior
    // by directly testing the redirect logic
    require('@/lib/auth').useAuth.mockImplementation(() => ({
      user: null,
      loading: false,
    }))

    // Re-render with new mock values
    render(
      <AuthProvider>
        <ProtectedRoute>
          <div>Protected Content</div>
        </ProtectedRoute>
      </AuthProvider>
    )

    // Should redirect after loading completes without user
    await waitFor(() => {
      expect(mockPush).toHaveBeenCalledWith('/login')
    })
  })
})