package it.unige.dibris.andrmperm;


import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utilities {
    private final static int BUFSIZE = 1024;

    public static void fileCopy(File src, File dst) {
        try{
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            byte[] buf = new byte[BUFSIZE];
            int len;
            while ((len = in.read(buf)) > 0){
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
        catch(IOException e){
            //TODO
        }

    }
}
