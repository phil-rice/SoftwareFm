set m2=c:\Users\phil\.m2\repository
cd ../softwarefm
mkdir lib
call "../core/copyUsedFiles"

copy %m2%\org\softwarefm\utilities\0.0.1-SNAPSHOT\utilities-0.0.1-SNAPSHOT.jar lib
copy %m2%\org\softwarefm\core\0.0.1-SNAPSHOT\core-0.0.1-SNAPSHOT.jar lib
cd ../core
