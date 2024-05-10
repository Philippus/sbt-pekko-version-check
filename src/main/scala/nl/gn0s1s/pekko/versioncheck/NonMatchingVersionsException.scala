package nl.gn0s1s.pekko.versioncheck

case object NonMatchingVersionsException extends IllegalStateException("Non-matching Pekko module versions found")
