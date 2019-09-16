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
// 暂未开发
// @Step(id = "OssDownloader", image = "oss-downloader.png", i18nPackageName = "org.pentaho.di.trans.steps.oss.downloader", name = "OssDownloader.Name", description = "OssDownloader.TooltipDesc", categoryDescription = "i18n:org.pentaho.di.trans.steps.oss.downloader:OssDownloader.Step.Category")
public class OssDownloaderMeta extends BaseStepMeta implements StepMetaInterface {

	/*
	 * XML tags
	 */
	private final String TAG_ENDPOINT = "endpoint";
	private final String TAG_ACCESS_KEY = "access_key";
	private final String TAG_SECURE_KEY = "secure_key";
	private final String TAG_BUCKET = "bucket";
	private final String TAG_TARGET_FILE_NAME = "target_file_name";
	private final String TAG_SOURCE_FILE_PATH = "source_file_path";
	private final String TAG_SOURCE_FILE_NAME = "source_file_name";
	private final String TAG_COVER_FILE = "cover_file";

	/*
	 * Form fields
	 */
	private String endpoint;
	private String accessKey;
	private String secureKey;
	private String bucket;
	private String targetFileName;
	private String sourceFilePath;
	private String sourceFileName;
	private boolean coverFile;

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

	public String getTargetFileName() {
		return targetFileName;
	}

	public void setTargetFileName(String targetFileName) {
		this.targetFileName = targetFileName;
	}

	public String getSourceFilePath() {
		return sourceFilePath;
	}

	public void setSourceFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public boolean isCoverFile() {
		return coverFile;
	}

	public void setCoverFile(boolean coverFile) {
		this.coverFile = coverFile;
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
		targetFileName = "";
		sourceFilePath = "";
		sourceFileName = "";
		coverFile = true;
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
			targetFileName = XMLHandler.getTagValue(stepnode, TAG_TARGET_FILE_NAME);
			sourceFilePath = XMLHandler.getTagValue(stepnode, TAG_SOURCE_FILE_PATH);
			sourceFileName = XMLHandler.getTagValue(stepnode, TAG_SOURCE_FILE_NAME);
			coverFile = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, TAG_COVER_FILE));
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
		retval.append("    ").append(XMLHandler.addTagValue(TAG_TARGET_FILE_NAME, targetFileName));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_SOURCE_FILE_PATH, sourceFilePath));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_SOURCE_FILE_NAME, sourceFileName));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_COVER_FILE, coverFile));
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
			targetFileName = rep.getStepAttributeString(id_step, TAG_TARGET_FILE_NAME);
			sourceFilePath = rep.getStepAttributeString(id_step, TAG_SOURCE_FILE_PATH);
			sourceFileName = rep.getStepAttributeString(id_step, TAG_SOURCE_FILE_NAME);
			coverFile = rep.getStepAttributeBoolean(id_step, TAG_COVER_FILE);
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
			rep.saveStepAttribute(id_transformation, id_step, TAG_TARGET_FILE_NAME, targetFileName);
			rep.saveStepAttribute(id_transformation, id_step, TAG_SOURCE_FILE_PATH, sourceFilePath);
			rep.saveStepAttribute(id_transformation, id_step, TAG_SOURCE_FILE_NAME, sourceFileName);
			rep.saveStepAttribute(id_transformation, id_step, TAG_COVER_FILE, coverFile);
		} catch (Exception e) {
			throw new KettleException("Unable to save step information to the repository for id_step=" + id_step, e);
		}
	}
}
