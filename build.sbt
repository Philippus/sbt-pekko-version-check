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
pluginCrossBuild / sbtVersion := "1.10.0" // minimum version we target

Compile / packageBin / packageOptions += Package.ManifestAttributes(
  "Automatic-Module-Name" -> "nl.gn0s1s.pekko.versioncheck"
)

scriptedLaunchOpts := {
  scriptedLaunchOpts.value ++ Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}

scriptedBufferLog := false
