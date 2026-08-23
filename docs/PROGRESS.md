# Progress

Working memory for what's in flight, updated at the end of every non-trivial session and
read at the start of the next one. This is short-lived — prune an entry once it's merged
to `main`, don't let this turn into a changelog (git history already is one).

## Current state

- Phase 1 (Instructions layer) done: `AGENTS.md`, `docs/architecture.md`,
  `docs/testing.md`, `docs/quality-gates.md` exist and are current.
- Phase 2 (Continuity across sessions) done: `docs/PROGRESS.md`, `docs/DECISIONS.md`,
  and `AGENTS.md` Session Exit Checklist all in place.
- Phase 3 (WIP + termination criteria) done: WIP=1 rule and the "feature complete means
  Layer 3, not just compiles" rule were already in `AGENTS.md` constraints #10–11 and
  `docs/quality-gates.md`'s Layer 1/2/2b/3 structure; added `docs/TASKS.md` (the
  remaining piece) and linked it from `AGENTS.md`.

## In progress

- (none)

## Blocked

- (none)

## Next steps

- Phase 4 (see `docs/TASKS.md`): add a true UI-flow test exercising the real navigation
  graph (splash → main) and an executable check that fails the build if a
  `ui/*`/`core/*Impl` module is depended on by anything other than `diApp`.
- Phase 5: PR checklist template mirroring the termination criteria.
- Phase 6: module-pair scaffold skill, git worktree note in `AGENTS.md`.
