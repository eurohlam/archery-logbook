archery-logbook
=================================

## Overview

This is a spring-boot based REST API that helps to build a back-end for [Archer's Log Book](https://roundkick.nz/) project

## Project

This API is a part of a free project [Archer's Log Book](https://roundkick.nz/).  
Archer's Log Book helps archers to manage their rounds, bows and overall progress.

## How to build

    mvn clean install

## How to run

The API is designed to work with MySQL/MariaDB as a database. In local environment it can be run as docker containers.

- Build the app using `mvn clean install`
- Copy archery-logbook.jar file from `target` into `docker` folder
- Navigate into `docker` folder and run `docker-compose up`. It will build and run two containers: mariadb and archer-logbook
- Open MariaDB admin console in browser: https://localhost:8080 and login with credentials: robin/hood
- Run in admin console the script `src/main/resources/create_tables.sql` to create database structure and testing data
- API endpoint is available as https://localhost:7878 

## Run via Docker

    sudo docker run \
        --name archery-logbook \
        --add-host host.docker.internal:host-gateway \
        --env SERVER_PORT=7878 \
        --env SERVER_SSL_KEYSTORE=classpath:archery-logbook-keystore.jks \
        --env SERVER_SSL_KEYSTORE_PASSWORD=changeit \
        --env SERVER_SSL_KEY_ALIAS=archery-logbook \
        --env SERVER_SSL_KEY_PASSWORD=changeit \
        --env DB_URL=jdbc:mysql://host.docker.internal:3306/archery \
        --env DB_USER=robin \
        --env DB_PWD=hood \
        -p 7878:7878 \
        --detach \
        eurohlam/archery-logbook:1.0.4

## Security

### TLS
TLS is enabled for API-server. It means connection can be established only via HTTPS protocol. By default, the server uses
self-signed certificate that comes with a docker image. It may be sufficient for testing purposes, but it must be overridden for
a production system. It can be achieved by setting env variables 
`SERVER_SSL_KEYSTORE, SERVER_SSL_KEYSTORE_PASSWORD, SERVER_SSL_KEY_ALIAS, SERVER_SSL_KEY_PASSWORD` for a docker container.

### Authentication
The API uses HMAC (Hash-Based Message Authentication Codes) authentication. Client must pass 4 mandatory HTTP headers for every API-call:
- key - unique key of client provided by API-owner. API-owner provides unique combination key/secret for every API-client
- nonce - unique string generated by client for every API-call
- timestamp - timestamp for API-call
- signature - base64 encoded string of HMAC encrypted signature with SHA-256 algorithm. Signature before encryption is made up as
a concatenation of `path + key + nonce + timestamp`


        //Example of HMAC signature for PHP
        // Build the signature string from the parameters.
        $signatureString = $path . $accessKey . $nonce . $timestamp;
        // Create the HMAC SHA-256 Hash from the signature string.
        $signature = base64_encode(hash_hmac('sha256', $signatureString, $secret, true));

## API

API specification is available in OpenAPI format in the file `archery-logbook-api.yaml`
