package io.github.edadma.provision

import scala.util.parsing.input.{Position, Positional}

def problem(p: Positional, msg: String): Nothing = problem(p.pos, msg)

def problem(pos: Position, msg: String): Nothing =
  if (pos eq null) Console.err.println(msg)
  else if (pos.line == 1) Console.err.println(s"$msg\n${pos.longString}")
  else Console.err.println(s"${pos.line}: $msg\n${pos.longString}")

  sys.exit(1)
