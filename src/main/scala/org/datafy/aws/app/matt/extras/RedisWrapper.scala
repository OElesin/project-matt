package org.datafy.aws.app.matt.extras

import com.redis.RedisClientPool
import com.typesafe.config.{Config, ConfigFactory}


object RedisWrapper {
  val config: Config = ConfigFactory.load()
  val host = config.getString("redis.host")
  val port = config.getInt("redis.port")
  val database = config.getInt("redis.db")

  val clients = new RedisClientPool(host, port, database = database)

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
