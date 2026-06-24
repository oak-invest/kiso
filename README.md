# Kiso
Kiso is a publishing engine that turns [Open Knowledge Format](https://github.com/GoogleCloudPlatform/knowledge-catalog/blob/main/okf/SPEC.md) bundles into static websites for humans and AI agents.

## Quick start
_If you need an OKF bundle to test Kiso, you can download our sample OKF bundle from the [Kiso website](https://oak-invest.github.io/kiso/download/kb-google-example.zip). Once downloaded, unzip it._

Download the latest release of Kiso for your operating system from the [releases page](https://github.com/oak-invest/kiso/releases), then run:

```bash
./kiso-cli build --source=examples/kb-google-example --destination=public
```

You will find the generated static website in the `public` directory. You can open the `index.html` file in your browser to view the generated site.

## Configuration

| Variable      | Default  | Description |
|---------------|----------|-------------|
| `source`      | `.`      | Directory containing the Markdown files to read. Defaults to the current directory. |
| `destination` | `public` | Directory where generated files are created. Defaults to the `public` directory. |

## Project structure

- `applications`: This directory contains all applications.
- `documentation`: This directory contains all documentation for the Kiso codebase, including guides, tutorials, and reference materials.
- `examples`: This directory contains example projects and code snippets demonstrating how to use Kiso.
- `libraries`: This directory contains reusable internal libraries shared across Kiso applications.
- `website`: This directory contains the source code for the Kiso website.
