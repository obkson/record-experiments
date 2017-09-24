import scala.collection.immutable.HashMap

case class Record(_data: Map[String, Any]) extends Selectable {
  def selectDynamic(name: String): Any = _data(name)

  override def toString = _data.keys
    .map(k => s"$k = ${_data(k)}")
    .mkString("{",", ","}")

}

object Record {
  def apply(_data: (String, Any)*) = new Record(_data = HashMap(_data: _*))

  implicit class RecordOps[R <: Record](r: R) extends AnyVal {
    def update[V](label: String, value: V)(implicit up: Updater[R, label.type, V]): up.Out =
      new Record(r._data + (label -> value)).asInstanceOf[up.Out]
  }
}

trait Updater0[R <: Record, L <: String, V] {
  type Out <: Record
}

// The covariance is needed to be able to derive that
//      R <: Record{name: String}
// ->   Updater[Record{name: String}, "age", Int] <: Updater[R, "age", Int]
// so that we can use the upper bound to create an updater locally in a polymorphic function scope
// using this upper bound, the second implicit resolution can start looking for an exact Updater0
trait Updater[-R <: Record, L <: String, V] {
  type Out <: Record
}

object Updater0 {
  // Used in main
  implicit val nameString_heightDouble: Updater0[Record{val name: String}, "height", Double]{
    type Out = Record{val name: String; val height: Double}
  } = new Updater0[Record{val name: String}, "height", Double] {
    type Out = Record{val name: String; val height: Double}
  }

  // This is the one that the local Updater will look for in second implicit resolution
  implicit val nameString_ageInt: Updater0[Record{val name: String}, "age", Int]{
    type Out = Record{val name: String; val age: Int}
  } = new Updater0[Record{val name: String}, "age", Int] {
    type Out = Record{val name: String; val age: Int}
  }

  // This one will be used in the outer scope to derive the return type of addAge(r)
  implicit val nameStringHeightDouble_ageInt: Updater0[Record{val name: String; val height: Double}, "age", Int] {
    type Out = Record{val name: String; val height: Double; val age: Int}
  } = new Updater0[Record{val name: String; val height: Double}, "age", Int] {
    type Out = Record{val name: String; val height: Double; val age: Int}
  }
}

object Updater {
  // Used in main
  implicit def nameString_heightDouble(implicit up0: Updater0[Record{val name: String}, "height", Double])
  : Updater[Record{val name: String}, "height", Double]{
    type Out = up0.Out
  } = new Updater[Record{val name: String}, "height", Double] {
    type Out = up0.Out
  }

  // The local scope will use this one, thanks to contra-variance
  //     R <: Record{val name: String}
  // ->  Updater[Record{val name: String}, "age", Int] <: Updater[R, "age", Int]
  // so it is eligible for the RecordOps.update function
  implicit def nameString_ageInt(implicit up0: Updater0[Record{val name: String}, "age", Int])
  : Updater[Record{val name: String}, "age", Int]{
    type Out = up0.Out
  } = new Updater[Record{val name: String}, "age", Int] {
    type Out = up0.Out
  }
}

object Main3 {

  def main(args: Array[String]): Unit = {
    val r = Record("name"->"Olle").asInstanceOf[Record{val name: String}]
    // Update works as before in for known mono-morphic record types
    val s = r.update("height", 1.78)
    println("--inside main--")
    println(s.name)
    println(s.height)

    // Now add an age field
    val t = addAge(s, 29)

    // Can still access name and height, and additionally the added age-field.
    println("--after adding age--")
    println(t.name)
    println(t.height)
    println(t.age)
  }

  // This is the fun stuff :)
  // Use Updater0 to derive the return type of the function from R and the added age-field
  def addAge[R <: Record{val name: String}](r: R, age: Int)(implicit up0: Updater0[R, "age", Int]): up0.Out
  = {
    println("--inside addAge--")
    // We can access the name field due to the upper bound on R
    println(r.name)
    // But of course, we know nothing about the height-field here
    // println(r.height) // this should not compile

    // Here R's upper bound Record{val name: String} is used to find an updater
    // Thanks to contra-variance, the found updater will be
    //    Updater.nameString_ageInt
    // That one requires an Updater0[Record{val name: String}, "age", Int]
    // Since the one in scope is an Updater[R, "age", Int], it is not eligible
    // and we will instead search globally and find
    //    Updater0.nameString_ageInt
    val s = r.update("age", age)
    // Since that is a stable path, the typer can infer that
    //    Updater0.nameString_ageInt.Out =:= Record{val name: String; val age: Int}
    // and we can access those fields:
    println(s.name)
    println(s.age)

    // This cast works as expected:
    val t: Record{val name: String; val age: Int} = s

    // s is NOT an ok return value, as the type of s is
    //    Updater0.nameString_ageInt.Out
    // which is NOT a sub-type of up0.Out:
    //
    // return s // Error:
    //    found:    Updater0.nameString_ageInt.Out(s)
    //    required: up0.Out

    // This cast IS an ok return value
    val u: up0.Out = s.asInstanceOf[up0.Out]
    // But then the name and age field are hidden locally
    // u.name // won't compile
    // u.age  // won't compile
    // And will instead re-appear at the call-site :D
    u
  }
}
