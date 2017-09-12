package object records {

  type Updater[L <: String, V] = [R <: Record] => _Updater[L, V, R]

  trait _Updater[L <: String, V, R <: Record] {
    type Out <: Record
  }

  object _Updater {

    // Hard-coded type-class implementations
    implicit val ageInt_nameString: _Updater["age", Int, Record{val name: String}]{
      type Out = Record{val age: Int; val name: String}
    } = new _Updater["age", Int, Record{val name: String}] {
      type Out = Record{val age: Int; val name: String}
    }
  }
}
