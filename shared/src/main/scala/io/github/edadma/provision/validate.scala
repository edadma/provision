package io.github.edadma.provision

import scala.collection.mutable
import scala.util.matching.Regex
import scala.util.parsing.input.Positional

private val nameRegex = """[a-z_][a-z0-9_-]*$?""".r
private val pathRegex = """(?:/[a-zA-Z0-9._]+)+""".r
private val modeRegex = """[0-7]{3,4}""".r
private val nonEmptyStringRegex = """.+""".r

def check(cond: Boolean, p: Positional, error: String): Unit = if !cond then problem(p, error)

def check(expr: ExprAST, regex: Regex, error: String, vars: Vars): Unit =
  check(matches(expr, regex, vars), expr, error)

def check(expr: Option[ExprAST], regex: Regex, error: String, vars: Vars): Unit =
  expr foreach { e => check(e, regex, error, vars) }

def matches(expr: ExprAST, regex: Regex, vars: Vars): Boolean = regex.matches(eval(expr, vars))

def validate(spec: SpecAST): Unit =
  val vars = new mutable.HashMap[String, String]

  spec.statements foreach {
    case DirectoryStat(dir, owner, group, mode, state) =>
      check(dir, pathRegex, "invalid directory path", vars)
      check(owner, nameRegex, "invalid owner name", vars)
      check(group, nameRegex, "invalid group name", vars)
      check(mode, modeRegex, "invalid mode", vars)
      check(state, "present|absent".r, "invalid directory state", vars)
    case CopyStat(src, dst, owner, group, mode) =>
      check(src, pathRegex, "invalid source path", vars)
      // todo: url
      check(owner, nameRegex, "invalid owner name", vars)
      check(group, nameRegex, "invalid group name", vars)
      check(mode, modeRegex, "invalid mode", vars)
    case BecomeStat(user) =>
      check(user, nameRegex, "invalid user name", vars)
    case TaskStat(task) =>
    case DefStat(name, value) =>
      check(!(vars contains eval(name, vars)), name, "duplicate definition")
      vars(eval(name, vars)) = value
    case UserStat(user, group, shell, home) =>
      check(user, nameRegex, "invalid user name", vars)
      group foreach { g => check(g, nameRegex, "invalid group name", vars) }
      check(shell, pathRegex, "invalid shell path", vars)
      check(home, pathRegex, "invalid home path", vars)
    case DebStat(deb) =>
    // todo: url
    case DefsStat(file) =>
      check(file, pathRegex, "invalid file path", vars)
    // todo: check existance
    case HostsStat(hosts) => // todo: check domain name
    case GroupStat(group, state) =>
      check(group, nameRegex, "invalid group name", vars)
      check(state, "present|absent".r, "invalid group state", vars)
    case ServiceStat(service, state) =>
      check(service, nameRegex, "invalid service name", vars)
      check(state, "started".r, "invalid service state", vars)
    case PackageStat(pkgs, state) =>
      pkgs foreach { p => check(p, nameRegex, "invalid package name", vars) }
      check(state, "latest|present|absent".r, "invalid package state", vars)
  }
