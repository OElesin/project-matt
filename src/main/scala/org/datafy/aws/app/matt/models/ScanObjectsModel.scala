package org.datafy.aws.app.matt.models

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import org.datafy.aws.app.matt.extras.{ElasticWrapper, RedisWrapper, S3KeySummary}
import org.slf4j.LoggerFactory
import io.circe._
import io.circe.generic.semiauto._
import io.circe.generic.auto._
import io.circe.syntax._

case class ObjectScanStats (
  s3Key: String,
  objectSummaryStats: List[Map[String, String]],
  classifier: String = "",
  scannedDate: String =  ZonedDateTime.now.
    format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"))
)

case class FullScanStats (
  s3Bucket: String,
  lastScannedKey: String,
  summaryStats: List[Map[String, String]],
  objectScanStats: List[ObjectScanStats],
  classifier: String = "",
  scannedDate: String =  ZonedDateTime.now.
    format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")),
  totalObjectsSize: Option[Int] = None
)

object ScanObjectsModel {

  val logger = LoggerFactory.getLogger("ScanObjectsModel")

  def saveScannedResults(scanStats: FullScanStats) = {
    logger.debug("attempting some saving here")

    val response = ElasticWrapper.saveDocument(scanStats)
    logger.debug("Payload saved: some saving here")
    response
  }

  def getLastScannedFromRedis(key: String) = {
    val lastScannedKey = RedisWrapper.getData(key)
    logger.info(s"Last Scanned Key: ${lastScannedKey}")
    lastScannedKey
  }

  def saveLastScannedToRedis(key: String, s3ObjectSummary: List[S3KeySummary]) = {
    RedisWrapper.setData(key, s3ObjectSummary.last.key)
    logger.info(s"Cached last scanned key: ${s3ObjectSummary.last.key} to Redis Server")
    s3ObjectSummary.last.key
  }

}
