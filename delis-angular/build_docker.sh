#!/bin/bash

ng build --prod --configuration=dev --base-href=/delis-gui-static/

cd docker

rm -R -f ./dist
cp -R ../dist ./dist

docker image build -t delis-gui-static:0.0.3 .

rm -R -f ./dist

cd ..
