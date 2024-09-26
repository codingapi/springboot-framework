#!/usr/bin/env bash

cd ../web/
yarn
yarn run build
rm -rf ../example-restapi/src/main/resources/static
mkdir -p ../example-restapi/src/main/resources/static
cp -r dist/* ../example-restapi/src/main/resources/static/

