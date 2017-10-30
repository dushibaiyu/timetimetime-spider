package me.duyu.yuedu.spider

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.client.WebClient
import io.vertx.core.logging.Logger
import io.vertx.core.logging.LoggerFactory
import kotlin.collections.mutableListOf;

typealias Handler = (Manger,HttpResponse<Buffer>) -> Unit

enum class ArticleType{
    YueDu,
    YuanChuang,
}

data class ArticleUrlInfo(val title : String, val url : String, val type : ArticleType)

class Manger(val vertx : Vertx, val options : WebClientOptions, val loger : Logger = LoggerFactory.getLogger("manger"))
{
    private val _urlList = mutableListOf<ArticleUrlInfo>()
    private var _currtUrl : Int = 1;
    private val startUrl: String = "http://www.timetimetime.net/";
    private val maxPage : Int = 216;

    var startHandleList = false;

    fun addUrl(url : ArticleUrlInfo)  = _urlList.add(url);

    fun getUrl() : ArticleUrlInfo? {
        var rv : ArticleUrlInfo? = null;
        if(_urlList.isNotEmpty()){
            rv = _urlList.get(0)
            _urlList.removeAt(0)
        }
        return rv;
    }

    fun isEndPage() : Boolean = _currtUrl > maxPage

    fun getPageUrl() : String {
        when(_currtUrl) {
            in 2..maxPage ->  return "${startUrl}page_${_currtUrl.toString()}.html"
            else -> return startUrl
        }
    }

}

fun Manger.handleList(mustCall : Boolean = false)
{
    val url = this.getUrl();
    when (url){
        null ->{ if(this.isEndPage()) this.vertx.close()}
        else ->{ if(mustCall || (!this.startHandleList)){
            //println(getPathName(url.url))
            this.startHandleList = true;
            this.vertx.setTimer(200){
                _->this.getData(url.url,{manger,rep -> handleArticleData(manger, rep,url)})
            }
        }}
    }
}

fun Manger.getData(url : String, handle : Handler){
    val client = WebClient.create(this.vertx,this.options)
    val request = client.getAbs(url);
    this.loger.trace("get the url {}", url);
    println("get the url : $url")
    request.setHeaders()
    request.send{
        if(it.succeeded()){
            handle(this,it.result())
        } else {
            println(it.toString())
        }
    }
}


fun <T> HttpRequest<T>.setHeaders(){
    this.headers().add("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
            .add("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
}