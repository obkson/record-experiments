import org.junit.Test
import org.junit.Assert._

import records.Record

class Test1 {
  @Test def t1(): Unit = {
    val r = Record("name"->"Olle").asInstanceOf[Record{val name: String}]
    assertEquals(r.name, "Olle")
  }

  @Test def t2(): Unit = {
    val r = Record("name"->"Olle").asInstanceOf[Record{val name: String}]
    val s = r.update("age", 29)
  }
}
