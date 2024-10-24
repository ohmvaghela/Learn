# Django

## Current django version : 5.1

## Why install django on `Python virtual Environment` and not `system wide`

- Say I have old version of django in one of my website
  - I want to create new website with latest version of django
  - So on updating version of django this might be and issue for old site
  - Hence we use `Python virtual environment` so different versions of django at same time on different apps

## Basics of Django

- It follows MVT design pattern (Model Views Template)
  - Model : Data that you want to present, usually from the database (like models in autobilling app, but this is inbuilt ORM)
  - Views : Handling requests
  - Template : HTML like files containing layout of webpage
  - URLs : on an incomming requrest, decides which url to send

## Creating a virtual evironment to work with

- To address different django version problem we can create different env for each version
- ### Creating new virtual env
        mkvirtualenv my_django_environment
- ### Using env
        workon my_django_environment
- ### Close env
        deactivate
- ### Remove evn
        rmvirtualenv my_django_environment

## Basic Commands

- creating project

  ```
    django-admin startproject mytestsite
  ```
  
- Starting project
  ```
    python3 manage.py runserver
  ```
## About files created in the project

- `__init__.py`
  - Instructs python to treat current dir as package
- `settings.py`
  - info of the apps we create, middlewares, static data location, database configs
- `urls.py`
  - URL-to-view mapping (usually direct to the url file of the perticlar application)
- `wsgi.py` (Web Server Gateway Interface)
  - help your Django application communicate with the web server
  - Treat as boilerplate code
- `asgi.py` (Asynchronous Server Gateway Interface)
  - ASGI provides a standard for both asynchronous and synchronous Python apps, whereas WSGI provided a standard for synchronous apps only
  - `Doesn't make much sense to my as of now`

## Creating an app in current project

- This command will create an app named "app_name"
  ```
  python3 manage.py startapp app_name
  ```
- This app will contain folder named `migrations` which will auto update database as we modify models

#### Registering the app

- Once the app is created then we need to register the app
- For this go to `settings.py` and in `INSTALLED_APPS` array add the config function of our app
- config function will be present in app.py file, say name of config function is `App_nameConfig`
- then we will add `app_name.apps.App_nameConfig` to the array

#### Setting Up database

- To setup the database we can add the following to `settings.py`
    ```python
    DATABASES = {
      'default': {
          'ENGINE': 'django.db.backends.sqlite3',
          'NAME': BASE_DIR / 'db.sqlite3',
      }
    }
    ```

#### setting timezone
- It is present in `settings.py` named `TIME_ZONE` for India it is `Asia/Kolkata`

#### Other settings
- Currently the `DEBUG` variable is set to `true` for development
- For prodcution it should be set to false
  
### Mapping url
- Add the boilerplate code to the `urls.py` of `my_app`  
```py
from django.urls import path
from . import views

urlpatterns = [

]
```
- Then add the path in urls.py, this will forward all the requrest to `/catalog`
```py
urlpatterns += [
    path('my_app/', include('my_app.urls')),
]
```
- We can also redirect the route like from base to /my_app (not necessary)
```py
urlpatterns += [
    path('', RedirectView.as_view(url='catalog/', permanent=True)),
]
```
- Django does not serve static files like CSS, JavaScript, and images by default
- Workaround for this is
```py
urlpatterns += static(settings.STATIC_URL, document_root=settings.STATIC_ROOT)
```


### Adding templates
- Say we created template folder in root of dir(on same level of manage.py)
- Now say we added two files in that dir `Home.html`, `About.html`
- To make these template usable we need to add following to settings
```py

TEMPLATES = [
    {
        ...
        'DIRS': [os.path.join(BASE_DIR, 'templates')],
        ...
    },
]
```
- Now we can freely use this .html files anywhere
- This is not limited to main app we can use templates in all apps of this project
- Like open views in main app and add 
```py
from django.shortcuts import render # to render page using html

def homepage(request):
  return render(request,'home.html')
def about(request):
  return render(request,'about.html')
```



### Adding CSS to template
- css are static files so first we create a `static` folder
- In that we create a `css` folder 
- To be able to use them globally add following in system.py
  ```py
  import os

  # under static_url
  STATICFILES_DIRS = [
    os.path.join(BASE_DIR,"static")
    #BASE_DIR will be mentioned above by system
  ]
  ```
- To add this css in html pages open the html file and add following
  ```html
  <!DOCTYPE html>
  <html lang="en">
  {% load static %}
  <head>
  .
  .
  .
    <link rel="stylesheet" href="{% static 'css/global.css' %}">
  </head>
  .
  .
  .
  </html>
  ```
### Adding JS to template
- Similarly create a js dir in static dir and add all the js scriptes that you want to run
- then add following to html code
    ```html
    <!DOCTYPE html>
    <html lang="en">
    {% load static %}
    <head>
    .
    .
    .
      <script src="{% static 'js/main.js' %}" defer></script>
    </head>
    .
    .
    .
    </html>
    ```

### Creating models 
- Here we will define set of rules for ORM and how we wil interact with database
- boilerplate code for models
```py
  class Model_name(models.Model):
    # This class consist of 4 main things
    title = models.CharField(max_length = 76)
    body = models.TextField()
    slug = models.SlugField()
    date = models.DateTimeField(auto_now_add = True)
```
- Once this is done we need to apply migrations to add it to project
```
  python3 manage.py makemigrations
  python3 manage.py migrate
```
- The return value for each model is defined using `__str__` method
```py
  class Model_name(models.Model):
    title = models.CharField(max_length = 76)
    .
    .
    .
    def __str__(self):
      return self.title
```

## How to access admin
```
python3 manage.py createsuperuser
```
- To see all models in admin apply following changes to `admin.py`
```py
from .models import Posts
admin.site.register(Posts)
```
## Passing data to render
- In similar way we can access the models anywhere in a perticular app
- Now say we want to pass the data to render so 
```py
from .models import Post
# Create your views here.
def post_list(request):
  post = Post.objects.all()
  return render(request,'posts/post_list.html',{'posts':post})
  # the third parameter is dictinoary
  # this will pass post data to html files 
```
## Using django html template
- This will use a html file as a base file and we can add data & html content to it  
- `base_page.html`
```html
<!DOCTYPE html>
<html lang="en">
  {% load static %}
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>
    {% block title %}
      default title
    {% endblock title %}
  </title>
  <link rel="stylesheet" href="{% static 'css/global.css' %}">
</head>
<body>
  <h1>
    {% block page %}
      page
    {% endblock page %}
  </h1>
<h3>
  {% block pagecontent %}
    content 
  {% endblock pagecontent %}
</h3>

  </body>
</html>
```
- using this in other pages say `post_page.html`
```html
{% extends "base.html" %}
{% block title %}
  Post List
{% endblock %}

{% block page %}
  <h1>  post page </h1>
{% endblock page %}

{% block pagecontent %}
<a href="/about">about</a>
<hr/>
<a href="/home">Home</a>
{% endblock pagecontent %}
```

### Using path convertor for repetative links
- Now say we want a custom path for all the diffenet entries in the models
- This is done via path converter, like say post/id1, post/id2, post/id2
- So in the `urls.py` we can add

```py
urlpatterns[
  .
  .
  .
  path('post/<str:post_id>', views.post, name='cur_post'),
]
```

- This will render view.post on `.../post/id1,2,3`
- The url pattern to identity the post is `name='cur_post'`
- Now in the html lets say we want to use this in `<a>` tag 

```html
  <a href="{% url 'single_post' post_slug=post.slug %}"\>
```

- Here 
  - `url` is the url identifier, 
  - `single_post` is the url_pattern
  - value to url_pattern is `post_slug=post.slug`
- Slug is a text with int,char,underscore,hypen which is SEO friendly
- To supply this post details in the `post/` path in views 
```py
def post_list(request, post_slug):
    return HttpResponse(post_slug)
```

- Giving namespaced url make pattern search much faster like in `urls.py`
```py
app_name = 'post_app'

urlpatterns = [
  path('',views.postPage,name='post_page'),
  path('<slug:post_slug>', views.post_list, name='single_post'),
]
```
- So the namespaces are : `post_page`, `single_post` and app name is `post_app`
- this is the sytax in html pages
```html
  <a href="{% url 'post_app:post_page' %}">Post</a>
```
### Adding media 
- settings.py
```py
MEDIA_URL = 'media/'
MEDIA_ROOT = os.path.join(BASE_DIR,'media')
```
- main project/urls.py
```py
from django.conf.urls.static import static
from django.conf import settings
urlpatterns += static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
```
- we need to update model to have images
  - Just add Pillow package `pip install Pillow`
```py

from django.db import models

class Post(models.Model):
  .
  .
  .
  banner = models.ImageField(default='fallback.png',blank=True)
  # here banner is the image holder field
```
- ### Migrate changes
- For adding this to html use following syntax
```py
    <img 
      class="banner"
      src="{{post.banner.url }}"
      alt="{{post.title}}"
    />

```

### creating a form for user registeration
- `views.py`
```py
from django.contrib.auth.forms import UserCreationForm

def register_view(request):
  form = UserCreationForm()
  return render(request, 'users/register.html', {'form':form})

def process_form(request):
  if request.method == "POST":
    form = UserCreationForm(request.POST)
    if form.is_valid():
      form.save()
      return redirect("post_app:post_page")
  return render(request, 'users/register.html', {'form':form})

```

- html : here action is the list send data of form to 
```html
  <form class="user_form" action="/users/process_form/" method="post">
    {% csrf_token %}
    {{form}}
    <button> Submit </button>
  </form>
```

## Changes for prodiction
- In `settings.py`
- Add `ports`,`website` links in `allowed_host` to avoid CORS
- Remove `import os` from `settings.py`
#### From
```py
STATIC_URL = 'static/'
MEDIA_URL = 'media/'
MEDIA_ROOT = os.path.join(BASE_DIR,'media')

STATICFILES_DIRS = [
    os.path.join(BASE_DIR, 'static')
]
```
#### to 
```py
STATIC_URL = 'static/'
MEDIA_URL = 'media/'

STATIS_ROOT = BASE_DIR/ 'assets'
MEDIA_ROOT = BASE_DIR/ 'media'

STATISFILES_DIRS = [
  BASE_DIR / 'static'
]
``` 
- Now run the command
```
python3 manage.py collectstatic
```
- This will create a static folder which will hold the static data
- We Need to run this all the time before starting the server if we change from os.path to direct path
  - So we only do this before deployments


# Django REST framework
- when we are creating api we need to serialise to JSON before sending and deserialize it while reciveing 
- add following to installed_apps in `settings.py`
```py
INSTALLED_APPS = [
  .
  .
  .
  'rest_framework',
  .
  .
  .
]
```
### Serialization
- Say we have a created a model with following fileds
- `billId`,`KeeperID`,`KeeperName`,`CustomerName`,`CustomerEmail`,`CustomerPhone`,`billAmount`,`billDescription`
- First we need to define a serializer for the model say it is named `serializers.py`
```py
from rest_framework import serializers
from .models import Bills

# ModelSerializer: Serializes the entire model, including all fields.
# Serializer: Can be used to serialize individual fields or custom data.class 
class BillSerializer(serializers.ModelSerializer):
  class Meta:
    model = Bills
    fields = ['billId', 'KeeperID', 'KeeperName', 'CustomerName', 'CustomerEmail', 'CustomerPhone', 'billAmount','billDescription']
    
```

### Defining `GET`, `POST`, `PUT`, `DELETE`
- We need to define type of request before function, we can pass multiple types of request to a single url

|  |  |
|-------------|------|
| `@api_view(['GET'])` | ``` @api_view(['GET']) ``` |
| Multiple types can also be processed for the same URL | ```py # Multiple types can also be processed for the same URL @api_view(['GET', 'POST', 'PUT']) ``` |

## Migrating from function based views to class based views
### function based view
```py
### views.py
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from .serializers import BillSerializer
from .models import Bills
from rest_framework.views import APIView

@api_view(['GET'])
def get_user(request):
    if request.method == 'GET'
      bills = Bills.objects.all()
      serializer = BillSerializer(bills,many = True)
      return Response(serializer.data)

@api_view(['POST'])
def create_user(request):
    serializer = BillSerializer(data=request.data)
    if serializer.is_valid():
        serializer.save()
        return Response(serializer.data, status=status.HTTP_201_CREATED)
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
### urls.py
urlpatterns = [
  path('get_data/', get_user, name="get_bill_data"),
  path('create_user/', create_user, name="create_bill"),
]
```
### Class based view for the same
```py
### views.py

# The base view from which we will inherit our classes
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework import status
from .serializers import BillSerializer
from .models import Bills
from rest_framework.views import APIView

class get_user(APIView):
  def get(self,request):
    bills = Bills.objects.all()
    serializer = BillSerializer(bills,many = True)
    return Response(serializer.data)

## second First way of writing 
class create_user(APIView):
  def post(self,request):
    serializer = BillSerializer(data=request.data)
    if serializer.is_valid():
        serializer.save()
        return Response(serializer.data, status=status.HTTP_201_CREATED)
    return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
  
### urls.py
# we just need to add `as_view()` at end
urlpatterns = [
  path('get_data/', get_user.as_view(), name="get_bill_data"),
  path('create_user/', create_user.as_view(), name="create_bill"),
]
```

## Auth token 

# Custom user creation (Fetch / login / signup)
- We inherit user model form default user model to have the functionalities but changed fields
- We can inherit two types of user model `AbstractUser` or `AbstractBaseUser`
  - AbstractUser: Use when we want to keep the existing field on user model and add custom fields
  - AbstractBaseUser: when we want to create user from scratch
- Then we also have `PremissionMixin` so we can access django premission framework for `is_superuser`,`groups`, `usepermissions`  
- Example of model anmed `siteUser.py`
```py
import os
from django.db import models
from django.contrib.auth.models import AbstractBaseUser, PermissionsMixin
from django.utils import timezone
from .manager import SiteUserManager

class SiteUser(AbstractBaseUser, PermissionsMixin):
  # profile picture
 
  email = models.EmailField("email id", max_length=254, unique=True)
  is_superuser = models.BooleanField("is_superuser", default=False)
  full_name = models.CharField("full name", max_length=50, default="Default name")
  bio = models.CharField("bio", max_length=500, default="", blank=True)
  institution = models.CharField("institution", max_length=250, default="", blank=True)  # Corrected typo
  role = models.CharField("role", max_length=250, default="", blank=True)
  country = models.CharField("country", max_length=250, default="INDIA", blank=True)
  contact_info = models.CharField("contact info", max_length=250, default="", blank=True)
  skills = models.CharField("skills", max_length=250, default="", blank=True)
  signup_date = models.DateTimeField("user creation date", default=timezone.now)

  USERNAME_FIELD = "email"
  REQUIRED_FIELDS = []

  objects = SiteUserManager()

  def __str__(self):
    return self.email

```
- `objects = SiteUserManager()` here manager is created for the model which is responsible for
  - `creating` and `managing` model instance
- Example of manager.py
```py
from django.contrib.auth.base_user import BaseUserManager
from django.utils.translation import gettext_lazy as _ 

class SiteUserManager(BaseUserManager):
  
  # create user using email
  def create_user(self,email,password,**extra_fields):
    
    if not email:
      raise ValueError("Email not present")
    email = self.normalize_email(email)
    user = self.model(email=email, **extra_fields)
    # create hashed password
    user.set_password(password)
    user.save()
    return user
  
  def create_superuser(self, email, password, **extra_fields):
    extra_fields.setdefault("is_superuser", True)
    return self.create_user(email,password,**extra_fields)
```
- `super()` is used to access functions of parent classes
- settings.py
```py
AUTH_USER_MODEL = "siteUser.SiteUser"
```

## serializers
- They are used to serialize and deserialize `JSON` data
- Serializing and deserializing data
```py
  serialized_data = CusotmSerializerFunction(deserialized_data)
  deserialized_data = CusotmSerializerFunction(data=serialized_data)
```
> When deserializing data, you always need to call `is_valid()` before attempting to access the validated data, or save an object instance.
#### serializer for fetching all users
```py
from rest_framework import serializers
from .models import SiteUser
from django.contrib.auth import authenticate

class SiteUserSerializer(serializers.ModelSerializer):
  class Meta:
    model = SiteUser
    fields = [
        'id', 'profile_picture', 'email', 'is_superuser', 'full_name', 
        'bio', 'institution', 'role', 'country', 'contact_info', 
        'skills', 'signup_date'
    ]
    read_only_fields = ['id', 'signup_date']
```
- `read_only_fields` : these fields can be seen in output but cannot be used while giving input

- Using Serialize in view
```py
class SiteUserList(APIView):
  def get(self,request):
    users = SiteUser.objects.all()
    serializer = SiteUserSerializer(users, many=True)
    return Response(serializer.data, status=status.HTTP_200_OK)
```
- Over here `serializer = SiteUserSerializer(users, many=True)`
  - `many=True` : serialize multiple instances
```py
@urls.py
from django.urls import path
from . import views

urlpatterns = [
  path("get_users/",views.SiteUserList.as_view(),name="list site user"),
]
```

#### serializer for Creating user
- We need to define a `create` method for serialize when we are siging up a user
```py
class SiteUserCreate(APIView):
  def post(self,request):
    # returns `SiteUser` instance  
    serializer = SiteUserCreateSerializer(data = request.data)
    # checks for all errors
    if serializer.is_valid():
      # as serializer is user instance 
        # hence we can use save to save it to db 
      user = serializer.save()
      return Response({
        "message":"user created succesfully",
        "user":SiteUserCreateSerializer(user).data
      },status.HTTP_202_ACCEPTED)
    return Response(serializer.errors,status = status.HTTP_400_BAD_REQUEST)      

```

```py
class SiteUserCreateSerializer(serializers.ModelSerializer):
  password = serializers.CharField(write_only=True)

  class Meta:
    model = SiteUser
    fields = [
        'profile_picture', 'email', 'password', 'full_name', 
        'bio', 'institution', 'role', 'country', 'contact_info', 
        'skills'
    ]
  # responsible for creating SiteUser instance
  def create(self, validated_data):
    user = SiteUser.objects.create_user(
        email=validated_data['email'],
        password=validated_data['password'],
        profile_picture=validated_data.get('profile_picture', 'profile_picture/base.jpg'),
        full_name=validated_data.get('full_name', 'Default name'),
        bio=validated_data.get('bio', ''),
        institution=validated_data.get('institution', ''),
        role=validated_data.get('role', ''),
        country=validated_data.get('country', 'INDIA'),
        contact_info=validated_data.get('contact_info', ''),
        skills=validated_data.get('skills', '')
    )
    return user
```

#### serializer for user login
- here to validate if user exist and is valid we modifly `validate` method

```py
class SiteUserLoginSerializer(serializers.Serializer):
  # individual serializer for email
  email = serializers.EmailField()
  # individual serializer for password
  password = serializers.CharField(write_only=True)

  def validate(self, data):
    email = data.get('email')
    password = data.get('password')

    if email and password:
      # even though email and password are provided but still `self.context.get('request')` is provided beacuse
        # it may have additional info for other backend tasks like  
          # token based authentication
          # session managenment
          # IP address etc 
      user = authenticate(request=self.context.get('request'), email=email, password=password)
      if not user:
        raise serializers.ValidationError("Invalid login credentials")
    else:
      raise serializers.ValidationError("Must include 'email' and 'password'")

    data['user'] = user
    return data
```
- view.py
```py
class SiteUserLogin(APIView):
  def post(self,request):
    serializer = SiteUserLoginSerializer(data = request.data)
    if serializer.is_valid():
      user = serializer.validated_data['user']
      return Response({"message": "Login successful", "user": user.email},
                      status=status.HTTP_200_OK)
    return Response(serializer.errors,status = status.HTTP_400_BAD_REQUEST)      

```
#### serializer for user update
- we need to enable partial field to true so partial model can be sent
``` 
serializer = SiteUserUpdateSerializer(user,data=request.data,partial=True)
```
```py

class SiteUpdateUser(APIView):
  def post(self,request,pk):
    try:
      user = SiteUser.objects.get(pk=pk)
    except SiteUser.DoesNotExist:
      return Response({"error":"user does not exist"},status=status.HTTP_404_NOT_FOUND)

    if 'email' in request.data or 'password' in request.data:
      return Response({'error': 'Email and password updates are not allowed'}, status=status.HTTP_400_BAD_REQUEST)
    
    serializer = SiteUserUpdateSerializer(user,data=request.data,partial=True)
    if serializer.is_valid():
      serializer.save()
      return Response(serializer.data, status=status.HTTP_202_ACCEPTED)
    return Response(serializer.errors,status=status.HTTP_400_BAD_REQUEST)
class SiteUpdatePassword(APIView):
  def post(self,request,pk):
    try:
      user = SiteUser.objects.get(pk=pk)
    except:
      return Response({"error":"user does not exist"},status=status.HTTP_404_NOT_FOUND)
    
    password = request.data.get('password')
    if password:
      user.set_password(password)
      user.save()
      return Response({"message":"password modified"},status=status.HTTP_202_ACCEPTED)
    return Response({"message":"password not found"},status=status.HTTP_400_BAD_REQUEST)
  
  
```

```py
class SiteUserUpdateSerializer(serializers.ModelSerializer):
  class Meta:
    model = SiteUser
    fields = [
        'profile_picture', 'full_name', 'bio', 'institution', 
        'role', 'country', 'contact_info', 'skills'
    ]
    read_only_fields = ['id', 'signup_date','email', 'password']
```

# Using JWT 
- pip install djangorestframework-simplejwt
- `settings.py`
```PY
from datetime import timedelta

INSTALLED_APPS = [
  .
  .
  .
  'rest_framework_simplejwt',
  .
  .
  .
]

REST_FRAMEWORK = {
    'DEFAULT_AUTHENTICATION_CLASSES': (
        'rest_framework_simplejwt.authentication.JWTAuthentication',
    ),
}
SIMPLE_JWT = {
    'ACCESS_TOKEN_LIFETIME': timedelta(minutes=5),
    'REFRESH_TOKEN_LIFETIME': timedelta(days=1),
}

```
## Handling login and signup
```py
from rest_framework_simplejwt.tokens import RefreshToken
# ###### #
# SignUp #
# ###### #
class SiteUserCreate(APIView):
  def post(self,request):
    serializer = SiteUserCreateSerializer(data = request.data)
    if serializer.is_valid():
      user = serializer.save()

      ## once the user is saved then we should generate token on its behalf 
      refresh = RefreshToken.for_user(user)
      access_token = str(refresh.access_token)
      refresh_token = str(refresh)

      response = Response({
        "message": "User created successfully",
        "user": SiteUserCreateSerializer(user).data,
        "access_token": access_token
      }, status=status.HTTP_201_CREATED)
      
      # Sending refresh token as cookie
      response.set_cookie(
        key='refresh_token',
        value=refresh_token,
        httponly=True,
        secure=True,
        samesite='Strict'
      )
      
      return response

    return Response(serializer.errors,status = status.HTTP_400_BAD_REQUEST)      
# ##### #
# login #
# ##### #
class SiteUserLogin(APIView):
  def post(self,request):
    serializer = SiteUserLoginSerializer(data = request.data)
    if serializer.is_valid():
      user = serializer.validated_data['user']
      refresh = RefreshToken.for_user(user)
      access_token = str(refresh.access_token)
      refresh_token = str(refresh)      
      response =  Response({
        "message": "Login successful", 
        "user": user.email,
        "access_token": access_token,
      },
      status=status.HTTP_200_OK)
      response.set_cookie(
        key='refresh_token',
        value=refresh_token,
        httponly=True,
        secure=True,
        samesite='Strict'
      )
      return response
    return Response(serializer.errors,status = status.HTTP_400_BAD_REQUEST)
```

## Handling request using token

```py
from rest_framework.permissions import IsAuthenticated
from rest_framework_simplejwt.authentication import JWTAuthentication

class SiteUpdateUser(APIView):
  # this class is responsible for decoding and verifying data and adding user info to request.user  
  authentication_classes=[JWTAuthentication]
  # this is responsible for managing permissions to the class, 
    # currently only token permission is required,
    # but for complex structues if more premissions are required then this can be used 
    # but now it can commented if required
  permission_classes=[IsAuthenticated]

  def post(self,request):
    # As token is used so user {email,password} are stored in request.user
    user = request.user

    if 'email' in request.data or 'password' in request.data:
      return Response({'error': 'Email and password updates are not allowed'}, status=status.HTTP_400_BAD_REQUEST)
    
    serializer = SiteUserUpdateSerializer(user,data=request.data,partial=True)
    if serializer.is_valid():
      serializer.save()
      return Response(serializer.data, status=status.HTTP_202_ACCEPTED)
    return Response(serializer.errors,status=status.HTTP_400_BAD_REQUEST)
  
class SiteUpdatePassword(APIView):
  # this class is responsible for decoding and verifying data and adding user info to request.user  
  authentication_classes=[JWTAuthentication]
  # this is responsible for managing permissions to the class, 
    # currently only token permission is required,
    # but for complex structues if more premissions are required then this can be used 
    # but now it can commented if required
  permission_classes=[IsAuthenticated]
  
  def post(self,request):
    # As token is used so user {email,password} are stored in request.user    
    user = request.user
    
    password = request.data.get('password')
    if password:
      user.set_password(password)
      user.save()
      refresh = RefreshToken.for_user(user)
      access_token = str(refresh.access_token)
      refresh_token = str(refresh)            
      response =  Response({
        "message": "password update successful", 
        "user": user.email,
        "access_token": access_token,
      },
      status=status.HTTP_200_OK)
      response.set_cookie(
        key='refresh_token',
        value=refresh_token,
        httponly=True,
        secure=True,
        samesite='Strict'
      )
      return response
    return Response({"message":"password not found"},status=status.HTTP_400_BAD_REQUEST)

```
```yaml
### update data
POST http://localhost:8000/siteUser/update/
Authorization: Bearer add_token_over_here
Content-Type: application/json

{
  "full_name":"ohm N vaghela"
}

### update password
POST http://localhost:8000/siteUser/update_password/
Authorization: Bearer add_token_over_here
Content-Type: application/json

{
  "password":"ohm123ohm"
}
```

# Handing authozation and permissions
## Creating instance under a user
- Getting the user_id and passing to instance can be tricky at time
- we can pass it to `self.context` as done here 
```py
@views # remove this 
class AddSimulation(APIView):
  authentication_classes=[JWTAuthentication]
  permission_classes=[IsAuthenticated]
  
  def post(self, request):    
    user = request.user
    request.data['user_id'] = user.id

    if 'zip_file' not in request.data or 'zip_photos' not in request.data:
      return Response(
        {"message":"zip file or zip photo does not exist"},
        status=status.HTTP_400_BAD_REQUEST
      )
    #################################################
    # this is way to pass user to instance creation #
    #################################################
    serializer = SimulationSerializer(
        data=request.data, 
        context={'request': request})
    
    
    if serializer.is_valid():
      serializer.save()
      return Response(serializer.data, status=status.HTTP_201_CREATED)
    return Response(serializer.errors,status=status.HTTP_400_BAD_REQUEST)

@serializer # remove this

class SimulationSerializer(serializers.ModelSerializer):  
  class Meta:
    model = Simulation
    fields = '__all__'
  
  def create(self,validated_data):
    ###########################################################
    # this is way to access user at time of instance creation #
    ###########################################################
    user = self.context['request'].user
    print("user_found :",user)
    simulation = Simulation.objects.create(
      user_id=user,
      title=validated_data.get('title','empty title'),
      description=validated_data.get('description','empty description'),
      Softwares=validated_data.get('Softwares','now expertise'),
      simulation_image=validated_data.get('simulation_image','simulations/default_photo.png'),
      zip_file=validated_data.get('zip_file',None),
      zip_photos=validated_data.get('zip_photos',None),
    )    
    return simulation
```

## getting data associated to a user
- views.py
```py
class GetSimsByID(APIView):
  
  def get(self,request,pk):
    user = SiteUser.objects.get(pk=pk)
    sims = Simulation.objects.filter(user_id=user)
    serializer = SimulationSerializer(sims,many=True)
    return Response(serializer.data,status=status.HTTP_200_OK)
```
## Update or Delete Access to a perticular user 
```py

class UpdateSimulation(APIView):
  
  authentication_classes = [JWTAuthentication]
  permission_classes = [IsAuthenticated]
  
  def post(self, request,pk):
    data = request.data.copy()
    try:
        simulation = Simulation.objects.get(pk=pk)
    except Simulation.DoesNotExist:
        return Response({"error": "Simulation not found"}, status=status.HTTP_404_NOT_FOUND)
      
    # Is the user is not creator of simulation then dont allow to access   
    # though it will be handled in frontend but a check on backend is good
    if simulation.user_id.id != request.user.id:
      return Response({"ids":f"user_id:{request.user.id}, sim:{simulation.user_id.id}","error": "user cannot update others model"}, status=status.HTTP_401_UNAUTHORIZED)
    
    data['user_id'] = simulation.user_id.id
     
    if 'zip_file' in request.FILES:
        simulation.delete_zip_file()
    if 'zip_photos' in request.FILES:
      simulation.delete_zip_photos()
    if 'simulation_image' in request.FILES:
      simulation.delete_simulation_image()
        
    serialize = SimulationUpdateSerializer(
      simulation,data=data,
      context={'request':request},
      partial=True
    )
    
    if serialize.is_valid():
      serialize.save()
      return Response({
        "message": "Simulation updated", 
        "title": serialize.data['title'],
        "description":serialize.data['description']
        },status=status.HTTP_202_ACCEPTED)
    
    return Response(serialize.errors, status=status.HTTP_400_BAD_REQUEST)

```
- serialization
```py
class SimulationUpdateSerializer(serializers.ModelSerializer):
  class Meta:
    model = Simulation
    fields = '__all__'
    read_only_fields = ['user_id']
```





