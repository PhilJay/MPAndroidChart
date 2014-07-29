
package com.github.mikephil.charting.utils;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Utilities class for interacting with the assets and the devices storage to
 * load and save DataSet objects from and to .txt files.
 * 
 * @author Philipp Jahoda
 */
public class FileUtils {

    private static final String LOG = "MPChart-FileUtils";

    /**
     * Loads a DataSet from a textfile from the sd-card. Textfile syntax is:
     * labelstring<linebreak>500.99#0<linebreak>350.80#1<linebreak>and so on...
     * 
     * @param path the name of the file on the sd card (path if needed)
     * @return
     */
    public static DataSet dataSetFromFile(String path) {

        File sdcard = Environment.getExternalStorageDirectory();

        // Get the text file
        File file = new File(sdcard, path);

        ArrayList<Entry> entries = new ArrayList<Entry>();
        String label = "";

        try {
            @SuppressWarnings("resource")
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            // firstline is the label
            label = line;

            while ((line = br.readLine()) != null) {
                String[] split = line.split("#");
                entries.add(new Entry(Float.parseFloat(split[0]), Integer.parseInt(split[1])));
            }
        } catch (IOException e) {
            Log.e(LOG, e.toString());
        }

        DataSet ds = new DataSet(entries, label);
        return ds;
    }

    /**
     * Loads a DataSet from a textfile from the assets folder. Textfile syntax
     * is: labelstring|newline|500.99#03|newline|50.80#1|newline|and so on...
     * 
     * @param am
     * @param path the name of the file in the assets folder (path if needed)
     * @return
     */
    public static DataSet dataSetFromAssets(AssetManager am, String path) {

        String label = null;
        ArrayList<Entry> entries = new ArrayList<Entry>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(am.open(path), "UTF-8"));

            // do reading, usually loop until end of file reading
            label = reader.readLine();
            String line = reader.readLine();

            while (line != null) {
                // process line
                String[] split = line.split("#");
                entries.add(new Entry(Float.parseFloat(split[0]), Integer.parseInt(split[1])));
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(LOG, e.toString());
            
        } finally {
            
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG, e.toString());
                }
            }
        }

        DataSet ds = new DataSet(entries, label);
        return ds;
    }

    /**
     * Saves a DataSet to the specified location on the sdcard
     * 
     * @param ds
     * @param path
     */
    public static void saveToSdCard(DataSet ds, String path) {

        File sdcard = Environment.getExternalStorageDirectory();

        File saved = new File(sdcard, path);
        if (!saved.exists())
        {
            try
            {
                saved.createNewFile();
            } catch (IOException e)
            {
                Log.e(LOG, e.toString());
            }
        }
        try
        {
            // BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(saved, true));
            buf.append(ds.getLabel());
            buf.newLine();

            ArrayList<Entry> entries = ds.getYVals();

            for (Entry e : entries) {

                buf.append(e.getVal() + "#" + e.getXIndex());
                buf.newLine();
            }

            buf.close();
        } catch (IOException e)
        {
            Log.e(LOG, e.toString());
        }
    }
}
