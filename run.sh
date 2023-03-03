#!/bin/bash

gradlew bootJar
docker build -t hermes .
