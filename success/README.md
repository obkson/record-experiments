## Description

This fixes the problem in `fail2` by giving the hard-coded implementation of `Updater["age", Int, Record{val name: String}]` a name (`Updater_ageInt_nameString`) and then letting the implicit instance have the type `Updater_ageInt_nameString` instead of `Updater[...]`.
For some reason, this makes the path-dependent type `ageInt_nameString.Out` keep the structural type information.

## How to run

```
$sbt test
```
or
```
$sbt run
```

