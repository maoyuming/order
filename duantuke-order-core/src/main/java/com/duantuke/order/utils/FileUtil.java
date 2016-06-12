package com.duantuke.order.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件工具
 * @author 须俊杰
 * @version 1.0 2012-8-7
 */
public class FileUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	private static final int BUFFERSIZE = 1024;
	public static final String CLASSPATH = "classpath:";
	
	/**
	 * 读取文件
	 * @param fileName 文件名
	 * @return 读取返回文件Bytebuffer，失败返回null
	 * @throws IOException 
	 */
	public static ByteBuffer read(String fileName) throws IOException{
		if(null == fileName){
			logger.error("文件名不能为空");
			return null;
		}
		
		// 处理文件路径
		fileName = disposeFileName(fileName);
		
		logger.debug("开始读取文件{}",fileName);		
		
		FileInputStream fis = new FileInputStream(fileName);
		// 得到文件通道
		FileChannel fc = fis.getChannel();
		
		ByteBuffer result = ByteBuffer.allocate((int)fc.size());
		
		// 创建缓冲区
		ByteBuffer bf = ByteBuffer.allocate(BUFFERSIZE);
		/*
		 * 读取通道中的下一块数据到缓冲区中
		 * 
		 * 缓冲区的 position 即为当前缓冲区中最后有效位置
		 */
		while(fc.read(bf) != -1){
			
			//把缓冲中当前位置恢复为零之前把缓冲区的 limit 设置为之前 position 值
			bf.flip();
			
			while(bf.hasRemaining()){
				result.put(bf);
			}
			
			// 清空缓冲区
			bf.clear();
		}
		
		fc.close();
		fis.close();
		logger.debug("文件读取完毕,FileSize:{}",result.limit());	
		return result;
	}
	
	/**
	 * 写入文件
	 * 
	 * @param fileName 文件名
	 * @param content 内容
	 * @throws IOException
	 */
	public static boolean write(String fileName, ByteBuffer content) throws IOException{
		if(null == fileName){
			logger.error("文件名不能为空");
			return false;
		}
		
		// 处理文件路径
		fileName = disposeFileName(fileName);
		
		logger.debug("开始写文件{}",fileName);	
		FileOutputStream fos = new FileOutputStream(fileName);
		
		// 得到文件通道
		FileChannel fc = fos.getChannel();
		fc.write(content);
		
		fc.close();
		fos.close();
		logger.debug("文件写入完毕,FileSize:{}",content.limit());
		return true;
	}
	
	/**
	 * 处理文件名<p>
	 * 如果文件名以classpath:开头，则默认到classpath下查找文件
	 * @param fileName 文件名
	 * @return 处理后的文件名
	 */
	public static String disposeFileName(String fileName) {
		if (fileName.indexOf(":\\") != -1 || fileName.indexOf("/") == 0) {
			return fileName;
		}

		int index = fileName.indexOf(CLASSPATH);
		if (index != -1) {
			return Thread.currentThread().getContextClassLoader()
					.getResource("").getPath()
					+ fileName.substring(CLASSPATH.length(), fileName.length());
		} else {
			return Thread.currentThread().getContextClassLoader()
					.getResource("").getPath()
					+ fileName;
		}
	}
}
