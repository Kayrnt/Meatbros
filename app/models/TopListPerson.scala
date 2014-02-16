package models

import org.squeryl.dsl._
import helpers.SquerylEntryPoint._

/**
 * User: Kayrnt
 * Date: 09/02/2014
 * Time: 04:24
 */

case class TopListPerson(id: Long = 0, person_id: Long, target_person_id: Long, position: Int, comment: String) {
}
case class TopListPersonItem(position: Long = 0, name: String, comment: String)

object TopListPerson {

  import Database.topListPersonTable

  def apply(list: List[TopListPerson]) = {
    list.headOption.map{
      head =>
        val userId = head.person_id
        val current = get(userId)
        val toUpdateCurrent = current.filter(tlp => list.map(_.target_person_id).contains(tlp.target_person_id))
        val toUpdateList = list.filter(tlp => current.map(_.target_person_id).contains(tlp.target_person_id))
        val toDelete = current.filterNot(toUpdateCurrent.contains)
        val toCreate = list.filterNot((toDelete ++ toUpdateList).contains)
        (toCreate.map(insert), toUpdateList.map(update), toDelete.map(delete))
    }
  }

  def insert(item: TopListPerson): TopListPerson = inTransaction {
    topListPersonTable.insert(item)
  }

  def update(item: TopListPerson): Int = inTransaction {
    topListPersonTable.update(i =>
      where(i.person_id === item.person_id and i.target_person_id === item.target_person_id)
        set (i.position := item.position))
  }

  def delete(item: TopListPerson): Int = inTransaction {
    topListPersonTable.deleteWhere(i => i.person_id === item.person_id and i.target_person_id === item.target_person_id)
  }

  def get(userId : Long) = inTransaction {
    topListPersonTable.where(tlp => tlp.person_id === userId).toList
  }
}
