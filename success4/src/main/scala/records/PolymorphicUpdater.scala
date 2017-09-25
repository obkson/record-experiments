package records

import misc._

// This updater is invariant in the type param R
// @scala.annotation.implicitNotFound("Cannot update record of type ${R} with field ${L} of type ${V}")
trait PolymorphicUpdater[R <: Record, L <: String, V] {
  type Out <: Record
}

object PolymorphicUpdater {
  // Hard-coded implicits that will be provided automagically by the compiler-patch:

  implicit val nameString_heightDouble: PolymorphicUpdater[Record{val name: String}, "height", Double]{
    type Out = Record{val name: String; val height: Double}
  } = new PolymorphicUpdater[Record{val name: String}, "height", Double] {
    type Out = Record{val name: String; val height: Double}
  }

  implicit val nameString_ageInt: PolymorphicUpdater[Record{val name: String}, "age", Int]{
    type Out = Record{val name: String; val age: Int}
  } = new PolymorphicUpdater[Record{val name: String}, "age", Int] {
    type Out = Record{val name: String; val age: Int}
  }

  implicit val nameStringHeightDouble_ageInt: PolymorphicUpdater[Record{val name: String; val height: Double}, "age", Int] {
    type Out = Record{val name: String; val height: Double; val age: Int}
  } = new PolymorphicUpdater[Record{val name: String; val height: Double}, "age", Int] {
    type Out = Record{val name: String; val height: Double; val age: Int}
  }

  // Can update already existing fields as well
  // (side-note: in the example in Main, the outer scope / call-site is aware of the height field)
  implicit val nameStringHeightDoubleAgeInt_ageInt: PolymorphicUpdater[Record{val name: String; val height: Double; val age: Int}, "age", Int] {
    type Out = Record{val name: String; val height: Double; val age: Int}
  } = new PolymorphicUpdater[Record{val name: String; val height: Double; val age: Int}, "age", Int] {
    type Out = Record{val name: String; val height: Double; val age: Int}
  }

  // Upcasting update from B <: A  to  A
  implicit val fieldB_fieldA: PolymorphicUpdater[Record{val field: B}, "field", A] {
    type Out = Record{val field: A}
  } = new PolymorphicUpdater[Record{val field: B}, "field", A] {
    type Out = Record{val field: A}
  }
}

