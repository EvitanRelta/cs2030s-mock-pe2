clear;
echo -e "\n\n===============================================================\n\n"
javac -Xlint:unchecked,rawtypes cs2030s/fp/*.java

#javac Test1.java; javac Test2.java; javac Test3.java; javac Test4.java

java Test1 | grep failed
java Test2 | grep failed
java Test3 | grep failed
java Test4 | grep failed

java -jar ~cs2030s/bin/checkstyle.jar -c ~cs2030s/bin/cs2030_checks.xml cs2030s/fp/Try.java
