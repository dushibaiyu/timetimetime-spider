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
        println("tag tp string: ${tag.toString()}")
       // println(tag.text());
    }
}