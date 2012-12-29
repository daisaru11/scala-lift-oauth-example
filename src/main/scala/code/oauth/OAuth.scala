package code.oauth

import dispatch._
import dispatch.oauth._
import dispatch.oauth.OAuth._
import java.net.URI

trait OAuthProvider {

  def svc: Request

    /** Get a request token with no callback URL, out-of-band
     * authorization assumed */
  def request_token(consumer: Consumer): Handler[Token] =
    request_token(consumer, OAuth.oob)

  def request_token(consumer: Consumer, callback_url: String) =
    svc.secure.POST / "request_token" <@ (consumer, callback_url) as_token

  def authorize_url(token: Token) =
    svc / "authorize" with_token token

  def authenticate_url(token: Token) =
    svc / "authenticate" with_token token

  def access_token(consumer: Consumer, token: Token, verifier: String) =
    svc.secure.POST / "access_token" <@ (consumer, token, verifier) >% {
      m => (Token(m).get, m("user_id"))
    }

}

abstract class OAuthUserInfo ( val accessToken: Token, val uid: String )

abstract class OAuthAccess (
  val consumer: Consumer,
  val callbackUrl: String
) {
  def requestToken(): Token
  def authorizeUrl(requestToken: Token): URI
  //def accessToken(requestToken: Token): OAuthUserInfo
  def accessToken(requestToken: Token, verifier: String): OAuthUserInfo
}

