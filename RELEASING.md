# Releasing Wooden Accents Mod

Releases only happen when you run the **Release** workflow manually. Merging, pushing, or tagging doesn't start one.

The workflow publishes to GitHub Releases only. Modrinth and CurseForge are updated by hand.

## Versions

Versions look like `<minecraft_version>-<content_version>`, for example `1.21.1-1.3.0.0`. The version lives in `mod_version` in `gradle.properties`.

- Bump the second number (`1.3.0.0` to `1.4.0.0`) for big feature updates, like a new block family or workstation.
- Bump the third number (`1.3.0.0` to `1.3.1.0`) for smaller additions, like new recipes or tweaks to existing blocks.
- Bump the last number (`1.3.1.0` to `1.3.1.1`) for fixes, ports of the same content to another Minecraft version, and any other rebuild.
- Never republish a version. Every published build gets its own.
- Don't put loader names or `-beta` suffixes in the version. Fabric and NeoForge builds share a version, and the release type handles stability.

## Release types

| Type | Use for |
|---|---|
| `STABLE` | Tested on both loaders and ready for real worlds |
| `BETA` | Features are in, but compatibility or world testing isn't finished |
| `ALPHA` | Early ports or builds where the loaders don't match yet |

`BETA` and `ALPHA` are marked as prereleases on GitHub, but they're still real releases.

## Prepare a release

1. Set `mod_version` in `gradle.properties`.
2. Make sure `CHANGELOG.md` has a non-empty `## [<mod_version>] - YYYY-MM-DD` section. The build fails without one, since that section becomes the release notes.
3. Merge the version and changelog changes into `master` and confirm CI passes.

## Dry run

1. Open **Actions → Release → Run workflow** and pick the branch.
2. Leave **Dry run** on and **Confirm release** off.
3. Choose the release type and run it.

This builds the release JARs and uploads them as a workflow artifact for seven days without creating a tag or release. To do the same locally:

```bash
./gradlew clean publishMods -PdryRun=true -PreleaseType=BETA
```

## Publish

1. Open **Actions → Release → Run workflow** on `master`.
2. Choose the release type, turn **Dry run** off, and turn **Confirm release** on.
3. Check the `v<mod_version>` tag, release notes, and both JARs on GitHub Releases.

Real releases are rejected if they don't run from `master`, if **Confirm release** is off, or if the tag or release already exists.

If publishing fails partway, check GitHub Releases and tags for leftovers. Finish or delete any draft release before retrying.

## Older Minecraft versions

Before moving `master` to a new Minecraft version, create a branch for the old line (for example `1.21.1`) from its last commit. Make fixes on that branch and forward-port them while they still apply.

## Modrinth and CurseForge pages

`DESCRIPTION.md` is the Modrinth and CurseForge description, and `README.md` is the GitHub page. Whenever `DESCRIPTION.md` changes, even without a release:

1. Paste `DESCRIPTION.md` into the Modrinth project description.
2. Keep the summary as `Vanilla-scale furniture and structural accents for every wood type.`
3. Do the same on CurseForge.

Use absolute URLs in `DESCRIPTION.md` so links still work after pasting.
