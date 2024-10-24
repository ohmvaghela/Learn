# FROM ubuntu:20.04
FROM node:alpine3.10

WORKDIR /usr/src/app

COPY index.js package*.json ./

RUN npm install

EXPOSE 8000

CMD ["node", "index.js"]
