#!/bin/bash

gradlew bootJar
docker build -t notification_relay .