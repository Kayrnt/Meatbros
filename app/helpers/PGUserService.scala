/* PGUserService.scala
 * This module acts as an adapter, letting SecureSocial call a set of
 * methods defined in UserServicePlugin, that we implement depending on how
 * we'd like to do persistence.
 */

package helpers

import securesocial.core.{IdentityId, UserServicePlugin, Identity}
import securesocial.core.providers.Token
import models.{Person, SecureSocialToken}
import play.Logger

class PGUserService(application: play.api.Application) extends UserServicePlugin(application) { 
  def find(uid: IdentityId):Option[Identity] = {
    Logger.info("[PGUserService] find "+uid)
    Person.findByUserId(uid)
  }
  
  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    Logger.info("[PGUserService] Looking up user. email=" + email + ", providerId: " + providerId)
    Person.findByEmailSocialProvider(email, providerId)
  }
  
  def save(ssUser: Identity): Identity = {
    Logger.info("[PGUserService] save "+ssUser)
    val person = Person.findByUserId(ssUser.identityId)
    person.fold(Person.fromIdentity(ssUser))(Person.updatefromIdentity(ssUser, _))

  }

  def save(t: Token) {
    val sst = SecureSocialToken(t.uuid, t.email, t.creationTime, t.expirationTime, t.isSignUp)
    SecureSocialToken.insert(sst)
  }

  def findToken(uuid: String): Option[Token] = {
    SecureSocialToken.findByUUID(uuid) match {
      case Some(t) => Some(Token(t.uuid, t.email, t.creation_time, t.expiration_time, t.is_signup))
      case None => None
    }
  }

  def deleteToken(uuid: String) = SecureSocialToken.deleteByUUID(uuid)

  def deleteTokens() = SecureSocialToken.deleteAll 

  def deleteExpiredTokens() = SecureSocialToken.deleteExpired

}

