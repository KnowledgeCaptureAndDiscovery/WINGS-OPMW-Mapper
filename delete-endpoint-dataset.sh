
# Check if the environment variable ${ENDPOINT_PASSWORD} is set
if [ -z "${ENDPOINT_PASSWORD}" ]; then
  echo "ENDPOINT_PASSWORD is not set"
  exit 1
fi

curl -v -L \
'https://endpoint.mint.isi.edu/$/datasets' \
-X POST \
-u admin:${ENDPOINT_PASSWORD} \
--data-raw "dbName=test3&dbType=mem"

curl 'https://endpoint.mint.isi.edu/$/datasets/test3' \
  -u admin:${ENDPOINT_PASSWORD} \
  -u admin:CHANGEME \
  -X 'DELETE' \
  -H 'authority: endpoint.mint.isi.edu' \
  -H 'origin: https://endpoint.mint.isi.edu'