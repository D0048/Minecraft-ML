FILE=./Minecraft-ML-0.1a.jar

src :
build : src
	JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/ ./gradlew build --offline;

update :
	JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/ ./gradlew build;

test :
	cp ./build/libs/${FILE} ./run/Client/client_mod-debug/;
	#cd ./run/Client/; bash ./launch_game.sh | tee >(sed -n '/--MCML Start Init---/,/--MCML End Init---/p') >(sed -e '1,/--MCML End Init---/d')
	cd ./run/Client/; stdbuf -i0 -o0 -e0 zsh -c "stdbuf -i0 -o0 -e0 bash ./launch_game.sh | tee >(sed -n '/--MCML Start Init---/,/--MCML End Init---/p'|stdbuf -i0 -o0 -e0 grep -v 'is not currently supported, skipping') >(sed -e '1,/--MCML End Init---/d') >/dev/null"
	#cd ./run/Client/; bash ./launch_game.sh | lnav
clean :
	rm ./run/Client/client_mod-debug/${FILE}
