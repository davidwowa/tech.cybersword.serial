# tech.cybersword.serial  

serial(UART) fuzzer  

#Maven build  
`~/java_env/maven/bin/mvn archetype:generate -DgroupId=tech.cybersword -DartifactId=tech.cybersword.serial -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false`  
#build  
`~/java_env/maven/bin/mvn clean package`  
#run  
`~/java_env/jdk/Contents/Home/bin/java -jar target/tech.cybersword.serial-1.0-SNAPSHOT.jar /dev/tty.usbserial-0001 9600 8 100 1000 1024 true true true`  
#scp  
`scp /lokaler/pfad/ benutzer@192.168.1.5:/home/benutzer/dokument.txt`  
