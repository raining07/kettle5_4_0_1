package org.pentaho.di.trans.steps.oss.filesinput;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.oss.OSSFileObject;
import org.pentaho.di.core.oss.OssConfig;
import org.pentaho.di.core.oss.OssWorker;
import org.pentaho.di.core.oss.OssWorkerUtils;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputField;

import com.google.common.collect.Lists;

/**
 * OSS多文件输入<br/>
 * 1.oss文件位置<br/>
 * 1.1.oss连接信息<br/>
 * 1.2.Files:oss文件名前缀或者全名<br/>
 * <br/>
 * 2.读文件规则<br/>
 * 2.1.FileType:oss文件类型(目前两种：1.CSV/2.Excel)<br/>
 * 2.2.Separator:指定分割符<br/>
 * 2.3.Enclosure:文本限定符<br/>
 * 2.4.Format:格式(DOS/UNIX)<br/>
 * 2.5.Charset:文件编码格式<br/>
 * 2.6.第一行是否为表头<br/>
 * <br/>
 * 3.指定输出<br/>
 * 
 * @author xuejian
 * 
 */
public class OssFilesInput extends BaseStep implements StepInterface {

	private final String FIELD_FILE_NAME = "file_name";

	private static Class<?> PKG = OssFilesInputMeta.class; // for i18n purposes

	private OssFilesInputMeta meta;

	private OssFilesInputData data;

	public OssFilesInput(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		meta = (OssFilesInputMeta) smi;
		data = (OssFilesInputData) sdi;

		if (first) { // we just got started
			first = false;

			// 追加一列-文件名 begin
			List<TextFileInputField> fields = Lists.newArrayList(meta.getInputFields());
			fields.add(genFileNameField());
			meta.setInputFields(fields.toArray(new TextFileInputField[fields.size()]));
			// 追加一列-文件名 end

			data.outputRowMeta = new RowMeta();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this, repository, metaStore);
			data.convertRowMeta = data.outputRowMeta.cloneToType(ValueMetaInterface.TYPE_STRING);

			// get oss config
			OssConfig ossConfig = new OssConfig(data.endpoint, data.accessKey, data.secureKey, data.bucket);
			try {
				data.bookMark = OssWorkerUtils.createBookMark(ossConfig, data.fileName, data.lowerLimitMarker,
						meta.isPrevFlag(), 1000);
			} catch (Exception e) {
				throw new KettleException(e.getMessage(), e);
			}
			data.ossWorker = new OssWorker(ossConfig);
			// 匹配不到文件
			if (data.bookMark.hasNoBooks()) {
				log.logBasic("没有读到任何文件");
				setOutputDone();
				return false;
			} else {
				log.logBasic("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓读到文件列表↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
				for (String book : data.bookMark.getBookNames()) {
					log.logBasic(book);
				}
				log.logBasic("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑读到文件列表↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
			}
		}

		try {
			String line = readLine();

			if (data.bookMark == null || data.bookMark.allReaded()) {
				if (data.bookMark.allReaded()) {
					logBasic("读到 [" + data.bookMark.getBookNames().size() + "] 个文件:");
					for (Map.Entry<String, Integer> entry : data.bookMark.getReadLinesMap().entrySet()) {
						logBasic("文件[" + entry.getKey() + "] : " + entry.getValue() + "行");
					}
				}
				try {
					data.ossWorker.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					data.bookMark.close();
				} catch (IOException e) {
					// do nothing
				}
				setOutputDone();
				return false;
			}

			// String[] strings = StringUtils.split(line, data.separator);
			String[] strings = line.split("\\" + data.separator);
//			String line_ = "";
//			for(String str: strings) {
//				line_ += str + data.separator;
//			}
//			log.logBasic(meta.getCharset());
//			log.logBasic(line);
//			log.logBasic(line_);
			// 追加一列-文件名 begin
			List<String> list = Lists.newArrayList(strings);
			list.add(data.bookMark.getCurrentBook());
			strings = list.toArray(new String[list.size()]);
			// 追加一列-文件名 end

			Object[] r = getRows(data.outputRowMeta, data.convertRowMeta, meta.getInputFields(), strings);
			putRow(data.outputRowMeta, r);
		} catch (Throwable thr) {
			throw new KettleException(thr.getMessage(), thr);
		}

		return true;
	}

	private TextFileInputField genFileNameField() {
		TextFileInputField field = new TextFileInputField();
		field.setName(FIELD_FILE_NAME);
		field.setType(ValueMeta.getType("String"));
		return field;
	}

	/**
	 * 多文件读行:1.如果空文件,递归读下一个文件
	 * 
	 * @return
	 * @throws Exception
	 */
	private String readLine() throws Exception {
		if (data.bookMark.allReaded()) {
			return null;
		}

		// 询问打开下一本书
		boolean needNextBook = data.bookMark.isNeedNextBook();
		boolean firstLineReaded = needNextBook;
		String line = null;

		// 只读第一行
		if (needNextBook && !data.bookMark.allReaded() && firstLineReaded) {
			line = openNotEmptyBookAndReadFirstLine();
			if (line == null) {
				// 读到最后都是空文件
				return line;
			}
		}

		// 非第一行
		// 1.读到第一行,且为头部,则继续读下一行
		// 2.读到第一行,不为头部,则不读
		// 3.读到第一行之后的行,则读一行

		if ((firstLineReaded && meta.hasHeader()) || !firstLineReaded) {
			line = data.bookMark.readLine();
		}

		// 读到最后一行,丢弃空行,递归读下一个文件
		if (line == null && data.bookMark.isNeedNextBook()) {
			return readLine();
		}
		return line;
	}

	private String openNotEmptyBookAndReadFirstLine() throws Exception {
		// 打开书
		openBook();
		// 读第一行
		String line = data.bookMark.readLine();
		// 读到null,书签的游标自动指向下一本书,这时,都下一本书,读到非空文件或者读完所有文件还是为空,结束
		if (line == null && !data.bookMark.allReaded()) {
			logBasic("读到空文件: " + data.bookMark.getCurrentBook());
			return openNotEmptyBookAndReadFirstLine();
		}
		return line;
	}

	private Object[] getRows(RowMeta outputRowMeta, RowMetaInterface convertRowMeta,
			TextFileInputField[] textFileInputFields, String[] strings) throws Exception {
		Object[] r = RowDataUtil.allocateRowData(outputRowMeta.size());

		int nrfields = textFileInputFields.length;
		int fieldnr;

		for (fieldnr = 0; fieldnr < nrfields; fieldnr++) {
			TextFileInputField f = textFileInputFields[fieldnr];
			int valuenr = fieldnr;
			ValueMetaInterface valueMeta = outputRowMeta.getValueMeta(valuenr);
			ValueMetaInterface convertMeta = convertRowMeta.getValueMeta(valuenr);

			Object value = null;

			String nullif = fieldnr < nrfields ? f.getNullString() : "";
			String ifnull = fieldnr < nrfields ? f.getIfNullValue() : "";
			int trim_type = fieldnr < nrfields ? f.getTrimType() : ValueMetaInterface.TRIM_TYPE_NONE;
			if (fieldnr < strings.length) {
				String pol = strings[fieldnr];
				try {
					value = valueMeta.convertDataFromString(pol, convertMeta, nullif, ifnull, trim_type);
				} catch (Exception e) {
					throw e;
				}
			}
			if (r != null) {
				r[valuenr] = value;
			}
		}
		return r;
	}

	private void openBook() throws UnsupportedEncodingException {
		OSSFileObject ossFileObject = data.ossWorker.getOSSFileObject(data.bookMark.getCurrentBook());
		data.bookMark.openBook(ossFileObject.getContent(), this.meta.getCharset(), meta.getFileFormatTypeNr());
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (OssFilesInputMeta) smi;
		data = (OssFilesInputData) sdi;

		if (super.init(smi, sdi)) {
			try {
				data.endpoint = environmentSubstitute(meta.getEndpoint());
				data.accessKey = environmentSubstitute(meta.getAccessKey());
				data.secureKey = environmentSubstitute(meta.getSecureKey());
				data.bucket = environmentSubstitute(meta.getBucket());
				data.fileName = environmentSubstitute(meta.getFileName());
				data.separator = environmentSubstitute(meta.getSeparator());
				data.lowerLimitMarker = environmentSubstitute(meta.getLowerLimitMarker());
				return true;
			} catch (Exception e) {
				logError("An error occurred intialising this step: " + e.getMessage());
				stopAll();
				setErrors(1);
			}
		}
		return false;
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (OssFilesInputMeta) smi;
		data = (OssFilesInputData) sdi;
		super.dispose(smi, sdi);
	}

	public static final String[] guessStringsFromLine(VariableSpace space, LogChannelInterface log, String line,
			OssFilesInputMeta inf, String delimiter, String enclosure, String escapeCharacter) throws KettleException {
		List<String> strings = new ArrayList<String>();

		String pol; // piece of line

		try {
			if (line == null) {
				return null;
			}

			if (inf.getFileType().equalsIgnoreCase("CSV")) {

				// Split string in pieces, only for CSV!

				int pos = 0;
				int length = line.length();
				boolean dencl = false;

				int len_encl = (enclosure == null ? 0 : enclosure.length());
				int len_esc = (escapeCharacter == null ? 0 : escapeCharacter.length());

				while (pos < length) {
					int from = pos;
					int next;

					boolean encl_found;
					boolean contains_escaped_enclosures = false;
					boolean contains_escaped_separators = false;

					// Is the field beginning with an enclosure?
					// "aa;aa";123;"aaa-aaa";000;...
					if (len_encl > 0 && line.substring(from, from + len_encl).equalsIgnoreCase(enclosure)) {
						if (log.isRowLevel()) {
							log.logRowlevel(BaseMessages.getString(PKG, "OssFilesInput.Log.ConvertLineToRowTitle"),
									BaseMessages.getString(PKG, "OssFilesInput.Log.ConvertLineToRow",
											line.substring(from, from + len_encl)));
						}
						encl_found = true;
						int p = from + len_encl;

						boolean is_enclosure = len_encl > 0 && p + len_encl < length
								&& line.substring(p, p + len_encl).equalsIgnoreCase(enclosure);
						boolean is_escape = len_esc > 0 && p + len_esc < length
								&& line.substring(p, p + len_esc).equalsIgnoreCase(escapeCharacter);

						boolean enclosure_after = false;

						// Is it really an enclosure? See if it's not repeated twice or escaped!
						if ((is_enclosure || is_escape) && p < length - 1) {
							String strnext = line.substring(p + len_encl, p + 2 * len_encl);
							if (strnext.equalsIgnoreCase(enclosure)) {
								p++;
								enclosure_after = true;
								dencl = true;

								// Remember to replace them later on!
								if (is_escape) {
									contains_escaped_enclosures = true;
								}
							}
						}

						// Look for a closing enclosure!
						while ((!is_enclosure || enclosure_after) && p < line.length()) {
							p++;
							enclosure_after = false;
							is_enclosure = len_encl > 0 && p + len_encl < length
									&& line.substring(p, p + len_encl).equals(enclosure);
							is_escape = len_esc > 0 && p + len_esc < length
									&& line.substring(p, p + len_esc).equals(escapeCharacter);

							// Is it really an enclosure? See if it's not repeated twice or escaped!
							if ((is_enclosure || is_escape) && p < length - 1) {

								String strnext = line.substring(p + len_encl, p + 2 * len_encl);
								if (strnext.equals(enclosure)) {
									p++;
									enclosure_after = true;
									dencl = true;

									// Remember to replace them later on!
									if (is_escape) {
										contains_escaped_enclosures = true; // remember
									}
								}
							}
						}

						if (p >= length) {
							next = p;
						} else {
							next = p + len_encl;
						}

						if (log.isRowLevel()) {
							log.logRowlevel(BaseMessages.getString(PKG, "OssFilesInput.Log.ConvertLineToRowTitle"),
									BaseMessages.getString(PKG, "OssFilesInput.Log.EndOfEnclosure", "" + p));
						}
					} else {
						encl_found = false;
						boolean found = false;
						int startpoint = from;
						// int tries = 1;
						do {
							next = line.indexOf(delimiter, startpoint);

							// See if this position is preceded by an escape character.
							if (len_esc > 0 && next - len_esc > 0) {
								String before = line.substring(next - len_esc, next);

								if (escapeCharacter.equals(before)) {
									// take the next separator, this one is escaped...
									startpoint = next + 1;
									// tries++;
									contains_escaped_separators = true;
								} else {
									found = true;
								}
							} else {
								found = true;
							}
						} while (!found && next >= 0);
					}
					if (next == -1) {
						next = length;
					}

					if (encl_found) {
						pol = line.substring(from + len_encl, next - len_encl);
						if (log.isRowLevel()) {
							log.logRowlevel(BaseMessages.getString(PKG, "OssFilesInput.Log.ConvertLineToRowTitle"),
									BaseMessages.getString(PKG, "OssFilesInput.Log.EnclosureFieldFound", "" + pol));
						}
					} else {
						pol = line.substring(from, next);
						if (log.isRowLevel()) {
							log.logRowlevel(BaseMessages.getString(PKG, "OssFilesInput.Log.ConvertLineToRowTitle"),
									BaseMessages.getString(PKG, "OssFilesInput.Log.NormalFieldFound", "" + pol));
						}
					}

					if (dencl) {
						StringBuilder sbpol = new StringBuilder(pol);
						int idx = sbpol.indexOf(enclosure + enclosure);
						while (idx >= 0) {
							sbpol.delete(idx, idx + enclosure.length());
							idx = sbpol.indexOf(enclosure + enclosure);
						}
						pol = sbpol.toString();
					}

					// replace the escaped enclosures with enclosures...
					if (contains_escaped_enclosures) {
						String replace = escapeCharacter + enclosure;
						String replaceWith = enclosure;

						pol = Const.replace(pol, replace, replaceWith);
					}

					// replace the escaped separators with separators...
					if (contains_escaped_separators) {
						String replace = escapeCharacter + delimiter;
						String replaceWith = delimiter;

						pol = Const.replace(pol, replace, replaceWith);
					}

					// Now add pol to the strings found!
					strings.add(pol);

					pos = next + delimiter.length();
				}
				if (pos == length) {
					if (log.isRowLevel()) {
						log.logRowlevel(BaseMessages.getString(PKG, "OssFilesInput.Log.ConvertLineToRowTitle"),
								BaseMessages.getString(PKG, "OssFilesInput.Log.EndOfEmptyLineFound"));
					}
					strings.add("");
				}
			} else {
				// Fixed file format: Simply get the strings at the required positions...
				for (int i = 0; i < inf.getInputFields().length; i++) {
					TextFileInputField field = inf.getInputFields()[i];

					int length = line.length();

					if (field.getPosition() + field.getLength() <= length) {
						strings.add(line.substring(field.getPosition(), field.getPosition() + field.getLength()));
					} else {
						if (field.getPosition() < length) {
							strings.add(line.substring(field.getPosition()));
						} else {
							strings.add("");
						}
					}
				}
			}
		} catch (Exception e) {
			throw new KettleException(
					BaseMessages.getString(PKG, "OssFilesInput.Log.Error.ErrorConvertingLine", e.toString()), e);
		}

		return strings.toArray(new String[strings.size()]);
	}

}
