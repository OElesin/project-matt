package org.datafy.aws.app.matt.classifiers

import org.scalatest.FlatSpec

class RegexClassifierSpec extends FlatSpec{
  val regexClassifier = RegexClassifier

  "scanTextContent" should "scan PII in List of String" in {
    val someRandomStrings = List(
      "My emails are a@example.com",
      "My other email is elesin@gmail.com",
      "Sample Versicherungsnummer 65170839J003",
      "Steuer-Identifikationsnummer 316/5756/0463",
      "Personenkennziffer is 261083-C-20917",
      "My account number: PL 10 1140 2017 0000 4202 0971 5311",
      "{\"registration_dttm\":\"AF4w0OE3AABOfyUA\",\"id\":2," +
        "\"first_name\":\"Albert\",\"last_name\":\"Freeman\"," +
        "\"email\":\"afreeman1@is.gd\",\"gender\":\"Male\"," +
        "\"ip_address\":\"218.111.175.34\",\"cc\":\"\",\"country\":\"Canada\"," +
        "\"birthdate\":\"1/16/1968\",\"salary\":150280.17," +
        "\"title\":\"Accountant IV\",\"comments\":\"\"}"
    )
    val classifications = regexClassifier.scanTextContent(someRandomStrings).computeRiskStats()
    println(classifications)
    assert(!classifications.isEmpty)
  }
}
