package io_github_edadma.provision

import scala.collection.immutable.ArraySeq
import io_github_edadma.libssh2.Stat

trait SSH:
  def init(): Unit
  def session(username: String, password: String, hostname: String): SSHSession

abstract class SSHSession(val username: String, val password: String):
  def exec(commandline: String): Int
  def shutdown(status: Int): Nothing
  def sudo(commandline: String): Int = exec(s"""echo "$password" | sudo -S $commandline""")
  def command(commandline: String): Int = if username == "root" then sudo(commandline) else exec(commandline)
  def read(path: String): ArraySeq[Byte]
  def write(path: String, perm: Int, data: Seq[Byte]): Unit
  def mkdir(path: String, perm: Int): Unit
  def stat(path: String): Option[Stat]
