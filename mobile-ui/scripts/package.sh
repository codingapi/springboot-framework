rm -rf mobile

cd ..
yarn
yarn run build


cp -r ./dist/ ./scripts/mobile/