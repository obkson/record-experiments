package records

trait Updater[L, V, R] {
  def update(l: L, v: V, r: R): String
}

object Updater {
  // Hard-coded type-class implementations
  implicit val ageInt_nameString: Updater["age", Int, Record{val name: String}] =
    new Updater["age", Int, Record{val name: String}] {
      def update(l: "age", v: Int, r: Record{val name: String}): String
        = s"{ age: ${v}, name: ${r.name} }"
    }
}
