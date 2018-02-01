package com.sutd.statnlp.mhservice.service;

import com.sutd.statnlp.mhservice.model.MainModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.statnlp.example.mention_hypergraph.Span;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
public class ModelServiceImpl implements ModelService {

    private final Logger log = LoggerFactory.getLogger(ModelServiceImpl.class);

    private final MainModel mainModel;

    public ModelServiceImpl(MainModel mainModel) {
        this.mainModel = mainModel;
    }

    @Override
    @Async
    public CompletableFuture<List<Span>> analyzeBySmallModel(String text, Double penalty) {
        log.debug("request to analyze text: {} with penalty: {} by small model",text,penalty);
        return CompletableFuture.completedFuture(mainModel.executeSmall(text,penalty));
    }

    @Override
    @Async
    public CompletableFuture<List<Span>> analyzeByMainModel(String text, Double penalty) {
        log.debug("request to analyze text: {} with penalty: {} by main model",text,penalty);
        return CompletableFuture.completedFuture(mainModel.executeMain(text,penalty));
    }
}
