## Description

To test the general approach of using type-classes,
a hard coded implementation called `ageInt_nameString` is provided in `records.Update`.
As a further simplification, the return type of the update is just a String.

## How to run

```
sbt test
```

## The problem

As anticipated, the path-dependend type `this.type` is translated into
`records.Record{name: String}(r)` (a singleton type?),
which does not seem to equal the `Record{name: String}` required by the type class.
Therefore, implicit resolution fails.
