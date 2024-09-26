rm -rf admin

cd ..
yarn
yarn run build


cp -r ./dist/ ./scripts/admin/