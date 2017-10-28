package me.duyu.yuedu.spider

import org.jsoup.Jsoup

fun hanleHomePage(data : String)
{
    //println("start handle page!")
    val page = Jsoup.parse(data)
    val ele = page.getElementById("wrap")
    //println("wrap : \n${ele.toString()}")
    val lefts = ele.getElementsByClass("columnimg");
    //println("lefts size : ${lefts.size.toString()}")
    for (element in lefts){
        val tag = element.getElementById("columnimgleft").getElementsByTag("a").first();
        saveUrl(tag.attr("abs:href"),tag.attr("title"))
    }
}

fun saveUrl(url : String, title : String)
{
    println("url: $url")
    println("title: $title")
    if(url.startsWith("http://www.timetimetime.net/yuedu/"))
    {

    }

    if(url.startsWith("http://www.timetimetime.net/yuedu/")){

    }
}