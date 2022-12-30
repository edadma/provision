package io_github_edadma.provision

import io_github_edadma.libssh2.*

import scala.collection.immutable.ArraySeq

object NativeSSH extends SSH:
  var session: Session = new Session(null)
  var sock: Int = 0
  var rc: Int = 0

  def init(hostname: String): Unit =
    rc = io_github_edadma.libssh2.init(0)

    if rc != 0 then
      Console.err.println(s"libssh2 initialization failed ($rc)")
      sys.exit(1)

    sock = connectPort22(hostname) match
      case -1 =>
        Console.err.println("failed to connect!")
        sys.exit(1)
      case s => s

    session = sessionInit

    if session.isNull then
      Console.err.println("failed to initialize a session")
      sys.exit(1)

    session.setBlocking(true)

    while ({
      rc = session.handshake(sock); rc
    } == LIBSSH2_ERROR_EAGAIN) {}

    if rc != 0 then
      Console.err.println(s"Failure establishing SSH session: $rc")
      sys.exit(1)

    val nh = session.knownHostInit

    if nh.isNull then
      Console.err.println("failed to knownhost init")
      sys.exit(1)

    nh.readFile("known_hosts", KnownHostFile.OPENSSH)
    nh.writeFile("dumpfile", KnownHostFile.OPENSSH)

    val (fingerprint, _) =
      session.hostKey getOrElse {
        Console.err.println("hostKey() failed")
        sys.exit(1)
      }

    val (check, host) = nh.checkp(
      hostname,
      22,
      fingerprint,
      LIBSSH2_KNOWNHOST_TYPE_PLAIN | LIBSSH2_KNOWNHOST_KEYENC_RAW,
    )

    Console.err.println(
      s"Host check: $check, key: ${if check <= LIBSSH2_KNOWNHOST_CHECK_MISMATCH then host.key else "<none>"}",
    )
    nh.free()

    if password.nonEmpty then
      while {
          rc = session.userAuthPassword(username, password); rc
        } == LIBSSH2_ERROR_EAGAIN
      do {}
      if rc != 0 then
        Console.err.println("Authentication by password failed")
        shutdown(1)
    else
      while {
          rc = session.userauthPublickeyFromFile(
            username,
            s"/home/$username/.ssh/id_rsa.pub",
            s"/home/$username/.ssh/id_rsa",
            password,
          ); rc
        } == LIBSSH2_ERROR_EAGAIN
      do {}

    if rc != 0 then
      Console.err.println("Authentication by public key failed")
      shutdown(1)
  end init

  def exec(commandline: String): Int =
    var channel: Channel = new Channel(null)

    while {
        channel = session.openSession(); channel
      }.isNull && session.lastError._1 == LIBSSH2_ERROR_EAGAIN
    do session.waitsocket(sock)

    if channel.isNull then
      Console.err.println("Channel could not be opened")
      shutdown(1)

    while { rc = channel.exec(commandline); rc } == LIBSSH2_ERROR_EAGAIN do session.waitsocket(sock)

    if rc != 0 then
      Console.err.println("Command could not be executed")
      shutdown(1)

    Console.err.println(new String(channel.read().get.toArray))

    var exitcode = 127

    while {
        rc = channel.close; rc
      } == LIBSSH2_ERROR_EAGAIN
    do session.waitsocket(sock)

    val exitsignal: String =
      if rc == 0 then
        exitcode = channel.getExitStatus
        channel.getExitSignal._2
      else null

    if exitsignal ne null then Console.err.println(s"Got signal: $exitsignal")
    else if exitcode != 0 then Console.err.println(s"EXIT: $exitcode")

    channel.free
    exitcode
  end exec

  def shutdown(status: Int): Nothing =
    session.disconnect("Normal Shutdown, Thank you for playing")
    session.free()
    scala.scalanative.posix.unistd.close(sock)
    exit()
    sys.exit(status)
  end shutdown

  def read(path: String): ArraySeq[Byte] =
    val (channel, size) = session.scpRecv2(path)

    if channel.isNull then
      val (err, errmsg) = session.lastError

      Console.err.println(s"Unable to open a session: ($err) $errmsg")
      shutdown(1)

    Console.err.println("SCP session receiving file")

    val data = channel.read(size) getOrElse {
      Console.err.println(s"Error reading data: $rc")
      shutdown(1)
    }

    channel.free
    data
  end read

  def write(path: String, perm: Int, data: Seq[Byte]): Unit =
    val channel = session.scpSend(path, perm, data.length)

    if channel.isNull then
      val (err, errmsg) = session.lastError

      Console.err.println(s"Unable to open a session: ($err) $errmsg")
      shutdown(1)

    Console.err.println("SCP session waiting to send file")
    rc = channel.write(data)

    if rc < 0 then
      Console.err.println(s"Error writing data: $rc")
      shutdown(1)

    Console.err.println("Sending EOF")
    channel.sendEof
    Console.err.println("Waiting for EOF")
    channel.waitEof
    Console.err.println("Waiting for channel to close")
    channel.waitClosed
    channel.free
  end write
end NativeSSH
