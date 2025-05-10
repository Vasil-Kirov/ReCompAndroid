#!/bin/bash


pushd bin

DIR="$(pwd)"
APP_NAME=pong
APK="$DIR/$APP_NAME.apk"
SDK=/home/vasko/Android/Sdk
ADB="$SDK/platform-tools/adb"
PACKAGE_NAME=com.vasko.pong
$ADB install --fastdeploy $APK
$ADB shell am start -n $PACKAGE_NAME/com.vasko.pong.MainActivity


popd

./log.sh

