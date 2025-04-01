package main

import (
	"context"
	"encoding/json"
	"html/template"
	"log"
	"net/http"

	"github.com/gin-gonic/gin"
	"golang.org/x/oauth2"
	"golang.org/x/oauth2/github"
	"golang.org/x/oauth2/google"
)

var GOOGLE_CLIENT_ID = ""
var GOOGLE_CLIENT_SECRET = ""
var GITHUB_CLIENT_ID = ""
var GITHUB_CLIENT_SECRET = ""

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

	url := config.AuthCodeURL("state-token", oauth2.AccessTypeOffline)
	c.Redirect(http.StatusTemporaryRedirect, url)
}

func (a *App) oAuthCallbackHandler(c *gin.Context) {
	provider := c.Param("provider")
	var config *oauth2.Config
	var userInfoURL string

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

	c.JSON(http.StatusOK, userInfo)
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
