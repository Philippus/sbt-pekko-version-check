package nl.gn0s1s.pekko.versioncheck

private[versioncheck] object Compat {
  implicit class DefOps(private val self: sbt.Def.type) extends AnyVal {
    def uncached[A](a: A): A = a
  }
}
