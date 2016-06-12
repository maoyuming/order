package com.duantuke.order.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 配置文件助手
 * @author 须俊杰
 * @version 1.0 2012-8-8
 */
public class PropertyConfigurer{
	
	private static final Logger logger = LoggerFactory.getLogger(PropertyConfigurer.class);
	private static final String DEFAULT_PROPERTIES_FILE = "config.properties";
	private static final Properties properties = new Properties();
	private static final Map<String, Properties> propMap = new ConcurrentHashMap<String, Properties>();
	private static final Object LOCK = new Object();
	private String[] locations;
	
	/**
	 * 获取所有配置
	 * @return
	 */
	public static Properties getProperties(){
		return properties;
	}
	
	/**
	 * 获取默认配置文件配置信息
	 * @param key 
	 * @return
	 */
	public static String getProperty(String key) {
		return getProperty(DEFAULT_PROPERTIES_FILE, key);
	}

	/**
	 * 获取指定配置文件信息
	 * @param prop 指定配置文件
	 * @param key
	 * @return
	 */
	public static String getProperty(String prop, String key) {
		if(StringUtils.isEmpty(prop)){
			return null;
		}
		
		try {
			if (!propMap.containsKey(prop)) {
				synchronized (LOCK) {
					if (!propMap.containsKey(prop)) {
						loadProperties(prop);
						refresh();
					}
				}
			}
			String value = properties.getProperty(key);
			logger.debug("获取配置信息 key = {} , value = {}", key, value);
			return value;
		} catch (FileNotFoundException e) {
			logger.error("文件" + prop + "没找到", e);
		} catch (IOException e) {
			logger.error("IO异常", e);
		}
		return null;
	}
	
	/**
	 * 设定单个配置文件
	 * @param location
	 */
	public void setLocation(String location){
		if(!ArrayUtils.contains(this.locations, location)){
			this.setLocations(new String[]{location});
		}
	}
	
    /**
     * 设定多配置文件,然后加载
     * @param locations
     */
	public void setLocations(String[] locations) {
		this.locations = (String[]) ArrayUtils.addAll(this.locations, locations);
		try {
			loadAllProperties();
			refresh();
		} catch (FileNotFoundException e) {
			logger.error("文件没找到",e);
		} catch (IOException e) {
			logger.error("IO异常",e);
		}
	}
	
	/**
	 * 加载所以配置文件
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void loadAllProperties() throws FileNotFoundException, IOException {
		logger.info("加载所有的配置文件");
		for (String location : locations) {
			loadProperties(location);
		}
	}
	
	/**
	 * 加载配置文件
	 * @param location 文件路径
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void loadProperties(String location)
			throws FileNotFoundException, IOException {
		logger.info("加载配置文件{}", location);
		Properties prop = new Properties();
		prop.load(new FileInputStream(FileUtil.disposeFileName(location)));
		propMap.put(location, prop);
	}
	
	/**
	 * 刷新Properties缓存
	 */
	public static void refresh() {
		logger.info("刷新Properties");
		for (Properties prop : propMap.values()) {
			properties.putAll(prop);
		}
	}
}
