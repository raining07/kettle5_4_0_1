package org.pentaho.di.trans.steps.oss.downloader;

import java.io.File;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.oss.OssConfig;
import org.pentaho.di.core.oss.OssWorker;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

/**
 * Step
 * 
 * @author xuejian
 *
 */
public class OssDownloader extends BaseStep implements StepInterface {

	private static Class<?> PKG = OssDownloaderMeta.class; // for i18n purposes

	private OssDownloaderMeta meta;

	private OssDownloaderData data;

	public OssDownloader(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		meta = (OssDownloaderMeta) smi;
		data = (OssDownloaderData) sdi;

		Object[] r = getRow(); // get row, blocks when needed!

		if (r == null) {
			// no more input to be expected...
			setOutputDone();
			return false;
		}

		if (first) {
			first = false;

			// get the RowMeta
			data.previousRowMeta = getInputRowMeta().clone();

			// Source file path
			if (!Const.isEmpty(meta.getSourceFilePath())) {
				// cache the position of the reply to field
				if (data.indexOfSourceFilePath < 0) {
					String realSourceFilePath = meta.getSourceFilePath();
					data.indexOfSourceFilePath = data.previousRowMeta.indexOfValue(realSourceFilePath);
					if (data.indexOfSourceFilePath < 0) {
						throw new KettleException(BaseMessages.getString(PKG,
								"OssDownloader.Exception.CouldnotFindSourceFilePathField", realSourceFilePath));
					}
				}
			}

			// Source file name
			if (!Const.isEmpty(meta.getSourceFileName())) {
				// cache the position of the reply to field
				if (data.indexOfSourceFileName < 0) {
					String realSourceFileName = meta.getSourceFileName();
					data.indexOfSourceFileName = data.previousRowMeta.indexOfValue(realSourceFileName);
					if (data.indexOfSourceFileName < 0) {
						throw new KettleException(BaseMessages.getString(PKG,
								"OssDownloader.Exception.CouldnotFindSourceFileNameField", realSourceFileName));
					}
				}
			}
		}

		try {
			// Source file path
			String sourceFilePath = null;
			if (data.indexOfSourceFilePath > -1) {
				sourceFilePath = data.previousRowMeta.getString(r, data.indexOfSourceFilePath);
			}
			// Source file name
			String sourceFileName = null;
			if (data.indexOfSourceFileName > -1) {
				sourceFileName = data.previousRowMeta.getString(r, data.indexOfSourceFileName);
			}
			doUpload(data.endpoint, data.accessKey, data.secureKey, data.bucket, meta.isCoverFile(),
					data.targetFileName, sourceFilePath, sourceFileName);
		} catch (Exception e) {
			logError("Because of an error, this step can't continue: ", e);
			setErrors(1);
			stopAll();
			setOutputDone(); // signal end to receiver(s)
			return false;
		}
		return true;
	}

	private void doUpload(String endpoint, String accessKey, String secureKey, String bucket, boolean coverFile,
			String targetFileName, String sourceFilePath, String sourceFileName) throws Exception {
		OssWorker ossWorker = null;
		try {
			OssConfig ossConfig = new OssConfig(endpoint, accessKey, secureKey, bucket);
			ossWorker = new OssWorker(ossConfig);
			ossWorker.doUpload(bucket, coverFile, sourceFilePath + File.separator + sourceFileName, targetFileName);
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
		meta = (OssDownloaderMeta) smi;
		data = (OssDownloaderData) sdi;

		if (super.init(smi, sdi)) {
			try {
				data.endpoint = environmentSubstitute(meta.getEndpoint());
				data.accessKey = environmentSubstitute(meta.getAccessKey());
				data.secureKey = environmentSubstitute(meta.getSecureKey());
				data.bucket = environmentSubstitute(meta.getBucket());
				data.targetFileName = environmentSubstitute(meta.getTargetFileName());
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
		meta = (OssDownloaderMeta) smi;
		data = (OssDownloaderData) sdi;
		super.dispose(smi, sdi);
	}

}
