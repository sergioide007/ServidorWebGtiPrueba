/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gti.server;

/**
 *
 * @author Prueba
 */
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class SimpleWebServer
        extends Thread {

    public static final String VERSION = "SimpleWebServer  http://www.jibble.org/";
    public static final Hashtable MIME_TYPES = new Hashtable();
    private File _rootDir;
    private ServerSocket _serverSocket;
    private boolean _running = true;

    public SimpleWebServer(File paramFile, int paramInt)
            throws IOException {
        this._rootDir = paramFile.getCanonicalFile();
        if (!this._rootDir.isDirectory()) {
            throw new IOException("Not a directory.");
        }
        this._serverSocket = new ServerSocket(paramInt);
        start();
    }

    public void run() {
        while (this._running) {
            try {
                Socket localSocket = this._serverSocket.accept();
                RequestThread localRequestThread = new RequestThread(localSocket, this._rootDir);
                localRequestThread.start();
            } catch (IOException localIOException) {
                System.exit(1);
            }
        }
    }

    public static String getExtension(File paramFile) {
        String str1 = "";
        String str2 = paramFile.getName();
        int i = str2.lastIndexOf(".");
        if (i >= 0) {
            str1 = str2.substring(i);
        }
        return str1.toLowerCase();
    }

    public static void main(String[] paramArrayOfString) {
        try {
            SimpleWebServer localSimpleWebServer = new SimpleWebServer(new File("./"), 80);
        } catch (IOException localIOException) {
            System.out.println(localIOException);
        }
    }

    static {
        String str1 = "image/";
        MIME_TYPES.put(".gif", str1 + "gif");
        MIME_TYPES.put(".jpg", str1 + "jpeg");
        MIME_TYPES.put(".jpeg", str1 + "jpeg");
        MIME_TYPES.put(".png", str1 + "png");
        String str2 = "text/";
        MIME_TYPES.put(".html", str2 + "html");
        MIME_TYPES.put(".htm", str2 + "html");
        MIME_TYPES.put(".txt", str2 + "plain");
    }
}
