'use client'

import DashboardLayout from '@/components/layout/DashboardLayout'
import ProtectedRoute from '@/components/auth/ProtectedRoute'
import PrisonForm from '@/components/prisons/PrisonForm'

export default function NewPrisonPage() {
  const handleSubmit = async (prisonData) => {
    // In production, this would call the actual API
    console.log('Creating prison:', prisonData)
    
    // Mock API call
    return new Promise((resolve) => {
      setTimeout(() => {
        console.log('Prison created successfully')
        resolve()
      }, 1000)
    })
  }

  return (
    <ProtectedRoute>
      <DashboardLayout>
        <div className="max-w-7xl mx-auto py-6">
          <PrisonForm onSubmit={handleSubmit} />
        </div>
      </DashboardLayout>
    </ProtectedRoute>
  )
}