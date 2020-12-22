package org.pentaho.di.trans.steps.oss.downloader;

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

			data.outputRowMeta = new RowMeta();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this, repository, metaStore);
			data.convertRowMeta = data.outputRowMeta.cloneToType(ValueMetaInterface.TYPE_STRING);
		}

		try {
			// Source file path
			download(data.endpoint, data.accessKey, data.secureKey, data.bucket, meta.isFilenameAsPrevious(),
					data.fileName, data.lowerLimitMarker, meta.isDeleteOss(), data.downloadDir);

			// 数据需要传递到下一个组件
			putRow(data.outputRowMeta, r);
		} catch (Exception e) {
			logError("Because of an error, this step can't continue: ", e);
			setErrors(1);
			stopAll();
			setOutputDone(); // signal end to receiver(s)
			return false;
		}
		return true;
	}

	private void download(String endpoint, String accessKey, String secureKey, String bucket,
			boolean filenameAsPrevious, String fileName, String lowerLimitMarker, boolean deleteOss,
			String downloadDir) throws Exception {
		OssWorker ossWorker = null;
		try {
			OssConfig ossConfig = new OssConfig(endpoint, accessKey, secureKey, bucket);
			ossWorker = new OssWorker(ossConfig);
			ossWorker.doDownload(filenameAsPrevious, fileName, lowerLimitMarker, 1000, downloadDir, deleteOss);
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
				data.fileName = environmentSubstitute(meta.getFileName());
				data.lowerLimitMarker = environmentSubstitute(meta.getLowerLimitMarker());
				data.downloadDir = environmentSubstitute(meta.getDownloadDir());
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
