// Development environment configuration
export const environment = {
  production: false,
  
  // Shell app (main)
  shell: {
    url: 'http://localhost:4300',
    port: 4300
  },
  
  // Micro-frontend apps
  microFrontends: {
    student: {
      url: 'http://localhost:4200',
      port: 4200,
      host: 'localhost'
    },
    staff: {
      url: 'http://localhost:4201',
      port: 4201,
      host: 'localhost'
    },
    admin: {
      url: 'http://localhost:4202',
      port: 4202,
      host: 'localhost'
    },
    ai: {
      url: 'http://localhost:4203',
      port: 4203,
      host: 'localhost'
    }
  },
  
  // API endpoints (if needed)
  api: {
    baseUrl: 'http://localhost:8080',
    timeout: 30000
  }
};
