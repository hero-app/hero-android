package com.hero.utils.media;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class VideoDecoder
{
    public static String getCachedVideoPath(String url, Context context) throws Exception
    {
        // Build path to output file
        File file = new File(context.getExternalFilesDir(null), url.hashCode() + ".mp4");

        // Already cached?
        if ( file.exists() )
        {
            // Just return it
            return file.getAbsolutePath();
        }

        // Open a connection to that URL.
        URLConnection request = new URL(url).openConnection();

        // Prevent endless download
        request.setReadTimeout(30000);
        request.setConnectTimeout(10000);

        // Prepare buffers and streams
        InputStream is = request.getInputStream();
        FileOutputStream outStream = new FileOutputStream(file);
        BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);

        // 5kb buffer
        int len;
        byte[] buff = new byte[5 * 1024];

        // Read bytes (and store them) until there is nothing more to read
        while ((len = inStream.read(buff)) != -1)
        {
            // Write to file
            outStream.write(buff,0,len);
        }

        // Clean up and free memory
        outStream.flush();
        outStream.close();
        inStream.close();

        // Return absolute path to video file
        return file.getAbsolutePath();
    }
}
