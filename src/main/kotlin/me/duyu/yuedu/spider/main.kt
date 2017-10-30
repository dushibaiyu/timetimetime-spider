@file:JvmName("main")
package me.duyu.yuedu.spider

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.web.client.WebClientOptions;

fun main(args : Array<String>)
{
    val options = VertxOptions();
    options.workerPoolSize = 1;
    options.eventLoopPoolSize = 1;
    val vertx = Vertx.vertx(options);
    val webOptions = WebClientOptions().setUserAgentEnabled(true)
            .setUserAgent("Mozilla/5.0 (X11; Linux x86_64; rv:56.0) Gecko/20100101 Firefox/56.0")
            .setFollowRedirects(true).setTrustAll(true).setKeepAlive(false)
    val fsys = vertx.fileSystem()
    if(!fsys.existsBlocking("data"))
        fsys.mkdirBlocking("data");
    val manger = Manger(vertx, webOptions);
    val url = manger.getPageUrl();
    println(url);
    manger.getData(url, ::handlePageList);
}