#!/bin/bash

docker run -p 4200:80 -e "API_URL=http://localhost:8081" delis-gui-static:0.0.3