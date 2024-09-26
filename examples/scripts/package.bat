<# :
@powershell "icm ([scriptblock]::Create((gc '%~f0' -Raw -Encoding UTF8)))"
exit
#>

cd ..\web\
yarn
yarn run build
rm ..\example-restapi\src\main\resources\static -Force -Recurse
mkdir ..\example-restapi\src\main\resources\static
cp dist\* ..\example-restapi\src\main\resources\static\
