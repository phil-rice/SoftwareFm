set m2=c:\Users\phil\.m2\repository

cd ../../parentPom
call mvn -Dmaven.test.skip=true  install

cd ../softwareFmUtilities
call mvn -Dmaven.test.skip=true  install

cd ../softwarefm/build
call mvn -Dmaven.test.skip=true install


cd ../eclipse

mkdir lib
call "../build/copyUsedFiles"

cd ../build