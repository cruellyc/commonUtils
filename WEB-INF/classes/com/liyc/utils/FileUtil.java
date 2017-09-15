package com.liyc.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * 文件工具
 * @date 2017年9月13日 下午2:46:37
 */
public class FileUtil {
	private static Logger logger = Logger.getLogger(FileUtil.class);

	/***
	 * 是否是文件夹
	 * 
	 * @param srcFile
	 * @return
	 */
	public static File mkDir(File srcFile) {
		if (srcFile.isDirectory() && !srcFile.exists()) {
			srcFile.mkdir();
		}
		return srcFile;
	}

	/***
	 * 是否是文件
	 * 
	 * @param dirPath
	 * @param fileName
	 * @return
	 */
	public static File mkFiles(String dirPath, String fileName) {
		File srcFile = new File(dirPath);
		if (!srcFile.exists()) {
			srcFile.mkdirs();
		}
		srcFile = new File(dirPath + fileName);
		return srcFile;
	}

	/***
	 * 复制文件
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		if (sourceFile.exists() && !targetFile.exists()) {
			try {
				inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
				outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
				byte[] b = new byte[1024 * 5];
				int len;
				while ((len = inBuff.read(b)) != -1) {
					outBuff.write(b, 0, len);
				}
				outBuff.flush();
			} finally {
				if (inBuff != null)
					inBuff.close();
				if (outBuff != null)
					outBuff.close();
			}
		}
		logger.info("file copy success");
	}

	/**
	 * 判断文件是否存在，存在则删除
	 * 
	 * @param srcFile
	 */
	public static void removeFile(File srcFile) {
		if (srcFile.exists()) {
			srcFile.delete();
		}
	}

	/***
	 * 获取文件后缀名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileSuffix(String fileName) {
		String fileSuffix = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
		return fileSuffix;
	}

	/***
	 * 读取文件中的文本
	 * 
	 * @param file
	 */
	public static String readFileTxt(File file) {
		if (file.exists() && file.isFile()) {
			InputStreamReader streamReader;
			try {
				streamReader = new InputStreamReader(new FileInputStream(file), "utf-8");
				@SuppressWarnings("resource")
				BufferedReader br = new BufferedReader(streamReader);
				StringBuffer sb = new StringBuffer();
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				return sb.toString();
			} catch (FileNotFoundException e) {
				logger.error("找不到指定文件");
			} catch (IOException e) {
				logger.error("io异常");
			}
		}
		return "";
	}

}
