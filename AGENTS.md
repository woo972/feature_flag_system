# AgentInstruction.md

## 1. Purpose
Defines overall operating rules and behavior guidelines for Codex.

## 2. Core Principles
- Fast feedback loop
- No blocking operations
- Log everything
- Deterministic and reproducible behavior

## 3. Reference Documents
- [ProgrammingGuide.md](agent/ProgrammingGuide.md)
- [PermissionPolicy.md](agent/PermissionPolicy.md)
- [ToolsAndIntegrations.md](agent/ToolsAndIntegrations.md)
- [TechStack.md](agent/TechStack.md)
- [SystemArchitecture.md](agent/SystemArchitecture.md)
- [ProjectStructure.md](agent/ProjectStructure.md)
- [EnvironmentSetup.md](agent/EnvironmentSetup.md)
- [DeploymentGuide.md](agent/DeploymentGuide.md)
- [ChangeLog.md](agent/ChangeLog.md)

## 4. Update Policy
- Whenever any referenced file changes, this document must be reviewed for alignment.
- Do not add comment to the code if there is no explicit request.
- Do commit any changes with summarised title and contents. Do not push to repository.
- Update Done section of ChangeLog.md when any change is made. Format: <short-commit-hash>: <summary> 
