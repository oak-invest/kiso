# Maven commands =======================================================================================================
install:
    mvn install

test:
    mvn clean test

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
    mvn clean install -pl libraries/kiso-core -am -DskipTests
    mvn clean native:compile -pl applications/kiso-cli -Pnative -DskipTests

run_build_native:
    ./applications/kiso-cli/target/kiso-cli build --source=examples/kb-google-example --destination=public-native

# Release ==============================================================================================================
# ! Change the release number in applications/kiso-cli-action/action.yml

start_release:
    git remote set-url origin git@github.com:oak-invest/kiso.git
    git checkout development
    git pull
    git status
    mvn gitflow:release-start

finish_release:
    mvn gitflow:release-finish -DskipTests

# Website ==============================================================================================================
generate_website_demo:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="build --source=examples/kb-google-example --destination=website/examples/kb-google-example/"
