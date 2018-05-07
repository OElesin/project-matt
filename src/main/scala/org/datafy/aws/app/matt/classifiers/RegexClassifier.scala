package org.datafy.aws.app.matt.classifiers

import io.dataapps.chlorine.finder.{Finder, FinderEngine}
import io.dataapps.chlorine.pattern.RegexFinder
import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._

object RegexClassifier {

  private val customFinders =  ArrayBuffer[Finder]()

  private var matches = List[(String, java.util.List[String])]()

  private val AT_ZMRZAHL_FINDER = new RegexFinder("AT_ZMRZAHL", "[0-9]{12}")
  private val AT_ASVG_FINDER = new RegexFinder("AT_ASVG", "[0-9]{10}")
  private val AT_SSPIN_FINDER = new RegexFinder("AT_SSPIN", "[A-Za-z0-9+/]{22}[A-Za-z0-9+/=][A-Za-z0-9+/=]")
  private val BE_BEID_FINDER = new RegexFinder("BE_BEID", "[0-9]{2}\\.?[0-9]{2}\\.?[0-9]{2}-[0-9]{3}\\.?[0-9]{2}")
  private val BG_EGN_FINDER = new RegexFinder("BG_EGN", "[0-9]{2}[0,1,2,4][0-9][0-9]{2}[0-9]{4}")
  private val EU_CZH_ID_FINDER = new RegexFinder("EU_CZH_ID_FINDER", "[0-9]{2}[0,1,5][0-9][0-9]{2}/?[0-9]{4}")
  private val EU_CZH_NUM_FINDER = new RegexFinder("EU_CZH_NUM_FINDER", "[A-Z]{2}[0-9]{6}")
  private val DK_CPR_FINDER = new RegexFinder("DK_CPR", "[0-9]{2}[0,1][0-9][0-9]{2}-[0-9]{4}")
  private val EE_IK_FINDER = new RegexFinder("EE_IK", "[1-6][0-9]{2}[1,2][0-9][0-9]{2}[0-9]{4}")
  private val EU_IBAN_FINDER = new RegexFinder("EU_IBAN", "[A-Z]{2}?[ ]?[0-9]{2}[  ]?[0-9]{4}[ ]?[0-9]{4}[ ]?[0-9]{4}[ ]?[0-9]{4}[ ]?[0-9]{4}")
  private val FI_HETU_FINDER = new RegexFinder("FI_HETU", "[0-9]{2}\\.?[0,1][0-9]\\.?[0-9]{2}[-+A][0-9]{3}[A-Z]")
  private val FR_NIR_FINDER = new RegexFinder("FR_NIR", "[1,2][ ]?[0-9]{2}[  ]?[0,1,2,3,5][0-9][ ]?[0-9A-Z]{5}[ ]?[0-9]{3}[ ]?[0-9]{2}")
  private val DE_PK_FINDER = new RegexFinder("DE_PK", "[0-9]{2}[0,1][0-9][0-9]{2}-[A-Z]-[0-9]{5}")
  private val DE_STEUERID_FINDER = new RegexFinder("DE_STEUERID", "[0-9]{3}/?[0-9]{4}/?[0-9]{4}")
  private val DE_VSNRRVNR_FINDER = new RegexFinder("DE_VSNRRVNR", "[0-9]{2}[0-9]{2}[0,1][0-9][0-9]{2}[A-Z][0-9]{2}[0-9]")
  private val GR_TAUTOTITA_FINDER = new RegexFinder("GR_TAUTOTITA", "[A-Z][ -]?[0-9]{6}")
  private val HU_TAJ_FINDER = new RegexFinder("HU_TAJ", "[0-9]{3}[ ]?[0-9]{3}[ ][0-9]{3}")
  private val HU_SZAM_FINDER = new RegexFinder("HU_SZAM", "[1-8][ ]?[0-9]{2}[0,1][0-9][0-9]{2}[ ]?[0-9]{4}")
  private val IE_PPS_FINDER = new RegexFinder("IE_PPS", "[0-9]{7}[A-Z]W?")
  private val IT_CF_FINDER = new RegexFinder("IT_CF", "[A-Z]{6}[0-9]{2}[A-E,H,L,M,P,R-T][0-9]{2}[A-Z0-9]{5}")
  private val LV_PK_FINDER = new RegexFinder("LV_PK", "[0-9]{2}[0,1][0-9][0-9]-[0-9]{5}")
  private val LT_AK_FINDER = new RegexFinder("LT_AK", "[3-6][0-9]{2}[0,1][0-9][0-9]{2}[0-9]{4}")
  private val NL_BSN_FINDER = new RegexFinder("NL_BSN", "[0-9]{9}")
  private val NO_FN_FINDER = new RegexFinder("NO_FN", "[0-9]{2}[0,1][0-9][0-9]{2}[ ]?[0-9]{5}")
  private val PL_PESEL_FINDER = new RegexFinder("PL_PESEL", "[0-9]{4}[0-3]{1}[0-9}{1}[0-9]{5}]")
  private val RO_CNF_FINDER = new RegexFinder("RO_CNF", "[1-8][0-9]{2}[0,1][0-9][0-9]{2}[0-9]{6}")
  private val ES_DNI_FINDER = new RegexFinder("ES_DNI", "[0-9,X,M,L,K,Y][0-9]{7}[A-Z]")
  private val SE_PERSONNR_FINDER = new RegexFinder("SE_PERSONNR", "[0-9]{2}[0-1][0-9][0-9]{2}[-+][0-9]{4}")
  private val CH_AVS_FINDER = new RegexFinder("CH_AVS", "[0-9]{3}\\.?[0-9]{2}\\.?[0-9]{3}\\.?[0-9]{3}")
  private val CH_AVS2008_FINDER = new RegexFinder("CH_AVS2008", "756\\.?[0-9]{4}\\.?[0-9]{4}\\.?[0-9]{2}")
  private val EU_NI_FINDER = new RegexFinder("EU_NI", "[A-CEGHJ-PR-TW-Z][A-CEGHJ-NPR-TW-Z]{1}[0-9]{6}[A-DFM]?")
  private val EU_NINO_FINDER = new RegexFinder("EU_NINO", "^([ACEHJLMOPRSW-Yacehjlmoprsw-y][A-CEGHJ-NPRSTW-Za-ceghj-nprstw-z]|[Bb][A-CEHJ-NPRSTW-Za-cehj-nprstw-z]|[Gg][ACEGHJ-NPRSTW-Zaceghj-nprstw-z]|[Kk][A-CEGHJ-MPRSTW-Za-ceghj-mprstw-z]|[Nn][A-CEGHJLMNPRSW-Za-ceghjlmnprsw-z]|[Tt][A-CEGHJ-MPRSTW-Za-ceghj-mprstw-z]|[Zz][A-CEGHJ-NPRSTW-Ya-ceghj-nprstw-y])[0-9]{6}[A-Da-d ]?$")
  private val EU_NHS_FINDER = new RegexFinder("EU_NHS", "[0-9]{3}[ -]?[0-9]{3}[  -]?[0-9]{4}")

  customFinders.append(AT_ZMRZAHL_FINDER,
    AT_ASVG_FINDER,
    AT_SSPIN_FINDER,
    BE_BEID_FINDER,
    BG_EGN_FINDER,
    EU_CZH_ID_FINDER,
    EU_CZH_NUM_FINDER,
    DK_CPR_FINDER,
    EE_IK_FINDER,
    EU_IBAN_FINDER,
    FI_HETU_FINDER,
    FR_NIR_FINDER,
    DE_PK_FINDER,
    DE_STEUERID_FINDER,
    DE_VSNRRVNR_FINDER,
    GR_TAUTOTITA_FINDER,
    HU_TAJ_FINDER,
    HU_SZAM_FINDER,
    IE_PPS_FINDER,
    IT_CF_FINDER,
    LV_PK_FINDER,
    LT_AK_FINDER,
    NL_BSN_FINDER,
    NO_FN_FINDER,
    PL_PESEL_FINDER,
    RO_CNF_FINDER,
    ES_DNI_FINDER,
    SE_PERSONNR_FINDER,
    CH_AVS_FINDER,
    CH_AVS2008_FINDER,
    EU_NI_FINDER,
    EU_NINO_FINDER,
    EU_NHS_FINDER
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
    matches.groupBy(_._1).mapValues(_.map(_._2).length).toList
  }

}

object RiskLevel extends Enumeration {

  protected case class Val() {

  }

  type RiskLevel = Value
  val Very_High = Value(50)
  val High = Value(40)
  val Medium = Value(20)
  val Low = Value(10)
}
