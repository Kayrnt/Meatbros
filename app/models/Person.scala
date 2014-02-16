/*
 * Person.scala
 * David R. Albrecht for Prefiat LLC
 * Person represents a human being who exists in klarpm. They may, or may not,
 * actually use the site/be able to log in.
 */
package models

import _root_.java.io.File
import _root_.java.sql.Timestamp
import helpers.SquerylEntryPoint._
import org.squeryl.Query
import org.squeryl.dsl._
import securesocial.core._
import play.Logger
import securesocial.core.OAuth1Info
import scala.Some
import securesocial.core.OAuth2Info
import securesocial.core.PasswordInfo

case class Person(id: Long,
                  user_id: String = "",
                  auth_method: String = "",
                  provider_id: String = "",
                  avatar_url: Option[String] = None,
                  broGifUuid: Option[String] = None,
                  isMale: Boolean = true,
                  firstName: String = "",
                  lastName: String = "",
                  fullName: String = "",
                  email: Option[String] = None,
                  phone_number: Option[String] = None,
                  broname: Option[String] = None,
                  twitter: Option[String] = None,
                  facebook: Option[String] = None,
                  instagram: Option[String] = None,
                  website: Option[String] = None,
                  nationality: Option[String] = None,
                  isActive: Boolean = false,
                  isEmailVisible: Boolean  = false,
                   creationDate: Timestamp = new Timestamp(System.currentTimeMillis())) extends securesocial.core.Identity {

  lazy val oauth1CredentialSets: OneToMany[OAuth1CredentialSet] =
    Database.personToOAuth1Info.left(this)
  lazy val oauth2CredentialSets: OneToMany[OAuth2CredentialSet] =
    Database.personToOAuth2Info.left(this)
  lazy val passwordCredentialSets: OneToMany[PasswordCredentialSet] =
    Database.personToPasswordInfo.left(this)

  /*
   * SecureSocial Identity trait implementation
   */

  def authMethod: AuthenticationMethod = AuthenticationMethod(auth_method)

  def avatarUrl: Option[String] = avatar_url

  def identityId: IdentityId = IdentityId(user_id, provider_id)

  def oAuth1Info: Option[OAuth1Info] = inTransaction {
    oauth1CredentialSets headOption match {
      case Some(cs) => Some(OAuth1Info(cs.token, cs.secret))
      case None => None
    }
  }

  def oAuth2Info: Option[OAuth2Info] = inTransaction {
    oauth2CredentialSets headOption match {
      case Some(cs) => Some(OAuth2Info(cs.access_token, cs.token_type, cs.expires_in, cs.refresh_token))
      case None => None
    }
  }

  def passwordInfo: Option[PasswordInfo] = inTransaction {
    passwordCredentialSets headOption match {
      case Some(pw) => {
        Some(PasswordInfo(pw.hasher, pw.password, pw.salt))
      }
      case None => None
    }
  }


}

object Person {

  import Database._

  def insert(person: Person): Person = inTransaction {
    peopleTable.insert(person)
  }

  def update(person: Person) = inTransaction {
    peopleTable.update(person)
  }

  def delete(person: Person) = inTransaction {
    personToOAuth1Info.left(person).deleteAll
    personToOAuth2Info.left(person).deleteAll
    personToPasswordInfo.left(person).deleteAll
    peopleTable.deleteWhere(p => p.id === person.id)
    new File("static/gifs/"+person.broGifUuid+".gif").delete()
  }

  def update(id: Long, form: UserForm) = inTransaction {
    val pOpt = Person.findByBroId(id)
    pOpt.map {
      p =>
        val person = p.copy(
          isMale = form.isMale,
          broname = form.broname,
          twitter = form.twitter,
          facebook = form.facebook,
          instagram = form.instagram,
          website = form.website,
          nationality = form.nationality,
          isActive = form.isActive
        )
        peopleTable.update(person)
    }
  }

  def findByEmailSocialProvider(email: String, socialProvider: String): Option[Person] =
    inTransaction {
      findByEmailSocialProviderQ(email, socialProvider).toList.headOption
    }

  def findByIdentity(identity: Identity): Option[Person] = findByUserId(identity.identityId)

  def findByBroId(id: Long): Option[Person] = inTransaction {
    findByBroIdQ(id).toList.headOption
  }

  def findByUserId(uid: IdentityId): Option[Person] = inTransaction {
    findByUserIdQ(uid).toList.headOption
  }

  def fromIdentity(i: Identity): Person = inTransaction {
    val p = Person(0, i.identityId.userId, i.authMethod.method,
      i.identityId.providerId, i.avatarUrl, None, true, i.firstName,
      i.lastName, i.firstName + " " + i.fullName, i.email,
      None, None, None, None, None, None, None, false, false, new Timestamp(System.currentTimeMillis()))
    Person.insert(p) // Get id to associate OAuth objects

    // Save the three associated elements of Identity trait (oauth info, passwords)
    i.oAuth1Info match {
      case Some(ssoa1i) => {
        val oa1 = OAuth1CredentialSet(0, p.id, ssoa1i.token, ssoa1i.secret)
        OAuth1CredentialSet.insert(oa1)
      }
      case None => {}
    }

    i.oAuth2Info match {
      case Some(ssoa2i) => {
        val oa2 = OAuth2CredentialSet(0, p.id, ssoa2i.accessToken, ssoa2i.tokenType,
          ssoa2i.expiresIn, ssoa2i.refreshToken)
        OAuth2CredentialSet.insert(oa2)
      }
      case None => {}
    }

    i.passwordInfo match {
      case Some(sspwi) => {
        Logger.info("Saving password info.")
        Logger.info("Data: " + p.id + ", " + sspwi.hasher + ", " + sspwi.password + ", " +
          sspwi.salt + ", ")
        val pwi = PasswordCredentialSet(0, p.id, sspwi.hasher, sspwi.password, sspwi.salt)
        PasswordCredentialSet.insert(pwi)
      }
      case None => {}
    }

    p
  }

  def updatefromIdentity(i: Identity, from: Person): Person = inTransaction {
    val p = from.copy(from.id, i.identityId.userId, i.authMethod.method,
      i.identityId.providerId, i.avatarUrl, from.broGifUuid, from.isMale, i.firstName,
      i.lastName, i.firstName + " " + i.fullName, i.email.orElse(from.email),
      from.phone_number, from.broname, from.twitter, from.facebook, from.instagram,
      from.website, from.nationality, from.isActive, from.isEmailVisible, from.creationDate)
    Person.update(p) // Get id to associate OAuth objects

    // Save the three associated elements of Identity trait (oauth info, passwords)
    from.oauth1CredentialSets.deleteAll
    i.oAuth1Info match {
      case Some(ssoa1i) => {
        val oa1 = OAuth1CredentialSet(0, p.id, ssoa1i.token, ssoa1i.secret)
        OAuth1CredentialSet.insert(oa1)
      }
      case None => {}
    }

    from.oauth2CredentialSets.deleteAll
    i.oAuth2Info match {
      case Some(ssoa2i) => {
        val oa2 = OAuth2CredentialSet(0, p.id, ssoa2i.accessToken, ssoa2i.tokenType,
          ssoa2i.expiresIn, ssoa2i.refreshToken)
        OAuth2CredentialSet.insert(oa2)
      }
      case None => {}
    }

    from.passwordCredentialSets.deleteAll
    i.passwordInfo match {
      case Some(sspwi) => {
        Logger.info("Saving password info.")
        Logger.info("Data: " + p.id + ", " + sspwi.hasher + ", " + sspwi.password + ", " +
          sspwi.salt + ", ")
        val pwi = PasswordCredentialSet(0, p.id, sspwi.hasher, sspwi.password, sspwi.salt)
        PasswordCredentialSet.insert(pwi)
      }
      case None => {}
    }

    p
  }

  def remove(person: Person) = inTransaction {
    removeQ(person)
  }

  private def findByEmailSocialProviderQ(email: String, sp: String): Query[Person] = {
    Logger.info("Constructing query for email " + email + ", social provider: " + sp)
    from(peopleTable) {
      p => where(p.email === email and p.provider_id === sp).select(p)
    }
  }

  private def findByBroIdQ(bro_id: Long): Query[Person] = from(peopleTable) {
    person => where(person.id === bro_id).select(person)
  }

  private def findByUserIdQ(uid: IdentityId): Query[Person] = from(peopleTable) {
    person => where(person.user_id === uid.userId and
      person.provider_id === uid.providerId).select(person)
  }

  private def removeQ(person: Person) = {
    peopleTable.deleteWhere(p => person.id === p.id)
  }
}
