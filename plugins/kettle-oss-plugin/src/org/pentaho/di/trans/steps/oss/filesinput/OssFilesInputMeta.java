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
import org.pentaho.di.trans.steps.textfileinput.TextFileInputMeta;
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

	// private static final String NO = "N";
	private static final String YES = "Y";

	public static final int FILE_FORMAT_DOS = 0;
	public static final int FILE_FORMAT_UNIX = 1;
	public static final int FILE_FORMAT_MIXED = 2;

	public static final int FILE_TYPE_CSV = 0;
	public static final int FILE_TYPE_FIXED = 1;

	/*
	 * XML tags
	 */
	private final String TAG_ENDPOINT = "endpoint";
	private final String TAG_ACCESS_KEY = "access_key";
	private final String TAG_SECURE_KEY = "secure_key";
	private final String TAG_BUCKET = "bucket";
	private final String TAG_FILE_NAME = "file_name";
	private final String TAG_PREV_FLAG = "prev_flag";
	private final String TAG_FILE_TYPE = "file_type";
	private final String TAG_SEPARATOR = "separator";
	private final String TAG_ENCLOSURE = "enclosure";
	private final String TAG_FORMAT = "format";
	private final String TAG_CHARSET = "charset";
	private final String TAG_HEAD_FLAG = "head_flag";

	/*
	 * OSS config
	 */
	private String endpoint;
	private String accessKey;
	private String secureKey;
	private String bucket;
	private boolean prevFlag;
	private String fileName;

	/*
	 * Content
	 */
	private String fileType;
	private String separator;
	private String enclosure;
	private String format;
	private String charset; // encoding
	private boolean headFlag;

	/*
	 * Fields
	 */
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

	public boolean isPrevFlag() {
		return prevFlag;
	}

	public void setPrevFlag(boolean prevFlag) {
		this.prevFlag = prevFlag;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getEnclosure() {
		return enclosure;
	}

	public void setEnclosure(String enclosure) {
		this.enclosure = enclosure;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public boolean isHeadFlag() {
		return headFlag;
	}

	public void setHeadFlag(boolean headFlag) {
		this.headFlag = headFlag;
	}

	public TextFileInputField[] getInputFields() {
		return inputFields;
	}

	public void setInputFields(TextFileInputField[] inputFields) {
		this.inputFields = inputFields;
	}

	public int getFileFormatTypeNr() {
		// calculate the file format type in advance so we can use a switch
		if (getFormat().equalsIgnoreCase("DOS")) {
			return FILE_FORMAT_DOS;
		} else if (getFormat().equalsIgnoreCase("unix")) {
			return TextFileInputMeta.FILE_FORMAT_UNIX;
		} else {
			return TextFileInputMeta.FILE_FORMAT_MIXED;
		}
	}

	public OssFilesInputMeta() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setDefault() {
		// oss config
		endpoint = "";
		accessKey = "";
		secureKey = "";
		bucket = "";
		fileName = "";
		prevFlag = true;

		// content
		fileType = "CSV";
		separator = ";";
		enclosure = "\"";
		format = "DOS";
		charset = "";
		headFlag = true;

		// fields
		int nrfields = 0;
		allocate(nrfields);

		for (int i = 0; i < nrfields; i++) {
			inputFields[i] = new TextFileInputField("field" + (i + 1), 1, -1);
		}
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
			// oss config
			endpoint = XMLHandler.getTagValue(stepnode, TAG_ENDPOINT);
			accessKey = XMLHandler.getTagValue(stepnode, TAG_ACCESS_KEY);
			secureKey = XMLHandler.getTagValue(stepnode, TAG_SECURE_KEY);
			bucket = XMLHandler.getTagValue(stepnode, TAG_BUCKET);
			fileName = XMLHandler.getTagValue(stepnode, TAG_FILE_NAME);
			prevFlag = YES.equalsIgnoreCase(XMLHandler.getTagValue(stepnode, TAG_PREV_FLAG));

			// content
			fileType = XMLHandler.getTagValue(stepnode, TAG_FILE_TYPE);
			separator = XMLHandler.getTagValue(stepnode, TAG_SEPARATOR);
			enclosure = XMLHandler.getTagValue(stepnode, TAG_ENCLOSURE);
			format = XMLHandler.getTagValue(stepnode, TAG_FORMAT);
			charset = XMLHandler.getTagValue(stepnode, TAG_CHARSET);
			headFlag = YES.equalsIgnoreCase(XMLHandler.getTagValue(stepnode, TAG_HEAD_FLAG));

			// fields
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

	public void allocate(int nrfields) {
		this.inputFields = new TextFileInputField[nrfields];
	}

	@Override
	public String getXML() throws KettleException {
		StringBuffer retval = new StringBuffer();
		// oss config
		retval.append("    ").append(XMLHandler.addTagValue(TAG_ENDPOINT, endpoint));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_ACCESS_KEY, accessKey));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_SECURE_KEY, secureKey));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_BUCKET, bucket));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_FILE_NAME, fileName));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_PREV_FLAG, prevFlag));

		// content
		retval.append("    ").append(XMLHandler.addTagValue(TAG_FILE_TYPE, fileType));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_SEPARATOR, separator));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_ENCLOSURE, enclosure));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_FORMAT, format));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_CHARSET, charset));
		retval.append("    ").append(XMLHandler.addTagValue(TAG_HEAD_FLAG, headFlag));

		// fields
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
			// oss config
			endpoint = rep.getStepAttributeString(id_step, TAG_ENDPOINT);
			accessKey = rep.getStepAttributeString(id_step, TAG_ACCESS_KEY);
			secureKey = rep.getStepAttributeString(id_step, TAG_SECURE_KEY);
			bucket = rep.getStepAttributeString(id_step, TAG_BUCKET);
			fileName = rep.getStepAttributeString(id_step, TAG_FILE_NAME);
			prevFlag = rep.getStepAttributeBoolean(id_step, TAG_PREV_FLAG);

			// content
			fileType = rep.getStepAttributeString(id_step, TAG_FILE_TYPE);
			separator = rep.getStepAttributeString(id_step, TAG_SEPARATOR);
			enclosure = rep.getStepAttributeString(id_step, TAG_ENCLOSURE);
			format = rep.getStepAttributeString(id_step, TAG_FORMAT);
			charset = rep.getStepAttributeString(id_step, TAG_CHARSET);
			headFlag = rep.getStepAttributeBoolean(id_step, TAG_HEAD_FLAG);

			// fields
			int nrfields = rep.countNrStepAttributes(id_step, "field_name");
			for (int i = 0; i < nrfields; i++) {
				TextFileInputField field = new TextFileInputField();

				field.setName(rep.getStepAttributeString(id_step, i, "field_name"));
				field.setType(ValueMeta.getType(rep.getStepAttributeString(id_step, i, "field_type")));
				field.setFormat(rep.getStepAttributeString(id_step, i, "field_format"));
				field.setCurrencySymbol(rep.getStepAttributeString(id_step, i, "field_currency"));
				field.setDecimalSymbol(rep.getStepAttributeString(id_step, i, "field_decimal"));
				field.setGroupSymbol(rep.getStepAttributeString(id_step, i, "field_group"));
				field.setNullString(rep.getStepAttributeString(id_step, i, "field_nullif"));
				field.setIfNullValue(rep.getStepAttributeString(id_step, i, "field_ifnull"));
				field.setPosition((int) rep.getStepAttributeInteger(id_step, i, "field_position"));
				field.setLength((int) rep.getStepAttributeInteger(id_step, i, "field_length"));
				field.setPrecision((int) rep.getStepAttributeInteger(id_step, i, "field_precision"));
				field.setTrimType(
						ValueMeta.getTrimTypeByCode(rep.getStepAttributeString(id_step, i, "field_trim_type")));
				field.setRepeated(rep.getStepAttributeBoolean(id_step, i, "field_repeat"));

				inputFields[i] = field;
			}

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
			// oss config
			rep.saveStepAttribute(id_transformation, id_step, TAG_ENDPOINT, endpoint);
			rep.saveStepAttribute(id_transformation, id_step, TAG_ACCESS_KEY, accessKey);
			rep.saveStepAttribute(id_transformation, id_step, TAG_SECURE_KEY, secureKey);
			rep.saveStepAttribute(id_transformation, id_step, TAG_BUCKET, bucket);
			rep.saveStepAttribute(id_transformation, id_step, TAG_FILE_NAME, fileName);
			rep.saveStepAttribute(id_transformation, id_step, TAG_PREV_FLAG, prevFlag);

			// content
			rep.saveStepAttribute(id_transformation, id_step, TAG_FILE_TYPE, fileType);
			rep.saveStepAttribute(id_transformation, id_step, TAG_SEPARATOR, separator);
			rep.saveStepAttribute(id_transformation, id_step, TAG_ENCLOSURE, enclosure);
			rep.saveStepAttribute(id_transformation, id_step, TAG_FORMAT, format);
			rep.saveStepAttribute(id_transformation, id_step, TAG_CHARSET, charset);
			rep.saveStepAttribute(id_transformation, id_step, TAG_HEAD_FLAG, headFlag);

			// fields
			for (int i = 0; i < inputFields.length; i++) {
				TextFileInputField field = inputFields[i];

				rep.saveStepAttribute(id_transformation, id_step, i, "field_name", field.getName());
				rep.saveStepAttribute(id_transformation, id_step, i, "field_type", field.getTypeDesc());
				rep.saveStepAttribute(id_transformation, id_step, i, "field_format", field.getFormat());
				rep.saveStepAttribute(id_transformation, id_step, i, "field_currency", field.getCurrencySymbol());
				rep.saveStepAttribute(id_transformation, id_step, i, "field_decimal", field.getDecimalSymbol());
				rep.saveStepAttribute(id_transformation, id_step, i, "field_group", field.getGroupSymbol());
				rep.saveStepAttribute(id_transformation, id_step, i, "field_nullif", field.getNullString());
				rep.saveStepAttribute(id_transformation, id_step, i, "field_ifnull", field.getIfNullValue());
				rep.saveStepAttribute(id_transformation, id_step, i, "field_position", field.getPosition());
				rep.saveStepAttribute(id_transformation, id_step, i, "field_length", field.getLength());
				rep.saveStepAttribute(id_transformation, id_step, i, "field_precision", field.getPrecision());
				rep.saveStepAttribute(id_transformation, id_step, i, "field_trim_type", field.getTrimTypeCode());
				rep.saveStepAttribute(id_transformation, id_step, i, "field_repeat", field.isRepeated());
			}
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

	public boolean hasHeader() {
		return isHeadFlag();
	}

	public boolean nameIsPrevious() {
		return isPrevFlag();
	}

}
