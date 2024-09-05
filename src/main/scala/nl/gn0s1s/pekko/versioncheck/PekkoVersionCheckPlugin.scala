package nl.gn0s1s.pekko.versioncheck

import sbt._
import sbt.Keys._

object PekkoVersionCheckPlugin extends AutoPlugin {
  override def trigger = allRequirements

  object autoImport {
    lazy val pekkoVersionCheckFailBuildOnNonMatchingVersions =
      settingKey[Boolean]("Sets whether non-matching module versions fail the build")
    val pekkoVersionCheck                                    = taskKey[Unit]("Check that all Pekko modules have the same version")
  }

  import autoImport._

  override lazy val globalSettings = Seq(
    pekkoVersionCheckFailBuildOnNonMatchingVersions := false
  )

  override lazy val projectSettings = Seq(
    pekkoVersionCheck := checkModuleVersions(
      updateFull.value,
      streams.value.log,
      pekkoVersionCheckFailBuildOnNonMatchingVersions.value
    )
  )

  private val pekkoModules           = Set(
    "pekko",
    "pekko-actor",
    "pekko-actor-testkit-typed",
    "pekko-actor-tests",
    "pekko-actor-typed",
    "pekko-actor-typed-tests",
    "pekko-cluster",
    "pekko-cluster-metrics",
    "pekko-cluster-sharding",
    "pekko-cluster-sharding-typed",
    "pekko-cluster-tools",
    "pekko-cluster-typed",
    "pekko-coordination",
    "pekko-discovery",
    "pekko-distributed-data",
    "pekko-multi-node-testkit",
    "pekko-osgi",
    "pekko-persistence",
    "pekko-persistence-query",
    "pekko-persistence-shared",
    "pekko-persistence-tck",
    "pekko-persistence-typed",
    "pekko-protobuf",
    "pekko-protobuf-v3",
    "pekko-remote",
    "pekko-serialization-jackson",
    "pekko-slf4j",
    "pekko-stream",
    "pekko-stream-testkit",
    "pekko-stream-typed",
    "pekko-testkit"
  )
  private val pekkoHttpModules       = Set(
    "pekko-http",
    "pekko-http-caching",
    "pekko-http-core",
    "pekko-http-jackson",
    "pekko-http-marshallers-java",
    "pekko-http-marshallers-scala",
    "pekko-http-root",
    "pekko-http-spray-json",
    "pekko-http-testkit",
    "pekko-http-testkit-munit",
    "pekko-http-xml",
    "pekko-http2-support",
    "pekko-parsing"
  )
  private val pekkoManagementModules = Set(
    "pekko-discovery-consul",
    "pekko-discovery-aws-api",
    "pekko-discovery-marathon-api",
    "pekko-discovery-aws-api-async",
    "pekko-discovery-kubernetes-api",
    "pekko-lease-kubernetes",
    "pekko-management",
    "pekko-management-cluster-bootstrap",
    "pekko-management-cluster-http"
  )

  private sealed trait Group

  private case object Pekko extends Group

  private case object PekkoHttp extends Group

  private case object PekkoManagement extends Group

  private case object Others extends Group

  private def checkModuleVersions(
      updateReport: UpdateReport,
      log: Logger,
      failBuildOnNonMatchingVersions: Boolean
  ) = {
    log.info("Checking Pekko module versions")
    val allModules        = updateReport.allModules
    val grouped           = allModules.groupBy(m =>
      if (m.organization == "org.apache.pekko") {
        val nameWithoutScalaV = m.name.dropRight(5)
        if (pekkoModules(nameWithoutScalaV)) Pekko
        else if (pekkoHttpModules(nameWithoutScalaV)) PekkoHttp
        else if (pekkoManagementModules(nameWithoutScalaV)) PekkoManagement
        else Others
      }
    )
    val pekkoOk           = grouped.get(Pekko).forall(verifyVersions("Pekko", _, log, failBuildOnNonMatchingVersions))
    val pekkoHttpOk       =
      grouped.get(PekkoHttp).forall(verifyVersions("Pekko HTTP", _, log, failBuildOnNonMatchingVersions))
    val pekkoManagementOk =
      grouped.get(PekkoManagement).forall(verifyVersions("Pekko Management", _, log, failBuildOnNonMatchingVersions))

    for {
      pekkoVersion     <- grouped.get(Pekko).flatMap(_.map(m => Version(m.revision)).sorted.lastOption)
      pekkoVersionHttp <- grouped.get(PekkoHttp).flatMap(_.map(m => Version(m.revision)).sorted.lastOption)
    } yield verifyPekkoHttpPekkoRequirement(pekkoVersion, pekkoVersionHttp, log)

    if (failBuildOnNonMatchingVersions && (!pekkoOk || !pekkoHttpOk || !pekkoManagementOk))
      throw NonMatchingVersionsException
  }

  private def verifyVersions(
      project: String,
      modules: Seq[ModuleID],
      log: Logger,
      failBuildOnNonMatchingVersions: Boolean
  ): Boolean = {
    val modulesLatestRevision = modules.maxBy(m => Version(m.revision)).revision
    val modulesTobeUpdated     = modules.collect { case m if m.revision != modulesLatestRevision => m.name.dropRight(5) }.sorted
    if (modulesTobeUpdated.nonEmpty) {
      val groupedByVersion = modules
        .groupBy(_.revision)
        .toSeq
        .sortBy(r => Version(r._1))
        .map { case (k, v) => k -> v.map(_.name.dropRight(5)).sorted.mkString("[", ", ", "]") }
        .map { case (k, v) => s"($k, $v)" }
        .mkString(", ")
      val report           = s"You are using version $modulesLatestRevision of $project, but it appears " +
        s"you (perhaps indirectly) also depend on older versions of related artifacts. " +
        s"You can solve this by adding an explicit dependency on version $modulesLatestRevision " +
        s"of the [${modulesTobeUpdated.mkString(", ")}] artifacts to your project. " +
        s"Here's a complete collection of detected artifacts: $groupedByVersion. " +
        "See also: https://pekko.apache.org/docs/pekko/current/common/binary-compatibility-rules.html#mixed-versioning-is-not-allowed"

      if (failBuildOnNonMatchingVersions)
        log.error(report)
      else
        log.warn(report)
    }
    modulesTobeUpdated.isEmpty
  }

  private def verifyPekkoHttpPekkoRequirement(pekkoVersion: Version, pekkoHttpVersion: Version, log: Logger): Unit = {
    if (pekkoHttpVersion.version.startsWith("1.1.") && pekkoVersion.version.startsWith("1.0."))
      log.warn("It is strongly recommended that you avoid using Pekko 1.0.x artifacts with Pekko HTTP 1.1.x, you should " +
        "use Pekko 1.1.x artifacts where possible. Problems running with Pekko 1.0.x artifacts aren't expected, but " +
        "Pekko HTTP 1.1 is built with Pekko 1.1.")
  }
}
