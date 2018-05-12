package org.datafy.aws.app.matt.models

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import org.datafy.aws.app.matt.extras.{ElasticWrapper, RedisWrapper, S3KeySummary}
import org.slf4j.LoggerFactory
import io.circe._
import io.circe.generic.semiauto._
import io.circe.generic.auto._
import io.circe.syntax._

case class RiskStats(piiColumn: String, value: Int)

case class ObjectScanStats (
  s3Key: String,
  objectSummaryStats: List[RiskStats],
  classifier: String = "",
  scannedDate: Long =  System.currentTimeMillis()
)

case class FullScanStats (
  s3Bucket: String,
  lastScannedKey: String,
  summaryStats: List[RiskStats],
  objectScanStats: List[ObjectScanStats],
  classifier: String = "",
  scannedDate: Long =  System.currentTimeMillis(),
  totalObjectsSize: Option[Int] = None
)

object ScanObjectsModel {

  val logger = LoggerFactory.getLogger("ScanObjectsModel")

  def saveScannedResults(scanStats: FullScanStats) = {
    logger.info("attempting some saving here")

    val response = ElasticWrapper.saveDocument(scanStats)
    logger.info("Payload saved: some saving here")
    response
  }

  def getLastScannedFromRedis(key: String) = {
    val lastScannedKey = RedisWrapper.getData(key)
    lastScannedKey
  }

  def saveLastScannedToRedis(key: String, s3ObjectSummary: List[S3KeySummary]) = {
    RedisWrapper.setData(key, s3ObjectSummary.last.key)
    logger.info(s"Cached last scanned key: ${s3ObjectSummary.last.key} to Redis Server")
    s3ObjectSummary.last.key
  }

}
