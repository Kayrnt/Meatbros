/* Stores OAuth1 credentials for securesocial */

package models

import org.squeryl.dsl._
import helpers.SquerylEntryPoint._

case class OAuth1CredentialSet(id: Long, person_id: Long, token: String, secret: String) {
  lazy val person: ManyToOne[Person] = Database.personToOAuth1Info.right(this)
}

object OAuth1CredentialSet {
  import Database.oauth1InfoTable
  
  def insert(oa1cs: OAuth1CredentialSet): OAuth1CredentialSet = inTransaction {
    oauth1InfoTable.insert(oa1cs)
  }
}
