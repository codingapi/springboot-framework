serverHost=server
serverAccount=root
serverPort=22
serverPath=/opt/test/


scp -o ConnectTimeout=30 -P $serverPort  -r * $serverAccount@$serverHost:$serverPath
ssh -p $serverPort $serverAccount@$serverHost "cd $serverPath && sed -i 's/\r//g' *.sh && sh install.sh"
