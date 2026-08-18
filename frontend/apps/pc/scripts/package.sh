rm -rf admin

# 切换到 frontend 根目录（frontend/apps/pc/scripts -> frontend）
cd ../../..

pnpm install
pnpm build:pc

cp -r ./apps/pc/dist/ ./apps/pc/scripts/admin/
