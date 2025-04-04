<h1> Authorization / Authentication </h1>

- [Bcrypt](#bcrypt)
  - [Generating hashed password](#generating-hashed-password)
  - [Comparing Hashed Password](#comparing-hashed-password)
  - [Get cost of Hashed Password](#get-cost-of-hashed-password)
- [JWT](#jwt)
  - [JWT secret](#jwt-secret)
  - [Claims](#claims)
    - [1. MapClaims](#1-mapclaims)
    - [2. Registed claims](#2-registed-claims)
    - [3. Custom claim](#3-custom-claim)
  - [Generating token from start](#generating-token-from-start)
    - [Generating token in 3 steps](#generating-token-in-3-steps)
    - [Generating token in 2 steps](#generating-token-in-2-steps)
    - [Get unsigned token](#get-unsigned-token)
  - [Validating JWT token](#validating-jwt-token)
- [OAuth2.0](#oauth20)
  - [Authorization Code Request](#authorization-code-request)
  - [`Third-Party Authorization Server` calls `client callback url` with `authorization code` in query params](#third-party-authorization-server-calls-client-callback-url-with-authorization-code-in-query-params)
  - [`Client(myApp)` Request for resources from `Resource server`](#clientmyapp-request-for-resources-from-resource-server)
  - [Full working code](#full-working-code)


# Bcrypt
- Bcrypt takes care of salting
## Generating hashed password
  - Syntax 
   
    ```go
    func GenerateFromPassword(password []byte, cost int) ([]byte, error)
    ```

  - Use

    ```go
    // Default cost is 10
    password := "MyPassowrd"
  	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)
    // HashedPassword is byte slice 
      // hence is needed to be converted to string before send
    hashedPasswordString := string(hashedPassword)
    ```

## Comparing Hashed Password
- Syntax

  ```go
  func CompareHashAndPassword(hashedPassword, password []byte) error
  ```

- Use

  ```go
  // Generally fetched from DB
  hashedPasswordString := fetchPasswordWithUsername("ohm")
  // Generally comes as request input
  password := getInputPassowrd()

  err := bcrypt.CompareHashAndPassword([]byte(hashedPasswordString), []byte(password))
  if err == nil {/*Password matched*/}
  else {/*Password incorrect*/}
  ```

## Get cost of Hashed Password
- Syntax

  ```go
  func Cost(hashedPassword []byte) (int, error)
  ```

- Use

  ```go
  // Get hashed password string
  hashedPasswordString := fetchPasswordWithUsername("ohm")
  // cost : int | err : error
  cost, err := Cost([]byte(hashedPasswordString))
  ```

# JWT

## JWT secret
- It can be 
  - HMAC (Symmetric) Algorithms (e.g., HS256, HS384, HS512)

    ```go
    var jwtSecret []byte
    ```

  - RSA (Asymmetric) Algorithms (e.g., RS256, RS384, RS512)

    ```go
    pemData, err := os.ReadFile("private.pem") 
    privateKey, err := jwt.ParseRSAPrivateKeyFromPEM(pemData)
    ```

  - ECDSA (Asymmetric) Algorithms (e.g., ES256, ES384, ES512)

    ```go
    pemData, err := os.ReadFile("private.pem") 
    privateKey, err := jwt.ParseECPrivateKeyFromPEM(pemData)
    ```

## Claims
- There are three ways to create claims
  - MapClaims : For simple claims
  - RegisteredClaims : Registered claims are defined in the JWT specifications (RFC 7519)
  - Custom Claims : To have type safety when defining claims use custom claims

### 1. MapClaims
- Often used for simple claims
- Ways of defining MapClaims

  ```go
  // 1
  claims := jwt.MapClaims{
      "username": "john_doe",
      "exp":      time.Now().Add(time.Hour).Unix(),
      "role":     "admin",
  }

  // 2
  claims := jwt.MapClaims{}
  claims["username"] = "john_doe"
  claims["exp"] = time.Now().Add(time.Hour).Unix()
  claims["role"] = "admin"

  // 3
  claims := make(jwt.MapClaims)
  claims["username"] = "john_doe"
  claims["exp"] = time.Now().Add(time.Hour).Unix()
  claims["role"] = "admin"

  token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
  ```


### 2. Registed claims
- Registered claims are defined in the JWT specifications (RFC 7519)
- So when use specification (RFC 7519) use this
- Syntax
  
  ```go
  type RegisteredClaims struct {
  	Issuer string `json:"iss,omitempty"`
  	Subject string `json:"sub,omitempty"`
  	Audience ClaimStrings `json:"aud,omitempty"`
  	ExpiresAt *NumericDate `json:"exp,omitempty"`
  	NotBefore *NumericDate `json:"nbf,omitempty"`
  	IssuedAt *NumericDate `json:"iat,omitempty"`
  	ID string `json:"jti,omitempty"`
  }
  ```

- Crreating Registed claim

  ```go
  now := time.Now()

  // 1
  // we are not required to fill all fields
  claim := jwt.RegisteredClaims{
    ExpiresAt: jwt.NewNumericDate(now.Add(time.Hour)),
    IssuedAt:  jwt.NewNumericDate(now),
    NotBefore: jwt.NewNumericDate(now),
    Issuer:    "my-app",
    Subject:   "user123",
    Audience:  []string{"my-client"},
    JWTID:     "unique-jwt-id",
  }
  
  // 2
  // we are not required to fill all fields
  claims := jwt.RegisteredClaims{}
  claims.ExpiresAt = jwt.NewNumericDate(now.Add(time.Hour))
  claims.IssuedAt = jwt.NewNumericDate(now)
  claims.NotBefore = jwt.NewNumericDate(now)
  claims.Issuer = "my-app"
  claims.Subject = "user123"
  claims.Audience = []string{"my-client"}
  claims.JWTID = "unique-jwt-id"

  // 1
  token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
  // 2
  token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.RegisteredClaims{
    ExpiresAt: jwt.NewNumericDate(now.Add(time.Hour)),
    IssuedAt:  jwt.NewNumericDate(now),
    NotBefore: jwt.NewNumericDate(now),
    Issuer:    "my-app",
    Subject:   "user123",
    Audience:  []string{"my-client"},
    JWTID:     "unique-jwt-id",
  })
  ```

### 3. Custom claim

  ```go
  type MyClaims struct {
      UserID int    `json:"user_id"`
      Role   string `json:"role"`
      jwt.RegisteredClaims
  }

  claims := MyClaims{
      UserID: 123,
      Role:   "admin",
      RegisteredClaims: jwt.RegisteredClaims{
          ExpiresAt: jwt.NewNumericDate(time.Now().Add(time.Hour)),
          Issuer:    "my-app",
      },
  }
  token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
  ```

## Generating token from start

### Generating token in 3 steps
- create a siging method for token
  
  ```go
  token := jwt.New(jwt.SigningMethodRS256) 
  ```

- Adding token

  ```go
  // 1. Directly update token's claim's values
  claims := token.Claims.(jwt.MapClaims)
  claims["username"] = "john_doe"
  claims["user_id"] = 123
  claims["exp"] = time.Now().Add(time.Hour).Unix()
  claims["role"] = "admin"

  // create a claim and add update token's claim
  claims := MyClaims{
      Username: "john_doe",
      UserID:   123,
      Role:     "admin",
      RegisteredClaims: jwt.RegisteredClaims{
        ExpiresAt: jwt.NewNumericDate(time.Now().Add(time.Hour)),
        Issuer:    "my-app",
      },
    }
  token.Claims = claims
  ```

- Signing token 
  
  ```go
  // We can have two types of key
    // Symmetric key 
  var SecretKey []byte = "secret_key"
    // Asymmetric key of type *ecdsa.PrivateKey
	privateKey, err := jwt.ParseECPrivateKeyFromPEM(pemData)
    // Asymmetric key of type *rsa.PrivateKey
	privateKey, err := jwt.ParseRSAPrivateKeyFromPEM(pemData)

  // Now we want sign token with claim
    // with Symmetric key 
	signedToken, err := token.SignedString(SecretKey)
    // with Asymmetric key 
	signedToken, err := token.SignedString(privateKey)
  ```

### Generating token in 2 steps
- Creating token with claim

  ```go
  // say we have defined claim
  claim := ...
  // Generating token with claim
  token := jwt.NewWithClaims(jwt.SigningMethodHS256, claim)
  ```

- Signing token 
  
  ```go
  // We can have two types of key
    // Symmetric key 
  var SecretKey []byte = "secret_key"
    // Asymmetric key of type *ecdsa.PrivateKey
	privateKey, err := jwt.ParseECPrivateKeyFromPEM(pemData)
    // Asymmetric key of type *rsa.PrivateKey
	privateKey, err := jwt.ParseRSAPrivateKeyFromPEM(pemData)

  // Now we want sign token with claim
    // with Symmetric key 
	signedToken, err := token.SignedString(SecretKey)
    // with Asymmetric key 
	signedToken, err := token.SignedString(privateKey)
  ```

### Get unsigned token

  ```go
  // say you have a claim
  claim := ...
  // and you create token and add claim by any way
  token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
  // Unsigned token
	unsignedToken, err := token.SigningString()
    // Output : headerOrAlgo.EncodedClaim
    // Example Output : eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3NDMzNjA5NTQsInVzZXJuYW1lIjoiam9obl9kb2UifQ
  // signing token
	signedToken, err := token.SignedString(privateKey)
  ```

## Validating JWT token

```go
func ValidateJWT(tokenString string) (*jwt.Token, jwt.MapClaims, error) {

  token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
    // check if the signing method/algo of token is correct
    if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
    	return nil, fmt.Errorf("unexpected signing method")
    }
    // if signing method is same then return secretKey/PrivateKey/PublicKey to Parse function
    // And it will verify the token
    return jwtSecret, nil
  })
  // If the token is verified then Parse will return *jwt.Token

  // if token is invalid then err wont be nil
	if err != nil {
		return nil, nil, err
	}

  // If we have claims struct as MapClaims or custom claims 
    // we need to perform assertion check
    // and fetch claim
  claims, ok := token.Claims.(jwt.MapClaims)
	if !ok || !token.Valid {
		return nil, nil, fmt.Errorf("invalid token")
	}

 	return token, claims, nil 
}
```

# OAuth2.0
- It has `config` struct which has both client and auth-server info
- Syntax

  ```go
  type Config struct {
  	// Third Party app specific
  	ClientID string
  	ClientSecret string

    // oauth2.Endpoint
    // Details about third party Urls
  	Endpoint Endpoint

    // client callback url : where auth-server calls with auth-code
  	RedirectURL string

  	// Resources to be requested
  	Scopes []string
  }

  // Example endpoint
  var GitHub = oauth2.Endpoint{
	AuthURL:       "https://github.com/login/oauth/authorize",
	TokenURL:      "https://github.com/login/oauth/access_token",
	DeviceAuthURL: "https://github.com/login/device/code",
  }
  ```

- Example Config file

  ```go
  githubConfig: &oauth2.Config{
  	ClientID:     "GITHUB_CLIENT_ID",
  	ClientSecret: "GITHUB_CLIENT_SECRET",
  	RedirectURL:  "http://localhost:8080/auth/github/callback",
  	Scopes:       []string{"user:email"},
  	Endpoint:     github.Endpoint,
  }
  ```

## Authorization Code Request
- When user selects Third-party app to authenticate itself it triggers `oAuthHandler`
- `OAuthHandler` generates url to call `authorization server` to request `authorization code`
- It is generated using

  - Syntax

    ```go
    // Syntax
    // state : token string that is used to prevent CSRF attack
    func (c *Config) AuthCodeURL(state string, opts ...AuthCodeOption) string
    ```

  - Use

    ```go
    // Uses
    // 1. oauth2.AccessTypeOffline : requests a refresh token along with the access token.
    url := config.AuthCodeURL("state-token", oauth2.AccessTypeOffline)
    // 2. oauth2.AccessTypeOnline : No refresh token provided, access token only for current sessions
    url := config.AuthCodeURL("state-token", oauth2.AccessTypeOnline)

    // Additional Prompt
    // prompt:concent : Forces user consent screen every time, Used when switching accounts.
  	url := config.AuthCodeURL(
      "state-token", 
      oauth2.AccessTypeOffline, 
      oauth2.SetAuthURLParam("prompt", "consent")
    )
    ```

- URL generated is shown below

  ```bash
  https://github.com/login/oauth/authorize?
    access_type=offline
    client_id=Ov23liJBVEBB87xNNsmm
    prompt=consent
    redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fauth%2Fgithub%2Fcallback
    response_type=code
    scope=user%3Aemail
    state=state-token
  ```

- And then user is redirected to third-party app's login and conent page

  ```go
  // http.StatusTemporaryRedirect : 307
	c.Redirect(http.StatusTemporaryRedirect, url)
  ```

## `Third-Party Authorization Server` calls `client callback url` with `authorization code` in query params
- Third-Party Request
  - url
  
    ```
    GET localhost:8080/auth/github/callback?
          code=8bbb66d708ef1c9ad379&
          state=state-token
    ```

  -  <details>
      <summary style="font-size:2vw"> Headers </summary>
    
       ```yaml
       Headers:
         Sec-Fetch-User: [?1]
         Sec-Ch-Ua: ["Chromium";v="134", "Not:A-Brand";v="24", "Brave";v="134"]
         User-Agent: [Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36]
         Sec-Fetch-Dest: [document]
         Accept-Encoding: [gzip, deflate, br, zstd]
         Sec-Ch-Ua-Platform: ["Windows"]
         Accept: [text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8]
         Sec-Fetch-Mode: [navigate]
         Connection: [keep-alive]
         Sec-Ch-Ua-Mobile: [?0]
         Upgrade-Insecure-Requests: [1]
         Sec-Gpc: [1]
         Accept-Language: [en-US,en;q=0.6]
         Sec-Fetch-Site: [cross-site]
       ```

     </details>


- Extract `Authorization Code` 

  ```go
  // Authorization code
	code := c.Query("code")
  ```

- Authorization code is exchanged for Access Token and (Optional) Refresh Token

  ```go
	token, err := config.Exchange(context.Background(), code)
  // Here default context is provided
  // But if request other types of contexts can be provided like
  /*
  ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
  defer cancel() // Ensure cancel is always called to free resources
  token, err := config.Exchange(ctx, code)
  */
  ```

  - `config.Exchange` internally calls for POST method to `third party authorization server` mentioned in `config.Endpoint` 
  - Internal POST request looks like

  ```bash
  POST <TP-auth-server>/token HTTP/1.1
  Host: my-app.com
  Content-Type: application/x-www-form-urlencoded

  grant_type=authorization_code&
  client_id=YOUR_CLIENT_ID&
  client_secret=YOUR_CLIENT_SECRET&
  code=AUTHORIZATION_CODE&
  redirect_uri=http://localhost:8080/auth/google/callback&
  code_verifier=YOUR_CODE_VERIFIER
  ```

- And then response like following is recieved internally and converted to `oauth2.Token`
  
  ```json
  {
    "access_token": "ya29.a0AfH6S...",
    "expires_in": 3600,
    "refresh_token": "1//0g5sdR...",
    "scope": "email profile", // detials requested
    "token_type": "Bearer" // How access token will be sent to resource server
  }
  ```

- The converted `oauth2.Token` looks like

  - Syntax

    ```go
    // syntax
    type Token struct {
    	AccessToken string `json:"access_token"`
    	TokenType string `json:"token_type,omitempty"`
    	RefreshToken string `json:"refresh_token,omitempty"`
    	Expiry time.Time `json:"expiry,omitempty"`
    	ExpiresIn int64 `json:"expires_in,omitempty"`
    }
    ```

  - Actual Token
    - When using `oauth2.AccessTypeOffline` in `config.AuthCodeUrl` client(myApp) will recieve refresh token only on first concent
    - When using `oauth2.AccessTypeOnline` in `config.AuthCodeUrl` client(myApp) wont recieve refresh token 

    ```yaml
    AccessToken: <access-token>
    TokenType: Bearer
    RefreshToken: <only-returned-on-first-concent>
    Expiry: 2025-04-02 20:11:42.58320913 +0530 IST m=+3617.063303111
    Extra:
      id_token: <id-token>
    ```

## `Client(myApp)` Request for resources from `Resource server`
- To request resource `http.Client` is created and details are attached

  ```go
	client := config.Client(context.Background(), token)
  ```

- Then resoures are requested using client

  ```go
	client := config.Client(context.Background(), token)
	resp, err := client.Get(userInfoURL)
	if err != nil {return}
  ```

- Resources are extracted from body

  ```go
  // Close body once used
	defer resp.Body.Close()

	var userInfo map[string]interface{}
	err = json.NewDecoder(resp.Body).Decode(&userInfo)
	if err != nil {return}
  ```

> [!NOTE]
> <details>
>    <summary style="font-size:2vw"> Request Response Details </summary> 
> 
>    ```yaml
>    --- Request Details (Logging RoundTripper) ---
>    URL: https://api.github.com/user
>    Method: GET
>    Headers:
>    --- Response Details (Logging RoundTripper) ---
>    Status: 200 OK
>    Response Headers:
>      X-Ratelimit-Remaining: [4996]
>      X-Content-Type-Options: [nosniff]
>      X-Github-Request-Id: [F5C2:70FEF:A89865:D0A7FC:67ED4214]
>      Etag: [W/"0f72aea8fa2339b480f5f9b60f11f8555d4612368b760e58d3d37ff3f44ba550"]
>      X-Ratelimit-Reset: [1743603332]
>      X-Ratelimit-Resource: [core]
>      Access-Control-Expose-Headers: [ETag, Link, Location, Retry-After, X-GitHub-OTP, X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Used, X-RateLimit-Resource, X-RateLimit-Reset, X-OAuth-Scopes, X-Accepted-OAuth-Scopes, X-Poll-Interval, X-GitHub-Media-Type, X-GitHub-SSO, X-GitHub-Request-Id, Deprecation, Sunset]
>      Strict-Transport-Security: [max-age=31536000; includeSubdomains; preload]
>      Content-Type: [application/json; charset=utf-8]
>      Cache-Control: [private, max-age=60, s-maxage=60]
>      Last-Modified: [Sat, 15 Mar 2025 12:38:33 GMT]
>      X-Accepted-Oauth-Scopes: []
>      X-Oauth-Client-Id: [Ov23liJBVEBB87xNNsmm]
>      X-Github-Api-Version-Selected: [2022-11-28]
>      X-Ratelimit-Limit: [5000]
>      X-Ratelimit-Used: [4]
>      Date: [Wed, 02 Apr 2025 13:56:36 GMT]
>      X-Github-Media-Type: [github.v3; format=json]
>      Access-Control-Allow-Origin: [*]
>      X-Frame-Options: [deny]
>      X-Xss-Protection: [0]
>      Referrer-Policy: [origin-when-cross-origin, strict-origin-when-cross-origin]
>      Content-Security-Policy: [default-src 'none']
>      Server: [github.com]
>      Vary: [Accept, Authorization, Cookie, X-GitHub-OTP,Accept-Encoding, Accept, X-Requested-With]
>      X-Oauth-Scopes: [user:email]
>      Response Body: {"login":"ohmvaghela","id":58502850,"node_id":"MDQ6VXNlcjU4NTAyODUw","avatar_url":"https://avatars.githubusercontent.com/u/58502850?v=4","gravatar_id":"","url":"https://api.github.com/users/ohmvaghela","html_url":"https://github.com/ohmvaghela","followers_url":"https://api.github.com/users/ohmvaghela/followers","following_url":"https://api.github.com/users/ohmvaghela/following{/other_user}","gists_url":"https://api.github.com/users/ohmvaghela/gists{/gist_id}","starred_url":"https://api.github.com/users/ohmvaghela/starred{/owner}{/repo}","subscriptions_url":"https://api.github.com/users/ohmvaghela/subscriptions","organizations_url":"https://api.github.com/users/ohmvaghela/orgs","repos_url":"https://api.github.com/users/ohmvaghela/repos","events_url":"https://api.github.com/users/ohmvaghela/events{/privacy}","received_events_url":"https://api.github.com/users/ohmvaghela/received_events","type":"User","user_view_type":"public","site_admin":false,"name":"Ohm Vaghela","company":null,"blog":"","location":null,"email":null,"hireable":null,"bio":null,"twitter_username":null,"notification_email":null,"public_repos":6,"public_gists":0,"followers":7,"following":11,"created_at":"2019-12-04T06:34:28Z","updated_at":"2025-03-15T12:38:33Z"}
>    --- End RoundTrip ---
>    ```
> </details>

## Full working code

<details>
  <summary style="font-size:2vw"> Full Working code </summary> 

  ```go
  package main

  import (
  	"context"
  	"encoding/json"
  	"fmt"
  	"html/template"
  	"log"
  	"net/http"

  	// "myUtils"

  	"github.com/gin-gonic/gin"
  	"golang.org/x/oauth2"
  	"golang.org/x/oauth2/github"
  	"golang.org/x/oauth2/google"
  )

  var (
  	GOOGLE_CLIENT_ID = "<string-preset-values>"
  	GOOGLE_CLIENT_SECRET = "<string-preset-values>"
  	GITHUB_CLIENT_ID = "<string-preset-values>"
  	GITHUB_CLIENT_SECRET = "<string-preset-values>"
  )

  type App struct {
  	googleConfig *oauth2.Config
  	githubConfig *oauth2.Config
  }

  func (a *App) loginHandler(c *gin.Context) {
  	t, err := template.ParseFiles("index.html")
  	if err != nil {
  		log.Println(err)
  		c.String(http.StatusInternalServerError, "Internal Server Error")
  		return
  	}
  	t.Execute(c.Writer, nil)
  }

  func (a *App) oAuthHandler(c *gin.Context) {
  	provider := c.Param("provider")
  	var config *oauth2.Config

  	switch provider {
  	case "google":
  		config = a.googleConfig
  	case "github":
  		config = a.githubConfig
  	default:
  		c.String(http.StatusBadRequest, "Unsupported provider")
  		return
  	}

  	// url := config.AuthCodeURL("state-token", oauth2.AccessTypeOnline)
  	url := config.AuthCodeURL("state-token", oauth2.AccessTypeOffline )
  	fmt.Println("url : ",url)
  	c.Redirect(http.StatusTemporaryRedirect, url)
  }

  func (a *App) oAuthCallbackHandler(c *gin.Context) {
  	provider := c.Param("provider")
  	var config *oauth2.Config
  	var userInfoURL string

  	// myUtils.PrintRequest(c.Request)	

  	switch provider {
  	case "google":
  		config = a.googleConfig
  		userInfoURL = "https://www.googleapis.com/oauth2/v2/userinfo"
  	case "github":
  		config = a.githubConfig
  		userInfoURL = "https://api.github.com/user"
  	default:
  		c.String(http.StatusBadRequest, "Unsupported provider")
  		return
  	}

  	code := c.Query("code")
  	
  	token, err := config.Exchange(context.Background(), code)
  	if err != nil {
  		c.String(http.StatusBadRequest, "Failed to exchange token: %v", err)
  		return
  	}

  	client := config.Client(context.Background(), token)
  	resp, err := client.Get(userInfoURL)
  	if err != nil {
  		c.String(http.StatusBadRequest, "Failed to fetch user info: %v", err)
  		return
  	}
  	defer resp.Body.Close()

  	var userInfo map[string]interface{}
  	err = json.NewDecoder(resp.Body).Decode(&userInfo)
  	if err != nil {
  		c.String(http.StatusInternalServerError, "Failed to decode user info")
  		return
  	}

  	// c.JSON(http.StatusOK, userInfo)
  	c.Redirect(http.StatusFound, "/auth/login") 
  }

  func main() {
  	app := App{
  		googleConfig: &oauth2.Config{
  			ClientID:     GOOGLE_CLIENT_ID,
  			ClientSecret: GOOGLE_CLIENT_SECRET,
  			RedirectURL:  "http://localhost:8080/auth/google/callback",
  			Scopes:       []string{"email", "profile"},
  			Endpoint:     google.Endpoint,
  		},
  		githubConfig: &oauth2.Config{
  			ClientID:     GITHUB_CLIENT_ID,
  			ClientSecret: GITHUB_CLIENT_SECRET,
  			// RedirectURL:  "http://localhost:8080/auth/login",
  			RedirectURL:  "http://localhost:8080/auth/github/callback",
  			Scopes:       []string{"user:email"},
  			Endpoint:     github.Endpoint,
  		},
  	}

  	router := gin.Default()
  	router.GET("/auth/login", app.loginHandler)
  	router.GET("/auth/:provider/oauth", app.oAuthHandler)
  	router.GET("/auth/:provider/callback", app.oAuthCallbackHandler)

  	router.Run(":8080")
  }
  ```

</details>