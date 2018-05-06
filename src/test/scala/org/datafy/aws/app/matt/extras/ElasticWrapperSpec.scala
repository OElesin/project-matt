package org.datafy.aws.app.matt.extras

import org.scalatest.FlatSpec

class ElasticWrapperSpec extends FlatSpec {

  "getClusterState" should "return cluster name" in {
    val clusterName = ElasticWrapper.getClusterState()
    assert(clusterName != "")
  }
}
