# Kiso
Kiso is a publishing engine that turns [Open Knowledge Format](https://github.com/GoogleCloudPlatform/knowledge-catalog/blob/main/okf/SPEC.md) bundles into static websites for humans and AI agents (`llms.txt` and `sitemap.xml` included).

## Kiso cli quick start
_If you need an OKF bundle to test Kiso, you can download our sample OKF bundle from the [Kiso website](https://oak-invest.github.io/kiso/download/kb-google-example.zip). Once downloaded, unzip it._

Download the latest release of Kiso for your operating system from the [releases page](https://github.com/oak-invest/kiso/releases), then run:

```bash
./kiso-cli build --source=examples/kb-google-example --destination=public
```

You will find the generated static website in the `public` directory. You can open the `index.html` file in your browser to view the generated site.

## Kiso cli with GitHub Action
You can use Kiso CLI in GitHub Action to automatically build your OKF bundles into static websites whenever you push changes to your repository. Here's an example workflow configuration:

```yaml
- name: Build with Kiso
  uses: oak-invest/kiso/applications/kiso-cli-action@v0.1.2
  with:
    command: build
    source: examples/kb-google-example
    destination: website/examples/kb-google-example-latest
```

## Configuration

| Variable      | Default  | Description |
|---------------|----------|-------------|
| `source`      | `.`      | Directory containing the Markdown files to read. Defaults to the current directory. |
| `destination` | `public` | Directory where generated files are created. Defaults to the `public` directory. |

