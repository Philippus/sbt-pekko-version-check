scalaVersion := "2.13.14"

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-discovery-kubernetes-api" % "1.0.0",
  "org.apache.pekko" %% "pekko-management-cluster-http"  % "1.0.0"
)

pekkoVersionCheckFailBuildOnNonMatchingVersions := true
