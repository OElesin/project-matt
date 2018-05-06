package org.datafy.aws.app.matt.extras

import scala.concurrent.Future
import com.sksamuel.elastic4s.http.HttpClient
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.ElasticsearchClientUri
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.client.config.RequestConfig.Builder
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.datafy.aws.app.matt.models.{FullScanStats, ScanStats}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.elasticsearch.client.RestClientBuilder.{HttpClientConfigCallback, RequestConfigCallback}
import com.sksamuel.elastic4s.circe._
import io.circe.generic.auto._

case class Artist(name: String)

object ElasticWrapper {

  lazy val provider = {
    val provider = new BasicCredentialsProvider
    val credentials = new UsernamePasswordCredentials("elastic", "arqXrvaUYvKcbmpr2N1MdC47")
    provider.setCredentials(AuthScope.ANY, credentials)
    provider
  }

  private val client = getClusterConnection()
  private val INDEX_NAME = "macie_clone"
  private val TYPE_NAME = "scan_statistics"

  private def getClusterConnection() = {
    val uri = ElasticsearchClientUri("elasticsearch://" +
      "76d089ba624e21e30ad9070afa13adc8.us-east-1.aws.found.io:9243?ssl=true")
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

  def getClusterState() = {
    val response = client.execute {
      clusterState()
    }.await
    response.clusterName
  }

  def saveDocument(fullScanStats: FullScanStats) = {
    /**
      * someCaseClass should be changed to a generic case class holder
      */
    client.execute {
      indexInto(INDEX_NAME / TYPE_NAME) doc fullScanStats refresh(RefreshPolicy.IMMEDIATE)
    }.await
  }



}
