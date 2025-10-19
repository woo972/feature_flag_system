# ProgrammingGuide.md

## 1. Code Style
- Follow official language style guides (e.g., `gofmt`, `black`, `ktlint`).
- Enforce consistent naming, indentation, and imports.

## 2. Comments
- Explain **why**, not **what**.
- Use docstrings for public functions and classes.

## 3. Testing
- All modules must have test coverage ≥ 80%.
- Tests must run under 10 seconds.
- Prefer unit tests over integration tests.

## 4. Error Handling
- Fail fast, log clearly.
- No silent catches.

## 5. Version Control
- Use feature branches (`feature/...`).
- Commit messages follow conventional commits.

## 6. Code Review
- Reviewers focus on logic, clarity, and reproducibility.