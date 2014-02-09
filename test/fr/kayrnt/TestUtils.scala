package fr.kayrnt

import play.api.test.{WithApplication, FakeApplication, FakeRequest}
import securesocial.core.{UserService, AuthenticatorStore, Identity, Authenticator}
import org.squeryl.adapters.{H2Adapter}
import org.squeryl.Session
import play.api.Application
import play.api.db.DB
import models.Person
import org.specs2.mock.Mockito
import org.specs2.execute.AsResult

/**
 * User: Kayrnt
 * Date: 09/02/2014
 * Time: 14:30
 */
object TestUtils {

  @inline implicit def loggedInFakeRequestWrapper[T](x: FakeRequest[T]) = new LoggedInFakeRequest(x)

  final class LoggedInFakeRequest[T](val self: FakeRequest[T]) extends AnyVal {
    def withLoggedInUser(id: Long) = {
      val userToLogInAs:Identity = Person.findByBroId(id).orNull //get this from your database using whatever you have in Global
      val cookie = Authenticator.create(userToLogInAs) match {
          case Right(authenticator) => authenticator.toCookie
          case _ => null
        }
      self.withCookies(cookie)
    }
  }

}