# Build feature

**Generates a static website from .md files, outputting HTML files alongside the original Markdown files.**

## Next steps
- Copy the directory structure of the source directory to the output directory (Using source and output parameters).
- Load the knowledge bundle copied in the output directory.
- Going throw all bundles recursively and generate HTML files for all .md files existing.
- == GraalVM test ==
- Using template files to generate HTML files for all .md files existing.
    - Add menu.
    - Add links to parent directory.
    - Add links to subdirectories.
    - Add links to sibling files.

## Roadmap
- Add specific parameters to customize the generated HTML files (e.g., title, description, keywords, etc.).
- Add support for specific themes and templates for the generated HTML files.