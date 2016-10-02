package com.epickur.api

import org.scalatestplus.play._
import play.api.test.FakeRequest
import play.api.test.Helpers._

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  * For more information, consult the wiki.
  */
class ApplicationSpec extends PlaySpec with OneAppPerTest {

	"Routes" should {
		"Send 404 on a bad request" in {
			route(app, FakeRequest(GET, "/boomz")).map(status) mustBe Some(NOT_FOUND)
		}
	}
}