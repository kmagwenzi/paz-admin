import './globals.css'
import { AuthProvider } from '@/lib/auth'

export const metadata = {
  title: 'PAZ Admin Portal',
  description: 'Digital administration system for Prison Alliance Zimbabwe',
}

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className="font-sans">
        <AuthProvider>
          {children}
        </AuthProvider>
      </body>
    </html>
  )
}