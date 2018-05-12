package org.datafy.aws.app.matt.app

import akka.actor.{Actor, ActorLogging, Props}
import org.datafy.aws.app.matt.models.ScanObjectsModel

class ScanResultsActor extends Actor with ActorLogging {
  import ScanResultsActor._
  def receive = {
    case ScanRequestActor.ScanRequestMessage(fullScanResults) =>
      // save results here and send summary report
      log.info(s"Size of objects Scanned ${fullScanResults.totalObjectsSize.get}\n" +
        "Total number of bytes scanned 2000\n" +
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
