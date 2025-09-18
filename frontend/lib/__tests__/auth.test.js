import { render, screen, act, waitFor } from '@testing-library/react'
import { AuthProvider, useAuth } from '../auth'
import Cookies from 'js-cookie'

// Test component to use the auth hook
const TestComponent = () => {
  const { user, loading, login, logout } = useAuth()
  return (
    <div>
      <div data-testid="user">{user ? user.username : 'No user'}</div>
      <div data-testid="loading">{loading ? 'Loading' : 'Not loading'}</div>
      <button onClick={() => login({ username: 'test', password: 'test' })}>Login</button>
      <button onClick={() => logout()}>Logout</button>
    </div>
  )
}

describe('AuthProvider', () => {
  beforeEach(() => {
    jest.clearAllMocks()
    Cookies.get.mockReturnValue(null)
    Cookies.set.mockImplementation(() => {})
    Cookies.remove.mockImplementation(() => {})
  })

  it('renders with initial state', () => {
    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    )

    expect(screen.getByTestId('user')).toHaveTextContent('No user')
    expect(screen.getByTestId('loading')).toHaveTextContent('Loading')
  })

  it('handles successful login', async () => {
    const mockUser = { username: 'testuser', role: 'admin' }
    const mockTokens = { accessToken: 'access123', refreshToken: 'refresh123' }

    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ ...mockTokens, user: mockUser })
    })

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    )

    await act(async () => {
      screen.getByText('Login').click()
    })

    await waitFor(() => {
      expect(screen.getByTestId('user')).toHaveTextContent('testuser')
    })

    expect(Cookies.set).toHaveBeenCalledWith('accessToken', 'access123', { secure: true })
    expect(Cookies.set).toHaveBeenCalledWith('refreshToken', 'refresh123', { secure: true })
    expect(screen.getByTestId('loading')).toHaveTextContent('Not loading')
  })

  it('handles failed login', async () => {
    global.fetch.mockResolvedValueOnce({
      ok: false,
    })

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    )

    await act(async () => {
      screen.getByText('Login').click()
    })

    // User should remain null after failed login
    expect(screen.getByTestId('user')).toHaveTextContent('No user')
    expect(screen.getByTestId('loading')).toHaveTextContent('Not loading')
  })

  it('handles logout', async () => {
    // First, set up a logged-in state
    const mockUser = { username: 'testuser', role: 'admin' }
    const mockTokens = { accessToken: 'access123', refreshToken: 'refresh123' }

    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ ...mockTokens, user: mockUser })
    })

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    )

    await act(async () => {
      screen.getByText('Login').click()
    })

    await waitFor(() => {
      expect(screen.getByTestId('user')).toHaveTextContent('testuser')
    })

    // Now logout
    await act(async () => {
      screen.getByText('Logout').click()
    })

    expect(Cookies.remove).toHaveBeenCalledWith('accessToken')
    expect(Cookies.remove).toHaveBeenCalledWith('refreshToken')
    expect(screen.getByTestId('user')).toHaveTextContent('No user')
  })

  it('verifies token on mount if token exists', async () => {
    Cookies.get.mockReturnValue('existingToken')
    
    global.fetch.mockResolvedValueOnce({
      ok: true,
      json: async () => ({ username: 'existingUser', role: 'teacher' })
    })

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    )

    await waitFor(() => {
      expect(screen.getByTestId('user')).toHaveTextContent('existingUser')
    })

    expect(global.fetch).toHaveBeenCalledWith('/api/auth/verify', {
      headers: {
        Authorization: 'Bearer existingToken'
      }
    })
  })

  it('handles token verification failure', async () => {
    Cookies.get.mockReturnValue('invalidToken')
    
    global.fetch.mockResolvedValueOnce({
      ok: false,
    })

    render(
      <AuthProvider>
        <TestComponent />
      </AuthProvider>
    )

    await waitFor(() => {
      expect(screen.getByTestId('loading')).toHaveTextContent('Not loading')
    })

    expect(screen.getByTestId('user')).toHaveTextContent('No user')
    expect(Cookies.remove).toHaveBeenCalledWith('accessToken')
    expect(Cookies.remove).toHaveBeenCalledWith('refreshToken')
  })
})