version := "0.1"
scalaVersion := "2.12.19"

// transitive dependency mismatch (pekko-kryo-serialization pulls in pekko 1.0.2 modules)
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-stream-testkit" % "1.0.1",
  "io.altoo" %% "pekko-kryo-serialization" % "1.2.0"
)

pekkoVersionCheckFailBuildOnNonMatchingVersions := true

TaskKey[Unit]("check") := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  if (!last.contains("Transitive dependencies from [org.apache.pekko"))
    sys.error("expected mention of transitive dependencies")
}
