package io_github_edadma.provision

import scala.collection.immutable.ArraySeq

abstract class SSH:
  var username: String = null
  var password: String = null

  def init(hostname: String): Unit
  def exec(commandline: String): Int
  def shutdown(status: Int): Nothing
  def sudo(commandline: String): Int = exec(s"""echo "$password" | sudo -S $commandline""")
  def command(commandline: String): Int = if username == "root" then sudo(commandline) else exec(commandline)
  def read(path: String): ArraySeq[Byte]
