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
import org.apache.parquet.hadoop.util.HadoopInputFile
import org.apache.parquet.tools.json.JsonRecordFormatter
import org.apache.parquet.tools.read.{SimpleReadSupport, SimpleRecord}
import org.apache.tika.exception.TikaException
import org.apache.tika.sax.XHTMLContentHandler

import scala.util.Random


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
    val fileNamePrefix = Random.alphanumeric.take(5).mkString
    val tempFile = File.createTempFile(s"parquet-${fileNamePrefix}", ".parquet")
    IOUtils.copy(stream, new FileOutputStream(tempFile))

    val conf = new Configuration()
    val path = new Path(tempFile.getAbsolutePath)
    val parquetMetadata = ParquetFileReader.readFooter(conf, path, ParquetMetadataConverter.NO_FILTER)
    var defaultReader: ParquetReader[SimpleRecord] = null

    val columns = parquetMetadata.getFileMetaData.getSchema.getFields
    metadata.set(CONTENT_TYPE, PARQUET_RAW.toString)
    metadata.set("Total Number of Columns", columns.size.toString)
    metadata.set("Parquet Column Names", columns.toString)

    val xhtml = new XHTMLContentHandler(handler, metadata)
    xhtml.startDocument()
    xhtml.startElement("p")

    // ::TODO:: ensure parquet reader reads all files not only file row
    try {
      defaultReader = ParquetReader.builder(new SimpleReadSupport(), new Path(tempFile.getAbsolutePath)).build()
      if(defaultReader.read() != null) {
        val values: SimpleRecord = defaultReader.read()
        val jsonFormatter = JsonRecordFormatter.fromSchema(parquetMetadata.getFileMetaData.getSchema)

        val textContent: String = jsonFormatter.formatRecord(values)
        xhtml.characters(textContent)
        xhtml.endElement("p")
        xhtml.endDocument()
      }

    } catch {
        case e: Throwable => e.printStackTrace()
          if (defaultReader != null) {
          try {
            defaultReader.close()
          } catch{
            case _: Throwable =>
          }
        }
    } finally {
      if (tempFile != null) tempFile.delete()
    }
  }

}