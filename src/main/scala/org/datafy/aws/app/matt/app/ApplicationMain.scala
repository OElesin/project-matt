package org.datafy.aws.app.matt.app

import akka.actor.{ActorSystem, Props}
import org.datafy.aws.app.matt.extras.ElasticWrapper

object ApplicationMain {

    def main(args: Array[String]): Unit = {

        val system = ActorSystem("MyPIIScannerActorSystem")

        val systemSupervisor = system.actorOf(Props[AppSupervisor], "MyPIIScannerActorSystem")

        val s3BucketName = System.getenv("MY_S3_BUCKET")
        val s3Prefix = System.getenv("MY_S3_PREFIX")

        require(!s3BucketName.isEmpty, "ENV Variable: MY_S3_BUCKET must be available")

        systemSupervisor ! Props(new ScanRequestActor(bucketName=s3BucketName, s3Prefix = Some(s3Prefix)))

        val objectScanRequestActor = system.actorOf(
            Props(new ScanRequestActor(bucketName=s3BucketName, s3Prefix = Some(s3Prefix)))
        )
        // initialize scan request on bucket
        objectScanRequestActor !  ScanRequestActor.Initialize
        System.setProperty("log4j2.debug", "")
        ElasticWrapper.getClusterConnection().rest.close()
        system.terminate()
    }



}
