# WEB
- [API](./readme.md#api)
- [JWT](./readme.md#web)
- [Browser Storage](./readme.md#browser-storage)

# API
- REST v/s SOAP
- PUT v/s PATCH
- `application/json` v/s `application/x-www-form-urlencoded` v/s `multipart/form-data` v/s `text/plain` ....
- What is idempotency in REST APIs? Which HTTP methods are idempotent?
- What is API versioning? What are some common strategies for API versioning
- How can you secure an API? Explain authentication vs. authorization
- How does rate limiting work in APIs?
- What are API gateways, and why are they used
- What are webhooks, and how do they differ from polling?
- What is OpenAPI (Swagger), and why is it useful?
- Explain HATEOAS in RESTful APIs.
- What is GraphQL, and how is it different from REST?
- How would you handle pagination in an API?
- How do you optimize API performance? (e.g., caching, compression, load balancing)
- What are API contracts, and how do you enforce them?


- What is difference between bearer and auth token when sending token 
# JWT 
## Authorisation v/s Authentication
- `Authentication` :  is verifying the true identity of a user or entity
  - Done for when the user comes to website for first time
  - Like login/signup
- `Authorization` :  Determine access level of user, i.e. the resources that the user can access

  > ### JWT is used for authorization, `not for authentication`

- Authorization using SessionID
  - After authentication server generates SesisonID
  - Server stores SessionID, and also sends it back to client
  - Now for every request client sends sessionID to verify itself 
  - <img src="./images/image.png">
  - Here server needs to store `session ID`
- But this JWT uses a token instead of session ID
  - Here after authentication server creates a JWT with a secret key that only server has
  - Now each time client makes a request the server verifies the token with its secret key 
  - So here no extra memory is stored and no searching of id
  - <img src="./images/image-1.png">

## Ways of using JWT
- There are two types of tokens usually stored on client side
  - Access Token
  - Refresh Token
- `Access token` 
  - have relatively short lifespan (10-15min)
  - Are stored in session storad
- `Refresh Token`
  - Have relatively long lifespan (from days to months)
  - Are stored in HTTP-only cookies for enhanced security


# Browser Storage
- Browser has three types of storages   
  - Local Storage
  - Session Storage
  - Cookie Storage

|| Local Storage | Session Storage | Cookie Storage |
|-|-|-|-|
| Storage Cap | 5-10MB | 5MB | 5KB |
| Expiration | Never/set Manually | Till the tab is open | Never / set manually |
| Access | All tabs of browser | Same tab only | All tabs + server |
| Sent with request | Nop | Nop | Always sent with request |

## Local Storage & Session Storage 
- Data is stored as key value pairs

## CORS (Cross-Origin Resource Sharing)
- On both server and client side we need to mention 
  - What kind of resources are acceptable
  - From what urls resources are accepted
- While defining CORS option we specifiy 
  - Credentials : True/False : Are we expecting any of following credentials?
    - Cookies 
    - Authorization Header 
    - Server/Client-side SSL certificates 
  - AllowedHeader : List of Allowed headers
    - Auth-Token
    - Content-Type
  - Origin : List of urls from where server will accept requests
  - allowedMethods : Allowed methods : (GET, POST, PUT, DELETE, OPTIONS)
  - Prefilght : For complex request brower requests this(Preflight) from server

## Cookie Attribures
### Core Attributes
  - Name
  - Value
  - Domain : URL from where cookie is recieved
  - Path : The path where cookie is valid 
    - If path is `url/home`
    - And if you try to access cookie from `url/dashboard` it will be termed as invalid
    - It can also be accessed from `url/home/*`
  - Expires/ Max Age
  - Size : Brower defines it after determining it's size

### Security and Privacy Attribute
  - HttpOnly : If true client-side JS wont be able to access it
  - SameSite : Determines when the cookie is to be sent 
    > - SameSite : Two urls are samesite when scheme(HTTP/HTTPS) and domain matches
    > - CrossSite : When either of one schema or domain does not match
    - Strict : Cookie sent when user is on the SameSite
    - Lax : 
      - Cookie is sent on SameSite
      - When a new site opens from by clicking on url of sameSite cookie is sent
    - None : Allows all cross site 
  - Secure : When true, cookie is sent only to HTTPS secured server
  - Partitioned?
  - Priority?

