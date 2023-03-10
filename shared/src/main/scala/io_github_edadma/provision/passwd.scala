package io_github_edadma.provision

import scala.collection.mutable
import scala.language.postfixOps

case class User(name: String, password: String, uid: String, gid: String, comment: String, home: String, shell: String)

private val passwdRegex = """([^:]*):([^:]*):([^:]*):([^:]*):([^:]*):([^:]*):([^:]*)""".r

def passwd(impl: SSHSession): Map[String, User] =
  scala.io.Source.fromString(new String(impl.read("/etc/passwd").toArray)).getLines() map {
    case passwdRegex(name, password, uid, gid, comment, home, shell) =>
      name -> User(name, password, uid, gid, comment, home, shell)
  } toMap
