package org.datafy.aws.app.matt.extras

import org.scalatest.{FlatSpec}


class UtilitiesSpec extends FlatSpec {

  val utilities = Utilities

  val someParquetFile = getClass.getResourceAsStream("/UtilitiesSpec/part-r-00004.gz.parquet")
  val someCompressedJson = getClass.getResourceAsStream("/UtilitiesSpec/sample-data.json.gz")

  "getParsePlainStream" should "return parsed parquet file content with tika" in {
    println(someParquetFile)
    val fileContents = utilities.getParsePlainStream(someParquetFile)
    assert(fileContents.length != 0)
  }

  "checkIfStreamIsCompressed" should "check if a json inputstream is compressed" in {
    val check = utilities.checkIfStreamIsCompressed(someCompressedJson)
    assert(check)
  }

  "checkIfStreamIsCompressed" should "check if a parquet inputstream is compressed" in {
    val check = utilities.checkIfStreamIsCompressed(someCompressedJson)
    assert(!check)
  }

  "getParseCompressedStream" should "read content of compressed file" in {
    val textContent = utilities.getParseCompressedStream(someCompressedJson)
    println(textContent)
    assert(textContent.length != 0)
  }

  "getParseParquetStream" should "read content of parquet file" in {
    val textContent = utilities.getParseParquetStream(someParquetFile)
    println(textContent)
    assert(textContent.length != 0)
  }

}
