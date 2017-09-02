## Description

To circumvent the problem with path-dependent type `this.type` in the fail1-version
an implicit conversion method is provided in `Record.RecordOps`.
This makes calling `r.update("age", 29)` implicitly equivalent to calling `Updater.update("age", 29, r)`.
Furthermore, the return type `Updater.Out` is not only a string but the updated record type.

## How to run

```
$sbt test
```

## What works
Now the update function actually works as indicated by test `t1`.
(you have to comment out `t2` to see that though)

The following REPL-session also proves the point:
```
scala> import records.Record
import records.Record

scala> val r = Record("name"->"Olle").asInstanceOf[Record{val name: String}]  
val r: records.Record{name: String} = {name: Olle}

scala> r.name
val res0: String = "Olle"

scala> r.update("age", 29)
val res1: records.Updater.ageInt_nameString.Out = {name: Olle, age: 29}
```

## Problem 1
However, test `t2` does not compile with error

```
assertEquals(s.name, "Olle")
             ^^^^^^
value `name` is not a member of records.Updater.ageInt_nameString.Out
```

So, although the `Updater.Out` is a subclass of `Record` it seems like Dotty cannot infer that it is a `Selectable`.

## Problem 2 (Dotty bug?)
When trying to define a function using the type class, the REPL crashes strangely.

```
scala> def add_age[R <: Record](r: R)(implicit up: Updater["age", Int, R]) = r.update("age", 29)
-- Error: <console>:4:4 --------------------------------------------------------
4 |(""+"def add_age[R <: Record](r: R)(implicit up: Updater[\"age\".type, Int, R]): [R <: records.Record]
  |    ^
  |    unclosed string literal
-- Error: <console>:5:76 -------------------------------------------------------
5 |  (r: R)(implicit up: records.Updater[String("age"), Int, R]): up.type#Out\n"
  |                                                                            ^
  |                                                   unclosed string literal
-- [E007] Type Mismatch Error: <console>:6:1 -----------------------------------
6 |)}
  | ^
  | found:    Any => String
  | required: String
  | 
```

However, the code compiles if not run in the REPL.
(replace the assertions that can't compile in the tests with println-statements to see!)
