package io.github.edadma.provision

import io.github.edadma.libssh2
import io.github.edadma.libssh2.Session

object Native extends SSH:
  var session: libssh2.Session = new Session(null)
  var sock: Int = 0
  var rc: Int = 0

  def init(hostname: String, username: String, password: String): Unit =
    rc = libssh2.init(0)

    if rc != 0 then
      Console.err.println(s"libssh2 initialization failed ($rc)")
      sys.exit(1)

    sock = libssh2.connectPort22(hostname) match
      case -1 =>
        Console.err.println("failed to connect!")
        sys.exit(1)
      case s => s

    session = libssh2.sessionInit

  def shutdown(status: Int): Unit =
    session.disconnect("Normal Shutdown, Thank you for playing")
    session.free()
    scala.scalanative.posix.unistd.close(sock)
    Console.err.println("All done")
    libssh2.exit()
    sys.exit(status)
