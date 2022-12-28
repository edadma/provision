package io.github.edadma.provision

import pprint.pprintln

import scala.collection.mutable

@main def run(): Unit =
  val spec = SpecParser.parseSpec("""
      |task upgrade packages
      |update
      |""".stripMargin)

  pprintln(spec)
  validate(spec)
  execute(spec)
