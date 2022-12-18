package io.github.edadma.provision

import scala.util.parsing.input.Position

def problem(pos: Position, msg: String): Nothing = {
  printError(pos, msg)
  sys.error("error parsing/validating provisioning spec")
}

def printError(pos: Position, msg: String): Null = {
  if (pos eq null)
    Console.err.println(msg)
  else if (pos.line == 1)
    Console.err.println(s"$msg\n${pos.longString}")
  else
    Console.err.println(s"${pos.line}: $msg\n${pos.longString}")
  //      printError(pos.line, pos.col, msg, input)

  null
}
