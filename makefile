FILE=./Minecraft-ML-0.1a.jar
PS1=$'\e[0;31m$ \e[0m'


src :
build : src
	JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/ ./gradlew build --offline;

update :
	JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64/ ./gradlew build;

test :
	cp ./build/libs/${FILE} ./run/Client/client_mod-debug/;
	#cd ./run/Client/; bash ./launch_game.sh | tee >(sed -n '/--MCML Start Init---/,/--MCML End Init---/p') >(sed -e '1,/--MCML End Init---/d')
	echo -ne "`eval echo ${GREEN}`"
	echo abc
	echo -ne "${NORMAL}"
	cd ./run/Client/; zsh -c "bash ./launch_game.sh | tee >(sed -n '/--MCML Start Init---/,/--MCML End Init---/p') >(sed -e '1,/--MCML End Init---/d') >/dev/null"

clean :
	rm ./run/Client/client_mod-debug/${FILE}
