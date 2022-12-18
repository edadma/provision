package io.github.edadma.provision

case class SpecAST(statements: Seq[StatementAST])

trait StatementAST

case class PackageStatement(pkg: Ident) extends StatementAST
