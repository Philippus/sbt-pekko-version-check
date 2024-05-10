version := "0.1"
scalaVersion := "2.13.14"

// transitive dependency mismatch (pekko-kryo-serialization pulls in pekko 1.0.2 modules)
libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-discovery-kubernetes-api" % "1.0.0",
  "org.apache.pekko" %% "pekko-management-cluster-http" % "1.0.0"
)

pekkoVersionCheckFailBuildOnNonMatchingVersions := true
