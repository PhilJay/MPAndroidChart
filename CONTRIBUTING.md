# Contributing

This is the Android library. For iOS, go to [Charts](https://github.com/danielgindi/Charts).

## Issues

Search the [issues], open and closed, before you file one. Use the bug or feature template. Ask usage questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/mpandroidchart) with the `mpandroidchart` tag, not in the tracker.

## Pull requests

1. Search the open [pull requests] and [issues] first. For a large change, open an issue and agree on the approach before you write it.
2. Fork the repository and create a branch from `master`.
3. Write Kotlin in the official style. Every public member gets KDoc, and sizes are stored in dp and converted when drawing.
4. Run `./gradlew :MPChartLib:test :MPChartExample:compileDebugUnitTestKotlin dokkaGenerate` and try the change in the example app.
5. If you changed the public API on purpose, run `./gradlew apiDump` and commit the updated `api` folders; `./gradlew check` fails until the dump matches.
6. Open the pull request and fill in the template.

Keep each commit to one logical change, with a title of at most 50 characters and a body that says why.

[issues]: https://github.com/PhilJay/MPAndroidChart/issues
[pull requests]: https://github.com/PhilJay/MPAndroidChart/pulls
