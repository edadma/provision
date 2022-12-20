package io.github.edadma.provision

import pprint.pprintln

@main def run(): Unit =
  pprintln(SpecParser.parseSpec("""
      |become root
      |task first task
      |def server nginx
      |package $server installed
      |""".stripMargin))
