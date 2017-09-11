import org.junit.Test
import org.junit.Assert._

import records.Record
import records.Updater

class Test1 {
  @Test def t1(): Unit = {
    val r = Record("name"->"Olle").asInstanceOf[Record{val name: String}]
    assertEquals(r.name, "Olle")
  }

  @Test def t2(): Unit = {
    val r = Record("name"->"Olle").asInstanceOf[Record{val name: String}]
    val s = r.update("age", 29)
    assertEquals(s.name, "Olle")
    assertEquals(s.age, 29)
  }

  @Test def t3(): Unit = {
    def add_age[R <: Record](r: R)(implicit up: Updater["age", Int, R]) = r.update("age", 29)

    val r = Record("name"->"Olle").asInstanceOf[Record{val name: String}]
    val s = add_age(r)
    assertEquals(s.name, "Olle")
    assertEquals(s.age, 29)
  }
}
