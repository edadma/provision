package io_github_edadma.provision

def go(impl: SSH): Unit =
  val spec = SpecParser.parseSpec("""
                                    |task create file
                                    |file asdf dest afile
                                    |""".stripMargin)

  validate(spec)
  impl.username = "testuser"
  impl.password = "easypassword"
  impl.init("127.0.0.1")
  execute(spec, impl)
  impl.shutdown(0)
