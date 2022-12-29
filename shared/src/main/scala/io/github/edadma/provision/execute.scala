package io.github.edadma.provision

import scala.collection.mutable

def execute(spec: SpecAST, password: String): Unit =
  val vars = new mutable.HashMap[String, String]

  spec.statements foreach {
    case DirectoryStat(dir, owner, group, mode, state) =>
      println(s"mkdir -m ${eval(mode, vars)} ${eval(dir, vars)}")
    case CopyStat(src, dst, owner, group, mode) =>
    case BecomeStat(user)                       =>
    case TaskStat(task) =>
      println(task)
    case DefStat(name, value) =>
      vars(eval(name, vars)) = value
    case UserStat(user, group, shell, home) =>
    case DebStat(deb)                       =>
    case DefsStat(file)                     =>
    case HostsStat(hosts)                   =>
    case GroupStat(group, state)            =>
    case ServiceStat(service, state)        =>
    case PackageStat(pkgs, state)           =>
    case Update =>
      Native.exec(s"echo $password | sudo -S apt update")
    case Upgrade =>
      Native.exec(s"echo $password | sudo -S apt upgrade")
    case Autoclean =>
      println("apt autoclean")
    case Autoremove =>
      println("apt autoremove")
    case CommandStat(command) =>
      if Native.exec(eval(command, vars)) != 0 then sys.exit(1)
  }
