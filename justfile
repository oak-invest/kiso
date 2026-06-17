test:
    mvn test

run:
    mvn -pl applications/kiso-cli exec:java -Dexec.mainClass=com.oakinvest.kiso.cli.Application

build-native:
    # Native app is built as applications/kiso-cli/target/kiso-cli
    mvn -pl applications/kiso-cli -Pnative native:compile
