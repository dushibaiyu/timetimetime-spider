package me.duyu.yuedu.spider

import com.overzealous.remark.Options
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.core.buffer.Buffer
import org.jsoup.Jsoup
import java.net.URI;
import com.overzealous.remark.Remark
import io.vertx.core.file.FileSystem

fun handlePageList(manger : Manger, body : HttpResponse<Buffer>)
{
    val page = Jsoup.parse(body.bodyAsString()).getElementById("wrap")
    val lefts = page.getElementsByClass("columnimg");
    for (element in lefts){
        val tag = element.getElementById("columnimgleft").getElementsByTag("a").first();
        saveUrl(manger,tag.attr("abs:href"),tag.attr("title"))
    }
    manger.handleList();

    if(!manger.isEndPage()){
        val url = manger.getPageUrl();
        manger.vertx.setTimer(200,{_-> manger.getData(url, ::handlePageList)})
    }
}

fun saveUrl(manger : Manger, url : String, title : String)
{
    manger.loger.trace("get the article : url = {} ,  title = {}", url, title)
    //println("$url \n $title")
    if(url.indexOf("/yuedu/") > 0)
    {
        var article = ArticleUrlInfo(title,url,ArticleType.YueDu)
        manger.addUrl(article)
    }

    if(url.indexOf("/yuanchuang/") > 0){
        var article = ArticleUrlInfo(title,url,ArticleType.YuanChuang)
        manger.addUrl(article)
    }
}

fun handleArticleData(manger : Manger, body : HttpResponse<Buffer>, article : ArticleUrlInfo)
{
    val page = Jsoup.parse(body.bodyAsString()).getElementById("wrap").select("div.singlebox").first()
    val title = page.getElementsByTag("h1").first();
    val abody = page.select("div.neir.a2").first()
    val time = page.select("div.neirongxinxi.a9").first();
    val img = abody.select("a > img").first()
    val imgUrl= img?.attr("src")?:""

    val bufer = Buffer.buffer("# ");
    bufer.appendString(title?.text()?:"");
    bufer.appendString("\n------------------------\n\n");

    val remark = Remark(Options.github())
    bufer.appendString(remark.convert(abody?.html()))

    bufer.appendString("\n\n-----------------------\n* 发布信息：");
    bufer.appendString(time?.text()?:"");
    bufer.appendString("\n* 原文地址: ");
    bufer.appendString(article.url);
    bufer.appendString("\n* 原文标题： ");
    bufer.appendString(article.title);
    bufer.appendString("\n\n");

    saveToFile(manger, bufer, article,imgUrl)

    manger.handleList(true);
}

fun saveToFile(manger: Manger, buffer: Buffer, article : ArticleUrlInfo, imgUrl : String)
{
    val fsys = manger.vertx.fileSystem()
    val spath = "data/${article.getFolderName()}"
    val art = "$spath/${article.title}.md";
    val imageName = getPathName(imgUrl);
    val img = "$spath/$imageName";
    fsys.exists(spath){it->if(it.succeeded()){
        if(it.result()){
            saveToFile(manger,fsys,buffer, art, img ,imgUrl)
        } else {
            fsys.mkdir(spath){
                if(it.succeeded())
                    saveToFile(manger,fsys,buffer, art, img ,imgUrl)
            }
        }
    }}
}

fun saveToFile(manger: Manger,fsys: FileSystem, buffer: Buffer, fname : String,iname : String , imgUrl : String)
{
    fsys.writeFile(fname,buffer){_->}
    if(imgUrl.isNotEmpty()) {
        manger.getData(imgUrl) { _, res -> fsys.writeFile(iname, res.bodyAsBuffer()) { _ -> } }
    }
}

fun getPathName(url : String) : String
{
    return url.substringAfterLast('/');
}

fun ArticleUrlInfo.getFolderName() : String
{
    val uri = URI(this.url);
    val path = uri.path;
    return path.replace("/","_")
}