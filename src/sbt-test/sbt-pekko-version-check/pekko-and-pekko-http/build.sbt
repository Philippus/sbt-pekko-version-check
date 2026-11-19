scalaVersion := "2.13.14"

// version mismatch between pekko and pekko-http
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor" % "1.0.2",
  "org.apache.pekko" %% "pekko-http"  % "1.1.0-M1"
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
  if (!last.contains("It is strongly recommended that you avoid using Pekko 1.0.x artifacts with Pekko HTTP 1.1.x"))
    sys.error("expected recommendation to avoid using Pekko 1.0.x artifacts with Pekko HTTP 1.1.x artifacts")
}
