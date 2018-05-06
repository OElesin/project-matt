package org.datafy.aws.app.matt.classifiers

import org.scalatest.FlatSpec

class RegexClassifierSpec extends FlatSpec{
  val regexClassifier = RegexClassifier

  "scanTextContent" should "scan PII in List of String" in {
    val someRandomStrings = List(
      "My emails are a@example.com",
      "My other email is foo@bar.com",
      "Sample Versicherungsnummer 65170839J003",
      "Steuer-Identifikationsnummer 316/5756/0463",
      "Personenkennziffer is 261083-C-20917",
      "My account number: PL 10 1140 2017 0000 4202 0971 5311"
    )
    val classifications = regexClassifier.scanTextContent(someRandomStrings)
    assert(!classifications.computeRiskStats().isEmpty)
  }
}
