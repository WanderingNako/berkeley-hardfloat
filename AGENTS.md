# AGENTS.md

This file provides guidance to the AI agent when working with code in this repository.

## Overview

Berkeley HardFloat: IEEE floating-point units written in Chisel (Scala). All units operate on an internal **recoded format** — the exponent is one bit wider than IEEE format to encode subnormals (see README.md for the mapping). Don't assume IEEE bit layouts in the core modules.

## Build and Test

Two build systems exist in parallel: **sbt** (`build.sbt`, default) and **mill** (`build.sc`, used by CI to test multiple Chisel versions 3.5.6/3.6.0/5.0.0). Change both if you touch build config.

Source paths are non-standard: `hardfloat/src/main/scala` (main), `hardfloat/tests/src` + `hardfloat/tests/resources` (tests).

Tests cannot run with plain `sbt test` alone. Run tests via:

```
make test
```

which requires, in order:
1. Git submodules `berkeley-softfloat-3` and `berkeley-testfloat-3` (auto-initialized by make; they must not be deleted).
2. `testfloat_gen` on PATH — or make builds it in `berkeley-testfloat-3/build/Linux-x86_64-GCC/` (with softfloat built `SPECIALIZE_TYPE=RISCV`).
3. `verilator` and a JDK (CI uses 17) installed.

The test harness pipes `testfloat_gen` output into a Verilator-compiled DUT binary. `make clean` runs `git clean -fdx` on the repo AND submodules — destructive to all untracked files.

## Conventions

- Scala 2.13.10, Chisel 3.5.6. Imports use the old-style `chisel3._` bundle; match the existing style.
- License headers (BSD, John Hauser / UC Regents) are on every source file — preserve them on new/edited files.
