# Kiso

Open Knowledge Format (OKF) is available at : /documentation/specs/okf_spec_version_0.1.md

Kiso is a publishing engine for OKF. The idea is to develop a publishing engine that turns Markdown files into 
navigable knowledge systems for both humans and AI agents.

The goal is to build something similar to Hugo, but for the Open Knowledge Format.

It would provide a command-line application with commands such as:
- format: Cleans .md files (e.g., removes unnecessary empty lines).
- validate: Checks that all files in the directory are valid.
- serve: Runs a local web server that displays a website generated from the .md files.
- build: Generates a static website with HTML files created from the .md files, while also preserving the original .
  md files (so it can be published).

The project is in /applications/kiso-cli

## Design principles
When generating code for this repository, always prioritize:

1. simplicity
2. readability
3. beauty
4. performance

Prefer explicit code to clever code.

Avoid unnecessary abstraction.

Avoid introducing framework-specific behavior unless it is already part of the repository design.

## Naming rules
Use simple, explicit, full English names.
Choose expressive names to abbreviations.
Good names are short but clear.

Examples:

- `paymentRequirements`
- `networkId`
- `facilitatorUrl`

Avoid vague names like:

- `data`
- `info`
- `object`

## API and model rules
Prefer immutable models when possible.

If the repository already uses records for DTO-like structures, prefer records for new DTO-like types.

Keep models focused.

Do not mix unrelated concerns into the same class.

When adding fields to shared contracts:

- ensure names are explicit
- ensure nullability is intentional
- preserve JSON compatibility
- document semantics in Javadoc when the meaning is not obvious

## Validation rules
Validation must be predictable and easy to reason about.

Custom validation annotations and validators should:

- have clear names
- validate one concern
- produce understandable error messages
- avoid hidden side effects

Do not create overly generic validators when a narrow validator is clearer.

## Dependencies
Treat every new dependency as costly.

Before adding a dependency, prefer:

- existing repository dependencies
- plain Java
- small local code

Only add a dependency if it meaningfully improves correctness or maintainability.

## Testing expectations
When changing code in this repository:

- add or update unit tests when behavior changes
- cover serialization/deserialization when contracts change
- cover validation logic when validators change
- cover edge cases for shared utility code

Tests should be simple, direct, and readable.
