# Docker learn 

- [Virtual Machine v/s container](./readme.md#Virtual-Machine-v/s-container)
- [Docker Architecture](./readme.md#Docker-Architecture)
- [Docker Architecture inDepth](./readme.md#Docker-Architecture-inDepth)
- [Docker Images](./readme.md#Docker-Images)
- [Docker basic commands](./readme.md#Docker-basic-commands)
- [Storages in docker](./readme.md#Storages-in-docker)
- [Docker volumes v/s docker mounts](./readme.md#Docker-volumes-v/s-docker-mounts)
- [Docker Network](./readme.md#Docker-Network)
- [Few other options](./readme.md#Few-other-options)
- [Docker compose](./readme.md#Docker-compose)
- [Multi stage builds](./readme.md#Multi-stage-builds)
- [Deploying mysql using docker](./readme.md#Deploying-mysql-using-docker)
- [Best Security practice](./readme.md#Best-Security-practice)

## Virtual Machine v/s container
| Virutal Machine|Container|
|-|-|
|While virtual machine are an abstraction of a machines |Containers are isolated environment for running an application|
|Like say there is an OS, so on top of it there will a `hypervisor` that will manage other OS inside current OS |On the other hand these containers are lightweight and use the host OS as host |
|like : VMware, VirtalBox|More specifically they use `kernel of host`|
|But the problem with this is each OS is a full blown OS and hence are slow to start coz entire OS will be loaded like starting a your PC|They start quickly and need less resource|
|Also these are resource intensive and the hardware needs to be divied like out of 16GB ram 4GB will be given to aother OS| |

## Docker Architecture
- Client-Server Architecture
- We have docker client : Which interact with `docker daemon(dockerd)` using `Docker Engine API` and request are `RESTful API`
  - For local access : `/var/run/docker.sock`
  - For Remote Access or TCP scoket : `http://localhost:2375` or `https://remote-server:2376`
- Docker Daemon : Responsible for running and managing container
- Docker Host : The host machine which runs containers
- Docker Registery : DockerHub
- Docker Object : images, containers, volumes, networks, and other objects
- Docker Image : Read-only template, works as template for docker container
- Docker Container : Ready to run applications

![image](https://github.com/user-attachments/assets/60ab21c7-2b86-4ac0-8b69-9ae06bf616ab)

## Docker Architecture inDepth

- Docker Daemon : It communicates with OS using system calls
  - It interacts with OS for resources allocation
  - Once resources are allocated `dockerd` interacts with `containerd` and `runc` for managing container lifecycle
  - `dockerd` sends request to `containerd` and `runc` using `gRPC API` 
- Containerd : High level container runtime
  - It manages image pulling, creating and stoping container, managing container networks and storages
- Runc : Low Level `OCI(Open Container Initiative)` runtime
  - It is actually responsible for starting a container
  - It interacts with linux kernel for creating `namespace`, `cgroups`, security restrictions
    - cgroups : group with fixed amount of resources
- Final working of docker image
  - Docker CLI → Talks to Docker Daemon (dockerd) via REST API.
  - Docker Daemon → Talks to containerd via gRPC API.
  - containerd → Manages images, snapshots, storage, and networking.
  - containerd → Calls runc to actually create and start containers.
  - runc → Uses Linux Kernel features (cgroups, namespaces) to isolate and run the container.

![image](https://github.com/user-attachments/assets/a8fee2c5-08cd-432e-a629-bda0fdc4a65b)

### Docker Images
- Read-only templates containing instructions for creating container
- Think of an image like a blueprint or snapshot of what will be in a container when it runs.
- The image relies on the host operating system (OS) kernel
- Images can be build using `Dockerfile` or pulled from repo
- Docker images are immutable, so you cannot change them once they are created.

### Docker basic commands

|Purpose|Command|
|-|-|
|List all running container| ``` dosker ps ```|
|List all containers (running and dead) | ``` docker ps -a ``` | 
| List all images | ``` docker image ls ```| 
| Run docker image without entering | ``` docker run  <image-name> ``` |
| Stop docker image | ```docker stop <container-name>``` | 
| Interact with running container | ``` docker exec <container-id> <command> ``` |
| Attach current command line with container | ``` docker run -it <image-name> --name <container-name>```  container name is optional|

## Storages in docker
- When a container is closed dat is lost so to persist the data we used docker volumes
- Data volume :
  - docker managed volumes, have a default storage location on host os whcih can be changed
  - They persist even if container is removed
   
    ```
    docker volume create mydata
    docker run -d --mount type=volume,source=mydata,target=/app/data nginx
    ```

    ```yaml
    version: '3.8'  # Define the Docker Compose file format version
    
    services:
      web:
        image: nginx  # Use the official Nginx image
        ports:
          - "8080:80"  # Map port 8080 on host to port 80 on container
        volumes:
          - ./html:/usr/share/nginx/html  # Bind mount local directory to container
    
      db:
        image: mysql:8.0  # Use MySQL 8.0 image
        environment:
          MYSQL_ROOT_PASSWORD: root
          MYSQL_DATABASE: mydb
          MYSQL_USER: user
          MYSQL_PASSWORD: password
        volumes:
          - db_data:/var/lib/mysql  # Named volume for persistent data storage
    
    volumes:
      db_data:  # Declare a named volume
    ```

- Volume Containers :
  - This is also used for persisting data but the volume is availabe for sharing
  - Useful when multiple containers need to interact with same volume
  - Creating volume
    
    ```
    docker create -v /app/data --name data_container alpine
    ```

    ```yaml
    version: '3.8'
    services:
      app:
        image: myapp
        volumes:
          - app_data:/app/data  # Named volume
    
    volumes:
      app_data:  # Declare a named volume
    ```

  - Using volume with other containers

    ```
    docker run --rm --volumes-from data_container nginx
    ```

- Bind mounts :
  - If host dirs are to be tied with docker container
  - Mount /home/user/appdata from the host to /data inside the container

    ```
    docker run -d --mount type=bind,source=/home/user/appdata,target=/data nginx
    ```

- Storage Plugins (External Storage)

## Docker volumes v/s docker mounts
- Mounts help in more fine-grained control over data storage
1. Readablitiy and flexibility

  ```bash
  docker run --rm \
    --mount type=volume,source=mydata,target=/app/data \
    alpine ls /app/data
  ## same as
  docker run --rm -v mydata:/app/data alpine ls /app/data
  ```

2. Read only host dir

  ```
  docker run --rm \
    --mount type=bind,source=/var/log,target=/logs,readonly \
    ubuntu ls /logs
  ```

  ```
  version: '3.8'
  services:
    app:
      image: myapp
      volumes:
        - /var/log:/app/logs:ro 
  ```

3. Ephemeral In-Memory Filesystem

  ```
  docker run --rm \
    --mount type=tmpfs,target=/app/cache,tmpfs-size=500m \
    ubuntu df -h /app/cache
  ```

  ```yaml
  version: '3.8'
  services:
    cache:
      image: redis
      tmpfs:
        - /var/lib/redis:size=100m 
  ```

## Docker Network
1. Brigde Network (default)
    - Used when container on same host need to communicate with each other
    - by default outbound is enabled
    - Inbound communication requires port mapping
    - Containers on same network can communicate with container name

    ```bash
    # Create a custom bridge network
    docker network create my_bridge
    
    # Run containers connected to the bridge network
    docker run -d --network my_bridge --name web nginx
    docker run -d --network my_bridge --name db mysql
    ```

2. Host network
    - Container shares host network stack, i.e there is no network isolation between host and container
    - Useful when working with performance-sensitive applications like NGINX

   ```
   docker run --rm --network host nginx
   ```

3. None Network
    - Disables networking completely.
    - The container has no network access.

   ```
    docker run --rm --network none
   ```
   
### Port mapping
- When containers are isolated, we can expose certain ports to allow inbound requests using port mapping

  ```
  docker run -d -p <host_port>:<container_port> <image>
  docker run -d -p 8080:80 -p 443:443 nginx
  docker run -d -p 127.0.0.1:8080:80 nginx
  ```

### why custom network
- Say we want to make two different connection of container to communicate between themselves and not to each other
- This can be done using custom network
- Eg. 
  ```
  docker network create -d bridge custom-network-1
  docker network create -d bridge custom-network-2
  ```
  ```
  docker run -it --network=custom-network-1 --name=container10 alpine 
  docker run -it --network=custom-network-1 --name=container11 alpine 
  docker run -it --network=custom-network-1 --name=container12 alpine 
  ```
  ```
  docker run -it --network=custom-network-2 --name=container20 alpine 
  docker run -it --network=custom-network-2 --name=container21 alpine 
  docker run -it --network=custom-network-2 --name=container22 alpine 
  ```
- So here container10, container11, container12 can communicate with each other and container20, container21, container22 can communicate with each other 
- And container on custom-network-1 cannot communicate with custom-network-2 


## Few other options

### Passing env variables 
```
docker run -e key1=value1 -e key2=value2 <image-name> 
```

### Building docker image
- Use `-t` tag to name image
```
docker build -t <image-name> .
```

### Removing image
- First you need to remove all the container using the image

  ```
  docker rm <container-name>
  ```

- To remove all containers 

  ```
  docker rm $(docker ps -aq)
  ```

- Removing image

  ```
  docker image rm <image-name>
  ```

### Changing tag name of docker image

  ```
  docker tag <image_id> <new_image_name>:<new_tag>
  ```

### Layers Caching 

  - Docker caches automatically so if a same code runs it will be faster
  ```
  FROM ubuntu
  
  COPY package.json package.json
  COPY package-lock.json package-lock.json
  COPY main.js main.js
  
  RUN apt-get update
  RUN apt-get install -y curl
  RUN curl -SL https://deb.nodesource.com/setup_18.x | bash
  RUN apt-get upgrade -y
  RUN apt-get install -y nodejs
  RUN npm install
  
  CMD ["node", "index.js"]
  
  ```

- Say if we build this again then it will run faster
- but say have a change in main.js so till that line cache will be used but after that everything will run again 
- Hence it is better to organize Dockerfile correctly

  ```
  FROM ubuntu
  
  RUN apt-get update
  RUN apt-get install -y curl
  RUN curl -SL https://deb.nodesource.com/setup_18.x | bash
  RUN apt-get upgrade -y
  RUN apt-get install -y nodejs
  RUN npm install
  
  COPY package.json package.json
  COPY package-lock.json package-lock.json
  COPY main.js main.js
  
  CMD ["node", "index.js"]  
  ```
  
### Coping files from another folder 
- If the file structure is as follows

  ```
  Project-folder/
  ├── docker/
  │   ├── Dockerfile
  │   └── .dockerignore
  └── other file-folder
  ```

- Then build docker image from the `project-folder` and not from the `docker` folder

  ```
  docker build -t <image-name> -f docker/Dockerfile .
  ```

## Docker compose
- Create, run or destroy multiple container at once 
- file name `docker-compose.yml`

  ```yaml
  version: '3.8'
  services:
    postgres:
      image: postgres # hub.docker.com
      ports:
        -'5432:5432'
      environment:
        POSTGRES_USER: postgres
        POSTGRES_DB: review
        POSTGRES_PASSWORD: password
    redis:
      image: redis
      port: 
        - '6379:6379'
  ```

- Start/Stop docker-compose

  ```
  docker compose up
  docker compose down
  ```

- Name should be same `docker-compose.yml`
- to run `docker compose up` go to the directory having `docker-compose.yml` then run it.

### Inspecting network
- We can check the details of continer on a certain network using
``` 
docker network inspect <network-name>
```
### Remove network 
```
docker network rm <network-name>
```

## Multi stage builds
- There are some modules and files that are required for building and not for running 
- Hence these files consume extra space when running but are not required 
- To solve this there is multi-stage build
  - Here everything is installed and then only the necessary things are coppied to the image
- Eg.
```docker
##########
# Part 1 #
##########

FROM node: 14-alpine AS builder
# Add a work directory
WORKDIR /app
#Cache and Install dependencies
COPY package.json.
RUN npm install
# Copy app files
COPY..
ENV NODE_ENV production
#Build the app
RUN yarn build

##########
# Part 2 #
##########
FROM production.0-alpine as production
ENV NODE_ENV production
# Copy built assets from builder
COPY --from-builder /app/build /usr/share/nginx/html
RUN ls -latr /usr/share/nginx/html
# Add your nginx.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Expose port
EXPOSE 80
# Start nginx
CMD ["nginx", "-9", "daemon off;"]

###########################################################################
# Part 1 will be deleted after the build and will not take space in image #
###########################################################################

```

> ## `ADD` vs `COPY` in docker file
> - both are used to copy files to image, the difference is `ADD` can do much more from `COPY`
> - `ADD` can copy data from URL, or can unpack compressed file

## Deploying mysql using docker 
- Say you have mysql image and are going to use it as database for django
- To create a user at start use following script
  - `Caution` : Volume can be tricky over here
    - If container has once then volume will have details and on restart it may not run `mysql` files as desired
  ```yaml
    version: '3.8'

    services:
      mysql-test:
        image: mysql:9.0.1
        container_name: mysql
        environment:
          MYSQL_ROOT_PASSWORD: password
          MYSQL_DATABASE: mech_sim_db
          MYSQL_USER: mechsimvault-server
          MYSQL_PASSWORD: Ohm@42@42@42@
        ports:
          - "3306:3306"
        volumes:
          - /home/docker-volume:/var/lib/mysql
          - ./init.sql:/docker-entrypoint-initdb.d/init.sql

    volumes:
      mysql-data:
        driver: local
    ```
  - `init.mysql`
  ```sql
  #
  -- if user exist then delete it
  DROP USER IF EXISTS 'mechsimvault-server'@'%';
  CREATE USER 'mechsimvault-server'@'%' IDENTIFIED BY 'Ohm@42@42@42@';
  GRANT ALL PRIVILEGES ON mech_sim_db.* TO 'mechsimvault-server'@'%';
  FLUSH PRIVILEGES;
  ```
  - Note run this script before deploying django container or else django will try to connect mysql and mysql may not have started
  - To prevent this we can add check in entrypoint of django container
  ```bash
  #!/bin/sh

  # Function to check if MySQL is running
  check_mysql() {
    mysqladmin ping -h mysql -u root -ppassword > /dev/null 2>&1
  }

  # Wait for MySQL to be available
  until check_mysql; do
    echo "Waiting for MySQL..."
    sleep 2
  done

  echo "MySQL is up and running. Starting Django..."

  # Apply database migrations
  python manage.py makemigrations
  python manage.py migrate

  # Start the Django application
  python manage.py runserver 0.0.0.0:8000

  ```

## Best Security practice 
- Use Minimal Base Images: Use small, official, or trusted images (like alpine or debian), and avoid unnecessary software in the image.
- Run Containers as Non-Root Users: Always try to run containers with the least privilege, avoiding running them as the root user unless absolutely necessary.
- Use Docker Content Trust (DCT): Enabling DCT ensures that only signed images can be pulled, reducing the risk of using compromised images.
- Limit Container Capabilities: Use the --cap-drop flag to drop unnecessary Linux capabilities from containers and reduce the potential attack surface.
- Use Read-Only File Systems: Mount the file system as read-only where possible to prevent malicious modification of files within the container.
- Scan Images for Vulnerabilities: Regularly scan Docker images for vulnerabilities using tools like Clair or Anchore to ensure they don't contain known security -flaws.
- Avoid Privileged Containers: Don’t use the --privileged flag unless absolutely necessary, as it grants containers elevated privileges.
- Limit Network Access: Configure Docker networks to isolate containers and only expose necessary services. Use firewalls or other network security tools.
- Update Images Regularly: Ensure your containers use up-to-date images to patch any security vulnerabilities.
