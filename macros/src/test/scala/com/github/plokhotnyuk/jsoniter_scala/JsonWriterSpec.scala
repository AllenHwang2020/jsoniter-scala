package com.github.plokhotnyuk.jsoniter_scala

import java.io.ByteArrayOutputStream

import org.scalatest.{Matchers, WordSpec}

class JsonWriterSpec extends WordSpec with Matchers {
  "JsonWriter.writeVal for boolean" should {
    "write valid true and false values" in {
      serialized(_.writeVal(true)) shouldBe "true"
      serialized(_.writeVal(false)) shouldBe "false"
    }
  }
  "JsonWriter.writeVal for string" should {
    "write null value" in {
      serialized(_.writeVal(null.asInstanceOf[String])) shouldBe "null"
    }
    "write ascii chars" in {
      val text = new String(Array.fill[Char]('a')(10000))
      serialized(_.writeVal(text)) shouldBe '"' + text + '"'
    }
    "write strings with escaped whitespace chars" in {
      serialized(_.writeVal("\b\f\n\r\t\\")) shouldBe """"\b\f\n\r\t\\""""
    }
    "write strings with unicode chars" in {
      serialized(_.writeVal("иბ𝄞")) shouldBe "\"иბ\ud834\udd1e\""
    }
    "write strings with escaped unicode chars" in {
      serialized(WriterConfig(escapeUnicode = true))(_.writeVal("\u0001\b\f\n\r\t/Aиბ𝄞")) shouldBe
        "\"\\u0001\\b\\f\\n\\r\\t/A\\u0438\\u10d1\\ud834\\udd1e\""
    }
    "throw i/o exception in case of invalid character surrogate pair" in {
      assert(intercept[Exception](serialized(_.writeVal("\udd1e\ud834"))).getMessage.contains("illegal surrogate"))
    }
    "JsonWriter.writeVal for int" should {
      "write int values" in {
        serialized(_.writeVal(0)) shouldBe "0"
        serialized(_.writeVal(-0)) shouldBe "0"
        serialized(_.writeVal(123)) shouldBe "123"
        serialized(_.writeVal(-123)) shouldBe "-123"
        serialized(_.writeVal(123456)) shouldBe "123456"
        serialized(_.writeVal(-123456)) shouldBe "-123456"
        serialized(_.writeVal(123456789)) shouldBe "123456789"
        serialized(_.writeVal(-123456789)) shouldBe "-123456789"
        serialized(_.writeVal(2147483647)) shouldBe "2147483647"
        serialized(_.writeVal(-2147483648)) shouldBe "-2147483648"
      }
    }
    "JsonWriter.writeVal long" should {
      "write long values" in {
        serialized(_.writeVal(0L)) shouldBe "0"
        serialized(_.writeVal(-0L)) shouldBe "0"
        serialized(_.writeVal(123L)) shouldBe "123"
        serialized(_.writeVal(-123L)) shouldBe "-123"
        serialized(_.writeVal(123456L)) shouldBe "123456"
        serialized(_.writeVal(-123456L)) shouldBe "-123456"
        serialized(_.writeVal(123456789L)) shouldBe "123456789"
        serialized(_.writeVal(-123456789L)) shouldBe "-123456789"
        serialized(_.writeVal(123456789012L)) shouldBe "123456789012"
        serialized(_.writeVal(-123456789012L)) shouldBe "-123456789012"
        serialized(_.writeVal(123456789012345L)) shouldBe "123456789012345"
        serialized(_.writeVal(-123456789012345L)) shouldBe "-123456789012345"
        serialized(_.writeVal(123456789012345678L)) shouldBe "123456789012345678"
        serialized(_.writeVal(-123456789012345678L)) shouldBe "-123456789012345678"
        serialized(_.writeVal(9223372036854775807L)) shouldBe "9223372036854775807"
        serialized(_.writeVal(-9223372036854775808L)) shouldBe "-9223372036854775808"
      }
    }
    "JsonWriter.writeVal for float" should {
      "write float values" in {
        serialized(_.writeVal(0.0f)) shouldBe "0.0"
        serialized(_.writeVal(-0.0f)) shouldBe "-0.0"
        serialized(_.writeVal(12345.678f)) shouldBe "12345.678"
        serialized(_.writeVal(-12345.678f)) shouldBe "-12345.678"
        serialized(_.writeVal(1.23456788e14f)) shouldBe "1.23456788E14"
        serialized(_.writeVal(-1.2345679e-6f)) shouldBe "-1.2345679E-6"
      }
      "throw i/o exception on invalid JSON numbers" in {
        assert(intercept[Exception](serialized(_.writeVal(0.0f/0.0f))).getMessage.contains("illegal number"))
        assert(intercept[Exception](serialized(_.writeVal(1.0f/0.0f))).getMessage.contains("illegal number"))
        assert(intercept[Exception](serialized(_.writeVal(-1.0f/0.0f))).getMessage.contains("illegal number"))
      }
    }
    "JsonWriter.writeVal for double" should {
      "write double values" in {
        serialized(_.writeVal(0.0)) shouldBe "0.0"
        serialized(_.writeVal(-0.0)) shouldBe "-0.0"
        serialized(_.writeVal(123456789.12345678)) shouldBe "1.2345678912345678E8"
        serialized(_.writeVal(-123456789.12345678)) shouldBe "-1.2345678912345678E8"
        serialized(_.writeVal(123456789.123456e10)) shouldBe "1.23456789123456E18"
        serialized(_.writeVal(-123456789.123456e-10)) shouldBe "-0.0123456789123456"
      }
      "throw i/o exception on invalid JSON numbers" in {
        assert(intercept[Exception](serialized(_.writeVal(0.0/0.0))).getMessage.contains("illegal number"))
        assert(intercept[Exception](serialized(_.writeVal(1.0/0.0))).getMessage.contains("illegal number"))
        assert(intercept[Exception](serialized(_.writeVal(-1.0/0.0))).getMessage.contains("illegal number"))
      }
    }
    "JsonWriter.writeVal for BigInt" should {
      "write null value" in {
        serialized(_.writeVal(null.asInstanceOf[BigInt])) shouldBe "null"
      }
      "write number values" in {
        serialized(_.writeVal(BigInt("0"))) shouldBe "0"
        serialized(_.writeVal(BigInt("-0"))) shouldBe "0"
        serialized(_.writeVal(BigInt("12345678901234567890123456789"))) shouldBe "12345678901234567890123456789"
        serialized(_.writeVal(BigInt("-12345678901234567890123456789"))) shouldBe "-12345678901234567890123456789"
      }
    }
    "JsonWriter.writeVal for BigDecimal" should {
      "write null value" in {
        serialized(_.writeVal(null.asInstanceOf[BigDecimal])) shouldBe "null"
      }
      "write number values" in {
        serialized(_.writeVal(BigDecimal("0"))) shouldBe "0"
        serialized(_.writeVal(BigDecimal("-0"))) shouldBe "0"
        serialized(_.writeVal(BigDecimal("1234567890123456789.0123456789"))) shouldBe "1234567890123456789.0123456789"
        serialized(_.writeVal(BigDecimal("-1234567890123456789.0123456789"))) shouldBe "-1234567890123456789.0123456789"
      }
    }
  }

  def serialized(f: JsonWriter => Unit): String = serialized(WriterConfig())(f)

  def serialized(cfg: WriterConfig)(f: JsonWriter => Unit): String = {
    val out = new ByteArrayOutputStream(1024)
    val writer = new JsonWriter(new Array[Byte](2), 0, 0, out, cfg)
    try f(writer)
    finally writer.close()
    out.toString("UTF-8")
  }
}
