package org.datafy.aws.app.matt.extras

import java.util.Date

import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ListObjectsV2Request

import scala.collection.JavaConverters._

case class S3KeySummary(
 bucketName: String, key: String, size: Int,
 lastModifiedDate: Option[Date] = None,
 continuationToken: Option[String] = None
)

object S3Manager {

  private val AWS_S3_CLIENT = AmazonS3ClientBuilder.defaultClient()

  val S3_MAX_SCAN_SIZE: Int = 3145728 * 1024 * 1024
  val S3_MAX_RESULTS: Int = 1000

  def getMyBucketsSummary() = {
    val allBuckets = AWS_S3_CLIENT.listBuckets()
    allBuckets.asScala.toList.map(_.getName)
  }

  def getBucketObjects(bucketName: String) = {
    val bucketObjects = AWS_S3_CLIENT.listObjectsV2(bucketName)
    val objectSummaries = bucketObjects.getObjectSummaries
    objectSummaries.asScala.toList.map{
      s3Object =>  S3KeySummary(s3Object.getBucketName, s3Object.getKey,
        s3Object.getSize.toInt, Some(s3Object.getLastModified)
      )
    }
  }

  def getBucketObjects(bucketName: String, keyPrefix: String,
                               lastScannedObject: Option[String] = None) = {
    val objectsV2Request = new ListObjectsV2Request()
                                .withBucketName(bucketName)
                                .withPrefix(keyPrefix)
                                .withMaxKeys(S3_MAX_RESULTS)
                                .withStartAfter(lastScannedObject.getOrElse(""))

    val objectSummaries = AWS_S3_CLIENT.listObjectsV2(objectsV2Request).getObjectSummaries
    objectSummaries.asScala.toList
      .filter( _.getKey != keyPrefix )
      .map {
        s3Object => S3KeySummary(s3Object.getBucketName, s3Object.getKey,
          s3Object.getSize.toInt, Some(s3Object.getLastModified)
      )
    }
  }

  def getObjectContentAsStream(bucketName: String, objectKey: String) ={
    val contentStream = AWS_S3_CLIENT.getObject(bucketName, objectKey)
    contentStream.getObjectContent
  }

  def computeTotalObjectSize(s3KeySummary: List[S3KeySummary]) = {
    val bucketSummaryTuple = s3KeySummary.map {
      s3Object => (s3Object.bucketName, s3Object.size)
    }.groupBy(_._1).mapValues(_.map(_._2).sum).toList
    bucketSummaryTuple
  }

  def computeTotalScanCost(): Unit = {

  }

}
