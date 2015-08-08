package com.martin.kantidroid.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.martin.kantidroid.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Util {

    public static int getDark(Context context, String index) {
        return Color.parseColor(context.getResources().getStringArray(R.array.colors_dark)[Integer.parseInt(index)]);
    }

    public static int getLight(Context context, String index) {
        return Color.parseColor(context.getResources().getStringArray(R.array.colors_light)[Integer.parseInt(index)]);
    }

    public static int getNormal(Context context, String index) {
        return Color.parseColor(context.getResources().getStringArray(R.array.colors)[Integer.parseInt(index)]);
    }

    public static String formatKont(String used, String available) {
        String formatted;
        if (available.contentEquals("")) {
            formatted = "-";
        } else {
            if (used.contentEquals("")) {
                formatted = "0/" + available;
            } else {
                formatted = getAmoutUsed(used) + "/" + available;
            }
        }
        return formatted;
    }

    public static int getAmoutUsed(String kontSemester) {
        int used = 0;
        String[] used_splitted = kontSemester.split("\n");
        for (int i = 0; i < used_splitted.length; i++) {
            used += Integer.parseInt(used_splitted[i].split(" - ")[1]);
        }
        return used;
    }

    public static double getRequired(Context context, int id, int semester, String relevance, String goal) {
        DatabaseHandler db = new DatabaseHandler(context);
        Fach fach = db.getFach(id);

        double upper_term;
        double dRelevance = Double.parseDouble(relevance);
        double dGoal = Double.parseDouble(goal);

        if (semester == 1) {
            String sMarks = fach.getNoten1();

            String[] entries = sMarks.split("\n");
            int count = entries.length;
            double subtraktion = 0;
            double multiplikatoren = 0;

            for (int i = 0; i < count; i++) {
                String[] item = entries[i].split(" - ");
                subtraktion = subtraktion + (Double.parseDouble(item[0].replace(",", ".")) * Double.parseDouble(item[1].replace(",", ".")));
                multiplikatoren = multiplikatoren + Double.parseDouble(item[1].replace(",", "."));
            }
            upper_term = dGoal * (multiplikatoren + dRelevance) - subtraktion;
        } else {
            String sMarks = fach.getNoten2();

            String[] entries = sMarks.split("\n");
            int count = entries.length;
            double subtraktion = 0;
            double multiplikatoren = 0;

            for (int i = 0; i < count; i++) {
                String[] item = entries[i].split(" - ");
                subtraktion = subtraktion + (Double.parseDouble(item[0].replace(",", ".")) * Double.parseDouble(item[1].replace(",", ".")));
                multiplikatoren = multiplikatoren + Double.parseDouble(item[1].replace(",", "."));
            }
            upper_term = dGoal * (multiplikatoren + dRelevance) - subtraktion;
        }

        double needed = upper_term / dRelevance;

        BigDecimal bd = new BigDecimal(needed);
        return Double.parseDouble(bd.setScale(2, RoundingMode.HALF_UP).toString());
    }

    public static double getRequiredPerf(String[] entries, String relevance, String goal) {
        double upper_term;
        double dRelevance = Double.parseDouble(relevance);
        double dGoal = Double.parseDouble(goal);

        int count = entries.length;
        double subtraktion = 0;
        double multiplikatoren = 0;

        for (int i = 0; i < count; i++) {
            String[] item = entries[i].split(" - ");
            subtraktion = subtraktion + (Double.parseDouble(item[0].replace(",", ".")) * Double.parseDouble(item[1].replace(",", ".")));
            multiplikatoren = multiplikatoren + Double.parseDouble(item[1].replace(",", "."));
        }
        upper_term = dGoal * (multiplikatoren + dRelevance) - subtraktion;

        double needed = upper_term / dRelevance;

        BigDecimal bd = new BigDecimal(needed);
        return Double.parseDouble(bd.setScale(2, RoundingMode.HALF_UP).toString());
    }

    public static void setSeen(Context c, String key) {
        c.getSharedPreferences("Kantidroid", Context.MODE_PRIVATE).edit().putBoolean(key, true).commit();
    }

    public static boolean getSeen(Context c, String key) {
        return c.getSharedPreferences("Kantidroid", Context.MODE_PRIVATE).contains(key);
    }

    public static int backupLocal(Context context) {
        File localDest = new File(Environment.getExternalStorageDirectory(), "/Kantidroid/backup/local");
        return backupLocalWithDest(context, localDest);
    }

    private static int backupLocalWithDest(Context context, File localDest) {
        if (Environment.getExternalStorageState().contentEquals(Environment.MEDIA_MOUNTED)) {
            File dbPath = context.getDatabasePath("Kantidroid");
            if (dbPath.isFile()) {
                localDest.mkdirs();
                try {
                    copy(dbPath, new File(localDest + "/database"));
                } catch (IOException e) {
                    e.printStackTrace();
                    return 3;
                }
                if (saveSharedPreferencesToFile(context, new File(localDest + "/shared_prefs"))) {
                    return 0;
                }
                return 4;
            }
            return 2;
        }
        return 1;
    }

    public static int importLocal(Context context) {
        File localBackup = new File(Environment.getExternalStorageDirectory(), "/Kantidroid/backup/local");
        return importLocalWithSrc(context, localBackup);
    }

    private static int importLocalWithSrc(Context context, File localBackup) {
        if (Environment.getExternalStorageState().contentEquals(Environment.MEDIA_MOUNTED)) {
            File dbPath = context.getDatabasePath("Kantidroid");
            if (dbPath.isFile()) {
                File dbLocal = new File(localBackup + "/database");
                File prefsLocal = new File(localBackup + "/shared_prefs");
                if (dbLocal.isFile()) {
                    try {
                        copy(dbLocal, dbPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return 4;
                    }
                    if (prefsLocal.isFile()) {
                        if (loadSharedPreferencesFromFile(context, prefsLocal)) {
                            return 0;
                        }
                        return 6;
                    }
                    return 5;
                }
                return 3;
            }
            return 2;
        }
        return 1;
    }

    public static int backupDropbox(final Context context, final DropboxAPI<AndroidAuthSession> dbAPI) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() {
                File localDest = new File(Environment.getExternalStorageDirectory(), "/Kantidroid/backup/dropbox");
                int returnValue = backupLocalWithDest(context, localDest);
                if (returnValue == 0) {
                    File dbFile = new File(localDest + "/database");
                    File prefFile = new File(localDest + "/shared_prefs");
                    try {
                        FileInputStream dbInputStream = new FileInputStream(dbFile);
                        dbAPI.putFileOverwrite("/database", dbInputStream, dbFile.length(), null);
                        dbInputStream.close();
                        FileInputStream prefsInputStream = new FileInputStream(prefFile);
                        dbAPI.putFileOverwrite("/shared_prefs", prefsInputStream, prefFile.length(), null);
                        prefsInputStream.close();
                        return 0;
                    } catch (DropboxException e) {
                        e.printStackTrace();
                        return 6;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return 5;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return 7;
                    }
                }
                return returnValue;
            }
        };
        Future<Integer> future = executor.submit(callable);
        int returnValue = 8;
        try {
            returnValue = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return returnValue;
    }

    public static int importDropbox(final Context context, final DropboxAPI<AndroidAuthSession> dbAPI) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() {
                File localBackup = new File(Environment.getExternalStorageDirectory(), "/Kantidroid/backup/Dropbox");
                File dbFile = new File(localBackup + "/database");
                File prefsFile = new File(localBackup + "/shared_prefs");
                try {
                    FileOutputStream dbOutputStream = new FileOutputStream(new File(localBackup + "/database"));
                    dbAPI.getFile("/database", null, dbOutputStream, null);
                    dbOutputStream.close();
                    FileOutputStream prefsOutputStream = new FileOutputStream(new File(localBackup + "/shared_prefs"));
                    dbAPI.getFile("/shared_prefs", null, prefsOutputStream, null);
                    prefsOutputStream.close();
                } catch (DropboxException e) {
                    e.printStackTrace();
                    return 7;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return 8;
                } catch (IOException e) {
                    e.printStackTrace();
                    return 9;
                }
                return importLocalWithSrc(context, localBackup);
            }
        };
        Future<Integer> future = executor.submit(callable);
        int returnValue = 10;
        try {
            returnValue = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return returnValue;
    }

    private static boolean saveSharedPreferencesToFile(Context context, File dst) {
        boolean res = false;
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(dst));
            SharedPreferences pref = context.getSharedPreferences("Kantidroid", Context.MODE_PRIVATE);
            output.writeObject(pref.getAll());

            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    private static boolean loadSharedPreferencesFromFile(Context context, File src) {
        boolean res = false;
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new FileInputStream(src));
            SharedPreferences.Editor prefEdit = context.getSharedPreferences("Kantidroid", Context.MODE_PRIVATE).edit();
            prefEdit.clear();
            Map<String, ?> entries = (Map<String, ?>) input.readObject();
            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                Object v = entry.getValue();
                String key = entry.getKey();

                if (v instanceof Boolean)
                    prefEdit.putBoolean(key, ((Boolean) v).booleanValue());
                else if (v instanceof Float)
                    prefEdit.putFloat(key, ((Float) v).floatValue());
                else if (v instanceof Integer)
                    prefEdit.putInt(key, ((Integer) v).intValue());
                else if (v instanceof Long)
                    prefEdit.putLong(key, ((Long) v).longValue());
                else if (v instanceof String)
                    prefEdit.putString(key, ((String) v));
            }
            prefEdit.commit();
            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    private static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static int getNumberIndex(String checkable) {
        int index = -1;
        boolean indexFound = false;
        if (checkable.length() >= 3) {
            for (int i = 0; i < checkable.length(); i++) {
                for (int y = 1; y <= 6; y++) {
                    if (checkable.charAt(i) == (y + "").charAt(0)) {
                        if (indexFound) {
                            index = -1;
                        } else {
                            index = i;
                            indexFound = true;
                        }
                    }
                }
            }
        }
        return index;
    }

    public static int getDepartmentIndex(String checkable, int numberIndex) {
        int index = -1;
        if (numberIndex != -1) {
            char[] characters = {'G', 'H', 'F'};
            if (checkable.charAt(1) == ' ') {
                if (checkable.length() == 4) {
                    for (int i = 0; i < characters.length; i++) {
                        if (checkable.toUpperCase().charAt(2) == characters[i]) {
                            index = 2;
                        }
                    }
                }
            } else {
                if (checkable.length() == 3) {
                    for (int i = 0; i < characters.length; i++) {
                        if (checkable.toUpperCase().charAt(1) == characters[i]) {
                            index = 1;
                        }
                    }
                }
            }
        }
        return index;
    }

    public static int getLevelIndex(String checkable, int departmentIndex) {
        int index = -1;
        if (departmentIndex != -1) {
            char[] characters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p'};
            for (int i = 0; i < characters.length; i++) {
                if (checkable.toLowerCase().charAt(departmentIndex + 1) == characters[i]) {
                    index = departmentIndex + 1;
                }
            }
        }
        return index;
    }

    public static String getClassUrl(String checkable) {
        String classUrl = "error";
        int numberIndex = getNumberIndex(checkable);
        int departmentIndex = getDepartmentIndex(checkable, numberIndex);
        int levelIndex = getLevelIndex(checkable, departmentIndex);
        if (levelIndex != -1) {
            classUrl = checkable.charAt(numberIndex) + "%20" + checkable.toUpperCase().charAt(departmentIndex) + checkable.toLowerCase().charAt(levelIndex);
        }
        return classUrl;
    }

    public static boolean urlExists(String urlName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) new URL(urlName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static File getTimetableFile(String classUrl) {
        File download = null;
        if (Environment.getExternalStorageState().contentEquals(Environment.MEDIA_MOUNTED)) {
            File localDest = new File(Environment.getExternalStorageDirectory(), "/Kantidroid/timetables");
            localDest.mkdirs();
            download = new File(localDest + "/" + classUrl.replace("%20", " ") + ".pdf");
        }
        return download;
    }

    public static File[] getDownloadedFiles() {
        File f = new File(Environment.getExternalStorageDirectory(), "/Kantidroid/timetables");
        f.mkdirs();
        return f.listFiles();
    }

    public static String fileExt(File f) {
        String name = f.getName();
        if (name.indexOf("?") > -1) {
            name = name.substring(0, name.indexOf("?"));
        }
        if (name.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = name.substring(name.lastIndexOf("."));
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    public static double[] convertDoubles(List<Double> doubles)
    {
        double[] ret = new double[doubles.size()];
        Iterator<Double> iterator = doubles.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().doubleValue();
        }
        return ret;
    }
}
