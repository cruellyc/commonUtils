package com.liyc.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jbarcode.JBarcode;
import org.jbarcode.encode.Code39Encoder;
import org.jbarcode.paint.BaseLineTextPainter;
import org.jbarcode.paint.WideRatioCodedPainter;
import org.jbarcode.util.ImageUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.drew.imaging.ImageProcessingException;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

/**
 *图片处理
*/
public class UnionImageUtil {
	private static Logger logger = Logger.getLogger(UnionImageUtil.class);
	/**
	 * 上传图片
	 * @param request
	 * @param path
	 * @param ratio
	 * @return
	 * @throws IOException 
	 * @throws ImageProcessingException 
	 * @throws IllegalStateException 
	 */
	public static String upload(HttpServletRequest request,String path,int ratio) throws IllegalStateException, ImageProcessingException, IOException{
		// 创建一个通用的多部分解析器
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
						request.getSession().getServletContext());
		// 判断 request 是否有文件上传,即多部分请求
		if (multipartResolver.isMultipart(request)) {
			// 转换成多部分request
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			// 取得request中的所有文件名
			Iterator<String> iter = multiRequest.getFileNames();
			Pattern fileR = Pattern.compile("^.*\\.(?i)(bmp|png|gif|jpeg|jpg)$");
			while (iter.hasNext()) {
				// 取得上传文件
				MultipartFile file = multiRequest.getFile(iter.next());
				if (file != null) {
					// 取得当前上传文件的文件名称
					String myFileName = file.getOriginalFilename();

					String[] arr = myFileName.split("\\.");
					String picFormat = arr[(arr.length - 1)];// 后缀名

					logger.debug("--------------------------------------------" + picFormat);
					Matcher mat = fileR.matcher(myFileName);
					if (!mat.find()) {
						return null;
					}

					String prefix = RandUtil.getRandStr(16);// 文件名，不包括后缀
					File pathDir = new File(path);
					if (!pathDir.exists()) {
						pathDir.mkdirs();
					}
					logger.debug("bfImage bef" + file.getSize());
					BufferedImage bfImage = RotateImage.getBufferedImage(file.getInputStream(), file.getInputStream());

					logger.debug("bfImage aft");
					int imageWidth = bfImage.getWidth();
					int imageHeight = bfImage.getHeight();
					
					if(ratio==1){
						Thumbnails.of(bfImage).size(imageWidth, imageHeight).outputFormat(picFormat)
						.toFile(path + "/" + prefix + "." + picFormat);
					}else{
						imageWidth=imageWidth/ratio;
						imageHeight=imageHeight/ratio;
						Thumbnails.of(bfImage).sourceRegion(Positions.CENTER, imageWidth, imageHeight)
						.size(imageWidth, imageHeight).outputFormat(picFormat)
						.toFile(path + "/" + prefix + ratio+"." + picFormat);
					}
					return path + "/" + prefix + "." + picFormat;
				}
			}
		} else {
			logger.debug("is not MultiPart");
		}
		return null;
	}
	/**
	 * 显示图片
	 * @param thumb
	 * @param ratio
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public static void showImg(String thumb,int ratio,HttpServletRequest request, HttpServletResponse response) throws IOException{
		String[] arr = thumb.split("\\.");
		String picFormat = arr[(arr.length - 1)];// 后缀名
		String img = thumb.split("\\." + picFormat)[0];
		Resource res = new FileSystemResource(img + ratio + "." + picFormat);
		response.setHeader("Content-Type", "image/" + picFormat);
		response.setHeader("Content-Length", new String(String.valueOf(res.contentLength()).getBytes(), "UTF-8"));
		ServletOutputStream os = response.getOutputStream();

		FileCopyUtils.copy(res.getInputStream(), os);
	}
	
	/**
	 * 生成条形码
	 * @param logisticSn
	 * @param response
	 */
	public static void showBarcode(String logisticSn, HttpServletResponse response) {
		BufferedImage localBufferedImage = null;
		try {
			JBarcode localJBarcode = new JBarcode(Code39Encoder.getInstance(), WideRatioCodedPainter.getInstance(),
					BaseLineTextPainter.getInstance());
			localJBarcode.setCheckDigit(false);
			localBufferedImage = localJBarcode.createBarcode(logisticSn);
			logger.debug("localBufferedImage" + localBufferedImage);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		logger.debug(localBufferedImage);
		try {
			ServletOutputStream os = response.getOutputStream();
			ImageUtil.encodeAndWrite(localBufferedImage, "png", os);
			os.flush();
			os.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
}
