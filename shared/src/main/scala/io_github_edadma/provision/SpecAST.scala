package io_github_edadma.provision

import jdk.jshell.StatementSnippet

import scala.util.parsing.input.{Position, Positional}

case class SpecAST(statements: Seq[StatAST])

trait StatAST
case class PackageStat(pkgs: Seq[ExprAST], state: Option[ExprAST]) extends StatAST
case class ServiceStat(service: ExprAST, state: ExprAST) extends StatAST
case class BecomeStat(user: ExprAST) extends StatAST
case class CommandStat(command: ExprAST) extends StatAST
case class TaskStat(task: String) extends StatAST
case class UserStat(user: ExprAST, group: Seq[ExprAST], shell: ExprAST, home: ExprAST) extends StatAST
case class DirectoryStat(dir: ExprAST, owner: ExprAST, group: ExprAST, mode: ExprAST, state: ExprAST) extends StatAST
case class DefStat(name: ExprAST, value: String) extends StatAST
case class DefsStat(file: ExprAST) extends StatAST
case class CopyStat(src: ExprAST, dst: ExprAST, owner: ExprAST, group: ExprAST, mode: ExprAST) extends StatAST
case class GroupStat(group: ExprAST, state: ExprAST) extends StatAST
case class DebStat(deb: ExprAST) extends StatAST
case object Autoclean extends StatAST
case object Autoremove extends StatAST
case object Update extends StatAST
case object Upgrade extends StatAST
case class HostsStat(hosts: Seq[ExprAST]) extends StatAST

trait ExprAST extends Positional
case class VariableExpr(name: String) extends ExprAST
case class StringExpr(s: String) extends ExprAST
