#!/bin/bash
set -ex

echo "Allow current user to access docker socket"
sudo setfacl -m user:$USER:rw /var/run/docker.sock
