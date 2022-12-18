package io.github.edadma.provision

import scala.language.postfixOps
import scala.util.matching.Regex
import scala.util.parsing.input.{CharSequenceReader, Position, Positional}
import scala.util.parsing.combinator.{ImplicitConversions, RegexParsers}

class SpecParser extends RegexParsers with ImplicitConversions:

  override protected val whiteSpace: Regex = """(\s|//.*)+""".r

  def pos: Parser[Position] = positioned(success(new Positional {})) ^^ (_.pos)

  def kw(s: String): Regex = s"(?i)$s\\b".r

  def integer: Parser[String] = "[0-9]+".r

  def ident: Parser[Ident] =
    pos ~ """[a-zA-Z_$][a-zA-Z0-9_$]*""".r ^^ { case p ~ s =>
      Ident(s, p)
    }

  def spec: Parser[SpecAST] = rep(statement) ^^ SpecAST.apply

  def statement: Parser[StatementAST] =
    kw("package") ~> ident ^^ PackageStatement.apply

  def parseSpec(src: String): SpecAST =
    parseAll(spec, new CharSequenceReader(src)) match
      case Success(tree, _)       => tree
      case NoSuccess(error, rest) => problem(rest.pos, error)
