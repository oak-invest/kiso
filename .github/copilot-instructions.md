# Kiso

## Project description

- **Kiso short description**: Kiso is a publishing engine for Open Knowledge Format (OKF).
- **Kiso longer description**: Kiso is a publishing engine that turns Open Knowledge Format (OKF) bundles into static websites for humans and AI agents.
- **Kiso complete description**: Kiso is a publishing engine for the Open Knowledge Format (OKF). The project turns a folder of organized Markdown files into a navigable knowledge system. In the same way that Hugo turns Markdown files into a static website, Kiso aims to turn an OKF knowledge base into a structured, browsable, and usable documentation site. Kiso is also designed with AI usage in mind. Because the source remains structured Markdown, the knowledge base stays readable, versionable with Git, easy to review, and usable by AI agents. Kiso sits between two needs: simple text files for maintaining knowledge, and generated navigation for humans and tools to explore it.

- **Product analogy**: Kiso is positioned as Hugo for Open Knowledge Format (OKF).

- **Open Knowledge Format description**: Open Knowledge Format (OKF) is a way to structure knowledge using Markdown files enriched with metadata. The goal is to keep knowledge easy for humans to write and read, while making it structured enough for software tools and AI agents to understand, validate, connect, and reuse.
- **Open Knowledge Format (OKF) latest specification**: /documentation/specs/okf_spec_version_0.2.md

## Information

- **Repository**: https://github.com/oak-invest/kiso
- **Website**: https://oak-invest.github.io/kiso

## Kiso features

Kiso-cli (`/applications/kiso-cli`) is a command-line application with commands such as:
- `check`: Validates the Markdown files in an OKF bundle and reports formatting or structural errors.
- `build`: Generates a static website with HTML files alongside the original Markdown files.

## Design principles

When generating code for this repository, always prioritize:

1. simplicity
2. readability
3. beauty
4. performance

Prefer explicit code to clever code.

Avoid unnecessary abstraction and avoid introducing framework-specific behavior unless it is already part of the repository design.

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

Prefer immutable models when possible. If the repository already uses records for DTO-like structures, prefer
records for new DTO-like types.

Keep models focused and do not mix unrelated concerns into the same class.

When adding fields to shared contracts:
- Ensure names are explicit.
- Ensure nullability is intentional.
- Preserve JSON compatibility.
- Document semantics in Javadoc when the meaning is not obvious.

## Validation rules

Validation must be predictable and easy to reason about.

Custom validation annotations and validators should:
- Have clear names.
- Validate one concern.
- Produce understandable error messages.
- Avoid hidden side effects.

Do not create overly generic validators when a narrow validator is clearer.

## Dependencies

Treat every new dependency as costly.

Before adding a dependency, prefer:
- Existing repository dependencies.
- Plain Java.
- Small local code.

Only add a dependency if it meaningfully improves correctness or maintainability.

## Testing expectations
When changing code in this repository:
- Add or update unit tests when behavior changes.
- Cover serialization/deserialization when contracts change.
- Cover validation logic when validators change.
- Cover edge cases for shared utility code.

Tests should be simple, direct, and readable.

## Other rules
- Never use tertiary operators (e.g., `condition ? trueValue : falseValue`) in this repository.