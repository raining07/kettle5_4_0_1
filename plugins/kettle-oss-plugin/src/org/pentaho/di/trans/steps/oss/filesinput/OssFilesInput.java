package org.pentaho.di.trans.steps.oss.filesinput;

import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.oss.OssConfig;
import org.pentaho.di.core.oss.OssWorker;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputField;

/**
 * OSS多文件输入<br/>
 * 1.oss文件位置<br/>
 * 1.1.oss连接信息<br/>
 * 1.2.Files:oss文件名前缀或者全名<br/>
 * <br/>
 * 2.读文件规则<br/>
 * 2.1.File type:oss文件类型(目前两种：1.CSV/2.Excel)<br/>
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


		OssConfig ossConfig = null;
		if (first) { // we just got started
			first = false;

			meta.getFields(data.outputRowMeta, getStepname(), null, null, this, repository, metaStore);

			
			// 输出数据
			data.outputRowMeta = new RowMeta();

			// get oss config
			ossConfig = new OssConfig(data.endpoint, data.accessKey, data.secureKey, data.bucket);

			List<String> fileNames = getFileNames(ossConfig, data.fileName, meta.isPrevFlag());
			logBasic("读到 [" + fileNames.size() + "] 个文件");

		}

		return false;
	}

	private List<String> getFileNames(OssConfig ossConfig, String fileName, boolean prevFlag) {
		OssWorker ossWorker = null;
		try {
			ossWorker = new OssWorker(ossConfig);
			return ossWorker.getOssFiles(fileName, prevFlag);
		} catch (Exception e) {
			throw e;
		} finally {
			if (ossWorker != null) {
				ossWorker.close();
			}
		}
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

}
