# Sync run learnings

- 2026-10-14: The canonical `main` revision was `e770157508f974871de261abeff317cfcf5783b8`. This local project was behind it, including outdated package names and missing Java 11, 21, and 25 examples; `.idea/` remains workspace-specific.
- 2026-10-14: Synced reference files match all upstream blob hashes and `gradlew.bat test` passes. `git diff FETCH_HEAD` compares the index and omits untracked files, so exact sync verification must include every blob listed by `git ls-tree -r FETCH_HEAD`.
- 2026-10-19: Synced project files to reference commit `31507924261f6140c8f61adcd98e21a91d0a313e`, preserving `.idea/` and this skill. All 54 reference blobs match; `.\gradlew.bat test` passes. Reusable lesson: verify each reference blob against the worktree with `git hash-object`, because the sync intentionally leaves the index unchanged.
- 2026-10-19: Restored `week-01/` from the pre-sync project revision after the user clarified it must remain; `.\week-01\gradlew.bat -p .\week-01 test` passes. Reusable lesson: preserve existing project subprojects absent from the reference unless the user explicitly asks to remove them.
