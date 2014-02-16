package controllers

import play.api.mvc.{Action, Controller}
import models.Database._
import models.Database
import views.html
import java.net.{URISyntaxException, URI}
import securesocial.core.SecureSocial
import play.api.libs.json.Json

/**
 * User: Kayrnt
 * Date: 11/02/2014
 * Time: 01:09
 */
object Search extends Controller with SecureSocial {

  def autoComplete() = Action {
    request => val search = request.getQueryString("query")
      var searchLike: String = "%" + search + "%"
      searchLike = searchLike.toUpperCase
      val db = inTransaction {
        Database.peopleTable.where(p => (p.isActive === true) and (p.broname.getOrElse("") like searchLike)).toList
      }
      Ok(Json.toJson(db.map(_.broname).flatten))
  }

  def search(query: String) = UserAwareAction {
    implicit request =>
      val searchLike = s"%$query%"
      val db = inTransaction {
        Database.peopleTable.where(p => (p.isActive === true) and (p.broname.getOrElse("") like searchLike)).toList
      }
      Ok(html.index(request.user.orNull, db, null))
  }

  def searchBox = Action {
    implicit request =>
      val search = request.getQueryString("q")
      try {
        val uri: URI = new URI(search.getOrElse(""))
        Redirect(routes.Search.search(uri.toASCIIString))
      }
      catch {
        case e: URISyntaxException => {
          Redirect(routes.Application.index)
        }
      }

  }


}
