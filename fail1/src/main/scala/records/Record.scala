package records

import scala.collection.immutable.HashMap

case class Record(_data: Map[String, Any]) extends Selectable {
  def selectDynamic(name: String): Any = _data(name)

  def update[L <: String, V](label: L, value: V)(implicit up: Updater[label.type, V, this.type]): String =
    up.update(label, value, this)

  override def toString = _data.keys
    .map(k => s"$k: ${_data(k)}")
    .mkString("{",", ","}")
}

object Record {
  def apply(_data: (String, Any)*) = new Record(_data = HashMap(_data: _*))
}
