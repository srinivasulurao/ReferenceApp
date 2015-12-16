package com.texastech.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.os.Environment;

/**
 * Class is used to read and write json data into local storage (Txt File).
 */
public class FileUtil {

	/**
	 * Read the content of the file.
	 * 
	 * @param con
	 * @param fileName
	 */
	public static String readFile(Context con, String fileName) {

		FileInputStream fis;
		String result = "";
		try {
			fis = con.openFileInput(fileName);
			byte[] reader = new byte[fis.available()];
			while (fis.read(reader) != -1) {
			}
			result = new String(reader);
			if (fis != null) {
				fis.close();
			}
			MLog.v("File", "read sucessfully : " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Write the content of the file
	 * 
	 * @param con
	 * @param fileName
	 * @param jsonString
	 */
	public static void writeFile(Context con, String fileName, String jsonString) {
		try {
			File file = con.getFileStreamPath(fileName);
			if (file.exists()) {
				MLog.v("File", "delete sucessfully !");
				file.delete();
			}

			FileOutputStream fos = con.openFileOutput(fileName,
					Context.MODE_APPEND);
			fos.write(jsonString.getBytes());
			fos.close();

			MLog.v("File", "Write sucessfully : " + jsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void exportDatabse(Context context) {
		File file = new File(Environment.getExternalStorageDirectory()+ "/SellerSight");
		if (file.exists())
			DeleteRecursive(file);
		file.mkdirs();
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			MLog.v("sd.canWrite()", "" + sd.canWrite());
			if (sd.canWrite()) {
				String currentDBPath = "//data//" + "com.du.app"+ "//databases//" + "DuShop.sqlite";
				String backupDBPath = "SellerSight/DuShop.sqlite";
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB)
							.getChannel();
					FileChannel dst = new FileOutputStream(backupDB)
							.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public static void DeleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory())
			for (File child : fileOrDirectory.listFiles())
				DeleteRecursive(child);
		fileOrDirectory.delete();
	}
	
	
	
	public static String readFileFromAssets(Context context,String fileName) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br=null;
        try {
             br = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close(); //stop reading
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
