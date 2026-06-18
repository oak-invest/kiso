test:
    mvn test

run:
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application

run_help:
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="check --help"

run_check:
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="check"

run_build:
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="build"

build-native:
    # Native app is built as applications/kiso-cli/target/kiso-cli
    mvn -pl applications/kiso-cli -Pnative native:compile
