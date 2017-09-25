package object records {

  // This type-lambda allows us to write the Updater as a context bound
  type Updater[L <: String, V] = [R <: Record] => PolymorphicUpdater[R, L, V]

}
