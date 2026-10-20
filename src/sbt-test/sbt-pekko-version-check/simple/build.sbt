scalaVersion := "2.13.14"

// direct dependency mismatch
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-protobuf-v3" % "1.0.0",
  "org.apache.pekko" %% "pekko-testkit"     % "1.0.1"
)

pekkoVersionCheckFailBuildOnNonMatchingVersions := true

@transient
lazy val check = taskKey[Unit]("Check the previous task's log")

check := {
  val lastLog: File = BuiltinCommands.lastLogFile(state.value).get
  val last: String  = IO.read(lastLog)
  if (!last.contains("You are using version 1.0.1 of Pekko, but"))
    sys.error("expected mention of non-matching Pekko module versions")
}
