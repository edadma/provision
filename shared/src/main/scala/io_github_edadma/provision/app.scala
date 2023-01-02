package io_github_edadma.provision

import scopt.OParser

import java.nio.file.{Files, Paths}

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
  val loginRegex = """([a-zA-Z0-9._-]+):([^@]+)@([a-zA-Z0-9._-]+)""".r

  OParser.parse(parser, args, Config()) match {
    case Some(Config(None, _) | Config(_, None)) => println(OParser.usage(parser))
    case Some(Config(Some(loginRegex(username, password, host)), Some(script))) =>
      try
        val input = new String(Files.readAllBytes(Paths.get(script)))
        val spec = SpecParser.parseSpec(input)

        validate(spec)
        impl.init()

        val session = impl.session(username, password, host)

        execute(spec, session)
        session.shutdown(0)
      catch case e: Exception => Console.err.println(e)
    case _ => println(OParser.usage(parser))
  }
