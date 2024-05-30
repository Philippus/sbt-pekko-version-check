# sbt-pekko-version-check

[![build](https://github.com/Philippus/sbt-pekko-version-check/workflows/build/badge.svg)](https://github.com/Philippus/sbt-pekko-version-check/actions/workflows/scala.yml?query=workflow%3Abuild+branch%3Amain)
![Current Version](https://img.shields.io/badge/version-0.0.4-brightgreen.svg?style=flat "0.0.4")
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg?style=flat "Apache 2.0")](LICENSE)

This plugin can verify that the Apache Pekko module versions of a project match. Pekko also does this verification
itself, but the advantage is that this check can run before running any (integration) tests or running your application
in production. You can use it also as part of your build chain and make the build fail if non-matching versions are
found. This project started as a fork of [sbt-akka-version-check](https://github.com/johanandren/sbt-akka-version-check).

## Installation

sbt-pekko-version-check is published for sbt 1.10.0 and above. To start using it add the following to your
`plugins.sbt`:

```
addSbtPlugin("nl.gn0s1s" % "sbt-pekko-version-check" % "0.0.4")
```

## Usage
### Tasks

| Task              | Description          | Command                       |
|:------------------|:---------------------|:------------------------------|
| pekkoVersionCheck | Runs version check.  | ```$ sbt pekkoVersionCheck``` |

### Configuration
You can configure the configuration in your `build.sbt` file.

| Setting                                                                                 | Description                                                                                                                                           | Default Value |
|:----------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------|:--------------|
| pekkoVersionCheckFailBuildOnNonMatchingVersions | Sets whether non-matching versions fail the build, if `false` non-matching versions show up as warnings in the log, if `true` they show up as errors. | false         |

## License
The code is available under the [Apache 2.0 License](LICENSE).
