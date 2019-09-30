package org.pentaho.di.core.oss;

import java.util.List;

public class OssWorkerUtils {

	/**
	 * 浏览oss文件
	 * 
	 * @param ossConfig oss配置
	 * @param fileName  文件名
	 * @param lowerLimitMarker 文件名比较大小时的下限
	 * @param prevFlag  文件名是否为前缀
	 * @param limit     限定文件数量
	 * @return 书签
	 * @throws Exception 
	 */
	public static BookMark createBookMark(OssConfig ossConfig, String fileName, String lowerLimitMarker,
			boolean prevFlag, int limit) throws Exception {
		OssWorker ossWorker = null;
		try {
			ossWorker = new OssWorker(ossConfig);
			List<String> ossFiles = ossWorker.getOssFiles(fileName, lowerLimitMarker, prevFlag, limit);
			return new BookMark(ossFiles, -1);
		} catch (Exception e) {
			throw e;
		} finally {
			if (ossWorker != null) {
				ossWorker.close();
			}
		}
	}

}
