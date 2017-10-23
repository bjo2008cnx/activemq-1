package com.lwl.activemq;

import com.lwl.activemq.domain.News;
import com.lwl.activemq.service.PushService;
import com.lwl.activemq.service.SpringBeanUtil;

/**
 * PushMain
 *
 * @author Michael.Wang
 * @date 2017/10/23
 */
public class PushMain {
    public static void main(String[] args) {
        run();
    }

    public static void run() {
        SpringBeanUtil.initContextFile("applicationContext.xml");
        PushService newsPushService = SpringBeanUtil.getBean("newsPushService", PushService.class);
        News news = News.builder().author("zhangsuan").content("content.hs").url("http://www.aba.com").id(1).title("good book").build();
        newsPushService.push(news);
        System.out.println("pushed");
    }
}