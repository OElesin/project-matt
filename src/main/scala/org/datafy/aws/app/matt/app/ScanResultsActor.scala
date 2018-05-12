package org.datafy.aws.app.matt.app

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import akka.event.Logging

class ScanResultsActor extends Actor {

  val log = Logging(context.system, this)

  def receive = {
    case text =>
      // save results here and send summary report
      log.info(s"New message from executing actor - ScanRequestActor: ${text}")
      sender() ! PoisonPill
      self ! PoisonPill
  }
}

object ScanResultsActor {
  val props = Props[ScanResultsActor]
  case object Initialize
  case class ScanResultsMessage(text: String)
}
