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


object GooglePlusLogin {
  val title = "Google+"
  val name = "google_plus"
  val consumerKey = Props.get("oauth.google_plus.consumer_key") openOr "INPUT YOU CONSUMER KEY"
  val consumerSecret = Props.get("oauth.google_plus.consumer_secret") openOr "INPUT YOU CONSUMER SECRET"
  val consumer = Consumer(consumerKey, consumerSecret)
  val callbackUrl = "http://code.daisaru11.jp:8080/google_plus/callback"
  val scope = List("https://www.googleapis.com/auth/userinfo.profile"," https://www.googleapis.com/auth/plus.me")
  val oauth = new GooglePlusOAuthAccess(consumer, scope, callbackUrl)

  def callbackMenuLocParams: List[LocParam[Unit]] = 
    Hidden ::
    Template(() => callback(S.request)) ::
    Nil

  def callback(request: Box[Req]):NodeSeq = {
    (
     for (
       code <- S.param("code")
     ) yield {
       println("### GOOGLE AUTH CODE ###")
       println(code)
       val userInfo = oauth.accessToken(code)
       println("### GOOGLE OAUTH TOKEN ###")
       println(userInfo.accessToken.value)

       S.redirectTo("/")
     }
    ) getOrElse {
      S.error("invalid token")
      S.redirectTo("/")
    }
  }
}

class Googleplusloginbinder {

  def login(xhtml: NodeSeq) = {
    if (S.post_?) {
       val url = GooglePlusLogin.oauth.authorizeUrl()
       S.redirectTo(url.toString())
    }
    bind("login", xhtml, 
        AttrBindParam("action",S.uri,"action"),
        "submit" -> (<input type="submit" value="login with google plus" />))
  }
}

