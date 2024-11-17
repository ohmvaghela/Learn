# Docker learn 

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
- Server a.k.a Docker engine
- Client-Server communicate using RESTful API
- It is just like a process like other process running on a system
- On linux we can run linux container
- On windows we can run both windows and linux container
- On Mac OS we use a lightweight VM to run linux container

## Docker Images
- Read-only templates containing instructions for creating container
- Think of an image like a blueprint or snapshot of what will be in a container when it runs.
- The image relies on the host operating system (OS) kernel
- Images can be build using `Dockerfile` or pulled from repo
- Docker images are immutable, so you cannot change them once they are created.

## Docker container
- It is an isolated enviroment, like a seprate OS



## Docker commands

|Purpose|Command|
|-|-|
|List all running container| ``` dosker ps ```|
|List all containers (running and dead) | ``` docker ps -a ``` | 
| List all images | ``` docker image ls ```| 
| Run docker image without entering | ``` docker run  <image-name> ``` |
| Stop docker image | ```docker stop <container-name>``` | 
| Interact with running container | ``` docker exec <container-id> <command> ``` |
| Attach current command line with container | ``` docker run -it <image-name> --name <container-name>```  container name is optional|


## Port mapping 
- If I am running an app in a container then the ports are also inside container
- So we need to connect container's internal ports to host OS's port this is done using port mapping 
```
docker run -p <HostPort:containerport> <image-name> 
```

## Passing env variables 
```
docker run -e key1=value1 -e key2=value2 <image-name> 
```

## Building docker image
- Use `-t` tag to name image
```
docker build -t <image-name> .
```


## Removing image
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

## Changing tag name of docker image
```
docker tag <image_id> <new_image_name>:<new_tag>
```


## Layers Caching 
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
## Coping files from another folder 
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
- To start docker-compose
  ```
  docker compose up
  ```
- To stop docker-compose
  ```
  docker compose down
  ```
- Version is obsolete it can be removed
- Name should be same `docker-compose.yml`
- to run `docker compose up` go to the directory having `docker-compose.yml` then run it.
## Docker networking (How docker connect to internet)
```
docker run -it --network=<network-name> <image-name>
```
> here network name can be bridge, host, none, ipvlan, macvlan, other   
### Bridge driver
- By default it is `bridge` driver
- Commonly used when container on a same host need to communicate with each other
- For this docker creates a `docker bridge` under the host machine and then every container using bridge is connected to this `docker bridge`  
- Over here each container has its own ip address and ports required needs to be exposed
### Host driver
- It is directly connected to host's network
- So we dont need to expose port coz the container is using host's port
### none driver
- Container does not have internet access

### Creating custom network
```
docker network create -d <network-name> <custom-network-name>
docker network create -d bridge my-custom-bridge
```
> ### why custom network
> - Say we want to make two different connection of container to communicate between themselves and not to each other
> - This can be done using custom network
> - Eg. 
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
> - So here container10, container11, container12 can communicate with each other and container20, container21, container22 can communicate with each other 
> - And container on custom-network-1 cannot communicate with custom-network-2 

### Inspecting network
- We can check the details of continer on a certain network using
``` 
docker network inspect <network-name>
```
### Remove network 
```
docker network rm <network-name>
```
### docker volumes
- when continers are destroyed then their memory is also wiped out
- To prevent this we can use docker volumes
- We can mount a folder/directory of the machine on the container
- So the data will be stored that folder
- Ways to create docker volume
1. specifically mentioning the storage location on host machine
```
docker run -v <host-folder-location>:<continer-folder-location> <image-name>
docker run -v /home/ohm/temp:/var/lib/mysql/data alpine
```
```yaml
version: '3'
services:
  mongodb:
    image: mongo
    ports:
      - 27017:27017
    volumes:
      /home/ohm/temp:/var/lib/mysql/data
```

2. `Anonymous  volume` : Without mentioning location on host machine (docker will take care of path on host machine)
```
docker run -v <container-folder to store> <image>
docker run -v /var/lib/mysql/data apline
```
```yaml
version: '3'
services:
  mongodb:
    image: mongo
    ports:
      - 27017:27017
    volumes:
      /var/lib/mysql/data
```
3. `Named Volumes` : Improvement of Anonymous volume, it takes care of creating file/folder on host, but it allows us to give name to it
```
docker run -v name:<container-folder to store> <image>
docker run -v alpine_volume:/home/ohm apline
```
```yaml
version: '3'
services:
  mongodb:
    image: mongo
    ports:
      - 27017:27017
    volumes:
      apline_volume:/var/lib/mysql/data
# this needs to be specified at same level as service if using named volume
volumes:
  apline_volume
```

### Docker bind mount
- Mount is more explicit and verbose version of `-v` or `--volume`
- Difference
  - If the folder does not exist volume will create the folder but bind mount wont
  - volume cannot access external data present in the host machine folder but mount can
- Options in mount
  1. Recursive mount
    - Say if I have a dir and it has sub dirs, so we need to mount all the sub dirs in order to access subdirs
    - This option is not available for volumes
    ```yaml
    bind-recursive: enable # (default) submounts are readonly
    bind-recursive: disable # submounts not accessable(not mounted)
    bind-recursive: writable # submounts are read-write
    bind-recursive: readonly # submounts are readonly
    ```
  2. Read-only mount / Read-write mount 
    ```bash
    # using mount
    docker run -d \
      -it \
      --name devtest \
      --mount type=bind,source="$(pwd)"/target,target=/app,readonly \
      nginx:latest
    ```
    ```bash
    # using volume
    docker run -d \
      -it \
      --name devtest \
      -v "$(pwd)"/target:/app:ro \
      nginx:latest
    ```
### Docker tmpfs mount
  - In this file system is outside container 
  - when container stops the data is removed hence no data persistnace
  - Also the data cannot be shared accross containers
> - This folder can be used once the continer is destroyed or with anyother image


#### Custom volumes
> ### ?

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

> ### Container orchestration
> - deployment, scaling, securing, managing and healing container dynamically
> ### Cloud nativa applicaton 
> - an application that was designed to reside in the cloud from the start  
