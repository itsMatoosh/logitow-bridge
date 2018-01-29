@echo off
copy .\bin\Debug\LogitowWindowsNative.dll .\proxies

.\bin\jni4net\proxygen.exe .\proxies\LogitowWindowsNative.dll -wd .\proxies -cp "./bin/Windows.winmd","./bin/System.Runtime.dll"

copy %cd%\build.cmd %cd%\proxies
cd proxies
call build.cmd
copy %cd%\LogitowWindowsNative.j4n.dll %cd%\..\..\..\..\..\..\libs
copy %cd%\LogitowWindowsNative.j4n.jar %cd%\..\..\..\..\..\..\libs
copy %cd%\LogitowWindowsNative.dll %cd%\..\..\..\..\..\..\libs

copy %cd%\LogitowWindowsNative.j4n.dll %cd%\..\..\
copy %cd%\LogitowWindowsNative.j4n.jar %cd%\..\..\
copy %cd%\LogitowWindowsNative.dll %cd%\..\..\
pause