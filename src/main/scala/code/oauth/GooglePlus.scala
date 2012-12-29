package code.oauth


import dispatch._
import dispatch.oauth._
import dispatch.oauth.OAuth._
import dispatch.json._
import dispatch.json.JsHttp._
import Request.{encode_%, decode_%}
import java.net.URI
import collection.Map
import collection.immutable.{TreeMap, Map=>IMap}


object GooglePlusOAuthProvider extends OAuth2Provider {

  //https://accounts.google.com/o/oauth2/auth
  val svc = :/("accounts.google.com") / "o" / "oauth2"

  override def authorize_url(consumer: Consumer, redirect_uri:String, scope: List[String] ) =
    svc / "auth" <<? Map(
      "client_id" -> consumer.key,
      "redirect_uri" -> redirect_uri,
      "response_type" -> "code",
      "scope" -> scope.mkString(" ")
    )

  override def access_token(consumer: Consumer, code: String, redirect_uri:String) = {
    val filter_json: (JsValue => IMap[String,String]) = {
      case JsObject(d) => {
        for {
          (JsString(key), JsString(value)) <- d
        } yield key -> value
      }
      case _ => IMap.empty
    }
    svc.secure.POST / "token" << Map(
      "grant_type" -> "authorization_code",
      "client_id" -> consumer.key,
      "client_secret" -> consumer.secret,
      "code" -> code,
      "redirect_uri" -> redirect_uri
    ) ># ( filter_json andThen {
        (m: IMap[String,String]) => Token2(m).get
      }
    )
  }

}

class GooglePlusOAuthUserInfo ( accessToken: Token2 ) extends OAuth2UserInfo ( accessToken )

class GooglePlusOAuthAccess (
  consumer: Consumer,
  scope: List[String],
  callbackUrl: String
) extends OAuth2Access ( consumer, scope, callbackUrl) {

  def authorizeUrl(): URI = {
    //TwitterOAuthProvider.authorize_url(requestToken, callbackUrl).to_uri
    GooglePlusOAuthProvider.authorize_url(consumer, callbackUrl, scope).to_uri
  }

  def accessToken(code: String): OAuth2UserInfo = {
    // access tokenの取得
    //val (accessToken: Token)
    val accessToken:Token2
      = Http(GooglePlusOAuthProvider.access_token(consumer, code, callbackUrl ))
    new GooglePlusOAuthUserInfo(accessToken)
  }

}
