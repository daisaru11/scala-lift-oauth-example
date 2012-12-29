package code.oauth


import dispatch._
import dispatch.oauth._
import dispatch.oauth.OAuth._
import dispatch.json.JsHttp._
import Request.{encode_%, decode_%}
import java.net.URI
import collection.Map
import collection.immutable.{TreeMap, Map=>IMap}


object FacebookOAuthProvider extends OAuth2Provider {

  val svc = :/("graph.facebook.com") / "oauth"

  override def authorize_url(consumer: Consumer, redirect_uri:String, scope: List[String] ) =
    :/("www.facebook.com") / "dialog" / "oauth" <<? Map(
      "client_id" -> consumer.key,
      "redirect_uri" -> redirect_uri,
      "scope" -> scope.mkString(" ")
    )

  override def access_token(consumer: Consumer, code: String, redirect_uri:String) = {
    val split_decode: (String => IMap[String, String]) = {
      case null => IMap.empty
      case query => IMap.empty ++ query.trim.split('&').map { nvp =>
        nvp.split("=").map(decode_%) match {
          case Array(name) => name -> ""
          case Array(name, value) => name -> value
        }
      }
    }
    svc.secure.POST / "access_token" << Map(
      "grant_type" -> "authorization_code",
      "client_id" -> consumer.key,
      "client_secret" -> consumer.secret,
      "code" -> code,
      "redirect_uri" -> redirect_uri
    ) >- ( split_decode andThen {
        (m: IMap[String,String]) => Token2(m).get
      }
    )
  }

}

class FacebookOAuthUserInfo ( accessToken: Token2 ) extends OAuth2UserInfo ( accessToken )

class FacebookOAuthAccess (
  consumer: Consumer,
  scope: List[String],
  callbackUrl: String
) extends OAuth2Access ( consumer, scope, callbackUrl) {

  def authorizeUrl(): URI = {
    //TwitterOAuthProvider.authorize_url(requestToken, callbackUrl).to_uri
    FacebookOAuthProvider.authorize_url(consumer, callbackUrl, scope).to_uri
  }

  def accessToken(code: String): OAuth2UserInfo = {
    // access tokenの取得
    //val (accessToken: Token)
    val accessToken:Token2
      = Http(FacebookOAuthProvider.access_token(consumer, code, callbackUrl ))
    new FacebookOAuthUserInfo(accessToken)
  }

}

