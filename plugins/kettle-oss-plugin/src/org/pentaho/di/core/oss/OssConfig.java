package org.pentaho.di.core.oss;

/**
 * OSS config
 * 
 * @author xuejian
 *
 */
public class OssConfig {
	private String endpoint;
	private String accessKey;
	private String secureKey;
	private String bucket;

	public OssConfig(String endpoint, String accessKey, String secureKey, String bucket) {
		super();
		this.endpoint = endpoint;
		this.accessKey = accessKey;
		this.secureKey = secureKey;
		this.bucket = bucket;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecureKey() {
		return secureKey;
	}

	public void setSecureKey(String secureKey) {
		this.secureKey = secureKey;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}
}
