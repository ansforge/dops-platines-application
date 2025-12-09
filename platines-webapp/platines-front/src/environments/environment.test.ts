///
/// (c) Copyright 2017-2024, ANS. All rights reserved.
///

export const host = {
  API_ENDPOINT: 'http://localhost:8080/',
  FRONT_CONTEXT: '/',
}
export const environment = {
  production: false,
  API_ENDPOINT_INSECURE: `${host.API_ENDPOINT}insecure/`,
  API_ENDPOINT_SECURE: `${host.API_ENDPOINT}secure/`
};
