# Release guide

## Audit code

- If lots of changes have been made, review all files in the project and check for any issues. You can generate a checklist for the code review with the following command: `just release_create_code_review_checklist`. This will create a file called `code_review_checklist.md` in the root of the project. You can then use this file to check for any issues in the code.
- Run IntelliJ IDEA code inspection on the project.

## Test the release (local)

- Delete the `public` folder in the project root.
- Run `just release_build_native` to build a native app.
- Test the check command on Google knowledge base: `just release_run_check`
- Test the build command on Google knowledge base: `just release_run_build`

## Test the release (online)

- Deploy my personal knowledge base - It builds Kiso from the development branch to build the websites.

### Angara knowledge base
- Test content deployed on: https://knowledge.angara.finance
- Test content deployed on https://knowledge.angara.finance/entreprises/scub/05-expertise-tests-et-validation.html.
- Test social preview image deployed on: https://knowledge.angara.finance/entreprises/scub/05-expertise-tests-et-validation.png
- Test llms.txt deployed on: https://knowledge.angara.finance/llms.txt
- Test sitemap.xml deployed on: https://knowledge.angara.finance/sitemap.xml
- Test zip downloads on https://knowledge.angara.finance/entreprises/index.html

### Oak Invest knowledge base
- Test content deployed on: https://www.oak-invest.com/okf/
- Test content deployed on https://www.oak-invest.com/okf/entreprises/scub/05-expertise-tests-et-validation.html
- Test sociale preview image deployed on: https://www.oak-invest.com/okf/entreprises/scub/05-expertise-tests-et-validation.png
- Test llms.txt deployed on: https://www.oak-invest.com/llms.txt
- Test llms.txt deployed on: https://www.oak-invest.com/okf/llms.txt
- Test sitemap.xml deployed on: https://www.oak-invest.com/okf/sitemap.xml

## Pre-release steps

- Update the projet README.md content and release number in it.
- Change the release number in `applications/kiso-cli-action/action.yml`
- Commit the changes and push them to the `development` branch.

## Release steps

- `just start_release`
- `just finish_release`
- Wait for the release to appear here: https://github.com/oak-invest/kiso/releases

## Post-release steps:

- Add a release note.
- Change the release number in `.github/workflows/publish-website.yml`
- Close the milestone at https://github.com/oak-invest/kiso/milestones
- Update the project board at https://github.com/orgs/oak-invest/projects/1/views/1
- Wait to see if the website is correctly built and deployed on https://oak-invest.github.io/kiso/

## Communicate

- Write an English / French announcement.
- Post it on https://github.com/GoogleCloudPlatform/knowledge-catalog/discussions/new?category=show-and-tell
- Post on social media (LinkedIn, Twitter, Facebook, etc.)