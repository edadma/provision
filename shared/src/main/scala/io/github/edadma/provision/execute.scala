package io_github_edadma.provision

import scala.collection.mutable

def execute(spec: SpecAST, impl: SSH): Unit =
  val vars = new mutable.HashMap[String, String]

  def ev(e: ExprAST) = eval(e, vars)

  spec.statements foreach {
    case DirectoryStat(dir, owner, group, mode, state) =>
      println(s"mkdir -m ${ev(mode)} ${ev(dir)}")
    case CopyStat(src, dst, owner, group, mode) =>
    case BecomeStat(user)                       =>
      // todo: check user against /etc/passwd
      impl.username = ev(user)
    case TaskStat(task) =>
      println(s"${Console.BOLD}${Console.UNDERLINED}   $task   ${Console.RESET}")
    case DefStat(name, value) =>
      vars(ev(name)) = value
    case UserStat(user, group, shell, home) =>
    case DebStat(deb)                       =>
    case DefsStat(file)                     =>
    case HostsStat(hosts)                   =>
    case GroupStat(group, state)            =>
    case ServiceStat(service, state)        =>
    case PackageStat(pkgs, state) =>
      impl.sudo(s"apt -y install ${pkgs map ev mkString " "}")
    case Update =>
      impl.sudo("apt update")
    case Upgrade =>
      impl.sudo("apt upgrade")
    case Autoclean =>
      impl.sudo("apt autoclean")
    case Autoremove =>
      impl.sudo("apt autoremove")
    case CommandStat(command) =>
      if impl.exec(ev(command)) != 0 then sys.exit(1)
  }
