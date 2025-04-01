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
  - [`Third-Party Authorization Server` calls client callback with `authorization code`](#third-party-authorization-server-calls-client-callback-with-authorization-code)
  - [Client Request for resources from Resource server](#client-request-for-resources-from-resource-server)


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
- It has config struct which has both client and auth-server info
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

  ```go
  // Syntax
  // state : token that is used to prevent CSRF attack
  func (c *Config) AuthCodeURL(state string, opts ...AuthCodeOption) string

  // Uses
  // 1. oauth2.AccessTypeOffline : requests a refresh token along with the access token.
  url := config.AuthCodeURL("state-token", oauth2.AccessTypeOffline)
  // 2. oauth2.AccessTypeOnline : No refresh token provided, access token only for current sessions
  url := config.AuthCodeURL("state-token", oauth2.AccessTypeOnline)

  // Additional Prompt
  // prompt:concent : Forces user consent screen every time, Used when switching accounts.
	url := config.AuthCodeURL("state-token", oauth2.AccessTypeOffline, oauth2.SetAuthURLParam("prompt", "consent"))
  ```

- Using this url is generated

  ```bash
  https://github.com/login/oauth/authorize?access_type=offline
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

## `Third-Party Authorization Server` calls client callback with `authorization code`
- It has auth code

  ```go
	code := c.Query("code")
  ```
- Then using it access token and other details are extracted by making post request to resource server
  - For getting token in go

  ```go
	token, err := config.Exchange(context.Background(), code)
  ```

  - Internal POST request looks like

  ```bash
  POST /login/oauth/access_token HTTP/1.1
  Host: github.com
  Content-Type: application/x-www-form-urlencoded
    client_id=your-client-id&
    client_secret=your-client-secret&
    code=code
  ```

- And then response like following is recieved internally and converted to `oauth2.Token`
  
  ```json
  {
    "access_token": "ya29.a0AfH6S...",
    "expires_in": 3600,
    "refresh_token": "1//0g5sdR...",
    "scope": "email profile",
    "token_type": "Bearer"
  }
  ```

- The converted `oauth2.Token` looks like

  ```go
  // syntax
  type Token struct {
  	AccessToken string `json:"access_token"`
  	TokenType string `json:"token_type,omitempty"`
  	RefreshToken string `json:"refresh_token,omitempty"`
  	Expiry time.Time `json:"expiry,omitempty"`
  	ExpiresIn int64 `json:"expires_in,omitempty"`
  }
  // Actual token
  Token :  &{
    access_token_string // Access token 
    bearer  // Token type
    0001-01-01 00:00:00 +0000 UTC // Expiry      
    // idk what and why it this
    map[ 
      access_token:[
        access_token_string
      ] 
      scope:[
        user:email
      ] 
      token_type:[
        bearer
      ]
    ] 
    }
  ```

## Client Request for resources from Resource server
- To do the same `http.Client` is created and details are attached using

  ```go
	client := config.Client(context.Background(), token)
  ```

- List of details attached are 

  ```go
  &http.Client{
      Transport: &oauth2.Transport{
          Base: http.DefaultTransport,   // Uses default transport for networking.
          Source: oauth2.StaticTokenSource(token), // Fetches token when needed.
      },
  }
  ```

- Then request is send

  ```go
	response, err := client.Get(userInfoURL)
  ```

  - Now the request would look like 
  
  ```yaml
  GET /oauth2/v2/userinfo?fields=email,name HTTP/1.1
  Host: www.googleapis.com
  Authorization: Bearer ya29.a0AfH6SM...
  User-Agent: Go-http-client/1.1
  Accept-Encoding: gzip
  ```

- And then data is extraced from the response

  ```go
	client := config.Client(context.Background(), token)
	response, err := client.Get(userInfoURL)
	if err != nil {
		c.String(http.StatusBadRequest, "Failed to fetch user info: %v", err)
		return
	}
	defer response.Body.Close()

	var userInfo map[string]interface{}
	err = json.NewDecoder(response.Body).Decode(&userInfo)
	if err != nil {
		c.String(http.StatusInternalServerError, "Failed to decode user info")
		return
	}

	c.JSON(http.StatusOK, userInfo)
  ```