package records

import scala.collection.immutable.HashMap

case class Record(_data: Map[String, Any]) extends Selectable {
  def selectDynamic(name: String): Any = _data(name)

  def select(name: String) = _data(name)

  override def toString = _data.keys
    .map(k => s"$k: ${_data(k)}")
    .mkString("{",", ","}")
}

object Record {
  def apply(_data: (String, Any)*) = new Record(_data = HashMap(_data: _*))

  implicit class RecordOps[R <: Record](r: R) extends AnyVal {
    def update[L <: String, V]
      (label: L, value: V)
      (implicit up: Updater[label.type, V][R])
      : up.Out =
        new Record(r._data + (label -> value)).asInstanceOf[up.Out]
  }
}
