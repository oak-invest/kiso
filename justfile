# Maven commands =======================================================================================================
install:
    mvn clean install -DskipTests

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

run_build_straumat:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="build --source=examples/kb-stephane-traumat --destination=public/kb-stephane-traumat"

# Release ==============================================================================================================
start_release:
    git remote set-url origin git@github.com:oak-invest/kiso.git
    git checkout development
    git pull
    git status
    mvn gitflow:release-start

finish_release:
    mvn gitflow:release-finish -DskipTests

# Release tasks ========================================================================================================
release_create_code_review_checklist:
    find . \
      \( -path "*/target" -o -path "*/build" -o -path "*/generated" \) -prune -o \
      -type f \
      \( -name "*.java" -o -name "*.jte" -o -name "*.kte" \) \
      -print \
      | sort \
      | tee code-review-files.txt \
      | sed 's/^/- [ ] /' \
      > code-review-checklist.md

release_build_native:
    # Native app is built as applications/kiso-cli/target/kiso-cli
    mvn clean install -pl libraries/kiso-core -am -DskipTests
    mvn clean native:compile -pl applications/kiso-cli -Pnative -DskipTests

release_run_check_kb_google:
    ./applications/kiso-cli/target/kiso-cli check \
      --source=examples/kb-google-example

release_run_build_kb_google:
    ./applications/kiso-cli/target/kiso-cli build \
      --source=examples/kb-google-example \
      --destination=public/kb-google-example-native
