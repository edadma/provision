package io.github.edadma.provision

import pprint.pprintln

import scala.collection.mutable

@main def run(): Unit =
  val spec = SpecParser.parseSpec("""
      |task echo test
      |command echo testing123
      |""".stripMargin)

  pprintln(spec)
  validate(spec)
  execute(spec)
