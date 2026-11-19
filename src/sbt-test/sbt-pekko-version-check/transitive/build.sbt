scalaVersion := "2.13.14"

// transitive dependency mismatch (pekko-kryo-serialization pulls in pekko 1.0.2 modules)
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-stream-testkit"     % "1.0.1",
  "io.altoo"         %% "pekko-kryo-serialization" % "1.2.0"
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
  if (!last.contains("You are using version 1.0.2 of Pekko, but"))
    sys.error("expected mention of non-matching Pekko module versions")
}
