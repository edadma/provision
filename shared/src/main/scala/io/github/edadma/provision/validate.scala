package io.github.edadma.provision

import scala.util.matching.Regex
import scala.util.parsing.input.Positional

private val nameRegex = """[a-z_][a-z0-9_-]*$?""".r
private val pathRegex = """(?:/[a-zA-Z0-9._]+)+""".r
private val modeRegex = """[0-7]{3,4}""".r

def check(cond: Boolean, p: Positional, error: String): Unit = if !cond then problem(p, error)

def check(expr: ExprAST, regex: Regex, error: String, vars: VARS): Unit =
  check(matches(expr, regex, vars), expr, error)

def check(s: String, regex: Regex, error: String): Unit =
  check(matches(expr, regex, vars), expr, error)

def matches(expr: ExprAST, regex: Regex, vars: VARS): Boolean = regex.matches(eval(expr, vars))

def validate(spec: SpecAST, vars: VARS): Unit =
  spec.statements foreach {
    case DirectoryStat(dir, owner, group, mode, state) =>
      check(dir, pathRegex, "invalid path name", vars)
      check(owner, nameRegex, "invalid owner name", vars)
      check(group, nameRegex, "invalid group name", vars)
      check(mode, modeRegex, "invalid mode", vars)
      check(state, "latest|present|absent".r, "invalid directory state")
    case CopyStat(src, dst, owner, group, mode) =>
  }
