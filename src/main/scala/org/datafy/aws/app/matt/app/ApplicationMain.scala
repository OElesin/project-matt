package org.datafy.aws.app.matt.app

import org.datafy.aws.app.matt.classifiers.BaseClassifier
import org.datafy.aws.app.matt.extras.ElasticWrapper

import scala.concurrent.duration._

object ApplicationMain {

    def main(args: Array[String]): Unit = {

        val s3BucketName = System.getenv("MY_S3_BUCKET")
        val s3Prefix = System.getenv("MY_S3_PREFIX")

        require(!s3BucketName.isEmpty, "ENV Variable: MY_S3_BUCKET must be available")
        // initialize scan request on bucket
        val scanRequestMessage = BaseClassifier.setS3ScanInputPath(s3BucketName, Some(s3Prefix).orNull)
        sys.exit(0)
    }
}
