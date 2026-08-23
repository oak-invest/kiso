# Kiso

Kiso provides two applications built around [Open Knowledge Format](https://github.com/GoogleCloudPlatform/knowledge-catalog/blob/main/okf/SPEC.md) (OKF) bundles:

- **`kiso-cli`** validates an OKF bundle and publishes it as a static website containing generated HTML pages and the original Markdown files. It can be run from the command line or through GitHub Actions.
- **`kiso-mcp-server`** starts a Model Context Protocol (MCP) server that lets an MCP-compatible AI application access the knowledge contained in an OKF bundle.

The OKF bundle remains the single source of truth for both applications.

## Kiso CLI

### Quick start

_If you need an OKF bundle to test Kiso, you can download our sample OKF bundle from the [Kiso website](https://oak-invest.github.io/kiso/download/kb-google-example.zip). Once downloaded, unzip it._

Download the latest release of Kiso for your operating system from the [releases page](https://github.com/oak-invest/kiso/releases), then run:

```bash
./kiso-cli check --source=examples/kb-google-example
```

The `check` command validates the Markdown files in the OKF bundle and reports formatting or structural errors (it also reports broken links as warnings). Once the bundle is valid, build the static website:

```bash
./kiso-cli build --source=examples/kb-google-example --destination=public
```

You will find the generated static website in the `public` directory. You can open the `index.html` file in your browser to view the generated site.

If you use the native Kiso binary and want Kiso to generate social network preview images, install one of the supported native SVG to PNG conversion tools: `rsvg-convert`, `inkscape`, or `resvg`.

### GitHub Actions

You can use Kiso CLI in GitHub Action to automatically build your OKF bundles into static websites whenever you push changes to your repository. Here's an example workflow configuration:

```yaml
- name: Build with Kiso
  uses: oak-invest/kiso/applications/kiso-cli-action@v0.2.1
  with:
    command: build
    source: examples/kb-google-example
    destination: website/examples/kb-google-example-latest
```

### Commands

| Command | Description                                                                                                   |
|---------|---------------------------------------------------------------------------------------------------------------|
| `check` | Validates the Markdown files in an OKF bundle and reports formatting or structural errors.                    |
| `build` | Generates a static website, including the original Markdown files, HTML pages, `llms.txt`, and `sitemap.xml`. |

### Options

| Variable      | Command          | Default  | Description                                                                         |
|---------------|------------------|----------|-------------------------------------------------------------------------------------|
| `source`      | `check`, `build` | `.`      | Directory containing the Markdown files to read. Defaults to the current directory. |
| `destination` | `build`          | `public` | Directory where generated files are created. Defaults to the `public` directory.    |
| `profile`     | `build`          |          | Publishing profile loaded from `.kiso/<profile>/configuration.yaml`.                |

### Configuration file

The `build` command can be configured with an optional `.kiso/configuration.yaml` file located at the root of the source bundle. When the file or one of its sections is absent, Kiso uses safe default values.

```yaml
site:
  baseUrl: https://knowledge.example.com/
  name: Example Knowledge Base
  language: en
  title: My knowledge base
  description: Documentation for humans and AI agents

theme:
  name: corporate

content:
  ignorePatterns:
    - README.md
    - drafts/**
    - private/**
```

| Property                 | Default          | Description                                                                                     |
|--------------------------|------------------|-------------------------------------------------------------------------------------------------|
| `site.baseUrl`           |                  | Public base URL prepended to generated site links and sitemap entries.                          |
| `site.name`              |                  | Name of the site, used in the social share.                                                     |
| `site.language`          | `en`             | Language used by the generated HTML pages.                                                      |
| `site.title`             | Page title       | Title used for the root index page.                                                             |
| `site.description`       | Page description | Description used for the root index page.                                                       |
| `theme.name`             | `light`          | [DaisyUI theme](https://daisyui.com/docs/themes/#list-of-themes) used by the generated website. |
| `content.ignorePatterns` | `[]`             | Glob patterns identifying files and directories that must not be copied or published.           |

Ignore patterns are evaluated against paths relative to the bundle root. For example, `README.md` excludes only the root README, while `drafts/**` excludes the contents of the `drafts` directory. The source bundle is never modified: Kiso applies these exclusions while copying files to the destination directory, before loading, validating, and publishing the resulting bundle.

#### Publishing profiles

Publishing profiles allow the same OKF bundle to be published in different ways without changing its source content. Store profiles in `.kiso/<profile-name>/configuration.yaml` and select one with `--profile`:

```shell
kiso-cli build
kiso-cli build --profile public
kiso-cli build --profile internal
```

When no profile is selected, Kiso loads `.kiso/configuration.yaml`. When a profile is selected, Kiso loads only that profile's configuration; the default configuration is not read or merged.

## Kiso MCP Server

`kiso-mcp-server` exposes the knowledge contained in an OKF bundle to applications compatible with the [Model Context Protocol](https://modelcontextprotocol.io/). It does not generate a website and does not modify the source bundle.

Download the MCP server for your operating system from the [releases page](https://github.com/oak-invest/kiso/releases), then start it with:

```bash
./kiso-mcp-server --source=examples/kb-acme-example-v0.2
```

The server loads the bundle and listens on port `8080` by default. Connect the MCP-compatible AI application to:

```text
http://localhost:8080/mcp
```

The AI application can then search the bundle, retrieve concept content, and use this knowledge as context when answering questions.

### MCP tools

| Tool                  | Description                                                        |
|-----------------------|--------------------------------------------------------------------|
| `search`              | Searches for concepts in the OKF bundle using a text query.         |
| `get_concept_content` | Returns the Markdown content of a concept from its concept ID.      |

### Options

| Option            | Default | Description                                                    |
|-------------------|---------|----------------------------------------------------------------|
| `-s`, `--source`  | `.`     | Directory containing the OKF bundle to serve.                   |
| `-p`, `--port`    | `8080`  | Port used by the MCP server.                                    |

For example, use another port with:

```bash
./kiso-mcp-server --source=examples/kb-acme-example-v0.2 --port=8081
```
