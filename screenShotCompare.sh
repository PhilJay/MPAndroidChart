diffFiles=./screenshotDiffs
mkdir $diffFiles
#cp MPChartExample/build/outputs/connected_android_test_additional_output/debugAndroidTest/connected/emulator\(AVD\)\ -\ 9/* screenshotsToCompare
set -x
./git-diff-image/install.sh
GIT_DIFF_IMAGE_OUTPUT_DIR=$diffFiles git diff-image

# set error when diffs are there
[ "$(ls -A $diffFiles)" ] && exit 1 || exit 0
