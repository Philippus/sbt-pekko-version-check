scalaVersion := "2.13.14"

// transitive dependency mismatch (pekko-kryo-serialization pulls in pekko 1.0.2 modules)
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-stream-testkit"     % "1.0.1",
  "io.altoo"         %% "pekko-kryo-serialization" % "1.2.0"
)

pekkoVersionCheckFailBuildOnNonMatchingVersions := true

TaskKey[Unit]("check") := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  if (!last.contains("You are using version 1.0.2 of Pekko, but"))
    sys.error("expected mention of non-matching Pekko module versions")
}
