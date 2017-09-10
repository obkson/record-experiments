package records

trait Field[L <: String, T] {
  type Updater[R <: Record] = records.Updater[L, T, R]
}
