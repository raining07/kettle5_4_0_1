package org.pentaho.di.trans.steps.oss.uploader;

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
 * 
 * @author xuejian
 *
 */
@Step(id = "OssUploader", image = "oss-uploader.png", i18nPackageName = "org.pentaho.di.trans.steps.oss.uploader", name = "OssUploader.Name", description = "OssUploader.TooltipDesc", categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Output")
public class OssUploaderMeta extends BaseStepMeta implements StepMetaInterface {

	/*
	 * XML tags
	 */
	private final String TAG_ENDPOINT = "endpoint";

	/*
	 * Form fields
	 */
	private String endpoint;
	private String accessKey;
	private String secureKey;
	private String bucket;

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

	public OssUploaderMeta() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setDefault() {
		endpoint = "";
		accessKey = "";
		secureKey = "";
		bucket = "";
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StepDataInterface getStepData() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Q1:发现加载图标失败,参照其它插件写这个方法,试试效果:无效 <br/>
	 * Q2:换成已有图标,成功,证明是图片的问题 A:这个方法是读取kettle脚本(XML)的,并读取表单数据<br/>
	 * see
	 * org.pentaho.di.ui.trans.steps.oss.uploader.OssUploaderDialog.getInfo(OssUploaderMeta,
	 * boolean)
	 */
	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {
		readData(stepnode, metaStore);
	}

	private void readData(Node stepnode, IMetaStore metaStore) throws KettleXMLException {
		try {
			endpoint = XMLHandler.getTagValue(stepnode, TAG_ENDPOINT);
		} catch (Exception e) {
			throw new KettleXMLException("Unable to load step info from XML", e);
		}
	}

	@Override
	public String getXML() throws KettleException {
		StringBuffer retval = new StringBuffer();
		retval.append("    ").append(XMLHandler.addTagValue(TAG_ENDPOINT, endpoint));
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
		} catch (Exception e) {
			throw new KettleException("Unable to save step information to the repository for id_step=" + id_step, e);
		}
	}
}
