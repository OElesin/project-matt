package org.datafy.aws.app.matt.app

import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorInitializationException
import org.elasticsearch.client.transport.NoNodeAvailableException

/**
  * @author olalekanelesin
  */
class AppSupervisor extends Actor {
  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: NoNodeAvailableException => Restart
      case _: NullPointerException     => Restart
      case _: IllegalArgumentException => Restart
      case _: NoSuchElementException   => Restart
      case _: ActorInitializationException => Restart
      case _: Exception                => Escalate
    }

  def receive = {
    case p: Props => sender() ! context.actorOf(p)
  }
}