package org.pentaho.di.trans.steps.oss.downloader;

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
public class OssDownloaderData extends BaseStepData implements StepDataInterface {

	public RowMetaInterface previousRowMeta;

	public String endpoint;
	public String accessKey;
	public String secureKey;
	public String bucket;
	public String fileName; // 文件名
	public String lowerLimitMarker; // 文件名最小匹配标记
	public String downloadDir; // 下载目录

	// output begin
	public RowMeta outputRowMeta;
	// output end

	public RowMetaInterface convertRowMeta;

	public OssDownloaderData() {
		super();
	}
}
