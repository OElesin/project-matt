name := """project_matt"""

version := "1.0-BETA"

scalaVersion := "2.12.6"

val awsSDKVersion = "1.11.320"
val tikaVersion = "1.18"
val elastic4sVersion = "5.6.6"

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.7"
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % "2.8.7"
dependencyOverrides += "org.apache.logging.log4j" % "log4j-core" % "2.9.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.16",
  "com.typesafe.akka" %% "akka-actor" % "2.4.16" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "org.apache.tika" % "tika-core" % tikaVersion,
  "org.apache.tika" % "tika-parsers" % tikaVersion,
  "org.apache.tika" % "tika-serialization" % tikaVersion,
  "com.amazonaws" % "aws-java-sdk" % awsSDKVersion,
  "net.debasishg"  %% "redisclient" % "3.6",
  "org.visallo" % "visallo-core" % "4.0.0",
  "org.json" % "json" % "20180130",
  "com.sksamuel.elastic4s" %% "elastic4s-core" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-jackson" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-http" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-testkit" % elastic4sVersion % "test",
  "com.sksamuel.elastic4s" %% "elastic4s-embedded" % elastic4sVersion % "test",
  // a json library
  "com.sksamuel.elastic4s" %% "elastic4s-jackson" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-play-json" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-circe" % elastic4sVersion,
  "com.sksamuel.elastic4s" %% "elastic4s-json4s" % elastic4sVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "org.elasticsearch" % "elasticsearch" % "6.2.4",
  "org.apache.commons" % "commons-io" % "1.3.2",
  "io.dataapps.chlorine" % "chlorine-finder" % "1.1.5",
  "org.kohsuke.args4j" % "args4j-maven-plugin" % "2.33"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

assemblyOutputPath in assembly := file("deploy_app/artifacts/project-matt_1.0-BETA.jar")

lazy val execScript = taskKey[Unit]("Execute the shell script")

execScript := {
  "aws s3 cp deploy_app/artifacts/project-matt_1.0-BETA.jar s3://datafy-data-lake-public-artifacts/project-matt/ --acl public-read" !
}