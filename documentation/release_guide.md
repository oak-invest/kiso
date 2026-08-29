# Release guide

## Audit code

- Run IntelliJ IDEA code inspection on the project.

## Test kiso-cli (local)

- Delete the `public` folder in the project root.
- Run `just release_build_native` to build a native app (On slower PC: `export MAVEN_OPTS="-XX:ActiveProcessorCount=4 -Xmx6g"`).
- Test the check command on examples knowledge base: `just release_run_cli_check`.
- Test the build command on examples knowledge base: `just release_run_cli_build`.
- Open `public/kb-google-example-v0.1` in the browser and check the content.
- Open `public/kb-google-example-v0.2` in the browser and check the content.
- Open `public/kb-acme-example-v0.2` in the browser and check the content.

## Test kiso-cli (online)

- Deploy my personal knowledge base - It builds Kiso from the development branch to build the websites.

### Angara knowledge base

- Test content deployed on: https://knowledge.angara.finance
- Test content deployed on https://knowledge.angara.finance/entreprises/scub/05-expertise-tests-et-validation.html
- Test social preview image deployed on: https://knowledge.angara.finance/entreprises/scub/05-expertise-tests-et-validation.png
- Test tags deployed on: https://knowledge.angara.finance/tags/agriculture.html
- Test llms.txt deployed on: https://knowledge.angara.finance/llms.txt
- Test sitemap.xml deployed on: https://knowledge.angara.finance/sitemap.xml
- Test zip downloads on https://knowledge.angara.finance/entreprises/index.html

### Oak Invest knowledge base

- Test content deployed on: https://www.oak-invest.com/okf/
- Test content deployed on https://www.oak-invest.com/okf/entreprises/scub/05-expertise-tests-et-validation.html
- Test social preview image deployed on: https://www.oak-invest.com/okf/entreprises/scub/05-expertise-tests-et-validation.png
- Test llms.txt deployed on: https://www.oak-invest.com/llms.txt
- Test llms.txt deployed on: https://www.oak-invest.com/okf/llms.txt
- Test sitemap.xml deployed on: https://www.oak-invest.com/okf/sitemap.xml

## Test kiso-mcp-server (local)

- Run `just release_run_mcp_server` to start the server.
- Run `just run_kiso_mcp_server_call_search` to test the search API.
- Run `just run_kiso_mcp_server_call_get_concept_content` to test the get concept content API.
- Launch ChatGPT application.
- In Parameters/Settings/Plugins/MCP, add a new distant MCP server with the following URL: `http://localhost:8080/mcp` and `Kiso mcp server` as name.
- Open a new chat and ask: `I added "Kiso mcp server" to the plugins, is it working?` and check the answer.
- Ask `Using "Kiso mcp server", can you give me the SQL query to calculate revenue` and check the answer.

## Pre-release steps

- Update the project README.md content and change the release number here: `uses: oak-invest/kiso/applications/kiso-cli-action@`.
- Update the release number in `applications/kiso-cli-action/action.yml`
- Commit the changes and push them to the `development` branch.

## Release steps

- `just start_release`
- `just finish_release`
- Wait for the release to appear here: https://github.com/oak-invest/kiso/releases

## Post-release steps

- Add a release note here:  https://github.com/oak-invest/kiso/releases.
- Change the release number in `.github/workflows/publish-website.yml`
- Close the milestone at https://github.com/oak-invest/kiso/milestones
- Update the project board at https://github.com/orgs/oak-invest/projects/1/views/1
- Update the release number in `website/index.html`.
- Wait to see if the website is correctly built and deployed on https://oak-invest.github.io/kiso/

## Communicate

- Write an English / French announcement.
- Close previous announcements on https://github.com/GoogleCloudPlatform/knowledge-catalog/discussions?discussions_q=is%3Aopen+kiso
- Post it on https://github.com/GoogleCloudPlatform/knowledge-catalog/discussions/new?category=show-and-tell
- Post on social media:
    - LinkedIn at 14h00 – 15h00.
    - Twitter at 12h15 – 13h15 or 18h00 – 19h00.
    - Facebook at 19h00 – 20h30.

## Full audit

If lots of changes have been made, review all files in the project and check for any issues. You can generate a checklist for the code review with the following command: `just release_create_code_review_checklist`. This will create a file called `code_review_checklist.md` in the root of the project. You can then use this file to check for any issues in the code.