package org.pentaho.di.core.oss;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;

/**
 * OSS Client高程度封装工具
 * 
 * @author xuejian
 *
 */
public class OssWorker implements Closeable {
	private OssConfig config;
	private OSSClient ossClient;

	public OssWorker(OssConfig config) {
		super();
		this.config = config;
		this.ossClient = new OSSClient(config.getEndpoint(), config.getAccessKey(), config.getSecureKey());
	}

	public OssConfig getConfig() {
		return config;
	}

	/**
	 * OSS上传文件
	 * 
	 * @param bucket             目标节点
	 * @param coverMode          覆盖模式
	 * @param sourceFileFullPath 源文件全路径
	 * @param targetFileName     目标文件名
	 * @throws FileNotFoundException
	 */
	public void doUpload(String bucket, boolean coverMode, String sourceFileFullPath, String targetFileName)
			throws FileNotFoundException {
		if (!coverMode) {
			if (ossClient.doesObjectExist(bucket, targetFileName)) {
				int splitPostion = targetFileName.lastIndexOf(".");
				if (splitPostion <= 0) {
					splitPostion = targetFileName.length();
				}
				String filename = targetFileName.substring(0, splitPostion);
				String suffix = targetFileName.substring(splitPostion);
				targetFileName = filename + "2" + suffix;
			}
		}
		File file = new File(sourceFileFullPath);
		ObjectMetadata objectMeta = new ObjectMetadata();
		objectMeta.setContentLength(file.length());
		// 可以在metadata中标记文件类型
		// objectMeta.setContentType("image/jpeg");
		InputStream input = new FileInputStream(file);
		ossClient.putObject(bucket, targetFileName, input);
	}

	public void doDownload(String bucket, String remoteFileName, String localPath, String localFileName) {
	}

	@Override
	public void close() throws IOException {
		ossClient.shutdown();
		ossClient = null;
		config = null;
	}
}
