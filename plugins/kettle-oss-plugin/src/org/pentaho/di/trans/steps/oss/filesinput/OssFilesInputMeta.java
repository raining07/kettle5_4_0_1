package org.pentaho.di.trans.steps.oss.filesinput;

import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.variables.VariableSpace;
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
import org.pentaho.di.trans.steps.textfileinput.TextFileInputField;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

/**
 * StepMeta
 * 
 * @author xuejian
 *
 */
@Step(id = "OssFilesInput", image = "oss-filesInput.png", i18nPackageName = "org.pentaho.di.trans.steps.oss.filesInput", name = "OssFilesInput.Name", description = "OssFilesInput.TooltipDesc", categoryDescription = "i18n:org.pentaho.di.trans.steps.oss.filesInput:OssFilesInput.Step.Category")
public class OssFilesInputMeta extends BaseStepMeta implements StepMetaInterface {

	private static final String NO = "N";
	private static final String YES = "Y";

	/*
	 * XML tags
	 */
	private final String TAG_ENDPOINT = "endpoint";
	private final String TAG_ACCESS_KEY = "access_key";
	private final String TAG_SECURE_KEY = "secure_key";
	private final String TAG_BUCKET = "bucket";
	private final String TAG_FILE_NAME = "file_name";
	private final String TAG_PREV_FLAG = "prev_flag";

	/*
	 * Form fields
	 */
	private String endpoint;
	private String accessKey;
	private String secureKey;
	private String bucket;
	private String fileName;
	private boolean prevFlag;

	/** The fields to import... */
	private TextFileInputField[] inputFields;

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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isPrevFlag() {
		return prevFlag;
	}

	public void setPrevFlag(boolean prevFlag) {
		this.prevFlag = prevFlag;
	}

	public TextFileInputField[] getInputFields() {
		return inputFields;
	}

	public void setInputFields(TextFileInputField[] inputFields) {
		this.inputFields = inputFields;
	}

	public OssFilesInputMeta() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setDefault() {
		endpoint = "";
		accessKey = "";
		secureKey = "";
		bucket = "";
		fileName = "";
		prevFlag = true;

	}

	/**
	 * 提供getStep方法是为执行org.pentaho.di.trans.Trans.prepareExecution可以获取step
	 */
	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new OssFilesInput(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new OssFilesInputData();
	}

	/**
	 * Q1:发现加载图标失败,参照其它插件写这个方法,试试效果:无效 <br/>
	 * Q2:换成已有图标,成功,证明是图片的问题 A:这个方法是读取kettle脚本(XML)的,并读取表单数据<br/>
	 * see
	 * org.pentaho.di.ui.trans.steps.oss.filesInput.OssFilesInputDialog.getInfo(OssFilesInputMeta,
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
			fileName = XMLHandler.getTagValue(stepnode, TAG_FILE_NAME);
			prevFlag = YES.equalsIgnoreCase(XMLHandler.getTagValue(stepnode, TAG_PREV_FLAG));

			Node fields = XMLHandler.getSubNode(stepnode, "fields");
			int nrfields = XMLHandler.countNodes(fields, "field");

			allocate(nrfields);
			for (int i = 0; i < nrfields; i++) {
				Node fnode = XMLHandler.getSubNodeByNr(fields, "field", i);
				TextFileInputField field = new TextFileInputField();
				field.setName(XMLHandler.getTagValue(fnode, "name"));
				field.setType(ValueMeta.getType(XMLHandler.getTagValue(fnode, "type")));
				field.setFormat(XMLHandler.getTagValue(fnode, "format"));
				field.setCurrencySymbol(XMLHandler.getTagValue(fnode, "currency"));
				field.setDecimalSymbol(XMLHandler.getTagValue(fnode, "decimal"));
				field.setGroupSymbol(XMLHandler.getTagValue(fnode, "group"));
				field.setNullString(XMLHandler.getTagValue(fnode, "nullif"));
				field.setIfNullValue(XMLHandler.getTagValue(fnode, "ifnull"));
				field.setPosition(Const.toInt(XMLHandler.getTagValue(fnode, "position"), -1));
				field.setLength(Const.toInt(XMLHandler.getTagValue(fnode, "length"), -1));
				field.setPrecision(Const.toInt(XMLHandler.getTagValue(fnode, "precision"), -1));
				field.setTrimType(ValueMeta.getTrimTypeByCode(XMLHandler.getTagValue(fnode, "trim_type")));
				field.setRepeated(YES.equalsIgnoreCase(XMLHandler.getTagValue(fnode, "repeat")));

				inputFields[i] = field;
			}
		} catch (Exception e) {
			throw new KettleXMLException("Unable to load step info from XML", e);
		}
	}

	private void allocate(int nrfields) {
		this.inputFields = new TextFileInputField[nrfields];
	}

	@Override
	public String getXML() throws KettleException {
		StringBuffer retval = new StringBuffer();
		retval.append("    ").append(XMLHandler.addTagValue(TAG_ENDPOINT, endpoint));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_ACCESS_KEY, accessKey));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_SECURE_KEY, secureKey));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_BUCKET, bucket));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_FILE_NAME, fileName));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_PREV_FLAG, prevFlag));
		retval.append("    <fields>").append(Const.CR);
		for (int i = 0; i < inputFields.length; i++) {
			TextFileInputField field = inputFields[i];

			retval.append("      <field>").append(Const.CR);
			retval.append("        ").append(XMLHandler.addTagValue("name", field.getName()));
			retval.append("        ").append(XMLHandler.addTagValue("type", field.getTypeDesc()));
			retval.append("        ").append(XMLHandler.addTagValue("format", field.getFormat()));
			retval.append("        ").append(XMLHandler.addTagValue("currency", field.getCurrencySymbol()));
			retval.append("        ").append(XMLHandler.addTagValue("decimal", field.getDecimalSymbol()));
			retval.append("        ").append(XMLHandler.addTagValue("group", field.getGroupSymbol()));
			retval.append("        ").append(XMLHandler.addTagValue("nullif", field.getNullString()));
			retval.append("        ").append(XMLHandler.addTagValue("ifnull", field.getIfNullValue()));
			retval.append("        ").append(XMLHandler.addTagValue("position", field.getPosition()));
			retval.append("        ").append(XMLHandler.addTagValue("length", field.getLength()));
			retval.append("        ").append(XMLHandler.addTagValue("precision", field.getPrecision()));
			retval.append("        ").append(XMLHandler.addTagValue("trim_type", field.getTrimTypeCode()));
			retval.append("        ").append(XMLHandler.addTagValue("repeat", field.isRepeated()));
			retval.append("      </field>").append(Const.CR);
		}
		retval.append("    </fields>").append(Const.CR);
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
			fileName = rep.getStepAttributeString(id_step, TAG_FILE_NAME);
			prevFlag = rep.getStepAttributeBoolean(id_step, TAG_PREV_FLAG);
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
			rep.saveStepAttribute(id_transformation, id_step, TAG_FILE_NAME, fileName);
			rep.saveStepAttribute(id_transformation, id_step, TAG_PREV_FLAG, prevFlag);
		} catch (Exception e) {
			throw new KettleException("Unable to save step information to the repository for id_step=" + id_step, e);
		}
	}

	/**
	 * Simplified version: copy from text file input,
	 */
	@Override
	public void getFields(RowMetaInterface row, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {
		if (info != null) {
			boolean found = false;
			for (int i = 0; i < info.length && !found; i++) {
				if (info[i] != null) {
					row.mergeRowMeta(info[i]);
					found = true;
				}
			}
		}
		for (int i = 0; i < inputFields.length; i++) {
			TextFileInputField field = inputFields[i];

			int type = field.getType();
			if (type == ValueMetaInterface.TYPE_NONE) {
				type = ValueMetaInterface.TYPE_STRING;
			}
			try {
				ValueMetaInterface v = ValueMetaFactory.createValueMeta(field.getName(), type);
				v.setLength(field.getLength());
				v.setPrecision(field.getPrecision());
				v.setOrigin(name);
				v.setConversionMask(field.getFormat());
				v.setDecimalSymbol(field.getDecimalSymbol());
				v.setGroupingSymbol(field.getGroupSymbol());
				v.setCurrencySymbol(field.getCurrencySymbol());
				v.setDateFormatLenient(true); // 日期格式宽容模式
				v.setDateFormatLocale(null); // 默认地点
				v.setTrimType(field.getTrimType());
				row.addValueMeta(v);
			} catch (Exception e) {
				throw new KettleStepException(e);
			}
		}
	}

}
