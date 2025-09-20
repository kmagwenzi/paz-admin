/** @type {import('next').NextConfig} */
const nextConfig = {
  experimental: {},
  env: {
    API_URL: process.env.API_URL || 'http://localhost:8080/api',
  },
  async redirects() {
    return [
      {
        source: '/',
        destination: '/dashboard',
        permanent: false,
      },
    ];
  },
}

module.exports = nextConfig;