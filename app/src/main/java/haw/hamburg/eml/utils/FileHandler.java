package haw.hamburg.eml.utils;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import haw.hamburg.eml.MainActivity;

public class FileHandler {
    private FileHandler() {}

    private static FileHandler instance;

    public static FileHandler getInstance() {
        if (instance == null)
            instance = new FileHandler();

        return instance;
    }

    public List<String> getLabels(String path) {
        List<String> labels = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(path))) {
            while (scanner.hasNext()) {
                labels.add(scanner.nextLine());
            }
        } catch (IOException e) {
            //Nothing
        }

        return labels;
    }

    public void copyAssets(MainActivity instance) {
        AssetManager assetManager = instance.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("yoloFiles");
        } catch (IOException e) {
            //Nothing
        }

        if (files == null) return;

        for(String filename : files) {
            InputStream in;
            OutputStream out;

            try {
                in = assetManager.open("yoloFiles/" + filename);

                String outDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" ;
                File outFile = new File(outDir, filename);
                out = new FileOutputStream(outFile);

                copyFile(in, out);

                in.close();
                out.flush();
                out.close();
            } catch(IOException e) {
                //Nothing
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}
