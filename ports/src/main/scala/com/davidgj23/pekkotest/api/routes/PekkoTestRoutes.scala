package com.davidgj23.pekkotest.api.routes

import com.davidgj23.pekkotest.api.dtos.TestDTO
import io.circe.generic.auto._
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.mdedetrich.pekko.http.support.CirceHttpSupport._

class PekkoTestRoutes() {

  def routes: Route = test

  def test: Route =
    path("pekko-test") {
      post {
        entity(as[TestDTO]) { dto =>
          println(s"Here we are: ${dto.code}")
          Thread.sleep(5000)
          complete(StatusCodes.OK -> {
            println("Completing route...")
            "Success!"
          })
        }
      }
    }
}
