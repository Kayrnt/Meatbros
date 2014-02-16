package fr.kayrnt

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import models.{DBType, TopListPerson, Database, Person}
import java.sql.Timestamp
import Database._
import TestUtils._
import play.api.libs.json.{JsString, JsNumber, Json}

import controllers.{TopListController, Application}
import org.specs2.matcher.ShouldMatchers
import play.api.http.HeaderNames
import play.api.mvc.{Request, AnyContent}
import play.api.test.{PlaySpecification, FakeApplication, FakeRequest}
import securesocial.testkit.WithLoggedUser
import securesocial.testkit.WithLoggedUser._
import play.api.test.FakeApplication

/**
 * User: Kayrnt
 * Date: 09/02/2014
 * Time: 14:21
 */
class TopListSpec extends BaseSpec {

  "a top list" should {

    "be inserted properly" in new WithLoggedUser() {

      def testUser(i : Long) = Person(i, "", "", "", None, None, true, "", "", "",
        None, None, Some(""), None, None, None, None,
        Some(""), true, false, new Timestamp(System.currentTimeMillis()))

      (1 to 5).map(i => Person.insert(testUser(i)))

      val json = Json.obj(
        "rankings" -> Json.arr(
          Json.obj("target" -> JsNumber(2), "position" -> JsNumber(1), "comment" -> JsString("")),
          Json.obj("target" -> JsNumber(3), "position" -> JsNumber(2), "comment" -> JsString("")),
          Json.obj("target" -> JsNumber(4), "position" -> JsNumber(3), "comment" -> JsString(""))
        )
      )

      val req: Request[AnyContent] = FakeRequest().withJsonBody(json).
        withCookies(ck) // Fake cookie from the WithloggedUser trait

      val result = TopListController.apply().apply(req)

      //      println("status : " + status(result))

      status(result) must equalTo(OK)

      val person = Person.findByIdentity(user).get
      //      println("person test : "+person)

      val expected = List(TopListPerson(1, 6, 2, 1, ""), TopListPerson(2, 6, 3, 2, ""), TopListPerson(3, 6, 4, 3, ""))
      val insideDB = TopListPerson.get(person.id)
//            println("inside db: "+insideDB)
      insideDB must containAllOf(expected)
      //
      val json2 = Json.obj(
        "rankings" -> Json.arr(
          Json.obj("target" -> JsNumber(4), "position" -> JsNumber(1), "comment" -> JsString("")),
          Json.obj("target" -> JsNumber(5), "position" -> JsNumber(2), "comment" -> JsString("")),
          Json.obj("target" -> JsNumber(6), "position" -> JsNumber(3), "comment" -> JsString(""))
        )
      )

      val req2: Request[AnyContent] = FakeRequest().withJsonBody(json2).
        withCookies(ck) // Fake cookie from the WithloggedUser trait

      val result2 = TopListController.apply().apply(req2)

      val expected2 = List(TopListPerson(3, 6, 4, 1, ""), TopListPerson(4, 6, 5, 2, ""), TopListPerson(5, 6, 6, 3, ""))
      val insideDB2 = TopListPerson.get(person.id)
//      println("inside db 2 : "+insideDB2)
      insideDB2 must containAllOf(expected2)
    }

  }
}