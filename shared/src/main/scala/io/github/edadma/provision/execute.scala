package io.github.edadma.provision

import scala.collection.mutable

def execute(spec: SpecAST, impl: SSH): Unit =
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
    case PackageStat(pkgs, state) =>
      impl.sudo(s"apt -y install ${pkgs map (p => eval(p, vars)) mkString " "}")
    case Update =>
      impl.sudo("apt update")
    case Upgrade =>
      impl.sudo("apt upgrade")
    case Autoclean =>
      impl.sudo("apt autoclean")
    case Autoremove =>
      impl.sudo("apt autoremove")
    case CommandStat(command) =>
      if impl.exec(eval(command, vars)) != 0 then sys.exit(1)
  }
