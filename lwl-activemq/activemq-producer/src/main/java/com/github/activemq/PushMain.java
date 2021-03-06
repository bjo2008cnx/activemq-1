package com.github.activemq;

import com.github.activemq.domain.News;
import com.github.activemq.service.PushService;
import com.github.activemq.service.SpringBeanUtil;

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
        for (int i = 0; i < 1_0; i++) {
            News news = News.builder().author("zhangsuan" + i).content("content.hs" + i).url("http://www.aba.com" + i).id(i).title("good book" + i).build();
            newsPushService.push(news);
            System.out.println(Thread.currentThread() + ":" + i + ": pushed");
        }
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
}