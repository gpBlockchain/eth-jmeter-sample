# eth-jmeter-sample
Stress test the rpc interface of the eth chain by implementing `java request`

## require
java8,maven,linux or max(not support win)

## use
1. mod rpcUrl
  https://github.com/gpBlockchain/eth-jmeter-sample/blob/main/pom.xml#L14
  
2. mod stress jmx
  mvn package
  mvn jmeter:gui
  load jmx file
  src/test/jmeter/BDJOBS.jmx
4. stress
```
mvn jmeter:jmeter
```

