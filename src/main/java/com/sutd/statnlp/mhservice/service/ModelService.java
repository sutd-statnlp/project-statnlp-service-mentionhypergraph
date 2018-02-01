package com.sutd.statnlp.mhservice.service;

import org.statnlp.example.mention_hypergraph.Span;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ModelService {

    CompletableFuture<List<Span>> analyzeBySmallModel(String text, Double penalty);
    CompletableFuture<List<Span>> analyzeByMainModel(String text, Double penalty);
}
