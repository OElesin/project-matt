package org.datafy.aws.app.matt.classifiers

import java.io.InputStream

import org.datafy.aws.app.matt.extras.{S3KeySummary, S3Manager, Utilities}
import org.datafy.aws.app.matt.models.{FullScanStats, ObjectScanStats, ScanObjectsModel}
import com.typesafe.scalalogging._

/**
  * @groupdesc This is the base classfier class for Matt
  * @tparam T
  */

object BaseClassifier extends LazyLogging {

  def setS3ScanInputPath(bucketName: String, s3Prefix: String) = {
    /**
      * get s3 bucket and path to scan
      */

    // get last object and cache in redis
    val redisReferenceKey = generateReferenceKey(bucketName, s3Prefix)
    val lastScannedKey = ScanObjectsModel.getLastScannedFromRedis(redisReferenceKey)
    logger.info(s"Last Scanned S3 Key: ${lastScannedKey.orNull}")

    val bucketObjects: List[S3KeySummary] = S3Manager.getBucketObjects(bucketName, s3Prefix, lastScannedKey)

    logger.info(s"Total Number of S3 Objects for scanning: ${bucketObjects.length}")

    val newLastScannedKey = ScanObjectsModel.saveLastScannedToRedis(redisReferenceKey, bucketObjects)

    val totalSizeScanned: (String, Int) = S3Manager.computeTotalObjectSize(bucketObjects).head
    logger.info(s"Total size of scanned objects: ${totalSizeScanned._2}")
    // commence object scan here
    val payloadSummary = bucketObjects.map {
      s3Object =>
        val s3ObjectInputStream = S3Manager.getObjectContentAsStream(s3Object.bucketName, s3Object.key)
        val textContent = this.scanInputStream(s3ObjectInputStream)
        val objectStats: Seq[(String, Int)] = RegexClassifier.scanTextContent(textContent).computeRiskStats()
        val objectStatsSummary =  ObjectScanStats(s3Key=s3Object.key, objectSummaryStats=objectStats)
        (textContent, objectStatsSummary)
    }
    // all objects
    val fullScanStats = RegexClassifier
                          .scanTextContent(payloadSummary.map(_._1))
                          .computeRiskStats()

    val objectScanStats = payloadSummary.map(_._2)
    // return to save results actor
    val scanStats = FullScanStats(
      s3Bucket=bucketName,
      lastScannedKey=newLastScannedKey,
      summaryStats=fullScanStats,
      objectScanStats=objectScanStats,
      totalObjectsSize = Some(totalSizeScanned._2)
    )
    println("Full scan stats " + scanStats)
    scanStats
  }

  private def scanInputStream(inputStream: InputStream) = {
    // check if input stream is compressed
    val check = Utilities.checkIfStreamIsCompressed(inputStream)
    val streamTextContent =
      if(check) Utilities.getParseCompressedStream(inputStream)
      else Utilities.getParsePlainStream(inputStream)
    streamTextContent
  }

  private def generateReferenceKey(s3Bucket: String, s3Prefix: String) = {
    var referenceKey = s"s3Key_${s3Bucket}"
    if(!s3Prefix.isEmpty)
      referenceKey += s":s3Prefix_${s3Prefix}"
    referenceKey
  }

}
