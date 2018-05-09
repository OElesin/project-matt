package org.datafy.aws.app.matt.app

import akka.actor.{Actor, ActorLogging, Props}
import org.datafy.aws.app.matt.classifiers.BaseClassifier
import org.datafy.aws.app.matt.models.FullScanStats

class ScanRequestActor(val bucketName: String = null, val s3Prefix: Option[String] = None)
  extends Actor with ActorLogging  {

  import ScanRequestActor._

  if (bucketName == null) {
    throw new NullPointerException(s"Constructor AWS S3 BucketName cannot be null or empty")
    context.system.terminate()
  }

  val scanResultsActor = context.actorOf(ScanResultsActor.props, "scanResultsActor")

  def receive = {
    case Initialize =>
      log.info(s"Initialied S3 Scan Request on " +
        s"Bucket: ${bucketName} and Prefix: ${s3Prefix.orNull} ")
      // do bucket scanning here and send message to ScanResultsActor
      val scanRequestMessage = BaseClassifier.setS3ScanInputPath(bucketName, s3Prefix.orNull)
      scanResultsActor ! ScanRequestMessage(scanRequestMessage)
    case ScanResultsActor.ScanResultsMessage(text) =>
      log.info("In ScanResultsActor - received message: {}", text)
      context.system.terminate()
  }
}

object ScanRequestActor {
  val props: Props = Props[ScanRequestActor]
  case object Initialize
  case class ScanRequestMessage(fullScanStats: FullScanStats)
}
