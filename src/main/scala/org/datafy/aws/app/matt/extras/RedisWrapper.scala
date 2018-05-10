package org.datafy.aws.app.matt.extras

import com.redis.RedisClientPool
import com.typesafe.config.{Config, ConfigFactory}


object RedisWrapper {
  val config: Config = ConfigFactory.load()

  private val REDIS_HOST = System.getenv("REDIS_HOST")
  private val REDIS_PORT = System.getenv("REDIS_PORT").toInt
  private val REDIS_DB = Some(System.getenv("REDIS_DB"))
  private val REDIS_PASSWD = System.getenv("REDIS_PASSWD")

  require(!REDIS_HOST.isEmpty, "ENV Variable: REDIS_HOST must be available")
  require(!REDIS_PORT.isNaN, "ENV Variable: REDIS_PORT must be available")

  val clients = new RedisClientPool(REDIS_HOST,
    REDIS_PORT,
    database = 0,
    secret = Some(REDIS_PASSWD)
  )


  def getData(key: String) = {
    val payload = clients.withClient { client
      => client.get(key)
    }
    payload
  }

  def setData(key: String, data: String) = {
    clients.withClient {
      client =>
        client.set(key, data)
    }
  }

  def checkSet(key: String) = {
    clients.withClient {
      client =>
        client.exists(key)
    }
  }
}
