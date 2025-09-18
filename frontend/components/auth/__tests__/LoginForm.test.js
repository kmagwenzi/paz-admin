import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import LoginForm from '../LoginForm'
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
    login: jest.fn(),
  }),
  AuthProvider: ({ children }) => children,
}))

describe('LoginForm', () => {
  const mockLogin = jest.fn()

  beforeEach(() => {
    jest.clearAllMocks()
    // Reset the mock implementation for each test
    mockLogin.mockReset()
    require('@/lib/auth').useAuth.mockImplementation(() => ({
      login: mockLogin,
    }))
  })

  it('renders the login form with all elements', () => {
    render(
      <AuthProvider>
        <LoginForm />
      </AuthProvider>
    )

    expect(screen.getByText('PAZ Admin Portal')).toBeInTheDocument()
    expect(screen.getByText('Sign in to your account')).toBeInTheDocument()
    expect(screen.getByLabelText('Email Address')).toBeInTheDocument()
    expect(screen.getByLabelText('Password')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Sign in' })).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Enter your email')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Enter your password')).toBeInTheDocument()
    expect(screen.getByText('Demo credentials: admin@paz.org / password123')).toBeInTheDocument()
  })

  it('shows error message when login fails', async () => {
    mockLogin.mockResolvedValue({ success: false, error: 'Invalid credentials' })

    render(
      <AuthProvider>
        <LoginForm />
      </AuthProvider>
    )

    // Fill out the form
    fireEvent.change(screen.getByLabelText('Email Address'), {
      target: { value: 'test@example.com' },
    })
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'wrongpassword' },
    })

    // Submit the form
    fireEvent.click(screen.getByRole('button', { name: 'Sign in' }))

    // Wait for the error to appear
    await waitFor(() => {
      expect(screen.getByText('Invalid credentials')).toBeInTheDocument()
    })
  })

  it('disables the submit button when loading', async () => {
    mockLogin.mockImplementation(() => new Promise(() => {})) // Never resolves to simulate loading

    render(
      <AuthProvider>
        <LoginForm />
      </AuthProvider>
    )

    // Fill out the form
    fireEvent.change(screen.getByLabelText('Email Address'), {
      target: { value: 'test@example.com' },
    })
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'password123' },
    })

    // Submit the form
    fireEvent.click(screen.getByRole('button', { name: 'Sign in' }))

    // Button should be disabled during loading
    await waitFor(() => {
      expect(screen.getByRole('button', { name: 'Signing in...' })).toBeDisabled()
    })
  })

  it('calls login function with correct credentials', async () => {
    mockLogin.mockResolvedValue({ success: true })

    render(
      <AuthProvider>
        <LoginForm />
      </AuthProvider>
    )

    const email = 'admin@paz.org'
    const password = 'password123'

    // Fill out the form
    fireEvent.change(screen.getByLabelText('Email Address'), {
      target: { value: email },
    })
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: password },
    })

    // Submit the form
    fireEvent.click(screen.getByRole('button', { name: 'Sign in' }))

    // Check that login was called with correct credentials
    await waitFor(() => {
      expect(mockLogin).toHaveBeenCalledWith({ email, password })
    })
  })

  it('shows validation errors for empty fields', async () => {
    render(
      <AuthProvider>
        <LoginForm />
      </AuthProvider>
    )

    // Try to submit without filling fields
    fireEvent.click(screen.getByRole('button', { name: 'Sign in' }))

    // HTML5 validation should prevent submission, but we can check that login wasn't called
    await waitFor(() => {
      expect(mockLogin).not.toHaveBeenCalled()
    })

    // The required attributes should be present
    expect(screen.getByLabelText('Email Address')).toBeRequired()
    expect(screen.getByLabelText('Password')).toBeRequired()
  })

  it('handles network errors during login', async () => {
    mockLogin.mockResolvedValue({ success: false, error: 'Login failed' })

    render(
      <AuthProvider>
        <LoginForm />
      </AuthProvider>
    )

    // Fill out the form
    fireEvent.change(screen.getByLabelText('Email Address'), {
      target: { value: 'test@example.com' },
    })
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'password123' },
    })

    // Submit the form
    fireEvent.click(screen.getByRole('button', { name: 'Sign in' }))

    // Wait for the error to appear
    await waitFor(() => {
      expect(screen.getByText('Login failed')).toBeInTheDocument()
    })
  })

  it('clears error message when user starts typing again', async () => {
    mockLogin.mockResolvedValue({ success: false, error: 'Invalid credentials' })

    render(
      <AuthProvider>
        <LoginForm />
      </AuthProvider>
    )

    // Fill out and submit to trigger error
    fireEvent.change(screen.getByLabelText('Email Address'), {
      target: { value: 'test@example.com' },
    })
    fireEvent.change(screen.getByLabelText('Password'), {
      target: { value: 'wrongpassword' },
    })
    fireEvent.click(screen.getByRole('button', { name: 'Sign in' }))

    // Wait for error to appear
    await waitFor(() => {
      expect(screen.getByText('Invalid credentials')).toBeInTheDocument()
    })

    // Start typing again - error should clear
    fireEvent.change(screen.getByLabelText('Email Address'), {
      target: { value: 'new@example.com' },
    })

    // Error message should be gone
    expect(screen.queryByText('Invalid credentials')).not.toBeInTheDocument()
  })
})