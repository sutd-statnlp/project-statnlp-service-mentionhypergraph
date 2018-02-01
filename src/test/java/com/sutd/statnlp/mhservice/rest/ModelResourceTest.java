package com.sutd.statnlp.mhservice.rest;

import com.sutd.statnlp.mhservice.MentionHypergraphServiceApplication;
import com.sutd.statnlp.mhservice.dto.AnalysisDTO;
import com.sutd.statnlp.mhservice.service.ModelService;
import com.sutd.statnlp.mhservice.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MentionHypergraphServiceApplication.class)
public class ModelResourceTest {

    private static final String DEFAULT_TEXT = "AAAAAAAAAA AAAAAAAAAA AAAAAAAAAA AAAAAAAAAA AAAAAAAAAA AAAAAAAAAA";
    private static final Double DEFAULT_PENALTY = 0.0;

    @Autowired
    private ModelService modelService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restMockMvc;

    private AnalysisDTO analysisDTO;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ModelResource modelResource = new ModelResource(modelService);
        this.restMockMvc = MockMvcBuilders.standaloneSetup(modelResource)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    public static AnalysisDTO creaetAnalysisDTO() {
        AnalysisDTO analysisDTO = new AnalysisDTO();
        analysisDTO.setText(DEFAULT_TEXT);
        analysisDTO.setPenalty(DEFAULT_PENALTY);
        return analysisDTO;
    }

    @Before
    public void initTest() {
        analysisDTO = creaetAnalysisDTO();
    }


    @Test
    public void postAnalyzeTextBySmallModel() throws Exception {
        restMockMvc.perform(post("/api/analyze/small")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(analysisDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void postAnalyzeTextByMainModel() throws Exception {
        restMockMvc.perform(post("/api/analyze/main")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(analysisDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }
}
