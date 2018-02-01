package com.sutd.statnlp.mhservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AnalysisDTO {

    private String text;

    private Double penalty;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getPenalty() {
        return penalty;
    }

    public void setPenalty(Double penalty) {
        this.penalty = penalty;
    }

    @Override
    public String toString() {
        return "AnalysisDTO{" +
                "text='" + text + '\'' +
                ", penalty=" + penalty +
                '}';
    }
}
