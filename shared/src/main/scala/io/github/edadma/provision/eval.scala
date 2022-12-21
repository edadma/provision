package io.github.edadma.provision

import scala.collection.mutable

def eval(expr: ExprAST, vars: mutable.HashMap[String, String]): String =
  expr match
    case StringExpr(s) => s
    case VariableExpr(name) =>
      vars get name match
        case Some(value) => value
        case None        => problem(expr, s"variable '$name' not found")
