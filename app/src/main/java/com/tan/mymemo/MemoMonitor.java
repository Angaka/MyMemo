package com.tan.mymemo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by beau- on 14/01/2017.
 */

public class MemoMonitor {

    static final String MEMOS_FOLDER = "/memos/";
    static final String MEMO = "memo";
    private Context context;

    public MemoMonitor(Context context) {
        this.context = context;
    }

    public List<Memo> getMemos() {
        File appRootDir = new File(context.getFilesDir(), MEMOS_FOLDER);

        if (!appRootDir.exists())
            appRootDir.mkdir();

        File[] memosFiles = appRootDir.listFiles();
        List<Memo> memos = new ArrayList<>();
        if (memosFiles.length > 0) {
            for (File memosFile : memosFiles) {
                String data = readFromFile(memosFile.getPath());
                System.out.println( memosFile.getPath() + " " + data);
                Memo memo = new GsonBuilder().create().fromJson(data, Memo.class);
                memos.add(memo);
            }
        }

        return memos;
    }

    public boolean writeToFile(String path, String data) {
        try {
            deleteMemoByFilename(path + ".json");
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir() + MEMOS_FOLDER + path + ".json"), false);
            byte[] contentInBytes = data.getBytes();
            fos.write(contentInBytes);
            fos.close();
            Toast.makeText(context, "File " + context.getFilesDir() + path, Toast.LENGTH_LONG).show();

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String readFromFile(String path) {
        String ret = null;

        try {
            FileInputStream fis = new FileInputStream(new File(path));

            if (fis != null) {
                InputStreamReader isReader = new InputStreamReader(fis);
                BufferedReader buffReader = new BufferedReader(isReader);
                String data = "";
                StringBuilder sbuilder = new StringBuilder();

                while ((data = buffReader.readLine()) != null)
                    sbuilder.append(data);

                fis.close();
                ret = sbuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public void deleteMemoByFilename(String filename) {
        File file = new File(context.getFilesDir(), MEMOS_FOLDER + filename + ".json");
        if (file.exists()) {
            file.delete();
        }
    }

    public void deleteAllMemos() {
        File filesDirectory = new File(context.getFilesDir(), MEMOS_FOLDER);
        if (!filesDirectory.exists())
            filesDirectory.mkdir();
        File[] dirFiles = filesDirectory.listFiles();
        for (File file : dirFiles) {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
