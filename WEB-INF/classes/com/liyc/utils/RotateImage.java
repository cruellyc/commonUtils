package com.liyc.utils;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;

public class RotateImage {
    private static final Logger logger = Logger.getLogger(RotateImage.class);

    public static BufferedImage getBufferedImage(InputStream inputStream1,InputStream inputStream)throws IllegalStateException,IOException, ImageProcessingException{
        logger.info("enter");
        Metadata metadata = ImageMetadataReader.readMetadata(inputStream1);
        Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        int orientation = 1;//正常方向
        try {
            if(directory != null){
                orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            }else{
                logger.error("Could not get directory");
            }
        } catch (MetadataException me) {
            logger.error("Could not get orientation");
        }

        BufferedImage bfImage = ImageIO.read(inputStream);
        int rotation = 0;
        if(orientation == 3){
        	rotation = 180;
        }else if(orientation == 6){
        	rotation = 90;
        }else if(orientation == 8){
        	rotation = 270;
        }else{
            logger.info("正常"+orientation);
            return bfImage;
        }
        logger.info("旋转了"+rotation);
        BufferedImage des = RotateImage.Rotate(bfImage, rotation);
        return des;
    }

	
	public static BufferedImage Rotate(Image src, int angel) {
        int src_width = src.getWidth(null);
        int src_height = src.getHeight(null);
        // calculate the new image size
        Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(
                src_width, src_height)), angel);

        BufferedImage res = null;
        res = new BufferedImage(rect_des.width, rect_des.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = res.createGraphics();
        // transform  
        g2.translate((rect_des.width - src_width) / 2,
                (rect_des.height - src_height) / 2);
        g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);
  
        g2.drawImage(src, null, null);
        return res;
    }
  
    public static Rectangle CalcRotatedSize(Rectangle src, int angel) {
        // if angel is greater than 90 degree, we need to do some conversion
        if (angel >= 90) {
            if(angel / 90 % 2 == 1){
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }  
            angel = angel % 90;
        }  
  
        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);
  
        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_height));
        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
        return new Rectangle(new Dimension(des_width, des_height));
    }
    
    public static int getOrientation(String orientation)
    {
        int tag = 0;
        if ("Top, left side (Horizontal / normal)".equalsIgnoreCase(orientation)) {
            tag = 1;
        } else if ("Top, right side (Mirror horizontal)".equalsIgnoreCase(orientation)) {
            tag = 2;
        } else if ("Bottom, right side (Rotate 180)".equalsIgnoreCase(orientation)) {
            tag = 3;
        } else if ("Bottom, left side (Mirror vertical)".equalsIgnoreCase(orientation)) {
            tag = 4;
        } else if ("Left side, top (Mirror horizontal and rotate 270 CW)".equalsIgnoreCase(orientation)) {
            tag = 5;
        } else if ("Right side, top (Rotate 90 CW)".equalsIgnoreCase(orientation)) {
            tag = 6;
        } else if ("Right side, bottom (Mirror horizontal and rotate 90 CW)".equalsIgnoreCase(orientation)) {
            tag = 7;
        } else if ("Left side, bottom (Rotate 270 CW)".equalsIgnoreCase(orientation)) {
            tag = 8;
        }
        return  tag;
    }

}
