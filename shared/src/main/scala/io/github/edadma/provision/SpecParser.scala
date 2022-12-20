package io.github.edadma.provision

import scala.language.postfixOps
import scala.util.matching.Regex
import scala.util.parsing.input.{CharSequenceReader, Position, Positional}
import scala.util.parsing.combinator.{ImplicitConversions, RegexParsers}

object SpecParser extends RegexParsers with ImplicitConversions:

  override protected val whiteSpace: Regex = """(\s|//.*)+""".r

  def pos: Parser[Position] = positioned(success(new Positional {})) ^^ (_.pos)

  def kw(s: String): Regex = s"(?i)$s\\b".r

  def integer: Parser[String] = "[0-9]+".r

  def variableExpr: Parser[VariableExpr] = positioned("$" ~> """[a-zA-Z_$][a-zA-Z0-9_$]*""".r ^^ VariableExpr.apply)

  def line: Parser[String] = """.*""".r

  def string: Parser[String] = """[^\s]+""".r

  def stringExpr: Parser[StringExpr] = positioned(string ^^ StringExpr.apply)

  def expr: Parser[ExprAST] = variableExpr | stringExpr

  def spec: Parser[SpecAST] = rep(statement) ^^ SpecAST.apply

  def exprs: Parser[Seq[ExprAST]] = rep1sep(expr, ",")

  def statement: Parser[StatAST] =
    kw("task") ~> line ^^ TaskStat.apply
      | kw("package") ~> exprs ~ opt(kw("latest") | kw("installed")) ^^ PackageStat.apply
      | kw("service") ~> expr ~ kw("started") ^^ ServiceStat.apply
      | kw("become") ~> expr ^^ BecomeStat.apply
      | kw("user") ~> expr ~ (kw("group") ~> exprs) ~ (kw("shell") ~> expr) ~
      (kw("home") ~> expr) ^^ UserStat.apply
      | kw("dir") ~> expr ~ (kw("owner") ~> expr) ~ (kw("group") ~> expr) ~ (kw("state") ~> kw("present")) ~
      (kw("mode") ~> """[0-7]{3,4}""".r) ^^ DirectoryStat.apply
      | kw("def") ~> string ~ line ^^ DefStat.apply
      | kw("defs") ~> expr ^^ DefsStat.apply
      | kw("copy") ~> expr ~ expr ^^ CopyStat.apply
      | kw("group") ~> expr ~ kw("present") ^^ GroupStat.apply

  def parseSpec(src: String): SpecAST =
    parseAll(spec, new CharSequenceReader(src)) match
      case Success(tree, _)     => tree
      case Failure(error, rest) => problem(rest.pos, error)
      case Error(error, rest)   => problem(rest.pos, error)
