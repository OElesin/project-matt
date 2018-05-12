package org.datafy.aws.app.matt.extras

import java.io.{File, FileOutputStream, IOException, InputStream}
import java.util

import org.apache.commons.io.IOUtils
import org.apache.hadoop.conf.Configuration

import scala.collection.JavaConverters._
import org.apache.hadoop.fs.Path
import org.apache.hadoop.hive.serde2.objectinspector.StructField
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector
import org.apache.orc.OrcFile
import org.apache.orc.OrcFile.ReaderOptions
import org.apache.orc.Reader
import org.apache.orc.RecordReader
import org.apache.tika.exception.TikaException
import org.apache.tika.metadata.Metadata
import org.apache.tika.mime.MediaType
import org.apache.tika.parser.{AbstractParser, ParseContext}
import org.xml.sax.{ContentHandler, SAXException}

import scala.util.Random


class TikaHadoopOrcParser extends AbstractParser  {
  final val ORC_RAW = MediaType.application("x-orc")

  private val SUPPORTED_TYPES: Set[MediaType] = Set(ORC_RAW)

  def getSupportedTypes(context: ParseContext): util.Set[MediaType] = {
    SUPPORTED_TYPES.asJava
  }

  @throws(classOf[IOException])
  @throws(classOf[SAXException])
  @throws(classOf[TikaException])
  def parse(stream: InputStream, handler: ContentHandler,
            metadata: Metadata, context: ParseContext): Unit = {
    // create temp file from stream
    try {
      val fileNamePrefix = Random.alphanumeric.take(5).mkString
      val tempFile = File.createTempFile(s"orc-${fileNamePrefix}", ".orc")
      IOUtils.copy(stream, new FileOutputStream(tempFile))

      val path = new Path(tempFile.getAbsolutePath)
      val conf = new Configuration()
      val orcReader = OrcFile.createReader(path, new ReaderOptions(conf))
      val records: RecordReader = orcReader.rows()

      val storeRecord = null
      val firstBlockKey = null

    } catch {
      case e: Throwable => e.printStackTrace()
    }



//    val fields =

  }
}
