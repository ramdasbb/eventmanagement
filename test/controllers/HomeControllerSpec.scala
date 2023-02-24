package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.mvc.MessagesActionBuilder
import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {
  implicit val ec = app.injector.instanceOf[ExecutionContext]
  implicit val mc = app.injector.instanceOf[MessagesActionBuilder]

  "HomeController GET" should {
    "render the index page from a new instance of controller" in {
      val controller = new HomeController(stubControllerComponents())(ec, mc)
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Welcome to Play")
    }

    "render the index page from the application" in {
      val controller = inject[HomeController]
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Welcome to Play")
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Welcome to Play")
    }

    "render the index page from the router 2" in {
      val request = FakeRequest(GET, "/api/player/byid/1")
      val home = route(app, request).get
      println(" contentType(home)"+  contentType(home))
      status(home) mustBe OK
    }
  }
}
