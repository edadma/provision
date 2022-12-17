package io.github.edadma.provision

import scala.language.postfixOps
import scala.util.matching.Regex
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.{CharSequenceReader, Position, Positional}

class PSLParser extends RegexParsers:

  override protected val whiteSpace: Regex = """(\s|//.*)+""".r

  def pos: Parser[Position] = positioned(success(new Positional {})) ^^ (_.pos)

  def kw(s: String): Regex = s"(?i)$s\\b".r

  def integer: Parser[String] = "[0-9]+".r

  def ident: Parser[Ident] =
    pos ~ """[a-zA-Z_$][a-zA-Z0-9_$]*""".r ^^ { case p ~ s =>
      Ident(s, p)
    }

  def parsePS(src: String): DMLModel =
    parseAll(model, new CharSequenceReader(src)) match {
      case Success(tree, _)       => tree
      case NoSuccess(error, rest) => problem(rest.pos, error, src)
    }
