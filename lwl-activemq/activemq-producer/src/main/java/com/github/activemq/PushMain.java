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
    static PushService newsPushService;

    static {
        SpringBeanUtil.initContextFile("applicationContext.xml");
        newsPushService = SpringBeanUtil.getBean("newsPushService", PushService.class);
    }

    public static void main(String[] args) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                PushMain.run();
            }
        };
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(r);
            t.start();
        }
    }

    public static void run() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100_000; i++) {
            News news = News.builder().author("zhangsuan" + i).content("content.hs" + i).url("http://www.aba.com" + i).id(i).title("good book" + i).build();
            newsPushService.push(news);
            System.out.println(Thread.currentThread() + ":" + i + ": pushed");
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
}