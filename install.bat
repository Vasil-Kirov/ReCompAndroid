@ECHO OFF

pushd bin
set AppName=pong
set APK="%CD%\%AppName%.apk"
set SDK=E:\AndroidSDK
set ADB="%SDK%/platform-tools/adb.exe"
set PackageName=com.vasko.pong
%ADB% install --fastdeploy %APK% || exit /b 1 && echo ERROR: PHONE NOT CONNECTED!
%ADB% shell am start -n %PackageName%/com.vasko.pong.MainActivity || exit /b 1
popd

log.bat

