/* Persistence for the secure social "token" object */
package models

import helpers.SquerylEntryPoint._
import org.joda.time.DateTime
import org.squeryl.Query

case class SecureSocialToken(uuid: String, email: String, creation_time: DateTime,
    expiration_time: DateTime, is_signup: Boolean)

object SecureSocialToken {
  import Database.secureSocialTokenTable
  
  def insert(t: SecureSocialToken): SecureSocialToken = inTransaction {
    secureSocialTokenTable.insert(t)
  }
  
  def deleteAll: Int = inTransaction {
    secureSocialTokenTable.deleteWhere(t => 1 === 1)
  }
  
  def deleteExpired: Int = inTransaction {
    secureSocialTokenTable.deleteWhere(t => t.expiration_time < DateTime.now())
  }
  
  def deleteByUUID(uuid: String): Int = inTransaction {
    secureSocialTokenTable.deleteWhere(t => t.uuid === uuid)
  }
  
  def findByUUID(uuid: String): Option[SecureSocialToken] = inTransaction {
    SecureSocialToken.findByUUIDQ(uuid).headOption
  }
  
  private def findByUUIDQ(uuid: String): Query[SecureSocialToken] = from(secureSocialTokenTable) {
    t => where(t.uuid === uuid).select(t)
  }
}
