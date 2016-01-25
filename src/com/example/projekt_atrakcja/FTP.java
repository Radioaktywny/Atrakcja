package com.example.projekt_atrakcja;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.ImageFormat;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

public class FTP {
    
    private String server = "ftp.projektatrakcja.comlu.com" ;
    private int port = 21;
    private String user = "a1893178";
    private String pass = "testtest";
    private FTPClient ftpClient;
    private int returnCode;
    private static void showServerReply(FTPClient ftpClient) 
    {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }
    public void wyslijMiejsce(Bitmap photo, String nazwa_miejsca) throws FileNotFoundException {
        
        //policy 
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);      
        
        
        FTPClient ftpClient = new FTPClient();
    
        try {
            ftpClient.connect(server, port);
           
            showServerReply(ftpClient);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Operation failed. Server reply code: " + replyCode);
                return;
            }
            boolean success = ftpClient.login(user, pass);
            showServerReply(ftpClient);
            if (!success) {
                System.out.println("Could not login to the server");
                return;
            } else {
                System.out.println("LOGGED IN SERVER"); 
                ftpClient.changeWorkingDirectory("Projekt/miejsca/");
                ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE, ftpClient.BINARY_FILE_TYPE);
                ftpClient.setFileTransferMode(ftpClient.BINARY_FILE_TYPE);
                ftpClient.storeFile(nazwa_miejsca+".png", bitmapToSend(photo));
                ftpClient.logout();
            }
        } catch (IOException ex) {
            System.out.println("Oops! Something wrong happened");
            ex.printStackTrace();
        }
    }
    public void wyslijZdjecie(String login,Bitmap photo,Context context)
    {
        //policy 
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);      
        
        
        ftpClient = new FTPClient();
    
        try {
            ftpClient.connect(server, port);
           
            showServerReply(ftpClient);
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                System.out.println("Operation failed. Server reply code: " + replyCode);
                return;
            }
            boolean success = ftpClient.login(user, pass);
            showServerReply(ftpClient);
            if (!success) {
                System.out.println("Could not login to the server");
                return;
            } else {
                System.out.println("LOGGED IN SERVER"); 
                ftpClient.changeWorkingDirectory("Projekt/uzytkownicy/");
                ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE, ftpClient.BINARY_FILE_TYPE);
                ftpClient.setFileTransferMode(ftpClient.BINARY_FILE_TYPE);
                ftpClient.storeFile(login+".png", bitmapToSend(photo));
                ftpClient.logout();
            }
        } catch (IOException ex) {
            System.out.println("Oops! Something wrong happened");
            ex.printStackTrace();
        }
               FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(login + ".png",Context.MODE_PRIVATE);
            photo.compress(Bitmap.CompressFormat.PNG, 100, fos); // w miejscu 100 wpisujemy kompresje (mniejsza wartoœæ = silniejsza kompresja)
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
        
        
    
    private ByteArrayInputStream bitmapToSend(Bitmap photo)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        photo.compress(CompressFormat.PNG, 0 , bos);
        byte[] bitmapdata = bos.toByteArray();
        ByteArrayInputStream bs = new ByteArrayInputStream( bitmapdata);       
        
        return bs;        
    }
    public void pobierz(Context context,String folderToADD,String name)
    {
        File folder = new File(context.getFilesDir() + 
                File.separator + folderToADD);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            // Do so1mething on success
        } else {
            // Do something else on failure 
}
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);     
        ByteArrayInputStream bs=null;
        ftpClient = new FTPClient();
        try {
 
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            
            // using InputStream retrieveFileStream(String)
            String remoteFile2 = "/Projekt/miejsca/"+name+".png";
            File downloadFile2 = new File(folder.getAbsolutePath(),name+".png");
            OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(downloadFile2));
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
            byte[] bytesArray = new byte[4096];
            int bytesRead = -1;
            
            //if(checkFileExists("/Projekt/miejsca/"+name+".png"))
            {
            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                outputStream2.write(bytesArray, 0, bytesRead);
            }
 
            success = ftpClient.completePendingCommand();
            if (success) {
                System.out.println("File #2 has been downloaded successfully.");
            }
            outputStream2.close();
            inputStream.close(); 
        }}
            catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
       
    }
    
    public void pobierz_wszystko(Context context,String folderToADD,String name)
    {
        File folder = new File(context.getFilesDir() + 
                File.separator + folderToADD);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            // Do so1mething on success
        } else {
            // Do something else on failure 
}
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);     
        ByteArrayInputStream bs=null;
        ftpClient = new FTPClient();
        try {
        	int ilosc_zdjec=Integer.parseInt(name.replaceAll("[\\D]",""));
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            for(int i = 0 ; i<= ilosc_zdjec ; i++)
            {
            	try{
            		String remoteFile2 = "/Projekt/miejsca/"+i+".png";
                    File downloadFile2 = new File(folder.getAbsolutePath(),i+".png");
                    OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(downloadFile2));
                    InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
                    byte[] bytesArray = new byte[4096];
                    int bytesRead = -1;
                    while ((bytesRead = inputStream.read(bytesArray)) != -1) 
                    {
                         outputStream2.write(bytesArray, 0, bytesRead);
                    }
                    success = ftpClient.completePendingCommand();
                    if (success) 
                    {
                    	Log.d("Watek Zdjecia", "pobralem fote dla "+String.valueOf(i));
                    }
                        outputStream2.close();
                        inputStream.close(); 
                    }
            	catch(Exception e)
            	{
            		Log.d("Watek Zdjecia", "nie mam dla "+String.valueOf(i));
            	}
            }
        }
        catch (Exception ex) {
        System.out.println("Error: " + ex.getMessage());
        ex.printStackTrace();
    } finally {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
   
}
            
           
    
    
    
    boolean checkFileExists(String filePath) throws IOException {
        InputStream inputStream = ftpClient.retrieveFileStream(filePath);
        returnCode = ftpClient.getReplyCode();
        if (inputStream == null || returnCode == 550) {
            return false;
        }
        return true;
    }
        
    private Bitmap wczytaj(String name) 
    {
        
        // TODO Auto-generated method stub
        return null;
    }
    
}