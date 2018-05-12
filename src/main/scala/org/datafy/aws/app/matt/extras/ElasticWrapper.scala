package org.datafy.aws.app.matt.extras

import scala.concurrent.Future
import com.sksamuel.elastic4s.http.HttpClient
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.ElasticsearchClientUri
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.client.config.RequestConfig.Builder
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.datafy.aws.app.matt.models.{FullScanStats, ObjectScanStats}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.client.RestClientBuilder.{HttpClientConfigCallback, RequestConfigCallback}
import com.sksamuel.elastic4s.circe._
import io.circe._, io.circe.syntax._
import io.circe.generic.auto._
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.slf4j.LoggerFactory

import scala.concurrent.duration._


object ElasticWrapper {
  val logger = LoggerFactory.getLogger("ElasticWrapper")

  private val HTTP_AUTH_USERNAME = System.getenv("ES_USERNAME")
  private val HTTP_AUTH_PASSWD = System.getenv("ES_PASSWD")
  private val ES_HOST = System.getenv("ES_HOST")
  private val ES_PORT = System.getenv("ES_PORT")

  require(!ES_HOST.isEmpty, "ENV Variable: ES_HOST must be available")

  lazy val provider = {
    val provider = new BasicCredentialsProvider
    val credentials = new UsernamePasswordCredentials(HTTP_AUTH_USERNAME, HTTP_AUTH_PASSWD)
    provider.setCredentials(AuthScope.ANY, credentials)
    provider
  }

  private val client = getClusterConnection()
  private val INDEX_NAME = "project_matt"
  private val TYPE_NAME = "scan_statistics"

//  implicit val fullScanStatsDecoder: Decoder[FullScanStats] = deriveDecoder[FullScanStats]
//  implicit val objectScanStatsDecoder: Decoder[ObjectScanStats] = deriveDecoder[ObjectScanStats]

  @deprecated
  private def oldgetClusterConnection() = {
    val uri = ElasticsearchClientUri(s"elasticsearch://${ES_HOST}?ssl=true")
    HttpClient(uri, new RequestConfigCallback {
      override def customizeRequestConfig(requestConfigBuilder: Builder): Builder = {
        requestConfigBuilder
      }
    }, new HttpClientConfigCallback {
      override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
        httpClientBuilder.setDefaultCredentialsProvider(provider)
      }
    })
  }

  def getClusterConnection() = {
    logger.info("Initializing connection to elasticsearch")
    val host = ES_HOST.split(":")(0)
    val passwd = ES_HOST.split(":")(1).toInt
    val restClient = RestClient.builder(new HttpHost(host, passwd, "https"))
      .setHttpClientConfigCallback(new HttpClientConfigCallback () {
        override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder = {
          httpClientBuilder.setDefaultCredentialsProvider(provider)
        }
      }).build()
    HttpClient.fromRestClient(restClient)
  }


  def getClusterState() = {
    val response = client.execute {
      clusterState()
    }.await
    logger.info(s"Checked cluster state, cluster name: ${response.clusterName}")
    response.clusterName
  }

  def createIndexIfNotExists() ={
    logger.info(s"Creating Index if ${INDEX_NAME} and ${TYPE_NAME} on elasticsearch")
    val response = client.execute {
      createIndex(INDEX_NAME) indexSetting ("mapping.ignore_malformed", true) mappings
        mapping(TYPE_NAME).fields(
          textField("s3Bucket"),
          textField("lastScannedKey"),
          textField("classifier"),
          dateField("scannedDate").format("yyyy-MM-dd HH:mm:ss"),
          nestedField("summaryStats") fields(
            textField("piiColumnName"),
            intField("value")
          ),
          nestedField("objectScanStats") fields(
            textField("s3Key"),
            dateField("scannedDate").format("yyyy-MM-dd HH:mm:ss"),
            textField("classifier"),
            nestedField("objectSummaryStats") fields (
              textField("piiColumnName"),
              intField("value")
            )
          )
        )
    }.await
    response
  }

  def checkIndex() = {
    val response = client.execute(
      indexExists(INDEX_NAME)
    ).await
    response.exists
  }

  def saveDocument(fullScanStats: FullScanStats) = {
    /**
      * someCaseClass should be changed to a generic case class holder
      */
    val check = checkIndex()
    if(!check)
      createIndexIfNotExists()
    logger.info(s"Saving Data to Index ${INDEX_NAME} and ${TYPE_NAME} on elasticsearch")
    try{
      val response = client.execute {
        indexInto(INDEX_NAME / TYPE_NAME) doc fullScanStats refresh RefreshPolicy.IMMEDIATE
      }.await(20 seconds)
      println("Done saving data: ", response)
      response
    } catch {
      case e: Throwable => e.printStackTrace()
    }

  }



}
