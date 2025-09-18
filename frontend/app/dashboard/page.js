'use client'

import DashboardLayout from '@/components/layout/DashboardLayout'
import ProtectedRoute from '@/components/auth/ProtectedRoute'
import { Users, Building2, BookOpen, Calendar } from 'lucide-react'

const stats = [
  { name: 'Total Teachers', value: '24', icon: Users, change: '+4', changeType: 'positive' },
  { name: 'Active Prisons', value: '8', icon: Building2, change: '+2', changeType: 'positive' },
  { name: 'Ongoing Classes', value: '12', icon: BookOpen, change: '+3', changeType: 'positive' },
  { name: 'Upcoming Sessions', value: '5', icon: Calendar, change: '-1', changeType: 'negative' },
]

export default function Dashboard() {
  return (
    <ProtectedRoute>
      <DashboardLayout>
        <div className="space-y-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
            <p className="text-gray-600 mt-2">Welcome to PAZ Admin Portal</p>
          </div>

          {/* Stats Grid */}
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4 sm:gap-6">
            {stats.map((stat) => (
              <div
                key={stat.name}
                className="bg-white overflow-hidden shadow rounded-lg"
              >
                <div className="p-4 sm:p-5">
                  <div className="flex items-center">
                    <div className="flex-shrink-0">
                      <stat.icon className="h-5 w-5 sm:h-6 sm:w-6 text-gray-400" />
                    </div>
                    <div className="ml-4 sm:ml-5 w-0 flex-1">
                      <dl>
                        <dt className="text-xs sm:text-sm font-medium text-gray-500 truncate">
                          {stat.name}
                        </dt>
                        <dd className="flex items-baseline">
                          <div className="text-xl sm:text-2xl font-semibold text-gray-900">
                            {stat.value}
                          </div>
                          <div
                            className={`ml-2 flex items-baseline text-xs sm:text-sm font-semibold ${
                              stat.changeType === 'positive'
                                ? 'text-green-600'
                                : 'text-red-600'
                            }`}
                          >
                            {stat.change}
                          </div>
                        </dd>
                      </dl>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Recent Activity */}
          <div className="bg-white shadow rounded-lg">
            <div className="px-4 py-5 sm:p-6">
              <h3 className="text-lg font-medium text-gray-900">Recent Activity</h3>
              <div className="mt-4 space-y-4">
                <div className="flex items-center">
                  <div className="flex-shrink-0 bg-green-100 p-2 rounded-full">
                    <Users className="h-5 w-5 text-green-600" />
                  </div>
                  <div className="ml-3">
                    <p className="text-sm font-medium text-gray-900">
                      New teacher registered
                    </p>
                    <p className="text-sm text-gray-500">
                      John Doe joined the program
                    </p>
                  </div>
                  <div className="ml-auto text-sm text-gray-500">
                    2h ago
                  </div>
                </div>
                <div className="flex items-center">
                  <div className="flex-shrink-0 bg-blue-100 p-2 rounded-full">
                    <BookOpen className="h-5 w-5 text-blue-600" />
                  </div>
                  <div className="ml-3">
                    <p className="text-sm font-medium text-gray-900">
                      Class session completed
                    </p>
                    <p className="text-sm text-gray-500">
                      Mathematics class at Harare Central
                    </p>
                  </div>
                  <div className="ml-auto text-sm text-gray-500">
                    4h ago
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </DashboardLayout>
    </ProtectedRoute>
  )
}