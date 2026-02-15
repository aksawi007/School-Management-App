// Production environment configuration
export const environment = {
  production: true,
  
  // Shell app (main)
  shell: {
    url: 'https://your-domain.com',
    port: 443
  },
  
  // Micro-frontend apps
  microFrontends: {
    student: {
      url: 'https://student.your-domain.com',
      port: 443,
      host: 'student.your-domain.com'
    },
    staff: {
      url: 'https://staff.your-domain.com',
      port: 443,
      host: 'staff.your-domain.com'
    },
    admin: {
      url: 'https://admin.your-domain.com',
      port: 443,
      host: 'admin.your-domain.com'
    },
    ai: {
      url: 'https://ai.your-domain.com',
      port: 443,
      host: 'ai.your-domain.com'
    }
  },
  
  // API endpoints
  api: {
    baseUrl: 'https://api.your-domain.com',
    timeout: 30000
  }
};
