name         := "sbt-pekko-version-check"
organization := "nl.gn0s1s"
startYear    := Some(2024)
homepage     := Some(url("https://github.com/philippus/sbt-pekko-version-check"))
licenses += License.Apache2
developers   := List(
  Developer(
    id = "philippus",
    name = "Philippus Baalman",
    email = "",
    url = url("https://github.com/philippus")
  ),
  Developer(
    id = "johanandren",
    name = "Johan Andrén",
    email = "",
    url = url("https://github.com/johanandren")
  )
)

enablePlugins(SbtPlugin)

scalaVersion := "2.13.17"
crossScalaVersions += "3.7.3"

pluginCrossBuild / sbtVersion := {
  scalaBinaryVersion.value match {
    case "2.12" => "1.10.0"
    case _      => "2.0.0-RC6"
  }
}

Compile / packageBin / packageOptions += Package.ManifestAttributes(
  "Automatic-Module-Name" -> "nl.gn0s1s.pekko.versioncheck"
)

scriptedLaunchOpts := {
  scriptedLaunchOpts.value ++ Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}

scriptedBufferLog := false
