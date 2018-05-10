package org.datafy.aws.app.matt.classifiers

import io.dataapps.chlorine.finder.{Finder, FinderEngine}
import io.dataapps.chlorine.pattern.RegexFinder

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._

object RegexClassifier {

  private val customFinders =  ArrayBuffer[Finder]()

  private var matches = List[(String, java.util.List[String])]()

  private val EU_IBAN_FINDER = new RegexFinder("EU_IBAN",
    "[A-Z]{2}?[ ]?[0-9]{2}[\n]?[0-9]{4}[ ]?[0-9]{4}[ ]?[0-9]{4}[ ]?[0-9]{4}[ ]?[0-9]{4}")

  private val DE_PK_FINDER = new RegexFinder("DE_PK_ID",
    "[0-9]{2}[0,1][0-9][0-9]{2}-[A-Z]-[0-9]{5}")

  private val DE_STEUER_ID_FINDER = new RegexFinder("DE_STEUER_ID",
    "[0-9]{3}/?[0-9]{4}/?[0-9]{4}")

  private val DE_VSNR_FINDER = new RegexFinder("DE_VSNR_ID",
    "[0-9]{2}[0-9]{2}[0,1][0-9][0-9]{2}[A-Z][0-9]{2}[0-9]")

  customFinders.append(
    EU_IBAN_FINDER, DE_PK_FINDER,
    DE_STEUER_ID_FINDER, DE_VSNR_FINDER
  )

  private val SCAN_ENGINE = new FinderEngine(customFinders.toList.asJava)

  def scanTextContent(rawContent: List[String]) = {
    val matches = SCAN_ENGINE.findWithType(rawContent.asJava)
    this.matches = matches.asScala.toList
    this
  }

  def scanTextContent(rawContent: String) = {
    val matches = SCAN_ENGINE.findWithType(rawContent)
    this.matches = matches.asScala.toList
    this
  }

  def computeRiskStats() = {
    val riskStats =  matches.groupBy(_._1) mapValues(_.flatMap(_._2.asScala) size )
    riskStats.map { case(pii, count) => (pii, count) }.toList
  }

}

object RiskLevel extends Enumeration {

  protected case class Val(lowerBound: Int, upperBound: Int) extends super.Val {
//    def piiImpact: String = RiskLevel.
  }

  type RiskLevel = Value
  val Very_High = Value(50)
  val High = Value(40)
  val Medium = Value(20)
  val Low = Value(10)
}
