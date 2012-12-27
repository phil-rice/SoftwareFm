set m2=c:\Users\phil\.m2\repository

cd ../org.softwarefm.utilities
call mvn clean install
cd ../org.softwarefm.core
call mvn clean install

cd ../org.softwarefm.softwarefm

mkdir lib
call "../org.softwarefm.core/copyUsedFiles"

copy %m2%\org\softwarefm\utilities\0.0.1-SNAPSHOT\utilities-0.0.1-SNAPSHOT.jar lib
copy %m2%\org\softwarefm\eclipse\0.0.1-SNAPSHOT\eclipse-0.0.1-SNAPSHOT.jar lib

cd ../org.softwarefm.core