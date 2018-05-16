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
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Hashtable;

public class RequestThread
        extends Thread {

    private File _rootDir;
    private Socket _socket;

    public RequestThread(Socket paramSocket, File paramFile) {
        this._socket = paramSocket;
        this._rootDir = paramFile;
    }

    private static void sendHeader(BufferedOutputStream paramBufferedOutputStream, int paramInt, String paramString, long paramLong1, long paramLong2)
            throws IOException {
        paramBufferedOutputStream.write(("HTTP/1.0 " + paramInt + " OK\r\n" + "Date: " + new Date().toString() + "\r\n" + "Server: JibbleWebServer/1.0\r\n" + "Content-Type: " + paramString + "\r\n" + "Expires: Thu, 01 Dec 1994 16:00:00 GMT\r\n" + (paramLong1 != -1L ? "Content-Length: " + paramLong1 + "\r\n" : "") + "Last-modified: " + new Date(paramLong2).toString() + "\r\n" + "\r\n").getBytes());
    }

    private static void sendError(BufferedOutputStream paramBufferedOutputStream, int paramInt, String paramString)
            throws IOException {
        paramString = paramString + "<hr>" + "SimpleWebServer  http://www.jibble.org/";
        sendHeader(paramBufferedOutputStream, paramInt, "text/html", paramString.length(), System.currentTimeMillis());
        paramBufferedOutputStream.write(paramString.getBytes());
        paramBufferedOutputStream.flush();
        paramBufferedOutputStream.close();
    }

    public void run() {
        BufferedInputStream localBufferedInputStream = null;
        try {
            this._socket.setSoTimeout(30000);
            BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(this._socket.getInputStream()));
            BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(this._socket.getOutputStream());
            String str1 = localBufferedReader.readLine();
            if ((str1 == null) || (!str1.startsWith("GET ")) || ((!str1.endsWith(" HTTP/1.0")) && (!str1.endsWith("HTTP/1.1")))) {
                sendError(localBufferedOutputStream, 500, "Invalid Method.");
                return;
            }
            String str2 = str1.substring(4, str1.length() - 9);
            Object localObject1 = new File(this._rootDir, URLDecoder.decode(str2, "UTF-8")).getCanonicalFile();
            Object localObject2;
            if (((File) localObject1).isDirectory()) {
                localObject2 = new File((File) localObject1, "index.html");
                if ((((File) localObject2).exists()) && (!((File) localObject2).isDirectory())) {
                    localObject1 = localObject2;
                }
            }
            if (!((File) localObject1).toString().startsWith(this._rootDir.toString())) {
                sendError(localBufferedOutputStream, 403, "Permission Denied.");
            } else if (!((File) localObject1).exists()) {
                sendError(localBufferedOutputStream, 404, "File Not Found.");
            } else {
                Object localObject3;
                int i;
                if (((File) localObject1).isDirectory()) {
                    if (!str2.endsWith("/")) {
                        str2 = str2 + "/";
                    }
                    localObject2 = ((File) localObject1).listFiles();
                    sendHeader(localBufferedOutputStream, 200, "text/html", -1L, System.currentTimeMillis());
                    localObject3 = "Index of " + str2;
                    localBufferedOutputStream.write(("<html><head><title>" + (String) localObject3 + "</title></head><body><h3>Index of " + str2 + "</h3><p>\n").getBytes());
                    for (i = 0; i < Array.getLength(localObject2); i++) {
                        localObject1 = Array.get(localObject2, i);
                        String str3 = ((File) localObject1).getName();
                        String str4 = "";
                        if (((File) localObject1).isDirectory()) {
                            str4 = "&lt;DIR&gt;";
                        }
                        localBufferedOutputStream.write(("<a href=\"" + str2 + str3 + "\">" + str3 + "</a> " + str4 + "<br>\n").getBytes());
                    }
                    localBufferedOutputStream.write("</p><hr><p>SimpleWebServer  http://www.jibble.org/</p></body><html>".getBytes());
                } else {
                    localBufferedInputStream = new BufferedInputStream(new FileInputStream((File) localObject1));
                    localObject2 = (String) SimpleWebServer.MIME_TYPES.get(SimpleWebServer.getExtension((File) localObject1));
                    if (localObject2 == null) {
                        localObject2 = "application/octet-stream";
                    }
                    sendHeader(localBufferedOutputStream, 200, (String) localObject2, ((File) localObject1).length(), ((File) localObject1).lastModified());
                    localObject3 = new byte['?'];
                    while ((i = localBufferedInputStream.read((byte[]) localObject3)) != -1) {
                        localBufferedOutputStream.write((byte[]) localObject3, 0, i);
                    }
                    localBufferedInputStream.close();
                }
            }
            localBufferedOutputStream.flush();
            localBufferedOutputStream.close();
        } catch (IOException localIOException) {
            if (localBufferedInputStream != null) {
                try {
                    localBufferedInputStream.close();
                } catch (Exception localException) {
                }
            }
        }
    }
}
