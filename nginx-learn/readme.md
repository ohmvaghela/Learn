# NGINX
- To add nginx file 
	- Add file to sites-available
	- Like say the file name is django
		```
		server {
				listen 80;
				server_name localhost;

				location /static/ {
						root /home/ohm/static;
				}

				location / {
						proxy_pass http://localhost:8000;
				}
		} 
		```
	- Then create a symbolic link (like shortcut in windows)
		```
		sudo ln -s /etc/nginx/sites-available/<file-name> /etc/nginx/sites-enabled/
		sudo ln -s /etc/nginx/sites-available/django /etc/nginx/sites-enabled/
		```

- Uses 
  - Loadbalance
  - Reverse Proxy
- Nginx sits between internet and our server
- Now if we have encrypted data so we need to add code do decrypting data on each server
	- So nginx take care of encrypting and decrypting
- For most time we will be editing nginx.conf
	- It will be like a key value pair
	- Each key-value pair is called `directive`
	- The blocks of code with `{}` are known as context 
	- In the context we have directives for that context
- `include` directive include all the conf file at the mentioned location
	```
	include /etc/nginx/conf.d/*.conf
	```
- To handle all the request on a port
	```py
	listen 80 default_server;# for IPv4
	listen [::]:80 default_server;# for IPv6
	```
- location directive
	- Root word is used for setting location
	```
	root /location/of/the/file;
	```
	- under this file names can be mentioned which will be rendered for port call 
	```
	root /var/www/html;
	index index.html index.htm index. nginx-debian.html;
	```
- This entire thing will be packed in a block
```
server{

  listen 80 default_server;# for IPv4                                                                                                                                    listen [::]:80 default_server;# for IPv6

  root /var/www/html;
  index index.html index.htm index. nginx-debian.html;

}
```

> ## After making changes in nginx, instead of restarting, reload nginx 
> - so no request is lost in the time of restart
> - But before that we need to validate the file that we added to conf.d
> 	- ``` sudo nginx -t ```
> - Reload : `sudo systemctl reload nginx`
> - Restart : `sudo systemctl restart nginx`

## NGINX structure
- It is in tree hirerical structure
- There is a main block (the entire file), in the main block there are
	- Number of worker_process : these handles requests
	- User : the user which can access the port
	- PID : ProcessID
	- Log Creation
- Then in the main block there are few blocks:
	- Events block
		- Number of connection per worker process 
			- How many connections each worker process will handle 
		- There are other settings as well
	- Stream
		- TCP/UDP settings
		- Layer 3 and 4 settings can be done here
	- HTTP
		- How to process the request
		- There can be multiple server blocks inside http 
		- In these there are two blocks 
			- Server block
				- Virtual server / Virtual host are created in this 
				> ### what are these virtual server and virtual host
				- When we host a website there can be multiple domains and servers so all that setting is done over here	
				- There is another block inside server: `location` block
					- url or routing match or redirecting (imp as used in react)
					- There can be multiple location blocks in the server block
			- Upstream block
				- When nginx is used as reverse proxy and/or load balancer
		- At the end of http block there is 
		```
		include /etc/nginx/conf.d/*.conf;
        	include /etc/nginx/sites-enabled/*;	
		```
		- So the pairs of server, upstream can be we added to this files
		- Nginx has recommeded to add all the config files in `conf.d`
		- Then there is `server_name` which will server as domain name 
			- If we dont have domain then we can leave it empty
		- There is `index` directive
			- This tells which file to search for in the root directory
		- Then there is `location` block
			- all the request after ip:port/api are taken here if `/api` is used
			- it can be left empty as well ```ip:port/``` and ```location / {}```
		```
		location /api {

		}
		``` 
		- multiple location block can be used and regex can also be used
		- ## regex will be used for `django` project
		- `location` 
			- try_files $uri
			- this takes the next part after ip:port/api and maps it to root
			- like say ip:port/api/porduct then it will add product to location of root
			- if root is /home/user/ohm the it will try to find index files in /home/user/ohm/products
			- And if it is not found we can also add 404 error like this 
			- ` try_files $uri $uri/=404`
			- Second url is used if file is not found at root/location
- When creating a new file in conf.d 
	- convention is to use domain name as the file name followed by `.conf`
	- example autob.conf
	```
	server {
    		listen 80;
    		server_name localhost;

    		root /var/www/autob;
    		#root /home/ohm/temp;
    		index index2.html index.html;

    		location / {
        		try_files $uri $uri/ =404;
    		}
	}

	server {
        	listen 8080;
        	server_name localhost;

        	root /var/www/autob;
        	index index.html index2.html;

        	location /{
                	try_files $uri $uri/ =404;
        	}

	}
	```
	- comment unnecessary `include` directives
- Another tested way to add a file to sites-enabled and create a symbolic link
	- say there is file named `autob` in /etc/nginx/sites-enabled
	```
	server {
    	listen 80;
    	server_name localhost;

    	root /var/www/autob;
    	index index2.html index.html;

    	location / {
        	try_files $uri $uri/ =404;
    		}
	}
	```
	- and also index2.html or index.html or anyother file mentioned must exist at `root`
	- Also if the file path is in the root then you may need to add permission like my files are in `temp` folder
	```
	sudo chmod -R 755 /home/ohm/temp/
	sudo chown -R www-data:www-data /home/ohm/temp/
	sudo usermod -a -G ohm www-data
	sudo chmod g+x /home/ohm
	sudo chmod g+x /home/ohm/temp
	```
> ## To see full nginx conf that is used
> ``` sudo nginx -T ```

## Basic authentiaction
- Add `auth_basic` and `auth_basic_user_file` directive in server block
```
server {
	listen 80;
	root /var/www/dev;

	server_name _;
	auth_basic "Under development site";
	auth_basic_user_file /etc/nginx/.htpasswd;

	index index.html index.htm;

	location / {
		try_files $uri $uri/ =404;
	}
}
```	
- The create `.htpasswd` file
	- set username
	```
	sudo sh -c "echo -n '<user_name>:' >> /etc/nginx/.htpasswd"
	sudo sh -c "echo -n 'ohm:' >> /etc/nginx/.htpasswd"
	```
	- set password
	```
	sudo sh -c "openssl passwd apr1 >> /etc/nginx/.htpasswd"
	```
	- When you enter it will ask for password
	- `openssl passwd apr1` : this uses openssl to generate password with algorithm apr1
## NGINX as reverse proxy 
- we need to mention the port to be forwarded to in the upstream with block name 
- Like say the block name is `backend` and is running at 8000 and we want to forward the requests from 8090 to 8000 
```
upstream backend{
        server localhost:8000;
}

server {
        listen 8090;

        server_name _;

        location / {
        proxy_pass http://backend;
        }

}
```
- Here we dont need to mention the location of anyfile or anything
- And the upstream block name will be used in location to pass the request

## Load balancing 
- It uses round robin method for load balancing
- and will distribute request equally
```
upstream backend{
	server localhost:8000;
	server localhost:8001;
}

server {
        listen 8090;

        server_name _;

        location / {
        proxy_pass http://backend;
        }

}
```
- simply adding another server in upstream will do the job.
- But now we have run the app at both the ports
```
PORT=8001 node index.js
PORT=8000 node index.js
```
- hence two instances of server are running 
- we can adjust load by simply mentioning weight 
```
upstream backend{
	server localhost:8000 weight=3;
	server localhost:8001;
}
```
- So now frist 3 request will go to 8000 then one request will go to 8001 and this cycle will go on
- Now we can setup backup server as well like if the main server goes down then backup will handle request
```
upstream backend{
  server localhost:8000;
	server localhost:8001;
	server localhost:8002 backup;
}
```
- So normally 8000 and 8001 will take request if both goes down then request will go to 8002
- To manually stop request from going to server we can use `down` keyword
```
upstream backend{
  server localhost:8000;
  server localhost:8001 down;
  server localhost:8002 backup;
}
```
- now request wont be forwarded to 8001 port

## React redirecting problem (Reload problem)
- say this is my nginx for a static website
```
server{
        listen 4000;
        root /var/www/autobilling_client;

        server_name _;

        index index.html;

        location / {
                try_files $uri $uri/ =404;
        }
}
```
- So just add location of index.html instead of 404
```
server{
        listen 4000;
        root /var/www/autobilling_client;

        server_name _;

        index index.html;

        location / {
		# over here
                try_files $uri $uri/ /index.html;
        }
}
```

