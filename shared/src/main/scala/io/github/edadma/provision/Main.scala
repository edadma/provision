package io.github.edadma.provision

import pprint.pprintln

import scala.collection.mutable

@main def run(): Unit =
  val spec = SpecParser.parseSpec("""
      |become root
      |task first task
      |def server nginx
      |package $server, git present
      |user asdf
      |  group gasdf
      |  shell /usr/bin/sasdf
      |  home /home/asdf
      |""".stripMargin)

  pprintln(spec)
  validate(spec)
  execute(spec)
