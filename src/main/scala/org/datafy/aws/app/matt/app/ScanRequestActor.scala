package org.datafy.aws.app.matt.app

import akka.actor.{Actor, Props, PoisonPill}
import org.datafy.aws.app.matt.classifiers.BaseClassifier
import org.datafy.aws.app.matt.models.FullScanStats
import akka.event.Logging

class ScanRequestActor(val bucketName: String = null, val s3Prefix: Option[String] = None)
  extends Actor  {

  val log = Logging(context.system, this)

  var execCount = 1

  import ScanRequestActor._

  if (bucketName == null) {
    throw new NullPointerException(s"Constructor AWS S3 BucketName cannot be null or empty")
    context.system.terminate()
  }

  val scanResultsActor = context.actorOf(ScanResultsActor.props, "scanResultsActor")

  def receive = {
    case Initialize =>
      if(execCount > 0) {
        execCount -= 1
        log.info(s"Initialied S3 Scan Request on " +
          s"Bucket: ${bucketName} and Prefix: ${s3Prefix.orNull} ")
        // do bucket scanning here and send message to ScanResultsActor
        val scanRequestMessage = BaseClassifier.setS3ScanInputPath(bucketName, s3Prefix.orNull)
        self ! ScanResultsActor.ScanResultsMessage("Done")
      } else {
        self ! PoisonPill
      }
  }
}

object ScanRequestActor {
  val props: Props = Props[ScanRequestActor]
  case object Initialize
  case class ScanRequestMessage(fullScanStats: FullScanStats)
}
