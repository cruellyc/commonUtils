@echo off

set pkgName=cmmy
set appName=云聚社区

rd /s /q %pkgName%

call cordova create %pkgName% com.seed.%pkgName% "%appName%"
copy /y config_%pkgName%\config.xml %pkgName%\config.xml
xcopy /s config_%pkgName%\res %pkgName%\res\

cd %pkgName%
rd /s /q www
md www
xcopy /s \\10.16.2.130\webapps\%pkgName%\html\phone-liyc www\

call cordova platform add android
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-splashscreen
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-device
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-network-information
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-dialogs
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-notification
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-inappbrowser
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-version
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-toast
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-image
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-unionpay
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-input
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-guide
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-alipay
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-qrcode
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-storage
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-http-cache
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-shake-zhong-qiu
call cordova plugin add http://10.16.8.234/repo/cordova-plugin-amap
copy ..\ant.properties platforms\android\ant.properties
copy ..\android.keystore platforms\android\android.keystore
call cordova build android -release
cd ..
copy %pkgName%\platforms\android\ant-build\CordovaApp-release.apk output\smc_%pkgName%.apk

:EOF
