archery-logbook
=================================

## Overview

This is a springboot based REST API that helps to build a back-end for [Archer's Log Book](https://roundkick.nz/) project

## Project

This API is a part of a free project [Archer's Log Book](https://roundkick.nz/).  
Archery Logbook helps archers to manage their scores, bows and overall progress.

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
