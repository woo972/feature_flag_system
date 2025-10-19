# ToolsAndIntegrations.md

## 1. CLI Tools
| Tool | Purpose | Notes |
|------|----------|-------|
| `git` | Version control | Standard |
| `make` | Task runner | Required |
| `docker` | Container runtime | Optional |
| `kubectl` | Cluster operations | Authorized users only |

## 2. MCP Servers (Optional)
- `playwright-mcp` – For automated browser testing.

## 3. External APIs
- None by default. Add with explicit approval.

## 4. Logging & Monitoring
- Logs stored under `.logs/`.
- Use standard stdout for Codex interaction.