package io_github_edadma.provision

import pprint.pprintln

import scala.collection.mutable

@main def run(args: String*): Unit = app(NativeSSH$Session, args)
