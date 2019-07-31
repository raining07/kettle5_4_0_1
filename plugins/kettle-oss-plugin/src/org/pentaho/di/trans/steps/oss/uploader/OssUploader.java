package org.pentaho.di.trans.steps.oss.uploader;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class OssUploader extends BaseStep implements StepInterface {

	private OssUploaderMeta meta;

	private OssUploaderData data;

	public OssUploader(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		// TODO Auto-generated method stub
		return super.processRow(smi, sdi);
	}
	
	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		// TODO Auto-generated method stub
		return super.init(smi, sdi);
	}
	
	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		// TODO Auto-generated method stub
		super.dispose(smi, sdi);
	}
}
