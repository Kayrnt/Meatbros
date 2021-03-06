package fr.kayrnt

import play.api.test.FakeApplication
import securesocial.testkit.WithLoggedUser._
import play.api.test.FakeApplication
import securesocial.testkit.WithLoggedUser
import models.Person
import java.sql.{Time, Timestamp}

/**
 * User: Kayrnt
 * Date: 09/02/2014
 * Time: 23:35
 */
class PersonSpec extends BaseSpec {

  "a troll javascript dev" should {

    "be inserted properly and retrieved" in new WithLoggedUser() {

      val beerbro = Person(0, broname = Some("Beerbro"), nationality = Some("Parigo"))

      Person.insert(beerbro) mustNotEqual null

      val thatbro = Person.findByBroId(1).orNull

      thatbro mustEqual beerbro
    }

  }

}
