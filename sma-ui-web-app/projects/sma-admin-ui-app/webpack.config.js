const { shareAll, withModuleFederationPlugin } = require('@angular-architects/module-federation/webpack');

module.exports = withModuleFederationPlugin({

  name: 'sma-admin-ui-app',

  remotes: {
    "smaStudentUiApp": "http://localhost:4200/remoteEntry.js",
    "smaStaffUiApp": "http://localhost:4201/remoteEntry.js",
  },

  shared: {
    ...shareAll({ singleton: true, strictVersion: true, requiredVersion: 'auto' }),
  },

});
