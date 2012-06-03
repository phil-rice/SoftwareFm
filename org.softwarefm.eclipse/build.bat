set m2=c:\users\phil\.m2\repository

cd ../org.softwarefm.utilities
call mvn clean install
cd ../org.softwarefm.eclipse
call mvn clean install
cd ../org.softwarefm.softwarefm
copy %m2%\org\apache\httpcomponents\httpclient\4.1.1\httpclient-4.1.1.jar lib  
copy %m2%\org\apache\httpcomponents\httpcore\4.1\httpcore-4.1.jar lib  
copy %m2%\jdom\jdom\1.0\jdom-1.0.jar lib
copy %m2%\org\softwarefm\utilities\0.0.1-SNAPSHOT\utilities-0.0.1-SNAPSHOT.jar lib
copy %m2%\org\softwarefm\eclipse\0.0.1-SNAPSHOT\eclipse-0.0.1-SNAPSHOT.jar lib

