/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: false,
  env: {
    backendUrl: 'http://localhost:7070',
    defaultAvatarUrl: 'https://static-00.iconduck.com/assets.00/avatar-default-symbolic-icon-512x488-rddkk3u9.png'
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
