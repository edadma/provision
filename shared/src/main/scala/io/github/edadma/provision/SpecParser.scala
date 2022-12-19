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

  def variable: Parser[Ident] =
    pos ~ ("$" ~> """[a-zA-Z_$][a-zA-Z0-9_$]*""".r) ^^ { case p ~ s =>
      Ident(s, p)
    }

  def line: Parser[String] = """.*""".r

  def pathlit: Parser[Path] = pos ~ """^[\s]+""".r ^^ { case p ~ s =>
    Path(s, p)
  }

  def spec: Parser[SpecAST] = rep(statement) ^^ SpecAST.apply

  def idents: Parser[Seq[Ident]] = rep1sep(ident, ",")

  def statement: Parser[StatAST] =
    kw("task") ~> line ^^ TaskStat.apply
      | kw("package") ~> idents ~ opt(kw("latest") | kw("installed")) ^^ PackageStat.apply
      | kw("service") ~> ident ~ kw("started") ^^ ServiceStat.apply
      | kw("become") ~> ident ^^ BecomeStat.apply
      | kw("user") ~> ident ~ (kw("group") ~> idents) ~ (kw("shell") ~> path) ~
      (kw("home") ~> path) ^^ UserStat.apply
      | kw("dir") ~> path ~ (kw("owner") ~> ident) ~ (kw("group") ~> ident) ~ (kw("state") ~> kw("present")) ~
      (kw("mode") ~> """[0-7]{3,4}""".r) ^^ DirectoryStat.apply
      | kw("def") ~> ident ~ line ^^ DefStat.apply
      | kw("defs") ~> path ^^ DefsStat.apply
      | kw("copy") ~> path ~ path ^^ CopyStat.apply
      | kw("group") ~> ident ~ kw("present") ^^ GroupStat.apply

  def parseSpec(src: String): SpecAST =
    parseAll(spec, new CharSequenceReader(src)) match
      case Success(tree, _)       => tree
      case NoSuccess(error, rest) => problem(rest.pos, error)
