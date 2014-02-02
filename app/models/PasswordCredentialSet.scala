/* Stores username/password combo for SecureSocial */
package models

import org.squeryl.dsl._
import helpers.SquerylEntryPoint._

case class PasswordCredentialSet(id: Long, person_id: Long, hasher: String, password: String,
    salt: Option[String] = None) {
  lazy val person: ManyToOne[Person] = Database.personToPasswordInfo.right(this)
}

object PasswordCredentialSet {
  import Database.passwordInfoTable
  
  def insert(pwcs: PasswordCredentialSet): PasswordCredentialSet = inTransaction {
    passwordInfoTable.insert(pwcs)
  }
}
