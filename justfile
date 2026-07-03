# Maven commands =======================================================================================================
install:
    mvn clean install

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
      -Dexec.args="check --source=examples/kb-google-example"

run_build:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="build --source=examples/kb-google-example --destination=public/kb-google-example"

# Native commands ======================================================================================================
build_native:
    # Native app is built as applications/kiso-cli/target/kiso-cli
    mvn clean install -pl libraries/kiso-core -am -DskipTests
    mvn clean native:compile -pl applications/kiso-cli -Pnative -DskipTests

run_check_native:
    ./applications/kiso-cli/target/kiso-cli check --source=examples/kb-google-example

run_build_native:
    ./applications/kiso-cli/target/kiso-cli build --source=examples/kb-google-example --destination=public/kb-google-example-native

# Release ==============================================================================================================
# Change the release number in applications/kiso-cli-action/action.yml
# Change the release number in applications/kiso-cli/src/main/java/com/oakinvest/kiso/cli/Application.java
start_release:
    git remote set-url origin git@github.com:oak-invest/kiso.git
    git checkout development
    git pull
    git status
    mvn gitflow:release-start

finish_release:
    mvn gitflow:release-finish -DskipTests

# Website utils ========================================================================================================
generate_website_demo:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="build --source=examples/kb-google-example --destination=website/examples/kb-google-example/"

# Stéphane Traumat's stuffs ============================================================================================
straumat_run_check:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="check --source=examples/kb-stephane-traumat"

straumat_run_build:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="build --source=examples/kb-stephane-traumat --destination=public/kb-stephane-traumat"
