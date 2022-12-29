package io.github.edadma.provision

abstract class SSH:
  var username: String = null
  var password: String = null

  def init(hostname: String): Unit
  def exec(commandline: String): Int
  def shutdown(status: Int): Unit
  def sudo(commandline: String): Int = exec(s"""echo "$password" | sudo -S $commandline""")
  def command(commandline: String): Int = if username == "root" then sudo(commandline) else exec(commandline)
