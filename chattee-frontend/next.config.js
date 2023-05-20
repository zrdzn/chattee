/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: false,
  env: {
    backendUrl: 'http://localhost:7070'
  },

  async redirects() {
    return [
      {
        source: '/',
        destination: '/discussions',
        permanent: true
      }
    ]
  }
}

module.exports = nextConfig
