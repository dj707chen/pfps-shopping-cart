package shop.http.routes.secured

import cats.effect.Sync
import cats.implicits._
import io.estatico.newtype.ops._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server._
import shop.algebras.Orders
import shop.domain.order._
import shop.http.auth.users.CommonUser
import shop.http.json._

final class OrderRoutes[F[_]: Sync](
    orders: Orders[F]
) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/orders"

  private val httpRoutes: AuthedRoutes[CommonUser, F] = AuthedRoutes.of {

    case GET -> Root as user =>
      Ok(orders.findBy(user.value.id))

    case GET -> Root / UUIDVar(orderId) as user =>
      Ok(orders.get(user.value.id, orderId.coerce[OrderId]))

  }

  def routes(authMiddleware: AuthMiddleware[F, CommonUser]): HttpRoutes[F] = Router(
    prefixPath -> authMiddleware(httpRoutes)
  )

}
