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

      ck

      val beerbro = Person(0, "", "", "", None, None, true, "", "", "",
        None, None, Some("Beerbro"), None, None, None, None,
        Some("Parigo"), true, false, new Timestamp(System.currentTimeMillis()))

      Person.insert(beerbro) mustNotEqual null

      val thatbro = Person.findByBroId(1).orNull

      thatbro mustEqual beerbro
    }

  }

}
