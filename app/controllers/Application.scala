package controllers

import _root_.java.net.{URISyntaxException, URI}

import models._
import helpers.SquerylEntryPoint._
import securesocial.core._
import play.Logger
import scala.Some

import play.api._
import play.api.mvc._
import views._

object Application extends Controller with securesocial.core.SecureSocial {

  /**
   * This result directly redirect to the application home.
   */

  def Home =  UserAwareAction { implicit request =>
    val alert = request.user.map(Person.findByIdentity).flatten.map(_.isActive).map{
      if(_) null else InPageAlert(InPageAlertType.INFO, "Your account isn't visible, you need to configure it")
    }
     homeContent(request.user, alert.orNull)
  }

  def homeContent(user: Option[Identity], inPageAlert: InPageAlert = null) = {
    val db = inTransaction { Database.peopleTable.where(p => p.isActive === true).iterator.toList }
    Ok(html.index(user.orNull, db, inPageAlert))
  }

  def search(query: String) = UserAwareAction { implicit request =>
    val db = inTransaction { Database.peopleTable.where(p => p.isActive === true and p.broname.map(b => b like s"%$query%")).iterator.toList }
    Ok(html.index(request.user.orNull, db, null))
  }

  def searchBox = Action { implicit request =>
    val search = request.getQueryString("q")
    try {
      val uri: URI = new URI(search.getOrElse(""))
      Redirect(routes.Application.search(uri.toASCIIString))
    }
    catch {
      case e: URISyntaxException => {
        Redirect(routes.Application.index)
      }
    }

  }

  // -- Actions

  /**
   * Handle default path requests
   */
  def index = {
    Home
  }

  def hot() = {
    Home
  }

  def latest() = {
    Home
  }

  // -- Javascript routing

  def javascriptRoutes = Action { implicit request =>
    Ok(
      Routes.javascriptRouter("jsRoutes")(
      )
    ).as("text/javascript")
  }
}
            
