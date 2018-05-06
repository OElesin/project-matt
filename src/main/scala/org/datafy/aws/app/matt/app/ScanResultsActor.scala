package org.datafy.aws.app.matt.app

import akka.actor.{Actor, ActorLogging, Props}

class ScanResultsActor extends Actor with ActorLogging {
  import ScanResultsActor._
  def receive = {
    case ScanRequestActor.ScanRequestMessage(fullScanResults) =>
      // save results here and send summary report
      println(fullScanResults)
      log.info("Number of objects Scanned {}\n" +
        "Total number of bytes scanned {}\n" +
        "Estimated AWS Services Cost:\n" +
        s"\t\t AWS S3: USD {}\n ")
      // notifier ScanRequestActor when done shutdown system
      sender() ! ScanResultsMessage("Done ")
  }
}

object ScanResultsActor {
  val props = Props[ScanResultsActor]
  case object Initialize
  case class ScanResultsMessage(text: String)
}
