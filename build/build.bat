set m2=c:\Users\phil\.m2\repository

cd ../parentPom
call mvn clean install

cd ../build
call mvn clean install

cd ../eclipse

mkdir lib
call "../build/copyUsedFiles"

cd ../build