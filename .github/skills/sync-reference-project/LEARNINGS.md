# Sync run learnings

- 2026-10-14: The canonical `main` revision was `e770157508f974871de261abeff317cfcf5783b8`. This local project was behind it, including outdated package names and missing Java 11, 21, and 25 examples; `.idea/` remains workspace-specific.
- 2026-10-14: Synced reference files match all upstream blob hashes and `gradlew.bat test` passes. `git diff FETCH_HEAD` compares the index and omits untracked files, so exact sync verification must include every blob listed by `git ls-tree -r FETCH_HEAD`.
