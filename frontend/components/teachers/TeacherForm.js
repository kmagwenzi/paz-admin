'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { useForm } from 'react-hook-form'

export default function TeacherForm({ teacher, onSubmit }) {
  const router = useRouter()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    defaultValues: teacher || {
      firstName: '',
      lastName: '',
      email: '',
      phoneNumber: '',
      specialization: '',
      yearsOfExperience: '',
      prisonId: ''
    }
  })

  const handleFormSubmit = async (data) => {
    setLoading(true)
    setError('')

    try {
      await onSubmit(data)
      router.push('/teachers')
    } catch (err) {
      setError(err.message || 'Failed to save teacher')
    } finally {
      setLoading(false)
    }
  }

  // Mock prisons data - in production, this would come from an API
  const prisons = [
    { id: 1, name: 'Harare Central Prison' },
    { id: 2, name: 'Chikurubi Maximum' },
    { id: 3, name: 'Mutare Prison' },
    { id: 4, name: 'Bulawayo Prison' }
  ]

  return (
    <div className="max-w-2xl mx-auto">
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h2 className="text-lg font-medium text-gray-900 mb-6">
            {teacher ? 'Edit Teacher' : 'Add New Teacher'}
          </h2>

          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
              <div>
                <label htmlFor="firstName" className="block text-sm font-medium text-gray-700">
                  First Name *
                </label>
                <input
                  type="text"
                  id="firstName"
                  {...register('firstName', { required: 'First name is required' })}
                  className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 px-3 py-2"
                />
                {errors.firstName && (
                  <p className="mt-1 text-sm text-red-600">{errors.firstName.message}</p>
                )}
              </div>

              <div>
                <label htmlFor="lastName" className="block text-sm font-medium text-gray-700">
                  Last Name *
                </label>
                <input
                  type="text"
                  id="lastName"
                  {...register('lastName', { required: 'Last name is required' })}
                  className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 px-3 py-2"
                />
                {errors.lastName && (
                  <p className="mt-1 text-sm text-red-600">{errors.lastName.message}</p>
                )}
              </div>
            </div>

            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                Email Address *
              </label>
              <input
                type="email"
                id="email"
                {...register('email', { 
                  required: 'Email is required',
                  pattern: {
                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                    message: 'Invalid email address'
                  }
                })}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 px-3 py-2"
              />
              {errors.email && (
                <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-700">
                Phone Number
              </label>
              <input
                type="tel"
                id="phoneNumber"
                {...register('phoneNumber')}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 px-3 py-2"
                placeholder="+263 77 123 4567"
              />
            </div>

            <div>
              <label htmlFor="specialization" className="block text-sm font-medium text-gray-700">
                Specialization *
              </label>
              <select
                id="specialization"
                {...register('specialization', { required: 'Specialization is required' })}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 px-3 py-2"
              >
                <option value="">Select specialization</option>
                <option value="Mathematics">Mathematics</option>
                <option value="English Literature">English Literature</option>
                <option value="Science">Science</option>
                <option value="History">History</option>
                <option value="Geography">Geography</option>
                <option value="Life Skills">Life Skills</option>
                <option value="Vocational Training">Vocational Training</option>
              </select>
              {errors.specialization && (
                <p className="mt-1 text-sm text-red-600">{errors.specialization.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="yearsOfExperience" className="block text-sm font-medium text-gray-700">
                Years of Experience
              </label>
              <input
                type="number"
                id="yearsOfExperience"
                {...register('yearsOfExperience', { 
                  min: { value: 0, message: 'Experience cannot be negative' },
                  max: { value: 50, message: 'Experience seems too high' }
                })}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 px-3 py-2"
                min="0"
                max="50"
              />
              {errors.yearsOfExperience && (
                <p className="mt-1 text-sm text-red-600">{errors.yearsOfExperience.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="prisonId" className="block text-sm font-medium text-gray-700">
                Assigned Prison
              </label>
              <select
                id="prisonId"
                {...register('prisonId')}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 px-3 py-2"
              >
                <option value="">Select prison (optional)</option>
                {prisons.map(prison => (
                  <option key={prison.id} value={prison.id}>
                    {prison.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex justify-end space-x-3">
              <button
                type="button"
                onClick={() => router.push('/teachers')}
                className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={loading}
                className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50"
              >
                {loading ? 'Saving...' : (teacher ? 'Update Teacher' : 'Create Teacher')}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}