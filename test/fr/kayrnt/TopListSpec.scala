package fr.kayrnt

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import models.{DBType, TopListPerson, Database, Person}
import java.sql.Timestamp
import Database._
import TestUtils._
import play.api.libs.json.{JsNumber, Json}

import controllers.{TopListController, Application}
import org.specs2.matcher.ShouldMatchers
import play.api.http.HeaderNames
import play.api.mvc.{Request, AnyContent}
import play.api.test.{PlaySpecification, FakeApplication, FakeRequest}
import securesocial.testkit.WithLoggedUser
import securesocial.testkit.WithLoggedUser._
import play.api.test.FakeApplication
import play.api.test.FakeApplication

/**
 * User: Kayrnt
 * Date: 09/02/2014
 * Time: 14:21
 */
class TopListSpec extends BaseSpec {

  def app = FakeApplication(withoutPlugins = excludedPlugins, additionalPlugins = includedPlugins)

  "a top list" should {

    "be inserted properly" in new WithLoggedUser(app) {

      val json = Json.obj(
        "rankings" -> Json.arr(
          Json.obj("target" -> JsNumber(2), "position" -> JsNumber(1)),
          Json.obj("target" -> JsNumber(3), "position" -> JsNumber(2)),
          Json.obj("target" -> JsNumber(4), "position" -> JsNumber(3))
        )
      )

      val req: Request[AnyContent] = FakeRequest().withJsonBody(json).
        withCookies(ck) // Fake cookie from the WithloggedUser trait

      val result = TopListController.apply().apply(req)

      //      println("status : " + status(result))

      status(result) must equalTo(OK)

      val person = Person.findByIdentity(user).get
      //      println("person test : "+person)

      val expected = List(TopListPerson(1, 1, 2, 1), TopListPerson(2, 1, 3, 2), TopListPerson(3, 1, 4, 3))
      val insideDB = TopListPerson.get(person.id)
      insideDB must containAllOf(expected)
      //
      val json2 = Json.obj(
        "rankings" -> Json.arr(
          Json.obj("target" -> JsNumber(4), "position" -> JsNumber(1)),
          Json.obj("target" -> JsNumber(5), "position" -> JsNumber(2)),
          Json.obj("target" -> JsNumber(6), "position" -> JsNumber(3))
        )
      )

      val req2: Request[AnyContent] = FakeRequest().withJsonBody(json2).
        withCookies(ck) // Fake cookie from the WithloggedUser trait

      val result2 = TopListController.apply().apply(req2)

      val expected2 = List(TopListPerson(3, 1, 4, 1), TopListPerson(4, 1, 5, 2), TopListPerson(5, 1, 6, 3))
      val insideDB2 = TopListPerson.get(person.id)
      insideDB2 must containAllOf(expected2)
    }

  }
}