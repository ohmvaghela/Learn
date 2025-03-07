# Deployment Docs


## Gunicorn and WSGI (Web Server Gateway Interface)

- **WSGI Specification:**
    - WSGI is a specification that defines a standard interface between web servers and Python web applications like Django.
- **Role of WSGI:**
    - In a typical deployment, WSGI acts as a bridge between a web server (like Nginx) and a Django application.
- **Gunicorn as a WSGI Server:**
    - Gunicorn is a Python WSGI HTTP server. It's used to deploy Python-based web applications, making them accessible over the internet.
- **Request Handling:**
    - When a request comes in, it's received by Gunicorn, which then forwards it to the Django application. The response from Django is then sent back to the web server.
- **Web Server (Nginx):**
    - The web server in front of Gunicorn is most often Nginx, which acts as a reverse proxy.
- **Synchronous Nature:**
    - WSGI handles synchronous requests. For asynchronous requests, ASGI with Uvicorn is used.
- **Features of WSGI:**
    - Multithreading support.
    - Middleware support.
- **WSGI Concurrency:**
    - WSGI is multi-threaded but not fully asynchronous.
    - Multi-threaded: Yes, it can handle multiple requests concurrently, but each request is processed in a blocking manner.
    - Concurrency: Limited to handling simultaneous requests and not suitable for long-lived connections or asynchronous tasks.

> - While we can use gunicorn as server but in production we generally use it with other server
> - As gunicron lacks in many features like serving static files, reverse proxy, load balancing etc.


### Setting up guincorn
```py
# location of gunicorn executable
command = '/usr/bin/gunicorn'
# Loaction of manage.py
pythonpath = '/home/ohm/webd/MechSimVault'
# Path to port
bind = '0.0.0.0:8000'
# number of workers
workers = 3
```
- Running server
```
gunicorn --config <path-to-gunicorn.py> <Project Name>.wsgi:application
gunicorn --config gunicorn.py MechSimVault.wsgi:application
```
```
gunicorn -c <path-to-gunicorn.py> <Project Name>.wsgi
gunicorn -c gunicorn.py MechSimVault.wsgi
```


<img src="./Gunicorn_nginx.webp" >

## Uvicorn and ASGI (Asynchronous Server Gateway Interface)

- **ASGI Specification:**
    - ASGI is a specification that defines a standard interface between asynchronous Python web servers, frameworks, and applications. It extends WSGI to handle asynchronous operations.
- **Role of ASGI:**
    - ASGI enables asynchronous communication, making it suitable for applications that require real-time features like WebSockets.
- **Uvicorn as an ASGI Server:**
    - Uvicorn is a lightning-fast ASGI server, implemented in Python and Cython. It's used to run asynchronous Python web applications.
- **Request Handling:**
    - When a request comes in, it's received by Uvicorn, which then forwards it to the Django ASGI application. Responses are handled asynchronously.
- **Web Server (Nginx):**
    - Similar to WSGI, Nginx is often used as a reverse proxy in front of Uvicorn.
- **Asynchronous Nature:**
    - ASGI handles asynchronous requests, making it ideal for WebSockets and other long-lived connections.
- **Features of ASGI:**
    - Asynchronous request handling.
    - WebSocket support.
    - Concurrency for long-lived connections.
- **ASGI Concurrency:**
    - ASGI is designed for asynchronous concurrency, allowing efficient handling of numerous simultaneous connections and tasks without blocking.
    - Handles long lived connections.
    - Allows for asynchronous task execution.



# Setting up Gunicorn
1. ### Creating a `system socket file` for `Gunicorn`
  - This will setup `Gunicorn` to run as a `systemd service` on the server currently laptop
  - This will allow us to manage gunicorn using systemd
    - Hence better control over starting, stopping and restarting webservices
  #### System scoket file
  - It is a configuration file
  - It is used by systemd to define and manage socket-based activation for services
  - For Gunicorn it manages communication btwn `NGINX` and `Gunicorn`
  #### add the following data to 'sudo vim /etc/systemd/system/gunicorn.socket'
  ```python
  [Unit]
  Description=gunicorn socket

  [Socket]
  ListenStream=/run/gunicorn.sock

  [Install]
  WantedBy=sockets.target
  ``` 
  - here unit is a configuration file that `describes` a `service, socket, device, mount point, or other resource` that `systemd manages`
  - Socket : This line specifies the socket that systemd should create and listen on
  - Install : This line specifies when the socket should be started

```
server {
    listen 80;
    server_name localhost;

    location = /base.jpg { access_log off; log_not_found off; }
    location /static/ {
        root /home/ohm/webd/django_md/nginxGunicornTest;
    }

    location / {
        include proxy_params;
        proxy_pass http://127.0.0.1:8000;
    }
}
```
