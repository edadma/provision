package io_github_edadma.provision

import scala.language.postfixOps
import scala.util.matching.Regex
import scala.util.parsing.input.{CharSequenceReader, Position, Positional}
import scala.util.parsing.combinator.{ImplicitConversions, RegexParsers}

object SpecParser extends RegexParsers with ImplicitConversions:

  override protected val whiteSpace: Regex = """[ \t]|//.*""".r

  def pos: Parser[Position] = positioned(success(new Positional {})) ^^ (_.pos)

  def kw(s: String): Regex = s"(?i)$s\\b".r

  def integer: Parser[String] = "[0-9]+".r

  def variableExpr: Parser[VariableExpr] = "$" ~> positioned("""[a-zA-Z_$][a-zA-Z0-9_$]*""".r ^^ VariableExpr.apply)

  def line: Parser[String] = """.+""".r

  def string: Parser[String] = """[^\s]+""".r

  def stringExpr: Parser[StringExpr] = positioned(string ^^ StringExpr.apply)

  def lineExpr: Parser[StringExpr] = positioned(line ^^ StringExpr.apply)

  def expr: Parser[ExprAST] = variableExpr | stringExpr

  def eol: Parser[String] = """(\s|//.*)+""".r

  def onl: Parser[Option[String]] = eol.?

  def spec: Parser[SpecAST] = onl ~> repsep(statement, eol) <~ onl ^^ SpecAST.apply

  def exprs: Parser[Seq[ExprAST]] = rep1sep(onl ~> expr, ",")

  def statement: Parser[StatAST] =
    ">>" ~> line ^^ TaskStat.apply
      | kw("package") ~> exprs ~ opt(onl ~> stringExpr) ^^ PackageStat.apply
      | kw("service") ~> expr ~ expr ^^ ServiceStat.apply
      | kw("become") ~> expr ^^ BecomeStat.apply
      | kw("command") ~> lineExpr ^^ CommandStat.apply
      | kw("user") ~> expr ~ (onl ~> kw("group") ~> exprs) ~ (onl ~> kw("shell") ~> expr) ~
      (onl ~> kw("home") ~> expr) ^^ UserStat.apply
      | kw("directory") ~> expr ~ opt(onl ~> kw("owner") ~> expr) ~ opt(onl ~> kw("group") ~> expr) ~
      opt(onl ~> kw("mode") ~> expr) ~ opt(onl ~> kw("state") ~> expr) ^^ DirectoryStat.apply // present
      | kw("def") ~> stringExpr ~ line ^^ DefStat.apply
      | kw("defs") ~> expr ^^ DefsStat.apply
      | kw("copy") ~> expr ~ expr ~ (onl ~> kw("owner") ~> expr) ~ (onl ~> kw("group") ~> expr) ~
      (onl ~> kw("mode") ~> expr) ^^ CopyStat.apply
      | kw("file") ~> expr ~ (onl ~> kw("dest") ~> expr) ~ opt(onl ~> kw("owner") ~> expr) ~ opt(
        onl ~> kw("group") ~> expr,
      ) ~
      opt(onl ~> kw("mode") ~> expr) ^^ FileStat.apply
      | kw("group") ~> expr ~ (onl ~> kw("state") ~> expr) ^^ GroupStat.apply // present
      | kw("deb") ~> expr ^^ DebStat.apply
      | kw("autoclean") ^^^ Autoclean
      | kw("autoremove") ^^^ Autoremove
      | kw("update") ^^^ Update
      | kw("upgrade") ^^^ Upgrade
      | kw("hosts") ~> exprs ^^ HostsStat.apply
      | kw("symblink") ~> exprs ~ (onl ~> kw("target") ~> expr ^^ SymlinkStat.apply

  def parseSpec(src: String): SpecAST =
    parseAll(spec, new CharSequenceReader(src)) match
      case Success(tree, _)     => tree
      case Failure(error, rest) => problem(rest.pos, error)
      case Error(error, rest)   => problem(rest.pos, error)
