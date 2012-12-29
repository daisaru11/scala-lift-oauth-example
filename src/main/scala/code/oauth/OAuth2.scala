package code.oauth

import dispatch._
import dispatch.oauth._
import dispatch.oauth.OAuth._
import dispatch.json.JsHttp._
import Request.{encode_%, decode_%}
import java.net.URI
import collection.Map
import collection.immutable.{TreeMap, Map=>IMap}

case class Token2(value:String)
object Token2 {
  def apply[T <: Any](m: Map[String, T]): Option[Token2] = List("access_token").flatMap(m.get) match {
    case value :: Nil => Some(Token2(value.toString))
    case _ => None
  }
}

trait OAuth2Provider {

  def svc: Request

  def authorize_url(consumer: Consumer, scope: List[String] ) =
    svc / "authorize" <<? Map(
      "client_id" -> consumer.key,
      "scope" -> scope.mkString(" ")
      )

  def authorize_url(consumer: Consumer, redirect_uri:String, scope: List[String] ) =
    svc / "authorize" <<? Map(
      "client_id" -> consumer.key,
      "redirect_uri" -> redirect_uri,
      "scope" -> scope.mkString(" ")
    )

  def authenticate_url(consumer: Consumer, scope: List[String] ) =
    svc / "authenticate" <<? Map(
      "client_id" -> consumer.key,
      "scope" -> scope.mkString(" ")
      )

  def access_token(consumer: Consumer, code: String, redirect_uri:String) =
    svc.secure.POST / "access_token" << Map(
      "grant_type" -> "authorization_code",
      "client_id" -> consumer.key,
      "client_secret" -> consumer.secret,
      "code" -> code,
      "redirect_uri" -> redirect_uri
    ) ># ( 'access_token ! str andThen {
      m => new Token2(m)
    })

}

abstract class OAuth2UserInfo ( val accessToken: Token2 )

abstract class OAuth2Access (
  val consumer: Consumer,
  val scope: List[String],
  val callbackUrl: String
) {
  def authorizeUrl(): URI
  //def accessToken(requestToken: Token): OAuthUserInfo
  def accessToken(code: String): OAuth2UserInfo
}


