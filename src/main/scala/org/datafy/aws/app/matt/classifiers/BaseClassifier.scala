package org.datafy.aws.app.matt.classifiers

import java.io.InputStream

import org.datafy.aws.app.matt.extras.{S3KeySummary, S3Manager, Utilities}
import org.datafy.aws.app.matt.models.{FullScanStats, ObjectScanStats, RiskStats, ScanObjectsModel}
import com.typesafe.scalalogging._
import org.slf4j.LoggerFactory

/**
  * @groupdesc This is the base classfier class for Matt
  * @tparam T
  */

object BaseClassifier {

  val logger = LoggerFactory.getLogger("BaseClassifier")

  def setS3ScanInputPath(bucketName: String, s3Prefix: String) = {
    // get last object and cache in redis
    val redisReferenceKey = generateReferenceKey(bucketName, s3Prefix)
    val lastScannedKey = ScanObjectsModel.getLastScannedFromRedis(redisReferenceKey)
    logger.info(s"Last Scanned S3 Key: ${lastScannedKey.get}")

    val bucketObjects: List[S3KeySummary] = S3Manager.getBucketObjects(bucketName, s3Prefix, lastScannedKey)

    logger.info(s"Total Number of S3 Objects for scanning: ${bucketObjects.length}")
    if (!bucketObjects.isEmpty) {
      try {
        val totalSizeScanned: (String, Int) = S3Manager.computeTotalObjectSize(bucketObjects).head
        logger.info(s"Total size of scanned objects: ${totalSizeScanned._2}")
        // commence object scan here
        val payloadSummary = bucketObjects.map {
          s3Object =>
            val s3ObjectInputStream = S3Manager.getObjectContentAsStream(s3Object.bucketName, s3Object.key)
            val textContent = this.scanInputStream(s3ObjectInputStream, s3Object.key)
            val regexClassifier = RegexClassifier.scanTextContent(textContent)
            val objectStats: List[RiskStats] = regexClassifier.computeRiskStats()
            val riskLevel = regexClassifier.getDocumentRiskLevels()

            val objectStatsSummary = ObjectScanStats(s3Key = s3Object.key,
              objectSummaryStats = objectStats, classifier = "Regex")
            (textContent, objectStatsSummary)
        }
        // all objects
        val regexClassifier = RegexClassifier.scanTextContent(payloadSummary.map(_._1))
        val fullScanStats = regexClassifier.computeRiskStats()

        val objectScanStats = payloadSummary.map(_._2)
        // return to save results actor
        val scanStats = FullScanStats(
          s3Bucket = bucketName,
          lastScannedKey = "",
          summaryStats = fullScanStats,
          objectScanStats = objectScanStats,
          totalObjectsSize = Some(totalSizeScanned._2)
        )

        val savedKey = ScanObjectsModel.saveScannedResults(scanStats)
        val newLastScannedKey = ScanObjectsModel.saveLastScannedToRedis(redisReferenceKey, bucketObjects)
        scanStats
      } catch {
        case e: Throwable => e.printStackTrace()
      }
      true
    }else {
      logger.info("No files to scan at this time.")
      false
    }
  }

  private def scanInputStream(inputStream: InputStream, s3Key: String): String = {
    // check if input stream is compressed
    if (s3Key.contains("parquet"))
      return Utilities.getParseParquetStream(inputStream)

    val check = Utilities.checkIfStreamIsCompressed(inputStream)
    if(check)
      return Utilities.getParseCompressedStream(inputStream)

    Utilities.getParsePlainStream(inputStream)
  }

  private def generateReferenceKey(s3Bucket: String, s3Prefix: String) = {
    var referenceKey = s"s3Key_${s3Bucket}"
    if(!s3Prefix.isEmpty)
      referenceKey += s":s3Prefix_${s3Prefix}"
    referenceKey
  }

}
