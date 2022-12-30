package io_github_edadma.provision

import pprint.pprintln

import scala.collection.mutable

def go(impl: SSH): Unit =
  val spec = SpecParser.parseSpec("""
                                    |task echo test
                                    |command echo testing 123
                                    |task install
                                    |package curl
                                    |""".stripMargin)

  validate(spec)
  impl.username = "testuser"
  impl.password = "easypassword"
  impl.init("127.0.0.1")
  execute(spec, impl)
  impl.shutdown(0)
