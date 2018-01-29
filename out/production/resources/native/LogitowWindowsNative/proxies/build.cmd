@echo off
if not exist target mkdir target
if not exist target\classes mkdir target\classes


echo compile classes
javac -nowarn -d target\classes -sourcepath jvm -cp "V:\Projects\logitow\logitow-bridge\src\main\resources\native\LogitowWindowsNative\bin\Windows.winmd,\bin\System.Runtime.dll";"v:\projects\logitow\logitow-bridge\src\main\resources\native\logitowwindowsnative\bin\jni4net\jni4net.j-0.8.8.0.jar"; "jvm\logitowwindowsnative\DeviceEventReceiver.java" "jvm\logitowwindowsnative\DeviceEventReceiver_.java" "jvm\logitowwindowsnative\LogitowDevice.java" "jvm\logitowwindowsnative\Program.java" "jvm\logitowwindowsnative\Scanner.java" 
IF %ERRORLEVEL% NEQ 0 goto end


echo LogitowWindowsNative.j4n.jar 
jar cvf LogitowWindowsNative.j4n.jar  -C target\classes "logitowwindowsnative\DeviceEventReceiver.class"  -C target\classes "logitowwindowsnative\DeviceEventReceiver_.class"  -C target\classes "logitowwindowsnative\__DeviceEventReceiver.class"  -C target\classes "logitowwindowsnative\LogitowDevice.class"  -C target\classes "logitowwindowsnative\Program.class"  -C target\classes "logitowwindowsnative\Scanner.class"  > nul 
IF %ERRORLEVEL% NEQ 0 goto end


echo LogitowWindowsNative.j4n.dll 
csc /nologo /warn:0 /t:library /out:LogitowWindowsNative.j4n.dll /recurse:clr\*.cs  /reference:"V:\Projects\logitow\logitow-bridge\src\main\resources\native\LogitowWindowsNative\proxies\LogitowWindowsNative.dll" /reference:"V:\Projects\logitow\logitow-bridge\src\main\resources\native\LogitowWindowsNative\bin\jni4net\jni4net.n-0.8.8.0.dll" /reference:"V:\Projects\logitow\logitow-bridge\src\main\resources\native\LogitowWindowsNative\bin\Windows.winmd" /reference:"V:\Projects\logitow\logitow-bridge\src\main\resources\native\LogitowWindowsNative\bin\System.Runtime.dll"
IF %ERRORLEVEL% NEQ 0 goto end


:end
