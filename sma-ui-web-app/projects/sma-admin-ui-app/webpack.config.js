const { shareAll, withModuleFederationPlugin } = require('@angular-architects/module-federation/webpack');

module.exports = withModuleFederationPlugin({

  name: 'sma-admin-ui-app',

  exposes: {
    './Module': './projects/sma-admin-ui-app/src/app/app.module.ts',
  },

  remotes: {
    "smaStudentUiApp": "http://localhost:4200/remoteEntry.js",
    "smaStaffUiApp": "http://localhost:4201/remoteEntry.js",
  },

  shared: {
    ...shareAll({ singleton: true, strictVersion: true, requiredVersion: 'auto' }),
  },

});
