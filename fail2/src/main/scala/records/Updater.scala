package records

trait Updater[L <: String, V, R <: Record] {
  type Out <: Record
  def update(l: L, v: V, r: R): Out
}

object Updater {

  def update[L <: String, V, R <: Record]
    (label: L, value: V, record: R)
    (implicit up: Updater[label.type, V, R])
    : up.Out =
      up.update(label, value, record)

  // Hard-coded type-class implementations
  implicit val ageInt_nameString: Updater["age", Int, Record{val name: String}] =
    new Updater["age", Int, Record{val name: String}] {

      type Out = Record{val age: Int; val name: String}

      def update(l: "age", v: Int, r: Record{val name: String}) =
        new Record(r._data + (l -> v)).asInstanceOf[Out]

    }
}
