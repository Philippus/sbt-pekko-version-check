scalaVersion := "2.13.14"

// version mismatch between pekko and pekko-http
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor" % "1.0.2",
  "org.apache.pekko" %% "pekko-http"  % "1.1.0-M1"
)

pekkoVersionCheckFailBuildOnNonMatchingVersions := true

TaskKey[Unit]("check") := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  if (!last.contains("It is strongly recommended that you avoid using Pekko 1.0.x artifacts with Pekko HTTP 1.1.x"))
    sys.error("expected recommendation to avoid using Pekko 1.0.x artifacts with Pekko HTTP 1.1.x artifacts")
}
