package code.snippet

import code.oauth._
import scala.xml.NodeSeq
import net.liftweb.common._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import net.liftweb.http._
import net.liftweb.http.S._
import net.liftweb.http.SHtml._
import net.liftweb.util.Props
import net.liftweb.util.Helpers._
import dispatch.oauth._
import dispatch.oauth.OAuth._


object TwitterRequestTokenVar extends SessionVar[Box[Token]](Empty)

object TwitterLogin {
	val title = "Twitter"
	val name = "twitter"
  val consumerKey = Props.get("oauth.twitter.consumer_key") openOr "INPUT YOU CONSUMER KEY"
  val consumerSecret = Props.get("oauth.twitter.consumer_secret") openOr "INPUT YOU CONSUMER SECRET"
  val consumer = Consumer(consumerKey, consumerSecret)
  val callbackUrl = "http://code.daisaru11.jp:8080/twitter/callback"
  val oauth = new TwitterOAuthAccess(consumer, callbackUrl)

  def callbackMenuLocParams: List[LocParam[Unit]] = 
    Hidden ::
    Template(() => callback(S.request)) ::
    Nil

  def callback(request: Box[Req]):NodeSeq = {
    (
     for (
       oauthToken <- S.param("oauth_token");
       oauthVerifier <- S.param("oauth_verifier");
       requestToken <- TwitterRequestTokenVar
     ) yield {
       // access tokenの取得
       val userInfo = oauth.accessToken(requestToken, oauthVerifier)
       println("### TWITTER OAUTH TOKEN ###")
       println(userInfo.accessToken.value)
       println(userInfo.accessToken.secret)

       S.redirectTo("/")
     }
    ) getOrElse {
      S.error("invalid token")
      S.redirectTo("/")
    }
  }


}

class Twitterloginbinder {

  def login(xhtml: NodeSeq) = {
    if (S.post_?) {
       val requestToken = TwitterLogin.oauth.requestToken
       val url = TwitterLogin.oauth.authorizeUrl(requestToken)
       TwitterRequestTokenVar(Full(requestToken))
       S.redirectTo(url.toString())
    }
    bind("login", xhtml, 
        AttrBindParam("action",S.uri,"action"),
        "submit" -> (<input type="submit" value="login with twitter" />))
  }
}

