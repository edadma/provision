package io.github.edadma.provision

import scala.collection.mutable

type Vars = mutable.HashMap[String, String]

def eval(expr: ExprAST, vars: Vars): String =
  expr match
    case StringExpr(s) => s
    case VariableExpr(name) =>
      vars get name match
        case Some(value) => value
        case None        => problem(expr, s"variable '$name' not found")
