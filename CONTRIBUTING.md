# Contributing

Bug reports and focused pull requests are welcome. You do not need to ask before fixing a clear problem, but an issue is useful when a change affects gameplay or could reasonably be implemented more than one way.

This project targets Minecraft 1.21.1 on Fabric and NeoForge. Please do not bundle unrelated version upgrades, large refactors, or formatting changes into a feature or bug fix. They make review harder and usually create more work than they save.

## Development setup

Install JDK 21 and JDK 25 before building. The Gradle daemon uses Java 25, as configured in `gradle/gradle-daemon-jvm.properties`. The mod targets Java 21, and the regression tests require a local JDK 21 installation. Gradle does not download that test runtime automatically.

Clone the repository and run:

```bash
./gradlew build
```

Windows users can replace `./gradlew` with `gradlew.bat` in every command.

Useful tasks:

- `./gradlew :fabric:runClient` or `:neoforge:runClient` starts a development client.
- `./gradlew :fabric:runServer` or `:neoforge:runServer` starts a development server.
- `./gradlew :fabric:runDatagen` regenerates the shared models, block states, recipes, loot tables, tags, language entries, and advancements.
- `./gradlew :fabric:test` runs the shared narrow-bookshelf regression tests against Minecraft 1.21.1 on Java 21. These also run during `build` and in CI.
- `./gradlew build` compiles both loaders and creates distributable JARs in their respective `build/libs/` directories.

## Project layout

- `common/src/main/kotlin/` and `common/src/main/java/` hold shared gameplay code. Both loader projects compile these sources directly.
- `common/src/client/kotlin/` holds shared client code, including renderers and recipe viewer integrations.
- `common/src/main/resources/` holds hand-written resources, and `common/src/main/generated/` holds datagen output.
- `fabric/` and `neoforge/` hold loader entrypoints, registration, metadata, and loader-specific compatibility code.
- `fabric/src/test/java/` holds the shared narrow-bookshelf regression tests. There is no separate NeoForge test suite.

The `common` module supports compilation and IDE navigation; it does not produce a distributable mod JAR. Install the loader JAR, not a sources JAR.

## Generated data

Generated resources in `common/src/main/generated/` are committed on purpose. When changing a block, recipe, tag, model, loot table, language entry, or advancement:

1. Change the relevant data provider or source definition.
2. Run `./gradlew :fabric:runDatagen`.
3. Review the generated diff before committing it.

CI regenerates these resources and rejects stale or missing output. The data generator's `.cache/` directory is ignored and excluded from mod jars.

Do not manually patch generated JSON unless there is no source-side way to represent the file. Hand-written models and compatibility data belong in `src/main/resources/`.

## Code style

- Prefer straightforward Kotlin or Java over clever abstractions.
- Follow existing code and modern vanilla Minecraft patterns before inventing a new structure.
- Keep behavior close to the block, entity, or registry it belongs to.
- Use Kotlin features when they improve clarity or safety, not just because they exist.
- Avoid new dependencies unless they solve a real problem that would be unreasonable to maintain here.
- Add comments for constraints and reasoning, not for code that already explains itself.

The mod is Kotlin-first, but small Java implementations are fine when they fit Minecraft's APIs better.

## Before opening a pull request

- Keep the change focused.
- Run `./gradlew build`.
- Run datagen when generated output should change.
- Include generated files that belong to the change.
- Explain player-visible behavior and any non-obvious implementation decisions.
- Mention what you tested. A short manual test is often more useful here than a large test framework.

## Documentation

Keep player instructions in [README.md](README.md) and the public project description in [DESCRIPTION.md](DESCRIPTION.md) consistent when gameplay changes. Use absolute links in the description because it is pasted into Modrinth and CurseForge. Add player-visible changes to the existing changelog section for `mod_version`, and keep future ideas in [ROADMAP.md](ROADMAP.md).

Keep docs focused on how things work. Leave exact recipe yields, dependency versions, and past fixes in the recipes, mod metadata, and changelog unless readers need them to get started. Documentation-only edits do not need a mod version bump.

Release preparation is documented separately in [RELEASING.md](RELEASING.md).
