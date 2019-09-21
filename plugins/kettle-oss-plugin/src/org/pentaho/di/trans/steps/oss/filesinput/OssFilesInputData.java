package org.pentaho.di.trans.steps.oss.filesinput;

import org.pentaho.di.core.oss.BookMark;
import org.pentaho.di.core.oss.OssWorker;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * StepData
 * 
 * @author xuejian
 *
 */
public class OssFilesInputData extends BaseStepData implements StepDataInterface {

	public RowMetaInterface previousRowMeta;

	// params begin
	public String endpoint;
	public String accessKey;
	public String secureKey;
	public String bucket;
	public String fileName;
	public String separator;
	public String lowerLimitMarker;

	// params end

	// control begin
	public BookMark bookMark;
	public OssWorker ossWorker;
	// control end

	// output begin
	public RowMeta outputRowMeta;
	// output end

	public RowMetaInterface convertRowMeta;

	public OssFilesInputData() {
		super();
	}

}
