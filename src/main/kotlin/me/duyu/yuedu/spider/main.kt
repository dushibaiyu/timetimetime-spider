package me.duyu.yuedu.spider

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions;

const val startUrl: String = "http://www.timetimetime.net/"

fun main(args : Array<String>)
{
    val options = VertxOptions();
    options.workerPoolSize = 4;
    options.eventLoopPoolSize = 1;
    val vertx = Vertx.vertx(options);
    val webOptions = WebClientOptions().setUserAgentEnabled(true)
            .setUserAgent("Mozilla/5.0 (X11; Linux x86_64; rv:56.0) Gecko/20100101 Firefox/56.0")
            .setFollowRedirects(true).setTrustAll(true).setKeepAlive(false)
    val client = WebClient.create(vertx,webOptions)
    val request = client.getAbs(startUrl);
    request.headers().add("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
            .add("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    request.send{
        if(it.succeeded()){
            //val response = it.result()
            val data = it.result().bodyAsString();
            //print("get body \n $data");
            vertx.executeBlocking<Any>({ future ->
                hanleHomePage(data)
                future.complete()
            }, false,{vertx.close()})
//            vertx.setTimer(500){
//                getNextPage(vertx, webOptions,2)
//            }
        } else {
            println(it.toString())
        }
    }

}

fun getNextPage(vertx : Vertx,options: WebClientOptions,page : Int)
{
    val next = page + 1;
    if(next > 216) {
        vertx.close();
        return;
    }
    val pageUrl = "${startUrl}page_${page.toString()}.html"
    val client = WebClient.create(vertx,options)
    val request = client.getAbs(pageUrl);
    request.headers().add("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
            .add("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    request.send{
        if(it.succeeded()){
            //val response = it.result()
            hanleHomePage(it.result().bodyAsString())
            vertx.setTimer(2000){
                getNextPage(vertx, options,next)
            }
        } else {
            println(it.toString())
        }
    }

}