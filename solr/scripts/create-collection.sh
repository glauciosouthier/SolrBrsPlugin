#!/bin/bash
#
#set -euo pipefail

SCRIPT="$0"

if [[ "${VERBOSE:-}" == "yes" ]]; then
    set -x
fi

PROT="http";
SOLR_SERVER="localhost";
SOLR_PORT=8983;
SOLR_COLLECTION="juris";
SOLR_PATH="/mycores/$SOLR_COLLECTION";
SOLR_SHARDS=3;
SOLR_REPLICAS=1;

max_attempts=10;
wait_seconds=5;

while (( $# > 0 )); do
  case "$1" in
   -host)
      SOLR_SERVER="${2:-localhost}";
      shift 2;
     ;;

   -port)
      SOLR_PORT="${2:-8983}";
      shift 2;
     ;;

   -collection)
      SOLR_COLLECTION="${2:-juris}";
      SOLR_PATH="/mycores/$SOLR_COLLECTION";
      shift 2;
     ;;

   -shards)
      SOLR_SHARDS="${2:-3}";
      shift 2;
     ;;

   -replicas)
      SOLR_REPLICAS="${2:-1}";
      shift 2;
     ;;

   -path)
      PATH="${2}";
      shift 2;
     ;;

   -max-attempts)
     max_attempts="$2";
     shift 2;
     ;;

   -wait-seconds)
     wait_seconds="$2";
     shift 2;
     ;;

  esac
done



if [ "$SOLR_PORT" = "443" ]; then
   PROT="https";
fi

solr_url="$PROT://$SOLR_SERVER:$SOLR_PORT";

echo "solr-url: ${solr_url}";

((attempts_left=max_attempts))
while (( attempts_left > 0 )); do
  if wget -q -O - "$solr_url" | grep -i solr >/dev/null; then
    break
  fi
  (( attempts_left-- ))
  if (( attempts_left == 0 )); then
    echo "Solr is still not running; giving up"
    exit 1
  fi
  if (( attempts_left == 1 )); then
    attempts=attempt
  else
    attempts=attempts
  fi
  echo "Solr is not running yet on $solr_url. $attempts_left $attempts left"
  sleep "$wait_seconds"
done
echo "Solr is running on $solr_url"

ENDPOINT="${solr_url}/solr/admin/collections?action=list&wt=json"

if ! wget -q -O - ${ENDPOINT} | grep -i ${SOLR_COLLECTION} >/dev/null; then
   solr create_collection -c ${SOLR_COLLECTION} -d ${SOLR_PATH} -shards ${SOLR_SHARDS} -replicationFactor ${SOLR_REPLICAS} -p ${SOLR_PORT};
   exit
else
  echo "Collection ${COLLECTION} alredy exists" 
  exit
fi