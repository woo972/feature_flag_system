# DeploymentGuide.md

## 1. Deployment Environments
| Environment | Purpose | URL / Cluster |
|--------------|----------|----------------|
| staging | pre-release testing | internal |
| production | live system | public |

## 2. CI/CD
- GitHub Actions or ArgoCD for deployment.
- All build artifacts go to `/build`.

## 3. Rollback Strategy
- Keep previous 2 versions.
- Use blue-green or canary deployments.

## 4. Secrets Management
- Use AWS Secrets Manager or GCP Secret Manager.
- Never commit secrets in repo.

## 5. Monitoring
- Use New Relic / Prometheus for metrics.