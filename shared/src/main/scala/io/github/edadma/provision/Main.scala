package io.github.edadma.provision

import pprint.pprintln

@main def run(): Unit =
  pprintln(SpecParser.parseSpec("""
      |task first task
      |""".stripMargin))
