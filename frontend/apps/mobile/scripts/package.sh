rm -rf mobile

# 切换到 frontend 根目录（frontend/apps/mobile/scripts -> frontend）
cd ../../..

pnpm install
pnpm build:mobile

cp -r ./apps/mobile/dist/ ./apps/mobile/scripts/mobile/
