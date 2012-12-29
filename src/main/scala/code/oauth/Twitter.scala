package code.oauth

import dispatch._
import dispatch.oauth._
import dispatch.oauth.OAuth._
import java.net.URI

object TwitterOAuthProvider extends OAuthProvider {

  val svc = :/("api.twitter.com") / "oauth"

  //def access_token(consumer: Consumer, token: Token) = // verifierはなし
    //svc.secure.POST / "access_token" <@ (consumer, token) >% {
      //m => (Token(m).get, m("uid"))
    //}

}

class TwitterOAuthUserInfo ( accessToken: Token, uid: String ) extends OAuthUserInfo ( accessToken, uid )

class TwitterOAuthAccess (
  consumer: Consumer,
  callbackUrl: String
) extends OAuthAccess ( consumer, callbackUrl) {

  def requestToken(): Token = {
    val http = new Http
    http(TwitterOAuthProvider.request_token(consumer, callbackUrl))
  }

  def authorizeUrl(requestToken: Token): URI = {
    //TwitterOAuthProvider.authorize_url(requestToken, callbackUrl).to_uri
    TwitterOAuthProvider.authorize_url(requestToken).to_uri
  }

  def accessToken(requestToken: Token, verifier: String): OAuthUserInfo = {
    val (accessToken: Token, uid: String)
      = Http(TwitterOAuthProvider.access_token(consumer, requestToken, verifier))
    new TwitterOAuthUserInfo(accessToken, uid)
  }

}

