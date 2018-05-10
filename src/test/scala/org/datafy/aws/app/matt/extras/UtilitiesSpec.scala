package org.datafy.aws.app.matt.extras

import java.io.InputStream

import org.scalatest.FlatSpec


class UtilitiesSpec extends FlatSpec {

  val utilities: Utilities.type = Utilities

  val someCompressedJson: InputStream = getClass.getResourceAsStream("/UtilitiesSpec/sample-data.json.gz")

  "getParsePlainStream" should "return empty string when parsing parquet" in {
    val someParquetFile = getClass.getResourceAsStream("/UtilitiesSpec/part-r-00004.gz.parquet")
    val fileContents = utilities.getParsePlainStream(someParquetFile)
    someParquetFile.close()
    assert(fileContents.length == 0)
  }

  "checkIfStreamIsCompressed" should "check if a json input stream is compressed" in {
    val check = utilities.checkIfStreamIsCompressed(someCompressedJson)
    assert(check)
  }

  "getParseCompressedStream" should "read content of compressed file" in {
    val textContent = utilities.getParseCompressedStream(someCompressedJson)
    assert(textContent.length != 0)
  }

  "getParseParquetStream" should "read content of parquet file" in {
    val someParquetFile = getClass.getResourceAsStream("/UtilitiesSpec/part-r-00004.gz.parquet")
    val textContent = utilities.getParseParquetStream(someParquetFile)
    assert(textContent.length != 0)
  }

}
