# Check feature

**Validate Markdown files and report formatting or structural errors.**

## TODO
- Create a KnowledgeBundleScanner that returns a KnowledgeBundle.
- Validate data and set errors in KnowledgeBundle sub objects.
- Display all errors in a console report.

## Check list
- Every file must be `UTF-8` encoded.
- Every file must have a valid YAML front matter.
- Every file must have the `type` fields in the front matter.
- If present, ISO 8601 datetime format for the `timestamp` field in the front matter.
- Index files contain no frontmatter.
- In logs files, date headings MUST use ISO 8601 YYYY-MM-DD form.