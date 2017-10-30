package me.duyu.yuedu.spider

import io.vertx.ext.web.client.HttpResponse;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer
import org.jsoup.Jsoup

fun handlePageList(manger : Manger, body : HttpResponse<Buffer>)
{
    val page = Jsoup.parse(body.bodyAsString()).getElementById("wrap")
    val lefts = page.getElementsByClass("columnimg");
    for (element in lefts){
        val tag = element.getElementById("columnimgleft").getElementsByTag("a").first();
        saveUrl(manger,tag.attr("abs:href"),tag.attr("title"))
    }
//    manger.handleList();
//    if(!manger.isEndPage()){
//        val url = manger.getPageUrl();
//        manger.vertx.setTimer(200,{_-> manger.getData(url, ::handlePageList)})
//    }
}

fun saveUrl(manger : Manger, url : String, title : String)
{
    if(url.indexOf("/yuedu/") > 0)
    {
        var article = ArticleUrlInfo(url,title,ArticleType.YueDu)
        manger.addUrl(article)
    }

    if(url.indexOf("/yuanchuang/") > 0){
        var article = ArticleUrlInfo(url,title,ArticleType.YuanChuang)
        manger.addUrl(article)
    }
}

fun handleArticleData(manger : Manger, body : HttpResponse<Buffer>)
{
    val page = Jsoup.parse(body.bodyAsString()).getElementById("wrap")
    val arcticle = page.select("div.singlebox").first();
    val title = arcticle.getElementsByTag("h1").first();
    println("title : $title")
    val time = arcticle.select("i.fa.fa-clock-o").first()

//    manger.handleList(true);
}