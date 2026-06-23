# Maven commands =======================================================================================================
install:
    mvn install

test:
    mvn test

# Application run commandes ============================================================================================
run:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application

run_check:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="check"

run_build:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="build --source=examples/kb-google-example"

# Native build commands ================================================================================================

build_native:
    # Native app is built as applications/kiso-cli/target/kiso-cli
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn -pl applications/kiso-cli -Pnative package native:compile

run_build_native:
    ./applications/kiso-cli/target/kiso-cli build --source=examples/kb-google-example --destination=public-native

# Website ==============================================================================================================

generate_website_demo:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="build --source=examples/kb-google-example --destination=website/examples/kb-google-example/"
