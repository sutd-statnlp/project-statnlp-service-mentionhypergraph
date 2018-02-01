package com.sutd.statnlp.mhservice.model;

import com.sutd.statnlp.mhservice.util.ConstantUtil;
import com.sutd.statnlp.mhservice.util.FileUtil;
import org.statnlp.commons.POSTagger;
import org.statnlp.commons.types.Instance;
import org.statnlp.example.mention_hypergraph.*;
import org.statnlp.example.mention_hypergraph.MentionHypergraphInstance.WordsAndTags;
import org.statnlp.hypergraph.*;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* Copy from org.statnlp.example.MentionHypergraphDemo class and then customize */
public class MainModel {
    private ArrayList<Label> labels;

    //  add main function
    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InterruptedException, NoSuchFieldException {
        MainModel mainModel = new MainModel();

        List<Span> prediction =  mainModel.executeMain("Both the AMA and the Bush administration released reports this week saying out of control trial lawyers are driving doctors out of their practices all across the country .",ConstantUtil.DEFAULT_MENTION_PENALTY);
        System.out.println(prediction);
    }

    //  add executeMain function
    public  List<Span> executeMain(String text, Double penalty){
        if(FileUtil.writeTextToFile(text)) {
            try {
                return execute(ConstantUtil.MAIN_MODEL_PATH,ConstantUtil.TRIAL_DATA_PATH,penalty);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }


    //  add executeSmall function
    public List<Span> executeSmall(String text, Double penalty){
        if(FileUtil.writeTextToFile(text)) {
            try {
                return execute(ConstantUtil.SMALL_MODEL_PATH, ConstantUtil.TRIAL_DATA_PATH,penalty);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    //  customize execute function, which is the main function in org.statnlp.example.MentionHypergraphDemo class
    private List<Span> execute(String modelPath, String test_filename, Double mentionPenalty) throws IOException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InterruptedException {
        labels = new ArrayList();
        ObjectInputStream ois_label = new ObjectInputStream(new FileInputStream(ConstantUtil.LABELS_PATH));
        Label.LABELS = (Map) ois_label.readObject();
        Label.LABELS_INDEX = (Map) ois_label.readObject();
        ois_label.close();
        labels.addAll(Label.LABELS.values());
        System.out.println("labels:" + labels);
        int maxSize = 0;
        NetworkConfig.TRAIN_MODE_IS_GENERATIVE = false;
        NetworkConfig.CACHE_FEATURES_DURING_TRAINING = true;
        NetworkConfig.L2_REGULARIZATION_CONSTANT = 0.01D;
        NetworkConfig.OBJTOL = 1.0E-4D;
        NetworkConfig.PARALLEL_FEATURE_EXTRACTION = true;
        NetworkConfig.NUM_THREADS = 4;
        MentionHypergraphFeatureManager fm = new MentionHypergraphFeatureManager(new GlobalNetworkParam());
        MentionHypergraphNetworkCompiler compiler = new MentionHypergraphNetworkCompiler((Label[]) labels.toArray(new Label[labels.size()]), maxSize);
        if (NetworkConfig.TRAIN_MODE_IS_GENERATIVE) {
            GenerativeNetworkModel.create(fm, compiler);
        } else {
            DiscriminativeNetworkModel.create(fm, compiler, new PrintStream[0]);
        }

//        String modelPath = args[0];
        System.out.println("Reading Models...");
        long startTime = System.currentTimeMillis();
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelPath));
        NetworkModel model = (NetworkModel) ois.readObject();
        ois.close();
        Field _fm = NetworkModel.class.getDeclaredField("_fm");
        _fm.setAccessible(true);
        fm = (MentionHypergraphFeatureManager) _fm.get(model);
        long endTime = System.currentTimeMillis();
        System.out.printf("Done in %.3fs\n", (double) (endTime - startTime) / 1000.0D);
        int mentionPenaltyFeatureIndex = fm.getParam_G().getFeatureId(MentionHypergraphFeatureManager.FeatureType.MENTION_PENALTY.name(), "MP", "MP");
//        String test_filename = args[1];
        MentionHypergraphInstance[] testInstances = readData(test_filename, true, false);
        MentionHypergraphInstance[] var19 = testInstances;
        int var18 = testInstances.length;

        for (int var17 = 0; var17 < var18; ++var17) {
            MentionHypergraphInstance instance = var19[var17];
            maxSize = Math.max(maxSize, instance.size());
        }

//        double mentionPenalty = 0.0D;
//        if (args.length >= 3) {
//            mentionPenalty = Double.parseDouble(args[2]);
//        }

        if (mentionPenalty >= 0.0D) {
            fm.getParam_G().setWeight(mentionPenaltyFeatureIndex, mentionPenalty);
        }

        System.out.println(String.format("Mention penalty: %.1f", fm.getParam_G().getWeight(mentionPenaltyFeatureIndex)));
        Instance[] predictions = model.decode(testInstances, true);
        fm.getParam_G().setVersion(fm.getParam_G().getVersion() + 1);
        int corr = 0;
        int totalGold = 0;
        int totalPred = 0;
        int count = 0;
        Instance[] var26 = predictions;
        int var25 = predictions.length;

        for (int var24 = 0; var24 < var25; ++var24) {
            Instance inst = var26[var24];
            MentionHypergraphInstance instance = (MentionHypergraphInstance) inst;
            List<Span> goldSpans = (List) instance.output;
            List<Span> predSpans = (List) instance.prediction;
            int curTotalGold = goldSpans.size();
            totalGold += curTotalGold;
            int curTotalPred = predSpans.size();
            totalPred += curTotalPred;
            int curCorr = countOverlaps(goldSpans, predSpans);
            corr += curCorr;
            double precision = 100.0D * (double) curCorr / (double) curTotalPred;
            double recall = 100.0D * (double) curCorr / (double) curTotalGold;
            double var10000 = 2.0D / (1.0D / precision + 1.0D / recall);
            if (curTotalPred == 0) {
                precision = 0.0D;
            }

            if (curTotalGold == 0) {
                recall = 0.0D;
            }

            if (curTotalPred == 0 || curTotalGold == 0) {
                double var37 = 0.0D;
            }

            if (count < 3) {
               return instance.prediction;
            }

            ++count;
        }
        return new ArrayList<>();
    }

    private String toString(Object[] arr) {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        Object[] var6 = arr;
        int var5 = arr.length;

        for (int var4 = 0; var4 < var5; ++var4) {
            Object str = var6[var4];
            if (builder.length() > 0) {
                builder.append(" ");
            }

            builder.append(str + "(" + index + ")");
            ++index;
        }

        return builder.toString();
    }

    private int countOverlaps(List<Span> list1, List<Span> list2) {
        int result = 0;
        List<Span> copy = new ArrayList();
        copy.addAll(list2);
        Iterator var5 = list1.iterator();

        while (var5.hasNext()) {
            Span span = (Span) var5.next();
            if (copy.contains(span)) {
                copy.remove(span);
                ++result;
            }
        }

        return result;
    }

    private MentionHypergraphInstance[] readData(String fileName, boolean withLabels, boolean isLabeled) throws IOException {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        ArrayList<MentionHypergraphInstance> result = new ArrayList();
        int var6 = 1;

        while (br.ready()) {
            String words = br.readLine();
            MentionHypergraphInstance instance = new MentionHypergraphInstance(var6++, 1.0D);
            String posTags = br.readLine();
            String[] POStags = null;
            if (isLabeled) {
                POStags = posTags.trim().split(" ");
            } else {
                POStags = POSTagger.getPOSTags(words);
            }

            instance.input = new WordsAndTags(markWords(words.trim().split(" ")), POStags);
            String[] spans = br.readLine().split("\\|");
            if (spans.length == 1 && spans[0].length() == 0) {
                spans = new String[0];
            }

            List<Span> output = new ArrayList();
            String[] var16 = spans;
            int var15 = spans.length;

            for (int var14 = 0; var14 < var15; ++var14) {
                String span = var16[var14];
                String[] tokens = span.split(" ");
                String[] indices = tokens[0].split(",");
                int[] intIndices = new int[indices.length];

                for (int i = 0; i < 4; ++i) {
                    intIndices[i] = Integer.parseInt(indices[i]);
                }

                Label label = Label.get(tokens[1]);
                output.add(new Span(intIndices[0], intIndices[1], intIndices[2], intIndices[3], label));
            }

            instance.setOutput(output);
            if (isLabeled) {
                instance.setLabeled();
            } else {
                instance.setUnlabeled();
            }

            br.readLine();
            result.add(instance);
        }

        br.close();
        return (MentionHypergraphInstance[]) result.toArray(new MentionHypergraphInstance[result.size()]);
    }

    private AttributedWord[] markWords(String[] words) {
        AttributedWord[] result = new AttributedWord[words.length];

        for (int i = 0; i < result.length; ++i) {
            result[i] = new AttributedWord(words[i]);
        }

        return result;
    }
}