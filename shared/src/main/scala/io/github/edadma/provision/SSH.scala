package io.github.edadma.provision

trait SSH:
  def init(hostname: String, username: String, password: String): Unit
