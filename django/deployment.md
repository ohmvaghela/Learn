# Deployment Docs


## Gunicorn and WSGI (Web Server Gateway Interface)
- It is a `specification` that defines how `webserver` interact with web applications like django
- In this project WSGI acts a bridge between nginx and django application
- `Gunicorn` is a `Python WSGI HTTP server` and is used to make python based web application accessible to internet
- Basically whenever a request comes it is recieved by gunicorn and sent to django and the response is then sent back to server
- And the webserver is most of the times NGINX
- But these request are syncronus and sommetimes we may require async request so we can use `ASGI` with `Uvicorn`
- Features of WSGI
  - Multithreading
  - Middleware Support
> - WSGI is multi threading but not async fully
>   - Multi-threaded: Yes, but it processes each request in a blocking manner. Each request waits for the previous one to complete.
>   - Concurrency: Limited to handling simultaneous requests but not suitable for long-lived connections or asynchronous tasks.
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

## Uvicorn and ASGI
- To make request async gunicron creates and manages Uvicorn workers
- Uvicorn recieves request from Gunicorn and processes it
- As it will be a long lived request so it can manage long-lived connections like WebSockets or background tasks.
- Uvicorn communicates with your Django application via the ASGI interface
- Process under the hood

<center>

### Client → Gunicorn → Uvicorn → Django (ASGI) → Uvicorn → Gunicorn → Client

</center>




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
## WSGI and ASGI
- WSGI (Web Server Gateway Interface)
  - 

## Static and media files in production

