'use client'

import { useState, useEffect } from 'react'
import DashboardLayout from '@/components/layout/DashboardLayout'
import ProtectedRoute from '@/components/auth/ProtectedRoute'
import LoadingSpinner from '@/components/common/LoadingSpinner'
import { Plus, Search, Filter, Edit, Trash2, Users } from 'lucide-react'
import Link from 'next/link'

export default function PrisonsPage() {
  const [prisons, setPrisons] = useState([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')

  useEffect(() => {
    fetchPrisons()
  }, [])

  const fetchPrisons = async () => {
    try {
      // Mock data - in production, this would call the actual API
      const mockPrisons = [
        {
          id: 1,
          name: 'Harare Central Prison',
          location: 'Harare',
          capacity: 500,
          currentPopulation: 420,
          contactEmail: 'harare.prison@paz.org',
          contactPhone: '+263 24 277 0001'
        },
        {
          id: 2,
          name: 'Chikurubi Maximum',
          location: 'Harare',
          capacity: 1000,
          currentPopulation: 950,
          contactEmail: 'chikurubi@paz.org',
          contactPhone: '+263 24 277 0002'
        },
        {
          id: 3,
          name: 'Mutare Prison',
          location: 'Mutare',
          capacity: 300,
          currentPopulation: 250,
          contactEmail: 'mutare.prison@paz.org',
          contactPhone: '+263 20 600 0001'
        },
        {
          id: 4,
          name: 'Bulawayo Prison',
          location: 'Bulawayo',
          capacity: 400,
          currentPopulation: 380,
          contactEmail: 'bulawayo.prison@paz.org',
          contactPhone: '+263 29 200 0001'
        }
      ]
      setPrisons(mockPrisons)
    } catch (error) {
      console.error('Failed to fetch prisons:', error)
    } finally {
      setLoading(false)
    }
  }

  const filteredPrisons = prisons.filter(prison =>
    prison.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    prison.location.toLowerCase().includes(searchTerm.toLowerCase())
  )

  const getCapacityPercentage = (prison) => {
    return Math.round((prison.currentPopulation / prison.capacity) * 100)
  }

  const getCapacityColor = (percentage) => {
    if (percentage >= 90) return 'bg-red-100 text-red-800'
    if (percentage >= 75) return 'bg-yellow-100 text-yellow-800'
    return 'bg-green-100 text-green-800'
  }

  if (loading) {
    return (
      <ProtectedRoute>
        <DashboardLayout>
          <div className="flex items-center justify-center h-64">
            <LoadingSpinner size="large" />
          </div>
        </DashboardLayout>
      </ProtectedRoute>
    )
  }

  return (
    <ProtectedRoute>
      <DashboardLayout>
        <div className="space-y-6">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Prisons</h1>
              <p className="text-gray-600 mt-2">Manage all prison facilities</p>
            </div>
            <Link
              href="/prisons/new"
              className="bg-primary-600 text-white px-4 py-2 rounded-md hover:bg-primary-700 flex items-center"
            >
              <Plus className="h-5 w-5 mr-2" />
              Add Prison
            </Link>
          </div>

          {/* Search and Filter */}
          <div className="bg-white p-4 rounded-lg shadow">
            <div className="flex gap-4">
              <div className="flex-1 relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
                <input
                  type="text"
                  placeholder="Search prisons..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10 pr-4 py-2 w-full border border-gray-300 rounded-md focus:ring-primary-500 focus:border-primary-500"
                />
              </div>
              <button className="px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 flex items-center">
                <Filter className="h-5 w-5 mr-2" />
                Filter
              </button>
            </div>
          </div>

          {/* Prisons Grid */}
          <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
            {filteredPrisons.map((prison) => {
              const capacityPercentage = getCapacityPercentage(prison)
              const capacityColor = getCapacityColor(capacityPercentage)

              return (
                <div key={prison.id} className="bg-white rounded-lg shadow overflow-hidden">
                  <div className="p-6">
                    <div className="flex items-center justify-between">
                      <h3 className="text-lg font-semibold text-gray-900">{prison.name}</h3>
                      <span className={`px-2 py-1 text-xs font-medium rounded-full ${capacityColor}`}>
                        {capacityPercentage}% full
                      </span>
                    </div>
                    
                    <p className="text-gray-600 mt-2">{prison.location}</p>
                    
                    <div className="mt-4 space-y-2">
                      <div className="flex items-center text-sm text-gray-600">
                        <Users className="h-4 w-4 mr-2" />
                        {prison.currentPopulation} / {prison.capacity} inmates
                      </div>
                      
                      {prison.contactEmail && (
                        <div className="text-sm text-gray-600">
                          📧 {prison.contactEmail}
                        </div>
                      )}
                      
                      {prison.contactPhone && (
                        <div className="text-sm text-gray-600">
                          📞 {prison.contactPhone}
                        </div>
                      )}
                    </div>

                    <div className="mt-6 flex space-x-2">
                      <button className="flex-1 bg-primary-600 text-white px-3 py-2 rounded-md text-sm hover:bg-primary-700">
                        View Details
                      </button>
                      <button className="px-3 py-2 border border-gray-300 rounded-md text-sm hover:bg-gray-50">
                        <Edit className="h-4 w-4" />
                      </button>
                      <button className="px-3 py-2 border border-gray-300 rounded-md text-sm hover:bg-gray-50">
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </div>
                  </div>
                </div>
              )
            })}
          </div>
        </div>
      </DashboardLayout>
    </ProtectedRoute>
  )
}