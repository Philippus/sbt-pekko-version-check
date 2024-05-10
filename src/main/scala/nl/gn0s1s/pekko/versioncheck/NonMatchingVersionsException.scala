package nl.gn0s1s.pekko.versioncheck

case object NonMatchingVersionsException extends IllegalStateException("Non-matching versions found")
