@echo off
cd %~dp0

echo ��ʼ��������Ŀ¼
rd d:\deploy /S /Q -a
mkdir d:\deploy
mkdir d:\deploy\webapps
mkdir d:\deploy\msc\1-service-base\lib
mkdir d:\deploy\msc\2-service-b2b\lib
mkdir d:\deploy\msc\4-service-client\lib
mkdir d:\deploy\msc\6-service-count\lib
mkdir d:\deploy\msc\9-service-supervise\lib

echo ��ʼ����war��
copy ..\..\msc-webservice-b2b\target\msc-webservice-b2b.war d:\deploy\webapps\msc-webservice-b2b.war
copy ..\..\msc-web-admin\target\msc-web-admin.war d:\deploy\webapps\msc-web-admin.war
copy ..\..\msc-web-b2b\target\msc-web-b2b.war d:\deploy\webapps\msc-web-b2b.war
copy ..\..\msc-web-supervise\target\msc-web-supervise.war d:\deploy\webapps\msc-web-supervise.war

echo ��ʼ����service\lib
xcopy ..\..\msc-service-base\target\lib d:\deploy\msc\1-service-base\lib /s /e /h
xcopy ..\..\msc-service-b2b\target\lib d:\deploy\msc\2-service-b2b\lib /s /e /h
xcopy ..\..\msc-service-client\target\lib d:\deploy\msc\4-service-client\lib /s /e /h
xcopy ..\..\msc-service-count\target\lib d:\deploy\msc\6-service-count\lib /s /e /h
xcopy ..\..\msc-service-supervise\target\lib d:\deploy\msc\9-service-supervise\lib /s /e /h


echo ��ʼ����service\*.jar
copy ..\..\msc-service-base\target\msc-service-base.jar d:\deploy\msc\1-service-base\
copy ..\..\msc-service-b2b\target\msc-service-b2b.jar d:\deploy\msc\2-service-b2b\
copy ..\..\msc-service-client\target\msc-service-client.jar d:\deploy\msc\4-service-client\
copy ..\..\msc-service-count\target\msc-service-count.jar d:\deploy\msc\6-service-count\
copy ..\..\msc-service-supervise\target\msc-service-supervise.jar d:\deploy\msc\9-service-supervise\

echo. & pause