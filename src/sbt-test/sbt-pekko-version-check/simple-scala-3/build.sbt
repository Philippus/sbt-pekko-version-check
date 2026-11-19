scalaVersion := "3.3.4"

// direct dependency mismatch
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-protobuf-v3" % "1.0.0",
  "org.apache.pekko" %% "pekko-testkit"     % "1.0.1"
)

pekkoVersionCheckFailBuildOnNonMatchingVersions := true

@transient
lazy val check = taskKey[Unit]("Check the previous task's log")

check := {
  val taskStreams = (pekkoVersionCheck / streams).value
  val reader = taskStreams.readText(taskStreams.key)
  val last: String =
    try Iterator.continually(reader.readLine()).takeWhile(_ != null).mkString("\n")
    finally reader.close()
  if (!last.contains("You are using version 1.0.1 of Pekko, but"))
    sys.error("expected mention of non-matching Pekko module versions")
}
