package com.sutd.statnlp.mhservice.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static boolean  writeTextToFile(String text){
        text = text.replace("\n", "").replace("\r", "");

        Path path = Paths.get(ConstantUtil.TRIAL_DATA_PATH);
        try (BufferedWriter writer = Files.newBufferedWriter(path))
        {
            writer.write(text);
            writer.newLine();
            writer.newLine();
            writer.newLine();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }
}
