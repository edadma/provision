package io.github.edadma.provision

abstract class SSH:
  var sudopassword: String

  def init(hostname: String, username: String, password: String): Unit
  def exec(commandline: String): Int
  def shutdown(status: Int): Unit
  def sudo(commandline: String): Int =
    exec(s"echo $sudopassword | sudo -S $commandline")
