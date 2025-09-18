'use client'

import DashboardLayout from '@/components/layout/DashboardLayout'
import ProtectedRoute from '@/components/auth/ProtectedRoute'
import TeacherForm from '@/components/teachers/TeacherForm'

export default function NewTeacherPage() {
  const handleSubmit = async (teacherData) => {
    // In production, this would call the actual API
    console.log('Creating teacher:', teacherData)
    
    // Mock API call
    return new Promise((resolve) => {
      setTimeout(() => {
        console.log('Teacher created successfully')
        resolve()
      }, 1000)
    })
  }

  return (
    <ProtectedRoute>
      <DashboardLayout>
        <div className="max-w-7xl mx-auto py-6">
          <TeacherForm onSubmit={handleSubmit} />
        </div>
      </DashboardLayout>
    </ProtectedRoute>
  )
}