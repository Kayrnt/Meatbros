package controllers

import play.api.mvc._

import views._
import models._
import scala.util.Random
import play.api.Logger

/**
 * User: Kayrnt
 * Date: 11/01/2014
 * Time: 18:40
 */

object BroController extends Controller with securesocial.core.SecureSocial {

  def account() = UserAwareAction {
    implicit request =>
      val person = request.user.map(u => Person.
        findByUserId(u.identityId)).flatten

      person.fold(Redirect("/")) {
        p =>
          val values: Map[String, String] =
            Map("isMale" -> UserForm.boolToText(p.isMale),
              "isActive" -> UserForm.boolToText(p.isActive),
              "twitter" -> p.twitter.getOrElse(""),
              "facebook" -> p.facebook.getOrElse(""),
              "instagram" -> p.instagram.getOrElse(""),
              "website" -> p.website.getOrElse(""),
              "nationality" -> p.nationality.getOrElse(""),
              "broname" -> p.broname.getOrElse(""))
          val form = UserForm.form.bind(values)
          Ok(html.account.account(p.id, p, form))
      }
  }

  def update = UserAwareAction {
    implicit request =>
      val user: Option[Person] = (request.user map Person.findByIdentity).flatten.orElse(request.user map Person.fromIdentity)
      user.fold(Redirect(routes.Application.index)) {
        u =>
          UserForm.form.bindFromRequest.fold(
            formWithErrors => BadRequest(html.account.account(u.id, u, formWithErrors)),
            person => {
              Person.update(u.id, person)
              Application.homeContent(Some(u), InPageAlert(InPageAlertType.SUCCESS, "Your account has been successfully updated"))
            }
          )
      }
  }

  def delete() = UserAwareAction {
    implicit request =>
      request.user.fold(Redirect(securesocial.controllers.routes.LoginPage.logout())) {
        u =>
          Person.findByUserId(u.identityId).map(Person.delete)
          Redirect(securesocial.controllers.routes.LoginPage.logout())
      }
  }

}