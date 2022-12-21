package io.github.edadma.provision

import scala.util.parsing.input.Positional

private val nameRegex = """[a-z_][a-z0-9_-]*$?""".r

def check(cond: Boolean, p: Positional, error: String): Nothing = if !cond then problem(p, error)

def validate(spec: SpecAST): Unit =
  spec.statements foreach { case DirectoryStat(dir, owner, group, mode, state) =>
    check(eval(dir))
  }
