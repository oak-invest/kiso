# Applications

This directory contains all executable applications built from the Kiso codebase.

- `kiso-cli/` — the Kiso command-line interface (`kiso-cli build`, etc...).
- `kiso-cli-action/` — GitHub Actions for Kiso CLI.
- `kiso-mcp-server/` — the Kiso MCP server.

## Developer setup

```bash
sdk install java 25.3.4+1.r25-graal
sdk install maven 3.9.16
sudo apt install -y librsvg2-bin inkscape
cargo install resvg
```

During native build, if you want to limit the resource usage:

```bash
export MAVEN_OPTS="-XX:ActiveProcessorCount=4 -Xmx6g" 
```
