// Replace the example domain name with your deployed address.
export const environment = {
  production: true,
  // remove http/https to appear relative. xsrf-token skips absolute paths.
  participantManagerDatastoreUrl:
    '//participants.mizuerwi-dev.jcloudce.com/participant-manager-datastore',
  baseHref: '/participant-manager/',
  hydraLoginUrl: 'https://participants.mizuerwi-dev.jcloudce.com/oauth2/auth',
  authServerUrl: 'https://participants.mizuerwi-dev.jcloudce.com/auth-server',
  authServerRedirectUrl:
    'https://participants.mizuerwi-dev.jcloudce.com/auth-server/callback',
  hydraClientId: 'tV95fbTgG6KHdZDr',
  appVersion: 'v0.1',
  termsPageTitle: 'terms title goes here',
  termsPageDescription: 'terms description goes here',
  aboutPageTitle: 'about page title goes here',
  aboutPageDescription: 'about page description goes here',
};
