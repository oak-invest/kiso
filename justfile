# Maven commands =======================================================================================================
install:
    mvn clean install -DskipTests

test:
    mvn clean test

# Kiso cli =============================================================================================================
run_kiso_cli:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application

run_kiso_cli_check_v_0_1:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="check --source=examples/kb-google-example-v0.1"

run_kiso_cli_build_v_0_1:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="build --source=examples/kb-google-example-v0.1 --destination=public/kb-google-example-v0.1"

run_kiso_cli_check_v_0_2:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="check --source=examples/kb-google-example-v0.2"
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="check --source=examples/kb-acme-example-v0.2"

run_kiso_cli_build_v_0_2:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="build --source=examples/kb-google-example-v0.2 --destination=public/kb-google-example-v0.2"
    mvn compile -pl applications/kiso-cli exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.cli.Application \
      -Dexec.args="build --source=examples/kb-acme-example-v0.2 --destination=public/kb-acme-example-v0.2"

# Kiso MCP Server ======================================================================================================
run_kiso_mcp_server:
    mvn install -pl libraries/kiso-core -am -DskipTests
    mvn compile -pl applications/kiso-mcp-server exec:java \
      -Dexec.mainClass=com.oakinvest.kiso.mcp.server.Application \
      -Dexec.args="--source=examples/kb-acme-example-v0.2"

run_kiso_mcp_server_call_search:
    curl -X POST http://localhost:8080/mcp \
        -H "Content-Type: application/json" \
        -d '{"jsonrpc":"2.0","id":1,"method":"tools/call","params":{"name":"search","arguments":{"text":"discount_amount"}}}' \
        | jq

run_kiso_mcp_server_call_get_concept_content:
    curl -X POST http://localhost:8080/mcp \
        -H "Content-Type: application/json" \
        -d '{"jsonrpc":"2.0","id":2,"method":"tools/call","params":{"name":"get_concept_content","arguments":{"conceptId":"computations/revenue-ytd"}}}' \
        | jq

# Pre release tasks ====================================================================================================
release_create_code_review_checklist:
    find . \
      \( -path "*/target" -o -path "*/build" -o -path "*/generated" \) -prune -o \
      -type f \
      \( -name "*.java" -o -name "*.jte" -o -name "*.kte" \) \
      -print \
      | sort \
      | sed -e 's|^\./||' -e 's/^/- [ ] /' \
      > code-review-checklist.md

release_build_native:
    mvn clean install -pl libraries/kiso-core -am -DskipTests
    # Native app is built as applications/kiso-cli/target/kiso-cli
    mvn clean native:compile -pl applications/kiso-cli -Pnative -DskipTests
    # Native app is built as applications/kiso-mcp-server/target/kiso-mcp-server
    mvn clean native:compile -pl applications/kiso-mcp-server -Pnative -DskipTests

release_run_cli_check:
    @echo
    @echo "=== Checking OKF v0.1 example ==="
    ./applications/kiso-cli/target/kiso-cli check \
      --source=examples/kb-google-example-v0.1
    @echo
    @echo "=== Checking OKF v0.2 Google example ==="
    ./applications/kiso-cli/target/kiso-cli check \
      --source=examples/kb-google-example-v0.2
    @echo
    @echo "=== Checking OKF v0.2 ACME example ==="
    ./applications/kiso-cli/target/kiso-cli check \
      --source=examples/kb-acme-example-v0.2

release_run_cli_build:
    @echo
    @echo "=== Building kb-google-example-v0.1 ==="
    ./applications/kiso-cli/target/kiso-cli build \
      --source=examples/kb-google-example-v0.1 \
      --destination=public/kb-google-example-v0.1
    @echo
    @echo "=== Building kb-google-example-v0.2 ==="
    ./applications/kiso-cli/target/kiso-cli build \
      --source=examples/kb-google-example-v0.2 \
      --destination=public/kb-google-example-v0.2
    @echo
    @echo "=== Building kb-acme-example-v0.2 ==="
    ./applications/kiso-cli/target/kiso-cli build \
      --source=examples/kb-acme-example-v0.2 \
      --destination=public/kb-acme-example-v0.2

release_run_mcp_server:
    ./applications/kiso-mcp-server/target/kiso-mcp-server \
      --source=examples/kb-acme-example-v0.2

# Release tasks ========================================================================================================
start_release:
    git remote set-url origin git@github.com:oak-invest/kiso.git
    git checkout development
    git pull
    git status
    mvn gitflow:release-start

finish_release:
    mvn gitflow:release-finish -DskipTests
