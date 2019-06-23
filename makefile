FILE=./Minecraft-ML-0.1a.jar
src :
build : src
	JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/ ./gradlew build --offline; 
update :
	JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/ ./gradlew build; 
test : build
	cp ./build/libs/${FILE} ./run/Client/client_mod-debug/;
	cd ./run/Client/;bash ./launch_game.sh|sed -n '/--MCML Start Init---/,/--MCML End Init---/p'
clean :
	rm ./run/Client/client_mod-debug/${FILE}
