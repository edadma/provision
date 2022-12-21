package io.github.edadma.provision

import scala.util.matching.Regex
import scala.util.parsing.input.Positional

private val nameRegex = """[a-z_][a-z0-9_-]*$?""".r
private val pathRegex = """(?:/[a-zA-Z0-9._]+)+""".r
private val modeRegex = """[0-7]{3,4}""".r
private val nonEmptyStringRegex = """.+""".r

def check(cond: Boolean, p: Positional, error: String): Unit = if !cond then problem(p, error)

def check(expr: ExprAST, regex: Regex, error: String, vars: VARS): Unit =
  check(matches(expr, regex, vars), expr, error)

def matches(expr: ExprAST, regex: Regex, vars: VARS): Boolean = regex.matches(eval(expr, vars))

def validate(spec: SpecAST, vars: VARS): Unit =
  spec.statements foreach {
    case DirectoryStat(dir, owner, group, mode, state) =>
      check(dir, pathRegex, "invalid path name", vars)
      check(owner, nameRegex, "invalid owner name", vars)
      check(group, nameRegex, "invalid group name", vars)
      check(mode, modeRegex, "invalid mode", vars)
      check(state, "latest|present|absent".r, "invalid directory state", vars)
    case CopyStat(src, dst, owner, group, mode) =>
      check(src, pathRegex, "invalid source path name", vars)
      check(dst, pathRegex, "invalid destination path name", vars)
      check(owner, nameRegex, "invalid owner name", vars)
      check(group, nameRegex, "invalid group name", vars)
      check(mode, modeRegex, "invalid mode", vars)
    case BecomeStat(user) =>
      check(user, nameRegex, "invalid user name", vars)
    case TaskStat(task) =>
    case DefStat(name, value) =>
      check(!(vars contains eval(name, vars)), name, "duplicate definition")
  }
