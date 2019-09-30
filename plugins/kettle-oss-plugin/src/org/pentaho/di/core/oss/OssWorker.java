package org.pentaho.di.core.oss;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectMetadata;
import com.google.common.collect.Lists;

/**
 * OSS Client高程度封装工具
 * 
 * @author xuejian
 *
 */
public class OssWorker implements Closeable {
	private OssConfig config;
	private OSSClient ossClient;
	private InputStream in;

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
	public void doUpload(boolean coverMode, String sourceFileFullPath, String targetFileName)
			throws FileNotFoundException {
		if (!coverMode) {
			if (ossClient.doesObjectExist(this.config.getBucket(), targetFileName)) {
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
		in = new FileInputStream(file);
		ossClient.putObject(this.config.getBucket(), targetFileName, in);
	}

	public void doDownload(String bucket, String remoteFileName, String localPath, String localFileName) {
	}

	public List<String> getOssFiles(String fileName, String lowerLimitMarker, boolean nameAsPrevious, int limit) {
		if (!nameAsPrevious) {
			// 文件名称不作为前缀,即全称
			return Lists.newArrayList(fileName);
		}
		ListObjectsRequest listObjectsRequest = new ListObjectsRequest(this.config.getBucket(), fileName,
				lowerLimitMarker, null, limit);
		List<OSSObjectSummary> objectSummaries = ossClient.listObjects(listObjectsRequest).getObjectSummaries();
		List<String> ossFiles = Lists.newArrayList();
		for (OSSObjectSummary ossObjectSummary : objectSummaries) {
			ossFiles.add(ossObjectSummary.getKey());
		}
		return ossFiles;
	}

	@Override
	public void close() {
		try {
			if (in != null) {
				in.close();
			}
		} catch (Throwable thr) {
			thr.printStackTrace();
		}
		try {
			ossClient.shutdown();
		} catch (Throwable thr) {
			thr.printStackTrace();
		}
		ossClient = null;
		config = null;
		in = null;
	}

	public OSSFileObject getOSSFileObject(String fileName) {
		OSSFileObject ossFileObject = new OSSFileObject();
		OSSObject ossObject = ossClient.getObject(config.getBucket(), fileName);
		ossFileObject.setContent(ossObject.getObjectContent());
		ObjectMetadata objectMetadata = ossObject.getObjectMetadata();
		ossFileObject.setEncoding(objectMetadata.getContentEncoding());
		ossFileObject.setContentType(objectMetadata.getContentType());
		ossFileObject.setContentLength(objectMetadata.getContentLength());
		ossFileObject.setContentMD5(objectMetadata.getContentMD5());
		return ossFileObject;
	}

}
