package securesocial.testkit

import play.api.Logger
import securesocial.core._
import play.api.mvc.{Result, Request}
import securesocial.core.IdentityId

class AlwaysValidIdentityProvider(app:play.api.Application) extends IdentityProvider(app){
  val logger = Logger("securesocial.stubs.AlwaysValidIdentityProvider")
  def authMethod: AuthenticationMethod = AuthenticationMethod("naive")


  def fillProfile(user: SocialUser): SocialUser = {
    user
  }

  def id: String = "naive"

  override def doAuth[A]()(implicit request: Request[A]): Either[Result, SocialUser] = {
    val userId = request.body.toString
    val r =Right(SocialUserGenerator.socialUserGen(IdentityId(userId, id), authMethod).sample.get)
    r
  }

}