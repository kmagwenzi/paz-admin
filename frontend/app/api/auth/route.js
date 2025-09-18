import { NextResponse } from 'next/server'

// Mock authentication - in production, this would call the actual backend API
const MOCK_USERS = [
  {
    id: 1,
    email: 'admin@paz.org',
    password: 'password123',
    username: 'admin',
    roles: ['ADMIN']
  },
  {
    id: 2,
    email: 'manager@paz.org',
    password: 'password123',
    username: 'manager',
    roles: ['PRISON_MANAGER']
  }
]

export async function POST(request) {
  try {
    const { email, password } = await request.json()

    // Find user by email and password
    const user = MOCK_USERS.find(u => u.email === email && u.password === password)

    if (!user) {
      return NextResponse.json(
        { error: 'Invalid credentials' },
        { status: 401 }
      )
    }

    // Generate mock JWT tokens
    const accessToken = generateMockToken(user, 'access')
    const refreshToken = generateMockToken(user, 'refresh')

    // Return user data without password
    const { password: _, ...userWithoutPassword } = user

    return NextResponse.json({
      accessToken,
      refreshToken,
      user: userWithoutPassword
    })

  } catch (error) {
    return NextResponse.json(
      { error: 'Authentication failed' },
      { status: 500 }
    )
  }
}

function generateMockToken(user, type) {
  const payload = {
    sub: user.id,
    email: user.email,
    roles: user.roles,
    type: type
  }
  
  // In a real implementation, this would be a proper JWT signed with a secret
  return Buffer.from(JSON.stringify(payload)).toString('base64')
}