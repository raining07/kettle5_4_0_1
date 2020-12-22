package org.pentaho.di.trans.steps.oss.downloader;

import java.util.List;

import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

/**
 * StepMeta
 * 
 * @author xuejian
 *
 */
@Step(id = "OssDownloader", image = "oss-downloader.png", i18nPackageName = "org.pentaho.di.trans.steps.oss.downloader", name = "OssDownloader.Name", description = "OssDownloader.TooltipDesc", categoryDescription = "i18n:org.pentaho.di.trans.steps.oss.downloader:OssDownloader.Step.Category")
public class OssDownloaderMeta extends BaseStepMeta implements StepMetaInterface {

	/*
	 * XML tags
	 */
	private final String TAG_ENDPOINT = "endpoint";
	private final String TAG_ACCESS_KEY = "access_key";
	private final String TAG_SECURE_KEY = "secure_key";
	private final String TAG_BUCKET = "bucket";
	private final String TAG_FILENAME_AS_PREVIOUS = "filenameAsPrevious";
	private final String TAG_FILENAME = "fileName";
	private final String TAG_LOWER_LIMIT_MARKER = "lowerLimitMarker";
	private final String TAG_DELETE_OSS = "deleteOss";
	private final String TAG_DOWNLOAD_DIR = "downloadDir";

	/*
	 * Form fields
	 */
	private String endpoint;
	private String accessKey;
	private String secureKey;
	private String bucket;
	private boolean filenameAsPrevious; // 文件名是否作为前缀
	private String fileName; // 文件名
	private String lowerLimitMarker; // 文件名最小匹配标记
	private String downloadDir; // 下载目录
	private boolean deleteOss; // 下载之后是否删除OSS文件

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

	public boolean isFilenameAsPrevious() {
		return filenameAsPrevious;
	}

	public void setFilenameAsPrevious(boolean filenameAsPrevious) {
		this.filenameAsPrevious = filenameAsPrevious;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLowerLimitMarker() {
		return lowerLimitMarker;
	}

	public void setLowerLimitMarker(String lowerLimitMarker) {
		this.lowerLimitMarker = lowerLimitMarker;
	}

	public String getDownloadDir() {
		return downloadDir;
	}

	public void setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
	}

	public boolean isDeleteOss() {
		return deleteOss;
	}

	public void setDeleteOss(boolean deleteOss) {
		this.deleteOss = deleteOss;
	}

	public OssDownloaderMeta() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setDefault() {
		endpoint = "";
		accessKey = "";
		secureKey = "";
		bucket = "";
		filenameAsPrevious = true;
		fileName = "";
		lowerLimitMarker = "";
		deleteOss = false;
		downloadDir = "";
	}

	/**
	 * 提供getStep方法是为执行org.pentaho.di.trans.Trans.prepareExecution可以获取step
	 */
	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new OssDownloader(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new OssDownloaderData();
	}

	/**
	 * Q1:发现加载图标失败,参照其它插件写这个方法,试试效果:无效 <br/>
	 * Q2:换成已有图标,成功,证明是图片的问题 A:这个方法是读取kettle脚本(XML)的,并读取表单数据<br/>
	 * see
	 * org.pentaho.di.ui.trans.steps.oss.downloader.OssDownloaderDialog.getInfo(OssDownloaderMeta,
	 * boolean)
	 */
	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {
		readData(stepnode, metaStore);
	}

	private void readData(Node stepnode, IMetaStore metaStore) throws KettleXMLException {
		try {
			endpoint = XMLHandler.getTagValue(stepnode, TAG_ENDPOINT);
			accessKey = XMLHandler.getTagValue(stepnode, TAG_ACCESS_KEY);
			secureKey = XMLHandler.getTagValue(stepnode, TAG_SECURE_KEY);
			bucket = XMLHandler.getTagValue(stepnode, TAG_BUCKET);
			filenameAsPrevious = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, TAG_FILENAME_AS_PREVIOUS));
			fileName = XMLHandler.getTagValue(stepnode, TAG_FILENAME);
			lowerLimitMarker = XMLHandler.getTagValue(stepnode, TAG_LOWER_LIMIT_MARKER);
			downloadDir = XMLHandler.getTagValue(stepnode, TAG_DOWNLOAD_DIR);
			deleteOss = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, TAG_DELETE_OSS));
		} catch (Exception e) {
			throw new KettleXMLException("Unable to load step info from XML", e);
		}
	}

	@Override
	public String getXML() throws KettleException {
		StringBuffer retval = new StringBuffer();
		retval.append("    ").append(XMLHandler.addTagValue(TAG_ENDPOINT, endpoint));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_ACCESS_KEY, accessKey));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_SECURE_KEY, secureKey));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_BUCKET, bucket));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_FILENAME_AS_PREVIOUS, filenameAsPrevious));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_FILENAME, fileName));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_LOWER_LIMIT_MARKER, lowerLimitMarker));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_DOWNLOAD_DIR, downloadDir));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_DELETE_OSS, deleteOss));
		return retval.toString();
	}

	/**
	 * Q1:方法readRep实现了,但仍然无法保存字段值<br/>
	 * Q2:找到问题,没获取到表单值
	 */
	@Override
	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases)
			throws KettleException {
		try {
			endpoint = rep.getStepAttributeString(id_step, TAG_ENDPOINT);
			accessKey = rep.getStepAttributeString(id_step, TAG_ACCESS_KEY);
			secureKey = rep.getStepAttributeString(id_step, TAG_SECURE_KEY);
			bucket = rep.getStepAttributeString(id_step, TAG_BUCKET);
			filenameAsPrevious = rep.getStepAttributeBoolean(id_step, TAG_FILENAME_AS_PREVIOUS);
			fileName = rep.getStepAttributeString(id_step, TAG_FILENAME);
			lowerLimitMarker = rep.getStepAttributeString(id_step, TAG_LOWER_LIMIT_MARKER);
			downloadDir = rep.getStepAttributeString(id_step, TAG_DOWNLOAD_DIR);
			deleteOss = rep.getStepAttributeBoolean(id_step, TAG_DELETE_OSS);
		} catch (Exception e) {
			throw new KettleException("Unexpected error reading step information from the repository", e);
		}
	}

	/**
	 * Q1:不能保存字段值,发现方法readRep没有实现
	 */
	@Override
	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step)
			throws KettleException {
		try {
			rep.saveStepAttribute(id_transformation, id_step, TAG_ENDPOINT, endpoint);
			rep.saveStepAttribute(id_transformation, id_step, TAG_ACCESS_KEY, accessKey);
			rep.saveStepAttribute(id_transformation, id_step, TAG_SECURE_KEY, secureKey);
			rep.saveStepAttribute(id_transformation, id_step, TAG_BUCKET, bucket);
			rep.saveStepAttribute(id_transformation, id_step, TAG_FILENAME_AS_PREVIOUS, filenameAsPrevious);
			rep.saveStepAttribute(id_transformation, id_step, TAG_FILENAME, fileName);
			rep.saveStepAttribute(id_transformation, id_step, TAG_LOWER_LIMIT_MARKER, lowerLimitMarker);
			rep.saveStepAttribute(id_transformation, id_step, TAG_DOWNLOAD_DIR, downloadDir);
			rep.saveStepAttribute(id_transformation, id_step, TAG_DELETE_OSS, deleteOss);
		} catch (Exception e) {
			throw new KettleException("Unable to save step information to the repository for id_step=" + id_step, e);
		}
	}
}
