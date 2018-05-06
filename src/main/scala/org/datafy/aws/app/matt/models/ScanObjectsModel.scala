package org.datafy.aws.app.matt.models

import java.time.{ZoneId, ZonedDateTime}

import org.datafy.aws.app.matt.extras.{RedisWrapper, S3KeySummary, ElasticWrapper}

abstract class ScanStats {
  val scannedDate: ZonedDateTime =  ZonedDateTime.now()
}

case class ObjectScanStats (
  s3Key: String,
  objectSummaryStats: Seq[(String, Int)],
  classifier: Option[String] = None
) extends ScanStats

case class FullScanStats (
  s3Bucket: String,
  lastScannedKey: String,
  summaryStats: Seq[(String, Int)],
  objectScanStats: Seq[ObjectScanStats]
) extends ScanStats

object ScanObjectsModel {

  def saveScannedResults(scanStats: FullScanStats) = {
    val response = ElasticWrapper.saveDocument(scanStats)
    response.id
  }

  def getLastScannedFromRedis(key: String) = {
    val lastScannedKey = RedisWrapper.getData(key)
    lastScannedKey
  }

  def saveLastScannedToRedis(key: String, s3ObjectSummary: List[S3KeySummary]) = {
    val check = RedisWrapper.checkSet(key)
    // save new last scan key from redis
    if(check) RedisWrapper.getData(key)
    else
      RedisWrapper.setData(key, s3ObjectSummary.last.key)
      s3ObjectSummary.last.key
  }

}
