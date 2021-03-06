package au.edu.usyd.reviewer.server.util;

import java.io.BufferedInputStream;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileUtil {

	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {}

	public static void copyFile(String sourceFilename, String targetFilename) throws IOException {
		File sourceFile = new File(sourceFilename);
		File targetFile = new File(targetFilename);

		FileUtils fu = new FileUtils();
		fu.copyFile(sourceFile, targetFile);		
	}

	public static String escapeFilename(String filename) {
		return filename.replaceAll("[\\\\/:*?\"<>|]", "-");
	}
	
	public static void zipFolder(File sourceFolder, File targetFolder) {

		try {
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(targetFolder)));
			BufferedInputStream in = null;
			byte[] data = new byte[1024];
			File[] files = sourceFolder.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					in = new BufferedInputStream(new FileInputStream(file), 1000);
					out.putNextEntry(new ZipEntry(file.getName()));
					int count;
					while ((count = in.read(data, 0, 1024)) != -1) {
						out.write(data, 0, count);
					}
					out.closeEntry();
				}
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error zipping folder contents: " + sourceFolder, e);
		}
	}
	
	
	public static String replaceBlanks(String filename) {
		return filename.replaceAll("[\\s]", "-");
	}
}