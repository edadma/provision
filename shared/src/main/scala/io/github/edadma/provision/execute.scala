package io.github.edadma.provision

import scala.collection.mutable

def execute(spec: SpecAST): Unit =
  val vars = new mutable.HashMap[String, String]

  spec.statements foreach {
    case DirectoryStat(dir, owner, group, mode, state) =>
    case CopyStat(src, dst, owner, group, mode)        =>
    case BecomeStat(user)                              =>
    case TaskStat(task)                                =>
    case DefStat(name, value) =>
      vars(eval(name, vars)) = value
    case UserStat(user, group, shell, home) =>
    case DebStat(deb)                       =>
    case DefsStat(file)                     =>
    case HostsStat(hosts)                   =>
    case GroupStat(group, state)            =>
    case ServiceStat(service, state)        =>
    case PackageStat(pkgs, state)           =>
  }
