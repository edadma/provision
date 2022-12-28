package io.github.edadma.provision

import scala.collection.mutable

def execute(spec: SpecAST): Unit =
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
      println("apt update")
    case Upgrade =>
      println("apt upgrade")
    case Autoclean =>
      println("apt autoclean")
    case Autoremove =>
      println("apt autoremove")
    case CommandStat(command) =>
  }
