package com.gin.mergegfassets.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class TasksUtil {

    /**
     * 创建线程池
     * @param name     线程池名称
     * @param coreSize 核心线程池大小
     * @return 线程池
     */
    public static ThreadPoolTaskExecutor getExecutor(String name, Integer coreSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(coreSize);
        //最大线程数
        executor.setMaxPoolSize(coreSize);
        //队列容量
        executor.setQueueCapacity(1000);
        //活跃时间
        executor.setKeepAliveSeconds(30);
        //线程名字前缀
        executor.setThreadNamePrefix(name + "-");

        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        System.out.printf("Created %d Executors\n",coreSize);
        return executor;
    }
}
