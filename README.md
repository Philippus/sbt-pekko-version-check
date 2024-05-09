# sbt-pekko-version-check

A plugin that verifies Apache Pekko module versions of a project

## Usage

This plugin requires sbt 1.0.0+

Add the plugin to your project, for example in `project/plugins.sbt`:

```
addSbtPlugin("nl.gn0s1s" % "sbt-pekko-version-check" % "0.0.1")
```

Trigger the version check through the `checkPekkoModuleVersions` task. The check will fail the build if there
are problems with version conflicts in the Akka modules used in the project. 

The task can be useful to put in your PR validation on a CI-server to detect if changes accidentally
introduce transitive Apache Pekko module dependencies of mismatched versions.

### Testing

Run `test` for regular unit tests.

Run `scripted` for [sbt script tests](http://www.scala-sbt.org/1.x/docs/Testing-sbt-plugins.html).

### Publishing

`sbt publish`
