package io_github_edadma.provision

import scopt.OParser

def app(impl: SSH, args: Seq[String]): Unit =
  case class Config(
      login: Option[String] = None,
      script: Option[String] = None,
  )

  val builder = OParser.builder[Config]
  val parser =
    import builder._

    OParser.sequence(
      programName("provision"),
      head("Provision", "v0.0.1"),
      opt[Option[String]]('l', "login")
        .valueName("<user>:<password@ip")
        .optional()
        .action((l, c) => c.copy(login = l)),
      // .text(""),
      help('h', "help").text("prints this usage text"),
      version('v', "version").text("prints the version"),
      arg[Option[String]]("<path>")
        .optional()
        .action((s, c) => c.copy(script = s))
        .text(s"script file"),
    )
  val loginRegex = """([a-zA-Z0-9._-]+):([^@]+)@([a-zA-Z0-9._-]+))"""

  OParser.parse(parser, args, Config()) match {
    case Some(Config(None, _) | Config(_, None)) => println(OParser.usage(parser))
    case Some(conf) =>
      val spec = SpecParser.parseSpec("""
          |>> create directory
          |directory asdf
          |""".stripMargin)

      validate(spec)
      impl.init()

      val session = impl.session("testuser", "easypassword", "127.0.0.1")

      execute(spec, session)
      session.shutdown(0)
    case _ =>
  }
