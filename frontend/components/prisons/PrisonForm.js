'use client'

import { useState } from 'react'
import { useRouter } from 'next/navigation'
import { useForm } from 'react-hook-form'

export default function PrisonForm({ prison, onSubmit }) {
  const router = useRouter()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm({
    defaultValues: prison || {
      name: '',
      location: '',
      capacity: '',
      currentPopulation: '',
      contactEmail: '',
      contactPhone: ''
    }
  })

  const capacity = watch('capacity')
  const currentPopulation = watch('currentPopulation')

  const handleFormSubmit = async (data) => {
    setLoading(true)
    setError('')

    try {
      await onSubmit(data)
      router.push('/prisons')
    } catch (err) {
      setError(err.message || 'Failed to save prison')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="max-w-2xl mx-auto">
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:p-6">
          <h2 className="text-lg font-medium text-gray-900 mb-6">
            {prison ? 'Edit Prison' : 'Add New Prison'}
          </h2>

          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
            <div>
              <label htmlFor="name" className="block text-sm font-medium text-gray-700">
                Prison Name *
              </label>
              <input
                type="text"
                id="name"
                {...register('name', { required: 'Prison name is required' })}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 px-3 py-2"
                placeholder="e.g., Harare Central Prison"
              />
              {errors.name && (
                <p className="mt-1 text-sm text-red-600">{errors.name.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="location" className="block text-sm font-medium text-gray-700">
                Location *
              </label>
              <input
                type="text"
                id="location"
                {...register('location', { required: 'Location is required' })}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 px-3 py-2"
                placeholder="e.g., Harare"
              />
              {errors.location && (
                <p className="mt-1 text-sm text-red-600">{errors.location.message}</p>
              )}
            </div>

            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
              <div>
                <label htmlFor="capacity" className="block text-sm font-medium text-gray-700">
                  Capacity *
                </label>
                <input
                  type="number"
                  id="capacity"
                  {...register('capacity', { 
                    required: 'Capacity is required',
                    min: { value: 1, message: 'Capacity must be at least 1' },
                    max: { value: 10000, message: 'Capacity seems too high' }
                  })}
                  className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 px-3 py-2"
                  min="1"
                  max="10000"
                />
                {errors.capacity && (
                  <p className="mt-1 text-sm text-red-600">{errors.capacity.message}</p>
                )}
              </div>

              <div>
                <label htmlFor="currentPopulation" className="block text-sm font-medium text-gray-700">
                  Current Population
                </label>
                <input
                  type="number"
                  id="currentPopulation"
                  {...register('currentPopulation', { 
                    min: { value: 0, message: 'Population cannot be negative' },
                    validate: (value) => {
                      if (value && capacity && parseInt(value) > parseInt(capacity)) {
                        return 'Population cannot exceed capacity'
                      }
                      return true
                    }
                  })}
                  className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 px-3 py-2"
                  min="0"
                  max={capacity || 10000}
                />
                {errors.currentPopulation && (
                  <p className="mt-1 text-sm text-red-600">{errors.currentPopulation.message}</p>
                )}
                
                {capacity && currentPopulation && (
                  <p className="mt-1 text-sm text-gray-500">
                    {Math.round((currentPopulation / capacity) * 100)}% capacity
                  </p>
                )}
              </div>
            </div>

            <div>
              <label htmlFor="contactEmail" className="block text-sm font-medium text-gray-700">
                Contact Email
              </label>
              <input
                type="email"
                id="contactEmail"
                {...register('contactEmail', {
                  pattern: {
                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                    message: 'Invalid email address'
                  }
                })}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 px-3 py-2"
                placeholder="contact@prison.org"
              />
              {errors.contactEmail && (
                <p className="mt-1 text-sm text-red-600">{errors.contactEmail.message}</p>
              )}
            </div>

            <div>
              <label htmlFor="contactPhone" className="block text-sm font-medium text-gray-700">
                Contact Phone
              </label>
              <input
                type="tel"
                id="contactPhone"
                {...register('contactPhone')}
                className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:ring-primary-500 focus:border-primary-500 px-3 py-2"
                placeholder="+263 24 277 0000"
              />
            </div>

            <div className="flex justify-end space-x-3">
              <button
                type="button"
                onClick={() => router.push('/prisons')}
                className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={loading}
                className="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary-500 disabled:opacity-50"
              >
                {loading ? 'Saving...' : (prison ? 'Update Prison' : 'Create Prison')}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}