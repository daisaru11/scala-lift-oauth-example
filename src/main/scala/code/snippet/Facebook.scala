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



object FacebookLogin {
  val title = "Facebook"
  val name = "facebook"
  val consumerKey = Props.get("oauth.facebook.consumer_key") openOr "INPUT YOU CONSUMER KEY"
  val consumerSecret = Props.get("oauth.facebook.consumer_secret") openOr "INPUT YOU CONSUMER SECRET"
  val consumer = Consumer(consumerKey, consumerSecret)
  val callbackUrl = "http://code.daisaru11.jp:8080/facebook/callback"
  val scope = List("user_about_me")
  val oauth = new FacebookOAuthAccess(consumer, scope, callbackUrl)

  def callbackMenuLocParams: List[LocParam[Unit]] = 
    Hidden ::
    Template(() => callback(S.request)) ::
    Nil

  def callback(request: Box[Req]):NodeSeq = {
    println("callback facebook!")
    (
     for (
       code <- S.param("code")
     ) yield {
       // access tokenの取得
       println("### FACEBOOK AUTH CODE ###")
       println(code)
       val userInfo = oauth.accessToken(code)
       println("### FACEBOOK OAUTH TOKEN ###")
       println(userInfo.accessToken.value)



       S.redirectTo("/")
     }
    ) getOrElse {
      S.error("invalid token")
      S.redirectTo("/")
    }
  }
}

class Facebookloginbinder {

  def login(xhtml: NodeSeq) = {
    if (S.post_?) {
       val url = FacebookLogin.oauth.authorizeUrl()
       S.redirectTo(url.toString())
    }
    bind("login", xhtml, 
        AttrBindParam("action",S.uri,"action"),
        "submit" -> (<input type="submit" value="login with facebook" />))
  }
}


