I have experimented with phantom types, but ran into problems.

## Goal 
The goal was to achieve something similar to:

```
object RecPhantom extends Phantom {

  type Updater[L <: String, V, R <: Record] = this.Any {
  	type Out <: Record
  }
  
  implicit def ageInt_nameString: Updater["age", Int, Record{val name: String}] {
    Out = Record{val name: String; val age: Int}
  } = assume
  
}
```

And then

```
def addAgeField[R <: Record](r: R, age: Int)(implicit up: Updater["age", Int, R]): up.Out = 
  r.update("age", age)
```

would be erased to

```
def addAgeField[R <: Record](r: R, age: Int) = r.update("age", age)

```

## Problem

However, it seems like phantom types do not support type members.
For example, the following simple snippet

```
object RecPhantom extends Phantom {
  type Foo = this.Any { type Out = Int }
}
```

gives compile error:

```
  type Foo = this.Any { type Out = Int }

illegal trait inheritance: superclass Object does not derive from trait Any's super<nonsensical><none></nonsensical>
```

whereas using the standard type universe works fine:

```
scala> type Foo = Any { type Out = Int }
defined type alias Foo
scala> val i: Foo#Out = 42
val i: Int = 42
```

## Discussion

I don't know if this will be implemented later, or if it is a fundamental limitation of phantom types.
One can imagine that even if type members and type projections like ```Foo#Out``` will be available at some point, the *path dependent* type `up.Out` will still be problematic, since it is defined on an instance that will be erased? But that is of-course only pessimistic speculation :)

I posted a question in [https://github.com/lampepfl/dotty/issues/2040](https://github.com/lampepfl/dotty/issues/2040) to see what we can expect here.

## Some things that DO work

#### Implicits

```
package records

object RecPhantom extends Phantom {

  type Foo <: this.Any
  type Bar <: this.Any
  
  implicit def foo: Foo = assume
}
```

```
import records.RecPhantom._

def canThisBeCalled1()(implicit f: Foo) = println("yes!")
def canThisBeCalled2()(implicit f: Bar) = println("yes!")

scala> canThisBeCalled1()
yes!

scala> canThisBeCalled2()
-- Error: <console>:8:9 --------------------------------------------------------
8 |canThisBeCalled2()
  |                  ^
  |no implicit argument of type records.RecPhantom.Bar found for parameter b of method thisToo
```

#### Type parameters

```
package records

object RecPhantom extends Phantom {

  type Foo[A] <: this.Any

  implicit def foo: Foo[Int] = assume
}
```

```
import records.RecPhantom._

def canThisBeCalled1()(implicit f: Foo[Int]) = println("yes!")
def canThisBeCalled2()(implicit f: Foo[String]) = println("yes!")                                       

scala> canThisBeCalled1()
yes!

scala> canThisBeCalled2()
-- Error: <console>:8:9 --------------------------------------------------------                      
8 |canThisBeCalled2()
  |                  ^
  |no implicit argument of type records.RecPhantom.Foo[String] found for parameter f of method thisToo
```

## 