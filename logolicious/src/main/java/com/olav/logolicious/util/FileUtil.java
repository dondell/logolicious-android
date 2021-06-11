package com.olav.logolicious.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class FileUtil {

    private static final int ARROW_NONE = 0;
    //	private static final int ARROW_TOP_L = 1;
    private static final int ARROW_TOP_R = 2;
    //	private static final int ARROW_BOTTOM_L = 3;
    private static final int ARROW_BOTTOM_R = 4;
    private static final int ARROW_BOTTOM_C = 5;
    private static final int ARROW_NONE_DIDUKNOW = 6;
    private static final int ARROW_BOTTOM_ERASER = 7;
    public static int SHOW_RATE_WINDOW = 7;

    public static String saveConfigTip(String data, int what) {

        File outputDir = new File(FileUtil.getAppRootFolder() + File.separator + ".Face Fiesta");
        // ensure file directory is existed
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        } else {
        }
        File outputFile = null;
        if (what == ARROW_NONE) {
            outputFile = new File(outputDir, "configTip1.txt");//File.createTempFile("configTip", ".txt", outputDir);
        } else if (what == ARROW_TOP_R) {
            outputFile = new File(outputDir, "configTip2.txt");//File.createTempFile("configTip", ".txt", outputDir);
        } else if (what == ARROW_BOTTOM_R) {
            outputFile = new File(outputDir, "configTip3.txt");//File.createTempFile("configTip", ".txt", outputDir);
        } else if (what == ARROW_BOTTOM_C) {
            outputFile = new File(outputDir, "configTip4.txt");//File.createTempFile("configTip", ".txt", outputDir);
        } else if (what == ARROW_NONE_DIDUKNOW) {
            outputFile = new File(outputDir, "configTip5.txt");//File.createTempFile("configTip", ".txt", outputDir);
        } else if (what == ARROW_BOTTOM_ERASER) {
            outputFile = new File(outputDir, "configTip6.txt");//File.createTempFile("configTip", ".txt", outputDir);
        }

        if (outputFile.exists()) outputFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(outputFile);
            out.write(data.getBytes());
            out.close();

        } catch (Exception e) {
            // handle exception
        }

        return outputFile.getAbsolutePath();
    }

    public static String saveRateConfig(String data, int what) {

        File outputDir = new File(FileUtil.getAppRootFolder() + File.separator + ".Logolicious");
        // ensure file directory is existed
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        } else {
        }
        File outputFile = null;
        if (what == SHOW_RATE_WINDOW) {
            outputFile = new File(outputDir, "configRateCounter.txt");//File.createTempFile("configTip", ".txt", outputDir);
        } else if (what == 777) {
            if (what == SHOW_RATE_WINDOW) {
                outputFile = new File(outputDir, "xxx.txt");
            }
        }

        if (outputFile.exists()) outputFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(outputFile);
            out.write(data.getBytes());
            out.close();

        } catch (Exception e) {
            // handle exception
        }

        return outputFile.getAbsolutePath();
    }

    public static String readRateConfig(int what) {
        //Find the directory for the SD Card using the API
        //*Don't* hardcode "/sdcard"
        File sdcard = FileUtil.getAppRootFolder();

        //Get the text file
        File file = null;
        if (what == SHOW_RATE_WINDOW) {
            file = new File(sdcard.getAbsolutePath() + File.separator + ".Face Fiesta", "configRateCounter.txt");
        }

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString().trim();
    }

    public static String readConfigTip(int what) {
        //Find the directory for the SD Card using the API
        //*Don't* hardcode "/sdcard"
        File sdcard = FileUtil.getAppRootFolder();

        //Get the text file
        File file = null;
        if (what == ARROW_NONE) {
            file = new File(sdcard.getAbsolutePath() + File.separator + ".Face Fiesta", "configTip1.txt");
        } else if (what == ARROW_TOP_R) {
            file = new File(sdcard.getAbsolutePath() + File.separator + ".Face Fiesta", "configTip2.txt");
        } else if (what == ARROW_BOTTOM_R) {
            file = new File(sdcard.getAbsolutePath() + File.separator + ".Face Fiesta", "configTip3.txt");
        } else if (what == ARROW_BOTTOM_C) {
            file = new File(sdcard.getAbsolutePath() + File.separator + ".Face Fiesta", "configTip4.txt");
        } else if (what == ARROW_NONE_DIDUKNOW) {
            file = new File(sdcard.getAbsolutePath() + File.separator + ".Face Fiesta", "configTip5.txt");
        } else if (what == ARROW_BOTTOM_ERASER) {
            file = new File(sdcard.getAbsolutePath() + File.separator + ".Face Fiesta", "configTip6.txt");
        } else if (what == 777) {
            file = new File(sdcard.getAbsolutePath() + File.separator + ".Logolicious", "xxx.txt");
        }


        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();
    }

    /**
     * @param directoryName The .facefiesta directory.
     * @param files         null.
     * @param imageCode     The prefix code of the files or the criteria files that begins this prefix.
     * @return
     */
    public static File[] listf(String directoryName, ArrayList<File> files, String imageCode) {
        File directory = new File(directoryName);

        if (files == null) {
            files = new ArrayList<File>();
        }

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
                Log.d("listf ", file.getAbsolutePath());
            } else if (file.isDirectory()) {
                listf(file.getAbsolutePath(), files, imageCode);
            }
        }

        File[] aFile = files.toArray(new File[files.size()]);
        return aFile;
    }

    /**
     * @param directoryName items directory
     * @return This method delete the category items excluding the extension packages.
     */
    public static boolean deleteDirectoryFiles(File directoryName) {
        //		http://stackoverflow.com/questions/14930908/how-to-delete-all-files-and-folders-in-one-folder-on-android#
        if (directoryName.exists() && directoryName.isDirectory()) {
            String[] children = directoryName.list();
            if (null == children) {
                return false;
            }
            for (int i = 0; i < children.length; i++) {
                if (children[i].contains("l_")) {
                    boolean success = deleteDirectoryFiles(new File(directoryName, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        // The directory is now empty so delete it
        return directoryName.delete();
    }

    public static boolean deleteAFile(String path) {
        boolean mBoolean = false;
        File aFile = new File(path);
        if (aFile.exists()) {
            aFile.delete();
            mBoolean = true;
        } else {
            mBoolean = false;
        }
        return mBoolean;
    }

    public static void appendLog(String path, String text) {
        File logFile = new File(path);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            // BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param fileToCopy        A file to be copy
     * @param destinationOfFile A Directory where to put the file.
     * @param imageSourceType   use "drawable or sdcard"
     * @return This method will copy a file to the specified destination.
     */
    public static void copyFileTo(Context ctx, String fileToCopy, String destinationOfFile, String imageSourceType) {
        InputStream in = null;
        FileOutputStream out = null;
        //get the filename of the picture
        String fName = StringUtil.splitStr(fileToCopy, "/").get(StringUtil.splitStr(fileToCopy, "/").size() - 1);
        File fileOut = new File(destinationOfFile, fName);
        Random r = new Random();
        int rnd = r.nextInt(20);

        if (fileOut.exists())
            fileOut.delete();
        // create new name
        if (!imageSourceType.contains("drawable")) {
            if (fName.contains(".jpg")) {
                fName = fName + "".replace(".jpg", "") + rnd + ".jpg";
            } else if (fName.contains(".jpeg")) {
                fName = fName + "".replace(".jpeg", "") + rnd + ".jpeg";
            } else if (fName.contains(".png")) {
                fName = fName + "".replace(".png", "") + rnd + ".png";
            } else {
                fName = fName + "".replace(".png", "") + rnd + ".png";
            }
        } else {
            fName = fName + ".png";
        }

        fileOut = new File(destinationOfFile, fName);
        try {
            if (imageSourceType.contains("sdcard")) {
                in = new FileInputStream(fileToCopy);
            } else if (imageSourceType.contains("drawable")) {
                // convert drawable to inputstream
                int imageResource = ctx.getResources().getIdentifier(fileToCopy, null, ctx.getPackageName());
                in = ctx.getResources().openRawResource(imageResource);
            }
            out = new FileOutputStream(fileOut);
            copyFile(in, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
        }
    }

    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static boolean isFile(String path) {
        File file = new File(path);
        if (!file.isDirectory() && file.exists() && file.isFile())
            //Do something
            return true;
        else
            return false;
    }

    public static boolean isDir(String path) {
        File file = new File(path);
        if (file.isDirectory() && !file.isFile())
            //Do something
            return true;
        else
            return false;
    }

    public static boolean createFolder(String path, String folderName) {
        File f = new File(path + File.separator + folderName);
        if (!f.isDirectory() && !f.exists()) {
            f.mkdirs();
            Log.i("xxx", "folder created " + path + File.separator + folderName);
            return true;
        } else {
            return false;
        }
    }

    public static String getExternalSdCardPath() {
        String path = null;

        File sdCardFile = null;
        List<String> sdCardPossiblePath = Arrays.asList("external_sd", "ext_sd", "external", "extSdCard");

        for (String sdPath : sdCardPossiblePath) {
            File file = new File("/mnt/", sdPath);

            if (file.isDirectory() && file.canWrite()) {
                path = file.getAbsolutePath();

                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
                File testWritable = new File(path, "test_" + timeStamp);

                if (testWritable.mkdirs()) {
                    testWritable.delete();
                } else {
                    path = null;
                }
            }
        }

        if (path != null) {
            sdCardFile = new File(path);
        } else {
            sdCardFile = new File(FileUtil.getAppRootFolder().getAbsolutePath());
        }

        return sdCardFile.getAbsolutePath();
    }

    public static void createDirs(String[] dirs) {
        for (int i = 0; i < dirs.length; i++) {
            File path = new File(dirs[i]);
            if (!path.exists()) {
                path.mkdirs();
                Log.i("", "xxx createDirs " + dirs[i]);
            }
        }
    }

    public static int PREFERRED_LOGO_SIZE = (1024 * 2) + (1024 / 2); //2.5mb

    public static long getFileSize(String fPath) {
        long iRet = 0L;
        try {
            File file = new File(fPath);
            long length = file.length();
            length = length / 1024;
            iRet = length;
            System.out.println("File Path : " + file.getPath() + ", File size : " + length + " KB");
        } catch (Exception e) {
            System.out.println("File not found : " + e.getMessage() + e);
        }
        return iRet;
    }

    public static String updateSavingType(String data) {

        File outputDir = new File(FileUtil.getAppRootFolder() + File.separator + ".Logolicious" + File.separator + "config");
        // ensure file directory is existed
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        } else {
        }
        File outputFile = null;
        outputFile = new File(outputDir, "saving_type.txt");

        if (outputFile.exists()) outputFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(outputFile);
            out.write(data.getBytes());
            out.close();

        } catch (Exception e) {
            // handle exception
        }

        return outputFile.getAbsolutePath();
    }

    public static boolean isPrefDoneShowingForTheFirstTime() {
        File sdcard = FileUtil.getAppRootFolder();
        boolean bRet = true;
        //Get the text file
        File file = null;
        file = new File(sdcard.getAbsolutePath() + File.separator + ".Logolicious" + File.separator + "config", "isPrefDoneShowingForTheFirstTime.txt");
        // ensure file directory is existed
        if (!file.exists()) {
            try {
                file.createNewFile();
                bRet = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return bRet;
    }

    public static String getImageType(SharedPreferences preferences) {
        String type = preferences.getString("SavingType", "HR_PNG");
        if (type.matches(LogoliciousApp.TYPE_HR_PNG)) {
            return "png";
        } else if (type.matches(LogoliciousApp.TYPE_JPG_HQ)) {
            return "jpg";
        } else if (type.matches(LogoliciousApp.TYPE_JPG_L)) {
            return "jpg";
        }
        return "png";
    }


    public static Bitmap.CompressFormat getImageCompressType(SharedPreferences preferences) {
        String type = preferences.getString("SavingType", "JPG_HQ");
        if (type.matches(LogoliciousApp.TYPE_HR_PNG)) {
            return Bitmap.CompressFormat.PNG;
        } else if (type.matches(LogoliciousApp.TYPE_JPG_HQ)) {
            return Bitmap.CompressFormat.JPEG;
        } else if (type.matches(LogoliciousApp.TYPE_JPG_L)) {
            return Bitmap.CompressFormat.JPEG;
        }
        return Bitmap.CompressFormat.PNG;
    }

    public static String getImageQualityType(SharedPreferences preferences) {
        String type = preferences.getString("SavingType", "JPG_HQ");
        if (type.matches(LogoliciousApp.TYPE_HR_PNG)) {
            return LogoliciousApp.TYPE_HR_PNG;
        } else if (type.matches(LogoliciousApp.TYPE_JPG_HQ)) {
            return LogoliciousApp.TYPE_JPG_HQ;
        } else if (type.matches(LogoliciousApp.TYPE_JPG_L)) {
            return LogoliciousApp.TYPE_JPG_L;
        }
        return LogoliciousApp.TYPE_HR_PNG;
    }

    public static int getCompressQuality(SharedPreferences preferences) {
        String savingType = FileUtil.getImageQualityType(preferences);
        if (savingType.contains(LogoliciousApp.TYPE_HR_PNG))
            return 90;
        else if (savingType.contains(LogoliciousApp.TYPE_JPG_HQ))
            return 100;
        else if (savingType.contains(LogoliciousApp.TYPE_JPG_L))
            return 40;
        return 90;
    }

    public static String getImageQualityTypeDescription(SharedPreferences preferences) {
        String type = preferences.getString("SavingType", "JPG_HQ");
        if (type.matches(LogoliciousApp.TYPE_HR_PNG)) {
            return "high resolution .png";
        } else if (type.matches(LogoliciousApp.TYPE_JPG_HQ)) {
            return "high quality .jpg";
        } else if (type.matches(LogoliciousApp.TYPE_JPG_L)) {
            return "small file .jpg";
        }
        return "high quality .jpg";
    }

    // Prompt me always for saving pref
    public static String isShowSavingPrefAlways() {
        File sdcard = FileUtil.getAppRootFolder();

        //Get the text file
        File file = null;
        file = new File(sdcard.getAbsolutePath() + File.separator + ".Logolicious" + File.separator + "config", "isShowSavingPrefAlways.txt");
        // ensure file directory is existed
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();
    }

    public static String updateSavingPrefPrompt(String data) {

        File outputDir = new File(FileUtil.getAppRootFolder() + File.separator + ".Logolicious" + File.separator + "config");
        // ensure file directory is existed
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        } else {
        }
        File outputFile = null;
        outputFile = new File(outputDir, "isShowSavingPrefAlways.txt");

        if (outputFile.exists()) outputFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(outputFile);
            out.write(data.getBytes());
            out.close();

        } catch (Exception e) {
            // handle exception
        }

        return outputFile.getAbsolutePath();
    }

    public static File getAppRootFolder() {
        File dataFolder = new File(GlobalClass.getAppContext().getExternalFilesDir(null).getAbsolutePath(), "LogoLicious");
        Log.d("xxx parent", dataFolder.getParent());
        if (!dataFolder.exists()) {
            Log.d("xxx mkdir_success", "xxx Succesfully created directory: " + dataFolder.mkdirs());
        } else {
            Log.d("xxx fileexists", "xxx true");
        }
        return dataFolder;
    }

    //This function is going to rewrite everything.
    public static boolean fileWrite(String strFilePath, String strContent, boolean bAppend) {
        File dataFolder = new File(GlobalClass.getAppContext().getExternalFilesDir(null).getAbsolutePath(), "LogoLicious");
        Log.d("xxx parent", dataFolder.getParent());
        if (!dataFolder.exists()) {
            Log.d("xxx mkdir_success", "xxx Succesfully created directory: " + dataFolder.mkdirs());
        } else {
            Log.d("xxx fileexists", "xxx true");
        }

        File fNewFile = new File(dataFolder, "logfile");
        try {
            if (fNewFile.exists()) {
                if (!bAppend) {
                    fNewFile.delete();
                    fNewFile.createNewFile();
                }
            } else {
                if (fNewFile.createNewFile()) {
                    Log.i("xxx", "xxx File is now created " + strFilePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fNewFile, bAppend);
            fos.write((GlobalClass.df.format(new Date()) + ": " + strContent + "\n".toString()).getBytes());
            fos.flush();
            fos.close();
            return true;

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param imagesList Integer images from drawables.
     * @param destDir    Folder name to where it would be save.
     * @param filePrefix Image prefix
     */
    public static void fileCreator(Context context, List<String> imagesList, String destDir, String filePrefix) {
        File myDir = new File(destDir);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        myDir.mkdirs();
        int position = 0;
        AssetManager assetManager = context.getAssets();
        for (String dataImage : imagesList) {
            InputStream in = null;
            FileOutputStream out = null;
            String fname = filePrefix + (position + 1) + ".png";
            File fileOut = new File(myDir, fname);
            if (fileOut.exists())
                fileOut.delete();
            try {
                in = assetManager.open("logo_designs/" + dataImage);
                out = new FileOutputStream(fileOut);
                copyFile(in, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
            position = position + 1;
        }
    }

    public static String getPath(Context context, Uri uri) {
        String path;
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            path = uri.getPath();
        } else {
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }

        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }

}