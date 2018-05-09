package org.datafy.aws.app.matt.extras

import java.io.{File, FileOutputStream, IOException, InputStream}
import java.util

import scala.collection.JavaConverters._
import org.xml.sax.{ContentHandler, SAXException}
import org.apache.tika.metadata.Metadata
import org.apache.tika.metadata.HttpHeaders.CONTENT_TYPE
import org.apache.tika.mime.MediaType
import org.apache.tika.parser.{AbstractParser, ParseContext}
import org.apache.commons.io.IOUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.parquet.hadoop.ParquetFileReader
import org.apache.parquet.hadoop.ParquetReader
import org.apache.parquet.format.converter.ParquetMetadataConverter
import org.apache.parquet.tools.read.{SimpleReadSupport, SimpleRecord}
import org.apache.tika.exception.TikaException
import org.apache.tika.sax.XHTMLContentHandler

import scala.collection.mutable


class TikaParquetParser extends AbstractParser {
  // make some stuff here
  final val PARQUET_RAW = MediaType.application("x-parquet")

  private val SUPPORTED_TYPES: Set[MediaType] = Set(PARQUET_RAW)

  def getSupportedTypes(context: ParseContext): util.Set[MediaType] = {
    SUPPORTED_TYPES.asJava
  }

  @throws(classOf[IOException])
  @throws(classOf[SAXException])
  @throws(classOf[TikaException])
  def parse(stream: InputStream, handler: ContentHandler,
            metadata: Metadata, context: ParseContext): Unit = {
    // create temp file from stream
    val tempFile = File.createTempFile("parquet", "tmp")
    IOUtils.copy(stream, new FileOutputStream(tempFile))
    val conf = new Configuration()
    val path = new Path(tempFile.getAbsolutePath)
    val parquetMetadata = ParquetFileReader.readFooter(conf, path, ParquetMetadataConverter.NO_FILTER)

    val columns = parquetMetadata.getFileMetaData.getSchema.getFields
    metadata.set(CONTENT_TYPE, PARQUET_RAW.toString)
    metadata.set("Total Number of Columns", columns.size.toString)
    metadata.set("Parquet Column Names", columns.toString)

    val xhtml = new XHTMLContentHandler(handler, metadata)
    xhtml.startDocument()
    try {
      xhtml.startElement("p")
      val parquetReader = ParquetReader.builder(new SimpleReadSupport(), new Path(tempFile.getAbsolutePath)).build()
      val parquetRecordValues = parquetReader.read().getValues().asScala.toSet[SimpleRecord.NameValue]

      val charset = parquetRecordValues.mkString(", ")

      xhtml.characters(charset)
      xhtml.endElement("p")
      xhtml.endDocument()
    } finally {
      if (tempFile != null) tempFile.delete()
    }
  }

}