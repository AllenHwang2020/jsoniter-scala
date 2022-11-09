package com.github.plokhotnyuk.jsoniter_scala.benchmark

class ArrayOfOffsetTimesWritingSpec extends BenchmarkSpecBase {
  def benchmark: ArrayOfOffsetTimesWriting = new ArrayOfOffsetTimesWriting {
    setup()
  }

  "ArrayOfOffsetTimesWriting" should {
    "write properly" in {
      val b = benchmark
      toString(b.borer()) shouldBe b.jsonString
      toString(b.jsoniterScala()) shouldBe b.jsonString
      toString(b.preallocatedBuf, 64, b.jsoniterScalaPrealloc()) shouldBe b.jsonString
    }
  }
}