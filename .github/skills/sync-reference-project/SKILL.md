---
name: sync-reference-project
description: Compare this ticketing app with its canonical GitHub project, safely align the current workspace, run checks, and retain verified lessons. Invoke from Copilot Chat with /sync-reference-project.
---

# Synchronize with the reference project

Canonical reference: `https://github.com/markvaradi94/future-ready-ticketing-app` (default branch `main`).

Run this skill only in the current workspace. Never edit the GitHub reference or the separate `future-ready-ticketing-app` local checkout.

## Workflow

1. Read `LEARNINGS.md` in this directory, then identify the active project root and inspect its Git state.
2. Fetch the latest reference revision without changing configured remotes:
   `git fetch --no-tags https://github.com/markvaradi94/future-ready-ticketing-app.git main`
3. Compare the current worktree with `FETCH_HEAD`, including files that are untracked in the local branch. `git diff FETCH_HEAD` compares the index and does not include untracked files, so verify every reference blob from `git ls-tree -r FETCH_HEAD` against the corresponding local file hash when an exact comparison is needed. Preserve local IDE configuration under `.idea/` and this skill directory under `.github/skills/`; these are workspace-specific and are not part of the reference sync.
4. Before replacing any file that differs from the reference, inspect whether it contains local work. Preserve unrelated changes. If a conflicting local edit cannot be safely reconciled, stop and ask before overwriting it.
5. Apply reference updates only to the active workspace. Do not change branches, rewrite history, commit, push, or delete unrelated files.
6. Run the smallest relevant project build or tests. Report the reference commit, meaningful files changed, and the validation result.
7. At the end of every completed run, append a dated entry to `LEARNINGS.md` recording the reference commit, outcome, checks, and either a verified reusable lesson or that no new lesson was found. Use this history on later runs.
8. Refine this `SKILL.md` only when a verified, reusable lesson demonstrates a specific improvement. Keep changes limited to this workflow; never weaken the safeguards for preserving user changes.

## Chat invocation

In Copilot Chat, invoke `/sync-reference-project` and describe any specific scope or constraint for that run.
