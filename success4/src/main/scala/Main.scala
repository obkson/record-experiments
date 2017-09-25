import records._
import misc._

object Main {

  def main(args: Array[String]): Unit = {
    println("--inside main--")

    // create a structurally typed record using an explicit cast
    val r = Record("name"->"Olle").asInstanceOf[Record{val name: String}]

    // Update works as expected for statically known monomorphic record types
    val s = r.update("height", 1.78)
    println(s.name)
    println(s.height)


    // Now add an age field using a polymorphic function
    val t = addAge(s, 29)

    // Can still access name and height, and additionally the added age-field.
    println("--after adding age--")
    println(t.name)
    println(t.height)
    println(t.age)

    // It's an error (albeit cryptic) to access non-existent fields
    // t.foo // Error: value `foo` is not a member of records.PolymorphicUpdater.nameStringHeightDouble_ageInt.Out

    println("--adding age with explicit PolymorphicUpdater--")
    val u = addAgeWithExplicitReturnType(s, 29)
    println(u.name)
    println(u.height)
    println(u.age)

    println("--after birthday--")
    val v = birthday(t)
    println(v.name)
    println(v.height)
    println(v.age)

    println("--this would break or simply not work in most naïve record-implementations--")
    // (intentional space)

    {
      println("example 1:")
      val r = Record("field"->new B()).asInstanceOf[Record{val field: B}]
      println("field of type B: " + (r.field: B))

      val s = upcastingUpdate1(r)

      // println("field of type B: " + (s.field: B)) // Error: found: misc.A, required: required: misc.B
      println("field of type A: " + (s.field: A)) // This is the sound return type
    }
    {
      println("example 2:")
      val r = Record("field"->new B()).asInstanceOf[Record{val field: B}]
      println("field of type B: " + (r.field: B))

      val s = upcastingUpdate2(r)

      // println("field of type B: " + (s.field: B)) // Error: found: misc.A, required: required: misc.B
      println("field of type A: " + (s.field: A)) // This is the sound return type
    }
  }


  // This is the fun stuff :)
  // Uses a type-lambda as context bound, the correct (polymorphic) return type is inferred
  def addAge[R <: Record{val name: String} : Updater["age", Int]](r: R, age: Int) = {
    println("--inside addAge--")

    // We can access the name field due to the upper bound on R
    println("name before update: " + r.name)

    // Here R's upper bound Record{val name: String} is used to find an
    //    UpperBoundUpdater[Record{name: String}, "age", Int] {
    //      Out = Record{val name: String; val age: Int}
    //    }
    //
    // This trait is contra-variant in the supplied record type, so
    //    R <: Record{name: String}
    //              ->
    //    UpperBoundUpdater[Record{name: String}, "age", Int] <: UpperBoundUpdater[R, "age", Int]
    //
    // Thus, we can produce such an updater by using the upper bound of R.
    //
    // At the same time, the context-bound type lambda produces a
    //    PolymorphicUpdater[R, "age", Int] {
    //      Out = R merged with ("age" -> Int)
    //    }
    // which is in scope here.
    // However, in this local scope, all we know is that Out <: PolymorphicUpdater#Out <: Record.
    //
    // The type of s will be the intersection of the UpperBoundUpdater and the PolymorphicUpdater
    // which in practice is Record{val name: String; val age: Int}
    val s = r.update("age", age)
    // so we can access the name field provided by the upper bound of R
    println("name after update: " + s.name)
    // and the newly added age-field
    println("age after update: " + s.age)
    // On printing the record, ALL runtime fields will be taken into consideration (include height)
    println("the whole record: " + s)

    // We can also cast to the expected type
    val t: Record{val name: String; val age: Int} = s

    // And return the updated record s
    s
    // The inferred type at the *call-site* will be more specific than
    //    Record{val name: String; val age: Int}
    // since there we know the actual type of R and can calculate the proper Out type of
    //    PolymorphicUpdater.
  }


  // If we want/need to express the explicit polymorphic return type of the function, we can instead
  // declare the PolymorphicUpdater as an explicit implicit (no pun intended) to get a reference
  // to poly.Out
  def addAgeWithExplicitReturnType[R <: Record{val name: String}](r: R, age: Int)(implicit poly: PolymorphicUpdater[R, "age", Int]): poly.Out = {
    r.update("age", age)
  }

  // We can also update existing fields (type-checked and sound)
  def birthday[R <: Record{val age: Int} : Updater["age", Int]](r: R) = r.update("age", r.age+1)

  // Existing fields are casted correctly, regardless of if we know about them or not
  // Here r.field might already exist but we don't care since we just overwrite it with type A
  def upcastingUpdate1[R <: Record : Updater["field", A]](r: R) = r.update("field", new A())
  // Here we know that r.field exists, but it might have type B <: A at the call site.
  // That doesn't matter though, as the returned record type will have (field: A) regardless.
  def upcastingUpdate2[R <: Record{val field: A} : Updater["field", A]](r: R) = r.update("field", new A())
}
