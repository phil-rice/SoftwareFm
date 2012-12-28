set m2=c:\Users\phil\.m2\repository

cd ../parentPom
call mvn -Dmaven.test.skip=true  install

cd ../build
call mvn -Dmaven.test.skip=true install

cd ../eclipse

mkdir lib
call "../build/copyUsedFiles"

cd ../build