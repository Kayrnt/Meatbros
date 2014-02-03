/* Database.scala is the squeryl schema */

package models

import org.squeryl.{PrimitiveTypeMode, Schema}
import helpers.SquerylEntryPoint._
import org.squeryl.internals._
import org.squeryl.dsl.ast._

object Database extends Schema with  PrimitiveTypeMode {
  val peopleTable  = table[Person]("people")
  val oauth1InfoTable = table[OAuth1CredentialSet]("oauth1_credential_sets")
  val oauth2InfoTable = table[OAuth2CredentialSet]("oauth2_credential_sets")
  val passwordInfoTable = table[PasswordCredentialSet]("password_credential_sets")
  val secureSocialTokenTable = table[SecureSocialToken]("secure_social_tokens")
  
  val personToOAuth1Info = oneToManyRelation(peopleTable, oauth1InfoTable).
    via((person, oauth1info) => person.id === oauth1info.person_id)
  val personToOAuth2Info = oneToManyRelation(peopleTable, oauth2InfoTable).
    via((person, oauth2Info) => person.id === oauth2Info.person_id)
  val personToPasswordInfo = oneToManyRelation(peopleTable, passwordInfoTable).
    via((person, passwordInfo) => person.id === passwordInfo.person_id)
    
  // Tables
  on(peopleTable) { p => declare {
    p.id is(autoIncremented("people_klar_id_seq"), primaryKey)
  }}
  
  on(oauth1InfoTable) { x => declare {
    x.id is(autoIncremented("oauth1_credential_sets_id_seq"), primaryKey)
  }}

  on(oauth2InfoTable) { x => declare {
    x.id is(autoIncremented("oauth2_credential_sets_id_seq"), primaryKey)
    x.access_token is(indexed, dbType("text"))
  }}
  
  on(passwordInfoTable) { x => declare {
    x.id is(autoIncremented("password_credential_sets_id_seq"), primaryKey)
  }}
  
  on(secureSocialTokenTable) { x => declare {
    x.uuid is(primaryKey)
  }}

  /*try {
    create
  printDdl(s => println(""+s))
//  Session.currentSession.setLogger(msg => Logger.info(msg))
  } catch {
    case e : Throwable => println(" "+e.getMessage)
  }*/

  class RandomFunction()
    extends FunctionNode("rand()",Nil) {
    override def doWrite(sw: StatementWriter) = {
      sw.write(name)
    }
  }

  def rand() = new RandomFunction()

}

