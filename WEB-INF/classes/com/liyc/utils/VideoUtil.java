package com.liyc.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * 视频工具
 * 
 * @author liyc
 * @date 2017年11月7日 下午4:37:30
 */
public class VideoUtil {
	private static Logger logger = Logger.getLogger(VideoUtil.class);

	/**
	 * 上传视频
	 * @param request
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String uploadVideo(HttpServletRequest request, String path) throws IOException {
		logger.info("uploadVideo() start");

		// 创建一个通用的多部分解析器
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// 判断 request 是否有文件上传,即多部分请求
		if (multipartResolver.isMultipart(request)) {
			// 转换成多部分request
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			// 取得request中的所有文件名
			Iterator<String> iter = multiRequest.getFileNames();
			Pattern fileR = Pattern.compile("^.*\\.(?i)(mpg|mpeg|avi|rm|rmvb|mov|wmv|asf|dat|mp4)$");
			while (iter.hasNext()) {
				// 取得上传文件
				MultipartFile file = multiRequest.getFile(iter.next());
				if (file != null) {
					// 取得当前上传文件的文件名称
					String myFileName = file.getOriginalFilename();

					String[] arr = myFileName.split("\\.");
					String picFormat = arr[(arr.length - 1)];// 后缀名

					logger.debug(myFileName+"--------------------------------------------" + picFormat);
					Matcher mat = fileR.matcher(myFileName);
					if (!mat.find()) {
						return null;
					}

					File pathDir = new File(path);
					if (!pathDir.exists()) {
						pathDir.mkdirs();
					}
					InputStream in = file.getInputStream();
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					byte[] buff = new byte[2046];
					int rc = 0;
					byte[] downByte = null;
					while (-1 != (rc = in.read(buff))) {
						output.write(buff, 0, rc);
					}
					output.flush();
					downByte = output.toByteArray();
					output.close();
					in.close();
					OutputStream outStream = null;
					try {
						outStream = new FileOutputStream(pathDir + "/" + myFileName);
						outStream.write(downByte);
						outStream.flush();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							outStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					return pathDir + "/" + myFileName;
				}
			}
		}
		return null;
	}

	public static String uploadVideo2(File file, String path) throws IOException {
		System.out.println("uploadVideo() start" + file.getName());
		InputStream in = new FileInputStream(file);
		File pathDir = new File(path);
		if (!pathDir.exists()) {
			pathDir.mkdirs();
		}
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buff = new byte[2046];
		int rc = 0;
		byte[] downByte = null;
		while (-1 != (rc = in.read(buff))) {
			output.write(buff, 0, rc);
		}
		output.flush();
		downByte = output.toByteArray();
		output.close();
		in.close();
		OutputStream outStream = null;
		try {
			outStream = new FileOutputStream(pathDir + "/" + file.getName());
			outStream.write(downByte);
			outStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return pathDir + "/" + file.getName();
	}

	public static void main(String[] args) {
		try {
			uploadVideo2(new File("C:\\tempdir\\test.mp4"), "D:\\var\\video");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
