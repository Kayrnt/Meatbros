package controllers

import _root_.java.net.{URISyntaxException, URI}

import models._
import securesocial.core._

import play.api._
import play.api.mvc._
import views._
import Database._

object Application extends Controller with securesocial.core.SecureSocial {

  /**
   * This result directly redirect to the application home.
   */

  def Home = UserAwareAction {
    implicit request =>
      val alert = request.user.map(Person.findByIdentity).flatten.map(_.isActive).map {
        if (_) null else InPageAlert(InPageAlertType.INFO, "Your account isn't visible, you need to configure it")
      }
      homeContent(request.user, alert.orNull)
  }

  def homeContent(user: Option[Identity], inPageAlert: InPageAlert = null) = {
    val db = inTransaction {
      from(Database.peopleTable)(p =>
        where(p.isActive === true)
          select p
          orderBy rand()
      ).page(0, 20).toList
    }
    Ok(html.index(user.orNull, db, inPageAlert))
  }

  def all(page: Long) = UserAwareAction {
    implicit request =>
      if (page < 0)
        BadRequest("page number < 1")
      else {
        val (itemCount,db) = inTransaction {
          (from(Database.peopleTable)(p =>
            where(p.isActive === true)
              compute count(p.id)
          ).single.measures.toInt/20+1,

          from(Database.peopleTable)(p =>
            where(p.isActive === true)
              select p
          ).page(((page - 1) * 20).toInt, 20).toList)
        }

        Ok(html.all(request.user.orNull, (1 to itemCount).toList, db, null))
      }
  }

  // -- Actions

  /**
   * Handle default path requests
   */
  def index = {
    Home
  }

  def latest() = UserAwareAction {
    request =>
      val db = inTransaction {
        from(Database.peopleTable)(p =>
          where(p.isActive === true)
            select p
            orderBy (p.creationDate desc)
        ).page(0, 20).toList
      }
      Ok(html.index(request.user.orNull, db, null))
  }

  // -- Javascript routing

  def javascriptRoutes = Action {
    implicit request =>
      Ok(
        Routes.javascriptRouter("jsRoutes")(
          routes.javascript.TopListController.apply
        )
      ).as("text/javascript")
  }
}

