package org.datafy.aws.app.matt.classifiers

import io.dataapps.chlorine.finder.{Finder, FinderEngine}
import io.dataapps.chlorine.pattern.RegexFinder

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._
import org.datafy.aws.app.matt.extras.{And, Or}

object RegexClassifier {


  private val customFinders =  ArrayBuffer[Finder]()

  private var matches = List[(String, java.util.List[String])]()
  private var riskStats: List[(String, Int)] = List[(String, Int)]()

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

  def computeRiskStats() : List[Map[String, String]] = {
    val baseRiskStats =  matches.groupBy(_._1) mapValues(_.flatMap(_._2.asScala) size )
    val asList: List[(String, Int)] = baseRiskStats.map { case(pii, count) => (pii, count) }.toList
    val payload = asList.map(x => Map("piiColumn" -> x._1, "value" -> x._2.toString))
    println(payload)
    payload
  }

  def getDocumentRiskLevels(): RiskLevel.Value = {
    val sumScores = this.riskStats.map(_._2).sum
    val ccAndEmails: Seq[(String, Int)] = this.riskStats filter And (_._1 == "CreditCard", _._1 == "Email")
    val emailsCount: Int = this.riskStats.filter(_._1 == "Email").map(_._2).sum

    if(sumScores > 50 || ccAndEmails.nonEmpty)
      return RiskLevel.High
    if(emailsCount >= 5 && !sumScores.isNaN)
      return RiskLevel.Medium
    if(emailsCount < 5 && !sumScores.isNaN)
      return RiskLevel.Low

    RiskLevel.Low
  }

}

object RiskLevel extends Enumeration {
  type RiskLevel = Value
  val High,  Medium, Low = Value
}
