package com.github.mikephil.charting.utils

import android.content.res.AssetManager
import android.os.Environment
import android.util.Log
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import java.io.File
import java.io.IOException

/**
 * Loads and saves entries as simple text files, one entry per line with values separated by "#".
 *
 * Read errors are logged and yield an empty or partial list instead of an exception.
 * Malformed lines are not tolerated and throw while parsing.
 */
public object FileUtils {

    private const val LOG = "MPChart-FileUtils"

    /**
     * Reads entries from a file on external storage, expecting "x#y" per line. Lines with three or
     * more values become a [BarEntry] whose last value is the x value and the others are stack values.
     *
     * @param path file path relative to the external storage directory.
     * @throws NumberFormatException if a value is not a number.
     * @throws IndexOutOfBoundsException if a line has no "#".
     */
    @Suppress("DEPRECATION")
    public fun loadEntriesFromFile(path: String): List<Entry<*>> {
        val file = File(Environment.getExternalStorageDirectory(), path)
        val entries = mutableListOf<Entry<*>>()
        try {
            file.bufferedReader().useLines { lines ->
                lines.forEach { line -> entries.add(parseEntry(line, swapped = false)) }
            }
        } catch (e: IOException) {
            Log.e(LOG, e.toString())
        }
        return entries
    }

    /**
     * Reads entries from an asset file, expecting "y#x" per line. Lines with three or more values
     * become a [BarEntry] whose last value is the x value and the others are stack values.
     *
     * @param path file path inside the assets folder.
     * @throws NumberFormatException if a value is not a number.
     * @throws IndexOutOfBoundsException if a line has no "#".
     */
    public fun loadEntriesFromAssets(am: AssetManager, path: String): List<Entry<*>> {
        val entries = mutableListOf<Entry<*>>()
        try {
            am.open(path).bufferedReader(Charsets.UTF_8).useLines { lines ->
                lines.forEach { line -> entries.add(parseEntry(line, swapped = true)) }
            }
        } catch (e: IOException) {
            Log.e(LOG, e.toString())
        }
        return entries
    }

    private fun parseEntry(line: String, swapped: Boolean): Entry<*> {
        val split = line.split("#")
        if (split.size <= 2) {
            return if (swapped) {
                Entry(split[1].toFloat(), split[0].toFloat())
            } else {
                Entry(split[0].toFloat(), split[1].toInt().toFloat())
            }
        }
        val vals = List(split.size - 1) { split[it].toFloat() }
        return BarEntry(split[split.size - 1].toInt().toFloat(), vals)
    }

    /**
     * Appends [entries] as "y#x" lines to a file on external storage, creating the file if needed.
     *
     * @param path file path relative to the external storage directory.
     */
    @Suppress("DEPRECATION")
    public fun saveToSdCard(entries: List<Entry<*>>, path: String) {
        val saved = File(Environment.getExternalStorageDirectory(), path)
        if (!saved.exists()) {
            try {
                saved.createNewFile()
            } catch (e: IOException) {
                Log.e(LOG, e.toString())
            }
        }
        try {
            java.io.FileWriter(saved, true).buffered().use { buf ->
                for (e in entries) {
                    buf.append("${e.x}#${e.y}")
                    buf.newLine()
                }
            }
        } catch (e: IOException) {
            Log.e(LOG, e.toString())
        }
    }

    /**
     * Reads bar entries from an asset file, expecting "y#x" per line.
     *
     * @param path file path inside the assets folder.
     * @throws NumberFormatException if a value is not a number.
     * @throws IndexOutOfBoundsException if a line has no "#".
     */
    public fun loadBarEntriesFromAssets(am: AssetManager, path: String): List<BarEntry<*>> {
        val entries = mutableListOf<BarEntry<*>>()
        try {
            am.open(path).bufferedReader(Charsets.UTF_8).useLines { lines ->
                lines.forEach { line ->
                    val split = line.split("#")
                    entries.add(BarEntry(split[1].toFloat(), split[0].toFloat()))
                }
            }
        } catch (e: IOException) {
            Log.e(LOG, e.toString())
        }
        return entries
    }
}
