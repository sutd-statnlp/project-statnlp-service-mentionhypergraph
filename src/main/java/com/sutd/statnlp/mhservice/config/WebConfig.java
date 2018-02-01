package com.sutd.statnlp.mhservice.config;

import com.sutd.statnlp.mhservice.model.MainModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class WebConfig {

    @Bean
    public MainModel mainModel(){
        return new MainModel();
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setThreadNamePrefix("SUTDStatNLP-");
        executor.initialize();
        return executor;
    }
}
