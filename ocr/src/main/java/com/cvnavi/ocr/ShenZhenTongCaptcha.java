package com.cvnavi.ocr;


import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ShenZhenTongCaptcha {

    public static String doOcr(File file) throws IOException {
        return doOcr(ImageIO.read(file));
    }

    public static String doOcr(BufferedImage img) throws IOException {
        BufferedImage optimized=optimizeImg(img);
//        ImageIO.write(optimized,"png", new File("E:\\1.png"));

        ITesseract te=new Tesseract();
        try {
            te.setLanguage("eng");
            te.setDatapath("E:\\data");
            te.setTessVariable("tessedit_char_whitelist", "0123456789");
            te.setPageSegMode(ITessAPI.TessPageSegMode.PSM_SINGLE_LINE);

            String s=te.doOCR(optimized);
            return  s;
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage optimizeImg(BufferedImage src){
        //去除边缘
        BufferedImage sub=src.getSubimage(2,3,src.getWidth()-4,src.getHeight()-6);

        //去除3个数字之间得干扰
        for(int x=1;x<sub.getWidth()-1;x++){
            for(int y=1;y<sub.getHeight()-1;y++){

                if(x>12&& x<27){
                    sub.setRGB(x,y,-1);
                }
                if(x>38&& x<52){
                    sub.setRGB(x,y,-1);
                }

                if(sub.getRGB(x,y-1)==-1&&
                        sub.getRGB(x,y+1)==-1){
                    sub.setRGB(x,y,-1);
                }
            }
        }

        fillBlue(sub);
        fillBlue(sub);
        return sub;
    }

    public static void fillBlue(BufferedImage sub){
        for(int x=1;x<sub.getWidth()-1;x++){
            for(int y=1;y<sub.getHeight()-1;y++){

                if(sub.getRGB(x,y)!=-1&&
                        almostBlackOrBlue(sub.getRGB(x,y-1))&&
                        almostBlackOrBlue(sub.getRGB(x,y+1))&&
                        !almostBlue(sub.getRGB(x,y))){
                    sub.setRGB(x,y,sub.getRGB(x,y)&0xFF0000FF);
                }

                if(sub.getRGB(x,y)!=-1&&
                        almostBlackOrBlue(sub.getRGB(x-1,y))&&
                        almostBlackOrBlue(sub.getRGB(x+1,y))&&
                        !almostBlue(sub.getRGB(x,y))){
                    sub.setRGB(x,y,sub.getRGB(x,y)&0xFF0000FF);
                }
            }
        }
    }


    public static int getRed(int rgb){
        return (rgb&0xFF0000)>>16;
    }

    public  static int getGreen(int rgb){
        return (rgb&0xFF00)>>8;
    }

    public  static int getBlue(int rgb){
        return (rgb&0xFF);
    }

    public static boolean almostBlue(int rgb){
        int r=getRed(rgb);
        int g=getGreen(rgb);
        int b=getBlue(rgb);
        return b>80&&((r/(float)b)<0.2)&&((g/(float)b)<0.2);
    }

    public static boolean almostBlack(int rgb){
        int r=getRed(rgb);
        int g=getGreen(rgb);
        int b=getBlue(rgb);
        return r<70&&g<70&b<70;
    }

    public static boolean almostBlackOrBlue(int rgb){
        return almostBlue(rgb)||almostBlack(rgb);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(doOcr(new File("E:\\1.gif")));
    }
}
