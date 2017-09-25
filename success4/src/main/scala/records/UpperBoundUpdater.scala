package records

import misc._

// This updater is contra-variant in the type param R
// @scala.annotation.implicitNotFound("Cannot update record of type ${R} with field ${L} of type ${V}")
trait UpperBoundUpdater[-R <: Record, L <: String, V] {
  type Out <: Record
}

object UpperBoundUpdater {
  // Hard-coded implicits that will be provided automagically by the compiler-patch:

  implicit val nameString_heightDouble: UpperBoundUpdater[Record{val name: String}, "height", Double]{
    type Out = Record{val name: String; val height: Double}
  } = new UpperBoundUpdater[Record{val name: String}, "height", Double] {
    type Out = Record{val name: String; val height: Double}
  }

  implicit val nameString_ageInt: UpperBoundUpdater[Record{val name: String}, "age", Int]{
    type Out = Record{val name: String; val age: Int}
  } = new UpperBoundUpdater[Record{val name: String}, "age", Int] {
    type Out = Record{val name: String; val age: Int}
  }

  implicit val nameStringHeightDouble_ageInt: UpperBoundUpdater[Record{val name: String; val height: Double}, "age", Int] {
    type Out = Record{val name: String; val height: Double; val age: Int}
  } = new UpperBoundUpdater[Record{val name: String; val height: Double}, "age", Int] {
    type Out = Record{val name: String; val height: Double; val age: Int}
  }

  // Can update already existing fields as well
  // (side-note: in the example in Main, the local scope is not aware of neither name nor height)
  implicit val ageInt_ageInt: UpperBoundUpdater[Record{val age: Int}, "age", Int] {
    type Out = Record{val age: Int}
  } = new UpperBoundUpdater[Record{val age: Int}, "age", Int] {
    type Out = Record{val age: Int}
  }

  // Upcasting update from B <: A  to  A
  implicit val fieldB_fieldA: UpperBoundUpdater[Record{val field: B}, "field", A] {
    type Out = Record{val field: A}
  } = new UpperBoundUpdater[Record{val field: B}, "field", A] {
    type Out = Record{val field: A}
  }

  // Adding new field: A
  implicit val empty_fieldA: UpperBoundUpdater[Record{}, "field", A] {
    type Out = Record{val field: A}
  } = new UpperBoundUpdater[Record{}, "field", A] {
    type Out = Record{val field: A}
  }
}

