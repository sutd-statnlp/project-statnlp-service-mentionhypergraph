package com.sutd.statnlp.mhservice.rest;

import com.sutd.statnlp.mhservice.dto.AnalysisDTO;
import com.sutd.statnlp.mhservice.service.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.statnlp.example.mention_hypergraph.Span;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class ModelResource {

    private final Logger log = LoggerFactory.getLogger(ModelResource.class);

    private final ModelService modelService;

    public ModelResource(ModelService modelService) {
        this.modelService = modelService;
    }

    @PostMapping("/analyze/small")
    @Async
    public CompletableFuture<List<Span>> postAnalyzeBySmallModel(@RequestBody AnalysisDTO analysisDTO){
        log.debug("request to analyze text by small model: {}",analysisDTO);
        return modelService.analyzeBySmallModel(analysisDTO.getText(),analysisDTO.getPenalty());
    }

    @PostMapping("/analyze/main")
    @Async
    public CompletableFuture<List<Span>> postAnalyzeByMainModel(@RequestBody AnalysisDTO analysisDTO){
        log.debug("request to analyze text by main model: {}",analysisDTO);
        return modelService.analyzeByMainModel(analysisDTO.getText(),analysisDTO.getPenalty());
    }
}
