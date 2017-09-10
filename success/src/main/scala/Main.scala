import records.{Record, Updater, Field}

object Main {

  def addAgeField[R <: Record](r: R, age: Int)
    (implicit up: Updater["age", Int, R])
    = r.update("age", age)

  // partially applied context bound using type-lambda
  def addAgeField2[R <: Record : Field["age", Int]#Updater]
    (r: R, age: Int)
    = r.update("age", age)

  def main(args: Array[String]): Unit = {
    val m = Record("name"->"Olle").asInstanceOf[Record{val name:String}]
    println(s"This is me: $m")
    val mu = m.update("age", 29)
    println(s"I am ${mu.age} years old")

    val s = Record("name"->"Snuffles").asInstanceOf[Record{val name:String}]
    println(s"This mr Snuffles: $s")
    val su = addAgeField2(s, 4)
    println(s"He is ${su.age} years old")
  }
}
