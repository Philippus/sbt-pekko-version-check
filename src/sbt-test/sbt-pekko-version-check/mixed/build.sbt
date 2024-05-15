scalaVersion := "2.13.14"

// direct dependency mismatch
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-protobuf-v3"     % "1.0.0",
  "org.apache.pekko" %% "pekko-testkit"         % "1.0.1",
  "org.apache.pekko" %% "pekko-http"            % "1.0.1",
  "org.apache.pekko" %% "pekko-http-spray-json" % "1.0.0"
)

pekkoVersionCheckFailBuildOnNonMatchingVersions := true

TaskKey[Unit]("check") := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  if (!last.contains("You are using version 1.0.1 of Pekko, but") || !last.contains("You are using version 1.0.1 of Pekko HTTP, but"))
    sys.error("expected mention of non-matching Pekko and Pekko HTTP module versions")
}
