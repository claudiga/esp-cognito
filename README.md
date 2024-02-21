# esp-cognito

## Flow
 - The resource owner attempts to access a protected resource from postman/browser.
 - The first time they will be redirect to login from the browser
 - Once they login they will then be redirected back to the callback url
 - They will then get a jwt token response
 - They can then use this Jwt token as bearer token in postman which is then verified and authenticated and authorized
