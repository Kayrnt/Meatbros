package controllers

import play.api.mvc.Controller
import play.api.libs.json.{Json, Format}
import models.{TopListPerson, Person}
import views.html

/**
 * User: Kayrnt
 * Date: 09/02/2014
 * Time: 05:00
 */
object TopListController extends Controller with securesocial.core.SecureSocial {

  case class Ranking(target: Long, position: Int, comment: Option[String])

  case class RankingList(rankings: List[Ranking])

  object Ranking {
    implicit val format: Format[Ranking] = Json.format[Ranking]
  }

  object RankingList {
    implicit val format: Format[RankingList] = Json.format[RankingList]
  }

  def apply() = SecuredAction {
    implicit request =>
      val user = Person.findByIdentity(request.user) getOrElse Person.fromIdentity(request.user)
      request.body.asJson.fold(BadRequest("")) {
        json =>
          val list = Json.fromJson[RankingList](json)(RankingList.format)
          val topList = list.fold[List[TopListPerson]](
            valid = {
              rl => rl.rankings.map {
                r =>
                  TopListPerson(0, user.id, r.target, r.position, r.comment.getOrElse(""))
              }
            },
            invalid = {
              errors => errors.map(e => println("errors "+e._1)); Nil
            }
          )
          TopListPerson.apply(topList)
          Ok("")
      }
  }

  def edit = SecuredAction {
    implicit request =>
      val user = Person.fromIdentity(request.user)
      val list = TopListPerson.get(user.id)
      Ok(html.account.edit(user, list))
  }

  def list(userId: Long) = SecuredAction {
    implicit request =>
      val user = Person.fromIdentity(request.user)
      val list = TopListPerson.get(userId)
      if(userId == user.id) Ok(html.account.edit(user, list))
      else Ok(html.account.list(userId, user, list))
  }
}
