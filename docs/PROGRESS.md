# Progress

Working memory for what's in flight, updated at the end of every non-trivial session and
read at the start of the next one. This is short-lived — prune an entry once it's merged
to `main`, don't let this turn into a changelog (git history already is one).

## Current state

- Phase 1 (Instructions layer) done: `AGENTS.md`, `docs/architecture.md`,
  `docs/testing.md`, `docs/quality-gates.md` exist and are current.
- Phase 2 (Continuity across sessions) in progress: this file and `docs/DECISIONS.md`
  just added; `AGENTS.md` Session Exit Checklist added in the same session.

## In progress

- Harness-engineering plan phases 3–6 (WIP/termination criteria in `AGENTS.md`,
  `docs/TASKS.md`, the E2E navigation test + executable architectural constraint,
  the PR template, the module-pair scaffold skill) not started yet.

## Blocked

- (none)

## Next steps

- Phase 3: add WIP=1 rule + three-layer termination criteria to `AGENTS.md`, add
  `docs/TASKS.md`.
- Phase 4: add a true UI-flow test exercising the real navigation graph (splash → main)
  and an executable check that fails the build if a `ui/*`/`core/*Impl` module is
  depended on by anything other than `diApp`.
