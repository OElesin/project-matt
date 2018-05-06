package org.datafy.aws.app.matt.extras

import com.redis.RedisClientPool
import com.typesafe.config.{Config, ConfigFactory}


object RedisWrapper {
  val config: Config = ConfigFactory.load()

  private val REDIS_HOST = System.getenv("REDIS_PASSWD")
  private val REDIS_PORT = System.getenv("REDIS_DB").toInt
  private val REDIS_DB = System.getenv("REDIS_DB").toInt
  private val REDIS_PASSWD = System.getenv("REDIS_PASSWD")

  val clients = new RedisClientPool(REDIS_HOST,
    REDIS_PORT,
    database = REDIS_DB,
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
