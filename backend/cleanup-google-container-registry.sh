#!/usr/bin/env bash

# 1. param is Image name
# 2. param is number of images to keep

if [[ "$#" -ne 2 || "${1}" == '-h' || "${1}" == '--help' ]]; then
  cat >&2 <<"EOF"
cleanup-google-container-registry.sh cleans up tagged or untagged images
for a given repository (an image name without a tag/digest) but keeps the latest ones
USAGE:
  cleanup-google-container-registry.sh REPOSITORY NUMBER_OF_IMAGES_TO_KEEP
EXAMPLE
  cleanup-google-container-registry.sh gcr.io/ahmet/my-app 10
  would clean up everything under the gcr.io/ahmet/my-app repository
  but keeps the 10 latest images.
EOF
  exit 1
fi

re='^[0-9]+$'
if ! [[ $2 =~ $re ]] ; then
   echo "wrong NUMBER_OF_IMAGES_TO_KEEP format; use a numeric value" >&2
   exit 1
fi

IMAGE=$1
NUMBER_OF_IMAGES_TO_KEEP=$2

echo "Cleanup ${IMAGE} images but will keep the latest ${NUMBER_OF_IMAGES_TO_KEEP} images..."

for digest in $(gcloud container images list-tags ${IMAGE} --limit=999999 --sort-by=~TIMESTAMP --format='get(digest)'); do

    if [ $NUMBER_OF_IMAGES_TO_KEEP -gt 0 ]
    then
        echo "keep image ${IMAGE}@${digest}"
    else
      echo "deleting image ${IMAGE}@${digest}"
      gcloud container images delete -q --force-delete-tags "${IMAGE}@${digest}"
    fi

    ((NUMBER_OF_IMAGES_TO_KEEP=NUMBER_OF_IMAGES_TO_KEEP-1))
done

echo "Cleanup of ${IMAGE} images finished!"