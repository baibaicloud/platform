echo 'deployment baibai system'

if [ ! -d "/data" ]; then
 cp -r data /data
fi

docker-compose down
docker-compose up -d

echo 'success'
