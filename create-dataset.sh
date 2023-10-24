
# Check if the environment variable ${ENDPOINT_PASSWORD} is set
if [ -z "${ENDPOINT_PASSWORD}" ]; then
  echo "ENDPOINT_PASSWORD is not set"
  exit 1
fi

DATASET=$1

if [ -z "${DATASET}" ]; then
  echo "DATASET is not set"
  exit 1
fi


curl "https://endpoint.mint.isi.edu/$/datasets/${DATASET}" \
  -u admin:${ENDPOINT_PASSWORD} \
  -u admin:CHANGEME \
  -X 'DELETE' \
  -H 'authority: endpoint.mint.isi.edu' \
  -H 'origin: https://endpoint.mint.isi.edu'

curl -v -L \
'https://endpoint.mint.isi.edu/$/datasets' \
-X POST \
-u admin:${ENDPOINT_PASSWORD} \
--data-raw "dbName=${DATASET}&dbType=mem"