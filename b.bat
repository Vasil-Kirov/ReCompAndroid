@ECHO OFF

PUSHD bin

rcp ..\build.rcp || exit /b 1

REM set Arch1=v7a
REM set Arch2=arm-linux-androideabi
REM set Arch3=armeabi-%Arch1%
REM set Arch4=arm

set Arch1=v8a
set Arch2=arm64-linux-android
set Arch3=arm64-%Arch1%
set Arch4=arm64

set AndroidVersion=30
set AppName=pong
set APK="%CD%\%AppName%.apk"
set SDK=E:\AndroidSDK
set NDK=%SDK%\ndk
set JDK=E:\jdk-21.0.2
set BuildTools=%SDK%\build-tools\%AndroidVersion%.0.3
set Toolchain="%NDK%\toolchains\llvm\prebuilt\windows-x86_64"

set AndroidJar="%SDK%\platforms\android-%AndroidVersion%\android.jar"

set LibFiles=-lm -lGLESv2 -lEGL -landroid -llog array.obj init.obj io.obj math.obj mem.obj os.obj str.obj nostdlib.obj
set Objs=main.obj android.obj pthread.obj compile.obj backtrace.obj


set JAVA_HOME=%JDK%

set AAPT="%BuildTools%\aapt.exe"
set ZIPALIGN="%BuildTools%\zipalign.exe"
set APKSIGNER="%BuildTools%\apksigner.bat"
set NDKBUILD="%NDK%\ndk-build.cmd"
set ADB="%SDK%\platform-tools\adb.exe"
set LibName=main
set TargetTriple=%Arch4%%Arch1%-unknown-linux-androideabi%AndroidVersion%

"%Toolchain%\bin\clang.exe"^
 --target=%TargetTriple%^
 --sysroot="%Toolchain%\sysroot"^
 -shared^
 -uANativeActivity_onCreate^
 -o "%CD%\lib\%Arch3%\lib%LibName%.so"^
 %LibFiles% %Objs%


%AAPT% package --debug-mode -v -f -F %APK% -M "%CD%\AndroidManifest.xml" -A "android-assets" -S "android-resources" -I %AndroidJar% || exit /b 1
%AAPT% add %APK% lib/%Arch3%/lib%LibName%.so || exit /b 1

set TempAPK="%CD%\temp.apk"
%ZIPALIGN% -v -f 4 %APK% %TempAPK% || exit /b 1
del %APK% || exit /b 1
ren %TempAPK% %AppName%.apk || exit /b 1

set KEY="debug.keystore"
call %APKSIGNER% sign --ks %KEY% --ks-key-alias androiddebugkey --ks-pass pass:android --key-pass pass:android -v %APK% || exit /b 1
call %APKSIGNER% verify -v %APK% || exit /b 1


POPD


