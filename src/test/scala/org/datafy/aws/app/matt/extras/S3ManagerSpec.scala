package org.datafy.aws.app.matt.extras

import java.io.InputStream

import org.scalatest.FlatSpec

class S3ManagerSpec extends FlatSpec {

//  val s3Manager = S3Manager
  val testBucket = "aws-athena-query-results-514965996716-eu-west-1"
  val testObjectKey = "Unsaved/2017/12/30/03b1b25d-9c7a-4000-b24f-58df48064608.csv"

  /**

  "getMyBucketsSummary" should "return all my buckets summaries" in {
    val buckets = s3Manager.getMyBucketsSummary()
    assert(buckets.length != 0)
  }

  "getBucketObjects" should "list my bucket objects and summary" in {
    val objects = s3Manager.getBucketObjects(testBucket)
    assert(objects.length != 0)
  }

  "getObjectContentAsStream" should "Get object content as stream" in {
    val contentStream = s3Manager.getObjectContentAsStream(testBucket, testObjectKey)
    assert(contentStream.isInstanceOf[InputStream])
  }
    */

}
