package org.datafy.aws.app.matt.app

import akka.actor.{ActorSystem, Props}

object ApplicationMain extends App {

    val system = ActorSystem("MyPIIScannerActorSystem")
    println(System.getenv("MY_S3_BUCKET"))
    val s3BucketName = System.getenv("MY_S3_BUCKET")
    val s3Prefix = System.getenv("MY_S3_PREFIX")

    val objectScanRequestActor = system.actorOf(
      Props(new ScanRequestActor(bucketName=s3BucketName, s3Prefix = Some(s3Prefix)))
    )
    // initialize scan request on bucket
    objectScanRequestActor !  ScanRequestActor.Initialize

    system.terminate()

}
