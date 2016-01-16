
my_home := $$HOME/workspace_java
package_home := $(my_home)/fileparser

junit.jar:
mockito.jar:


Parser.class:	Parser.java junit.jar
	javac -classpath "$(package_home)/junit.jar:$(package_home)" Parser.java

Runner.class:	Runner.java junit.jar
	javac -classpath "$(package_home)/junit.jar:$(package_home)" Runner.java


tests:	Parser.class
	java -classpath "$(package_home)/junit.jar:$(package_home)/mockito.jar:$(package_home)" junit.textui.TestRunner fileParser.ParserTest


clean:	
	rm -rf *.class
