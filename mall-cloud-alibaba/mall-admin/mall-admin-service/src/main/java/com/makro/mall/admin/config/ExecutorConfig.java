package com.makro.mall.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/3/22
 */
@Configuration
@EnableAsync
public class ExecutorConfig {

    @Bean("threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 线程池创建的核心线程数，线程池维护线程的最少数量，即使没有任务需要执行，也会一直存活
        // 如果设置allowCoreThreadTimeout=true(默认false)，核心线程会超时关闭
        executor.setCorePoolSize(5);
        //  executor.setAllowCoreThreadTimeOut(true);

        // spring的executor默认用BlockingQueue-阻塞队列，当核心线程数达到最大时，新任务就会在队列中排队等待执行
        executor.setQueueCapacity(124);

        // 最大线程池数量，当线程数>=corePoolSize,且队列已满时，线程池会创建新线程来处理任务
        // 任务队列已经满时，且当线程数=maxPoolSize,线程池会拒绝处理任务抛出异常
        executor.setMaxPoolSize(20);

        // 当线程空闲时间达到keepAliveSeconds时，线程会退出，知道线程数量=corePoolSize
        // 允许线程空闲时间30s，当maxPoolSize的线程在空闲时间到达后销毁
        // 如果allowCoreThreadTimeOut设置为true，则会直到线程数量=0
        executor.setKeepAliveSeconds(30);

        // spring提供的ThreadPoolTaskExecutor线程池，有setThreadNamePrefix方法，可以供我们排查日志时跟踪
        executor.setThreadNamePrefix("异步处理调度-");

        // 拒绝策略
        // CallerRunsPolicy():交由调用方线程运行，比如main线程；如果添加线程池失败，那么主线程会自己去执行该任务，不会等待线程池的线程去执行
        // AbortPolicy():该策略是线程池的默认策略，如果线程池队列满了且线程池已到最大线程数丢掉这个任务并抛出RejectedExecutionException异常
        // DiscardPolicy():如果线程池队列满了且线程池达到最大线程数，会直接丢掉这个任务并且不会有任何异常
        // DiscardOldestPolicy():如果线程池队列满了切线程池达到最大线程数，直接从队列中丢弃最早进入队列的任务先删掉腾出空间，再尝试入队列
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
