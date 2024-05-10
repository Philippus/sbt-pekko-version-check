package nl.gn0s1s.pekko.versioncheck

import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

object PekkoVersionCheckPlugin extends AutoPlugin {
  case class PekkoVersionReport(pekkoVersion: Option[VersionNumber], pekkoHttpVersion: Option[VersionNumber])

  override def trigger = allRequirements

  object autoImport {
    val checkPekkoModuleVersions = taskKey[PekkoVersionReport]("Check that all Pekko modules have the same version")
  }

  import autoImport._

  override lazy val globalSettings = Seq()

  override lazy val projectSettings = Seq(
    checkPekkoModuleVersions := checkModuleVersions(updateFull.value, streams.value.log)
  )

  private val coreModules      = Set(
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
  private val pekkoHttpModules = Set(
    "pekko-http",
    "pekko-http-caching",
    "pekko-http-core",
    "pekko-http-jackson",
    "pekko-http-marshallers-java",
    "pekko-http-marshallers-scala",
    "pekko-http-root",
    "pekko-http-spray-json",
    "pekko-http-testkit",
    "pekko-http-xml",
    "pekko-http2-support",
    "pekko-parsing"
  )

  private sealed trait Group
  private case object Pekko     extends Group
  private case object PekkoHttp extends Group
  private case object Others    extends Group

  private def checkModuleVersions(updateReport: UpdateReport, log: Logger): PekkoVersionReport = {
    log.info("Checking Pekko module versions")
    val allModules       = updateReport.allModules
    val grouped          = allModules.groupBy(m =>
      if (m.organization == "org.apache.pekko") {
        val nameWithoutScalaV = m.name.dropRight(5)
        log.info(nameWithoutScalaV)
        if (coreModules(nameWithoutScalaV)) Pekko
        else if (pekkoHttpModules(nameWithoutScalaV)) PekkoHttp
        else Others
      }
    )
    log.info(grouped.toString)
    val pekkoVersion     = grouped.get(Pekko)
      .flatMap(verifyVersions("Pekko", _, updateReport))
      .map(VersionNumber.apply)
    log.info(pekkoVersion.toString)
    val pekkoHttpVersion = grouped.get(PekkoHttp)
      .flatMap(verifyVersions("Pekko HTTP", _, updateReport)
        .map(VersionNumber.apply))
    log.info(pekkoHttpVersion.toString)

    (pekkoVersion, pekkoHttpVersion) match {
      case (Some(pekkoV), Some(pekkoHttpV)) =>
        verifyPekkoHttpPekkoRequirement(pekkoV, pekkoHttpV)
      case _                                => // whatever
    }
    // FIXME is it useful to verify more inter-project dependencies Pekko vs Pekko Persistence Cassandra etc.
    PekkoVersionReport(pekkoVersion, pekkoHttpVersion)
  }

  private def verifyVersions(project: String, modules: Seq[ModuleID], updateReport: UpdateReport): Option[String] =
    modules.foldLeft(None: Option[String]) { (prev, module) =>
      prev match {
        case Some(version) =>
          if (module.revision != version) {
            val allModules   = updateReport.configurations.flatMap(_.modules)
            val moduleReport = allModules.find(r =>
              r.module.organization == module.organization && r.module.name == module.name
            )
            val tsText       = moduleReport match {
              case Some(report) =>
                s"Transitive dependencies from ${report.callers.mkString("[", ", ", "]")}"
              case None         =>
                ""
            }
            throw new MessageOnlyException(
              s"""| Non matching $project module versions, previously seen version $version, but module ${module.name} has version ${module.revision}.
                  | $tsText""".stripMargin.trim
            )

          } else Some(version)
        case None          => Some(module.revision)
      }
    }

  private def verifyPekkoHttpPekkoRequirement(pekkoHttpVersion: VersionNumber, pekkoVersion: VersionNumber): Unit = {
    // everything is OK as far as we know
  }
}
