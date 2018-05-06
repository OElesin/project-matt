package org.datafy.aws.app.matt.extras

import org.apache.tika.Tika
import org.apache.tika.metadata.Metadata
import java.io.{BufferedInputStream, IOException, InputStream, StringWriter}
import java.util.zip.GZIPInputStream

import org.xml.sax.SAXException
import org.apache.tika.exception.TikaException
import org.apache.tika.metadata.serialization.JsonMetadata
import org.apache.tika.parser.{AutoDetectParser, ParseContext}
import org.apache.tika.parser.pkg.CompressorParser
import org.apache.tika.sax.BodyContentHandler
import org.visallo.web.structuredingest.core.model.ParseOptions
import org.visallo.web.structuredingest.core.util.StructuredFileParserHandler
import org.visallo.web.structuredingest.parquet.{ParquetParser, ParquetStructuredIngestParser}

/**
  * @author: Datafy Ventures
  * @groupdesc: Basic Utilities wrapper
  */

object Utilities {

  private val MAX_STRING_LENGTH = 2147483647

  private val tika = new Tika()
  tika.setMaxStringLength(MAX_STRING_LENGTH)

  @throws(classOf[IOException])
  @throws(classOf[SAXException])
  @throws(classOf[TikaException])
  def getParsePlainStream(inputStream: InputStream): String = {

    val autoDetectParser = new AutoDetectParser()
    val bodyContentHandler = new BodyContentHandler(MAX_STRING_LENGTH)
    val fileMetadata = new Metadata()

    if (inputStream.read() == -1) {
      return "Could not scan inputStream less than 0 bytes"
    }
    autoDetectParser.parse(inputStream, bodyContentHandler, fileMetadata)
    val jsonMetadata = JsonMetadata.toJson(fileMetadata, new StringWriter())
    return bodyContentHandler.toString
  }

  @throws(classOf[IOException])
  @throws(classOf[SAXException])
  @throws(classOf[TikaException])
  def getParseCompressedStream(inputStream: InputStream) = {
    /**
      * Parses a compressed imput stream with Apache Tika
      */
    val bodyContentHandler = new BodyContentHandler()
    val fileMetadata = new Metadata()
    val parseContext = new ParseContext()

    //CompressorParser parser
    val compressorParser = new CompressorParser()
    compressorParser.parse(inputStream, bodyContentHandler, fileMetadata, parseContext)
    bodyContentHandler.toString
  }

  def getParseParquetStream(inputStream: InputStream): String = {
    val bodyContentHandler = new BodyContentHandler()
    val fileMetadata = new Metadata()
    val parseContext = new ParseContext()

    // Parquet parser
    val parquetStructuredParser = new ParquetStructuredIngestParser()
    val parquetParser = new ParquetParser()
    new StructuredFileParserHandler()
    parquetStructuredParser.ingest(inputStream, new ParseOptions(), new StructuredFileParserHandler())
    bodyContentHandler.toString
  }

  @throws(classOf[IOException])
  def checkIfStreamIsCompressed(myStream: InputStream) = {
    /**
      * Checks if an input stream is compressed
      * return: Boolean
      */
    var inputStream = myStream
    if(!inputStream.markSupported()) {
      inputStream = new BufferedInputStream(inputStream)
    }
    inputStream.mark(2)
    var magicBytes = 0
    try {
      magicBytes = inputStream.read() & 0xff | ((inputStream.read() << 8) & 0xff00)
      inputStream.reset()
    } catch  {
      case ioe: IOException => {
        ioe.printStackTrace()
        false
      }
    }
    magicBytes == GZIPInputStream.GZIP_MAGIC
  }



}
