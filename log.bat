@echo off

set SDK=E:\AndroidSDK
set ADB="%SDK%/platform-tools/adb.exe"
DEL a.txt
%ADB% logcat -c

%ADB% logcat -s vpong
REM %ADB% logcat -v raw %* > a.txt 2>&1

exit

REM %ADB% logcat vpong:V *:S
