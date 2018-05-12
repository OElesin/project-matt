package org.datafy.aws.app.matt.extras

import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec

class ElasticWrapperSpec extends FlatSpec with MockFactory {

  "getClusterState" should "return cluster name" in {
    val clusterName = ElasticWrapper.getClusterState()

    println(ElasticWrapper.checkIndex())
    assert(clusterName != "")
  }
}
