set -x
mvn jmeter:configure
basedir=`pwd`
for file in ${basedir}/target/*/jmeter
do
  cp ${basedir}/target/eth-jmeter-sample-1.0-SNAPSHOT-jar-with-dependencies.jar ${file}/lib/ext
  cp -r ${basedir}/src/test/jmeter/* ${file}/bin
done