#!/bin/bash

diffFiles=./screenshotDiffs
mkdir $diffFiles
#cp MPChartExample/build/outputs/connected_android_test_additional_output/debugAndroidTest/connected/emulator\(AVD\)\ -\ 9/* screenshotsToCompare
set -x
./git-diff-image/install.sh
GIT_DIFF_IMAGE_OUTPUT_DIR=$diffFiles git diff-image

source scripts/lib.sh

PR=$(echo "$GITHUB_REF_NAME" | sed "s/\// /" | awk '{print $1}')
echo pr=$PR

OS="`uname`"
case $OS in
  'Linux')
    ;;
  'FreeBSD')
    ;;
  'WindowsNT')
    ;;
  'Darwin')
    brew install jq
    ;;
  'SunOS')
    ;;
  'AIX') ;;
  *) ;;
esac

echo "=> delete all old comments, starting with Screenshot differs:$emulatorApi"
oldComments=$(curl_gh -X GET https://api.github.com/repos/"$GITHUB_REPOSITORY"/issues/"$PR"/comments | jq '.[] | (.id |tostring) + "|" + (.user.login | test("github-actions") | tostring) + "|" + (.body | test("Screenshot differs:'$emulatorApi'.*") | tostring)' | grep "true|true" | tr -d "\"" | cut -f1 -d"|")
echo "comments=$comments"
echo "$oldComments" | while read comment; do
  echo "delete comment=$comment"
  curl_gh -X DELETE https://api.github.com/repos/"$GITHUB_REPOSITORY"/issues/comments/"$comment"
done

pushd $diffFiles
pwd
body=""
COUNTER=0
ls -la

echo "=> ignore an error, when no files where found https://unix.stackexchange.com/a/723909/201876"
setopt no_nomatch
for f in *.png; do
  if [[ ${f} == "*.png" ]]
  then
    echo "nothing found"
  else
    (( COUNTER++ ))

    newName="$1-${f}"
    mv "${f}" "$newName"
    echo "==> Uploaded screenshot $newName"
    curl -i -F "file=@$newName" https://www.mxtracks.info/github
    echo "==> Add screenshot comment $PR"
    body="$body ${f}![screenshot](https://www.mxtracks.info/github/uploads/$newName) <br/><br/>"
  fi
done

if [ ! "$body" == "" ]; then
  curl_gh -X POST https://api.github.com/repos/"$GITHUB_REPOSITORY"/issues/$PR/comments -d "{ \"body\" : \"Screenshot differs:$emulatorApi $COUNTER <br/><br/> $body \" }"
fi

popd 1>/dev/null

# set error when diffs are there
[ "$(ls -A $diffFiles)" ] && exit 1 || exit 0
