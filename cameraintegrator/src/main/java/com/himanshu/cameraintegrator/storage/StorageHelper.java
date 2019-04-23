package com.himanshu.cameraintegrator.storage;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class StorageHelper {

    /**
     * Creates A Cache File In Internal Storage that can store upto 1MB of data,which is accessible to app only
     * and it gets deleted when user clears app data, or uninstalls the app,Android can delete it when storage is low
     *
     * @param context   context
     * @param fileName  filename without extension (ex .jpg)
     * @param extension extension
     * @return
     */
    public static File createCacheFile(Context context, String fileName, String extension) {

        String finalFileName = fileName + (extension.startsWith(".") ? extension : "." + extension);
        return createCacheFile(context, finalFileName);
    }

    /**
     * Creates A Cache File In Internal Storage that can store upto 1MB of data, which is accessible to app only
     * and it gets deleted when user clears app data or uninstalls the app,Android can delete it when storage is low
     *
     * @param context  context
     * @param fileName fileName with extension
     * @return
     */
    public static File createCacheFile(Context context, String fileName) {
        return new File(context.getCacheDir(), fileName);
    }


    /**
     * Creates A File In the Internal Storage which is accessible to app only
     * and it gets deleted when user clears app data or uninstalls app
     *
     * @param context   context
     * @param fileName  filename without extension (ex .jpg)
     * @param extension extension
     * @return
     */
    public static File createInternalFile(Context context, String fileName, String extension) {

        String finalFileName = fileName + (extension.startsWith(".") ? extension : "." + extension);
        return createInternalFile(context, finalFileName);
    }

    /**
     * Creates A Cache File In Internal Storage that can store upto 1MB of data
     * and it gets deleted when user clears app data, or uninstalls app
     *
     * @param context  context
     * @param fileName fileName with extension
     * @return file
     */
    public static File createInternalFile(Context context, String fileName) {
        return new File(context.getFilesDir(), fileName);
    }

    /**
     * Creates a file in public external directory, which is accessible to others apps too
     * this file will be retained even after app is uninstalled
     *
     * @param directoryName directory name
     * @param imageName     image name
     * @param extension     extensions
     * @return
     */
    public static File createExternalFile(String directoryName, String imageName, String extension) {

        String finalFileName = imageName + (extension.startsWith(".") ? extension : "." + extension);
        return createExternalFile(directoryName, finalFileName);
    }

    /**
     * Creates a file in public external directory, which is accessible to others apps too
     * this file will be retained even after app is uninstalled
     *
     * @param directoryName directory name
     * @param imageName     image name with extension
     * @return ref to file
     */
    public static File createExternalFile(String directoryName, @Nullable String imageName) {

        // Getting a reference to Target storage directory
        File storageDir = Environment.getExternalStoragePublicDirectory(directoryName);

        // Creating directory if not made already
        if (storageDir.mkdirs()) {
            return new File(storageDir, imageName);
        } else
            return null;

    }


    /**
     * Creates A Cache File In Eternal Storage ,files present in this directory is visib;e to other apps
     * and it gets deleted when user clears app data, or uninstalls the app,Android can delete it when storage is low
     *
     * @param context   context
     * @param fileName  filename without extension (ex .jpg)
     * @param extension extension
     * @return
     */
    public static File createExternalCacheFile(Context context, String fileName, String extension) {

        String finalFileName = fileName + (extension.startsWith(".") ? extension : "." + extension);
        return createExternalCacheFile(context, finalFileName);
    }

    /**
     * Creates A Cache File In Eternal Storage ,files present in this directory is visib;e to other apps
     * and it gets deleted when user clears app data or uninstalls the app,Android can delete it when storage is low
     *
     * @param context  context
     * @param fileName fileName with extension
     * @return
     */
    public static File createExternalCacheFile(Context context, String fileName) {
        return new File(context.getExternalCacheDir(), fileName);
    }


    /**
     * Copies Content Of #source to #destination
     *
     * @param source      source file
     * @param destination dest file
     */
    public static void copy(File source, File destination) {

        FileChannel src = null;
        FileChannel dest = null;

        try {
            src = new FileInputStream(source).getChannel();
            dest = new FileOutputStream(destination).getChannel();
            dest.transferFrom(src, 0, src.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                src.close();
                dest.close();
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
