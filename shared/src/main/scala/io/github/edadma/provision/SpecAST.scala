package io.github.edadma.provision

import jdk.jshell.StatementSnippet

import scala.util.parsing.input.Position

case class SpecAST(statements: Seq[StatAST])

trait StatAST
case class PackageStat(pkg: Seq[Ident], state: Option[String]) extends StatAST
case class ServiceStat(svc: Ident, state: String) extends StatAST
case class BecomeStat(user: Ident) extends StatAST
case class TaskStat(task: String) extends StatAST
case class UserStat(user: Ident, group: Seq[Ident], shell: Path, home: Path) extends StatAST
case class DirectoryStat(dir: Path, owner: Ident, group: Ident, state: String, mode: String) extends StatAST
case class DefStat(name: Ident, value: String) extends StatAST
case class DefsStat(file: Path) extends StatAST
case class CopyStat(src: Path, dst: Path) extends StatAST
case class GroupStat(group: Ident, state: String) extends StatAST

trait ExprAST extends Positioned
case class VariableExpr(s: String, pos: Position = null) extends ExprAST
