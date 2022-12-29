package io.github.edadma.provision

trait SSH:
  def initssh(hostname: String, username: String, password: String): Unit
  def exec(commandline: String): Int
  def shutdown(status: Int): Unit
