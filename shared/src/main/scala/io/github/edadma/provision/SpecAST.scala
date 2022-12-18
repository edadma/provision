package io.github.edadma.provision

import jdk.jshell.StatementSnippet

import scala.util.parsing.input.Position

case class SpecAST(statements: Seq[StatementAST])

trait StatementAST

case class PackageStatement(pkg: Ident, state: Option[String]) extends StatementAST
case class ServiceStatement(svc: Ident, state: String) extends StatementAST
case class BecomeStatement(user: Ident) extends StatementAST
case class TaskStatement(task: String) extends StatementAST
case class UserStatement(user: Ident, group: List[Ident], shell: Path, home: Path) extends StatementAST
case class DirectoryStatement(dir: Path, owner: Ident, group: Ident, state: String, mode: String) extends StatementAST
case class DefStatement(name: Ident, value: String) extends StatementAST
case class DefsStatement(file: Path) extends StatementAST
case class CopyStatement(src: Path, dst: Path) extends StatementAST

case class Ident(s: String, pos: Position = null)
case class Path(p: String, pos: Position = null)
