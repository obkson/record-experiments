package records

trait Updater[L <: String, V, R <: Record] {
  type Out <: Record
}

object Updater {

  // Hard-coded type-class implementations
  implicit val ageInt_nameString: Updater["age", Int, Record{val name: String}]{
    type Out = Record{val age: Int; val name: String}
  } = new Updater["age", Int, Record{val name: String}] {
      type Out = Record{val age: Int; val name: String}
    }
}
