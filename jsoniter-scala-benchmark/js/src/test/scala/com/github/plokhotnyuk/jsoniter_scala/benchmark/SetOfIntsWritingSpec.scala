package com.github.plokhotnyuk.jsoniter_scala.benchmark

import com.github.plokhotnyuk.jsoniter_scala.benchmark.JsoniterScalaCodecs.setOfIntsCodec
import com.github.plokhotnyuk.jsoniter_scala.core.readFromArray
import scala.collection.immutable.Set

class SetOfIntsWritingSpec extends BenchmarkSpecBase {
  def benchmark: SetOfIntsWriting = new SetOfIntsWriting {
    setup()
  }

  "SetOfIntsWriting" should {
    "serialize properly" in {
      val b = benchmark
      toString(b.avSystemGenCodec()) shouldBe b.jsonString
      toString(b.borer()) shouldBe b.jsonString
      toString(b.circe()) shouldBe b.jsonString
      toString(b.circeJsoniter()) shouldBe b.jsonString
      toString(b.jsoniterScala()) shouldBe b.jsonString
      toString(b.preallocatedBuf, 0, b.jsoniterScalaPrealloc()) shouldBe b.jsonString
      toString(b.playJson()) shouldBe b.jsonString
      toString(b.playJsonJsoniter()) shouldBe b.jsonString
      toString(b.smithy4sJson()) shouldBe b.jsonString
      toString(b.uPickle()) shouldBe b.jsonString
      toString(b.zioJson()) shouldBe b.jsonString
    }
  }
}