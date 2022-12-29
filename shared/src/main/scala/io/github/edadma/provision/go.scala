package io.github.edadma.provision

import pprint.pprintln

import scala.collection.mutable

def go(impl: SSH): Unit =
  val spec = SpecParser.parseSpec("""
                                    |task echo test
                                    |command echo testing 123
                                    |task update
                                    |update
                                    |task upgrade
                                    |upgrade
                                    |""".stripMargin)

  pprintln(spec)
  validate(spec)
  impl.init("127.0.0.1", "testuser", "easypassword")
  execute(spec, impl)
  impl.shutdown(0)
