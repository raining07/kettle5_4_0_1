package org.pentaho.di.trans.steps.oss.uploader;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * StepData
 * 
 * @author xuejian
 *
 */
public class OssUploaderData extends BaseStepData implements StepDataInterface {

	public RowMetaInterface previousRowMeta;

	public String endpoint;
	public String accessKey;
	public String secureKey;
	public String bucket;
	public String targetFileName;

	public int indexOfSourceFilePath;
	public int indexOfSourceFileName;

	public OssUploaderData() {
		super();
		indexOfSourceFilePath = -1;
		indexOfSourceFileName = -1;
	}

}
