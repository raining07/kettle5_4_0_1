package org.pentaho.di.ui.trans.steps.oss.downloader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.steps.oss.downloader.OssDownloaderMeta;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

/**
 * 
 * @author xuejian
 *
 */
public class OssDownloaderDialog extends BaseStepDialog implements StepDialogInterface {

	private static Class<?> PKG = OssDownloaderMeta.class; // for i18n purposes

	private OssDownloaderMeta input;

	/*
	 * Endpoint
	 */
	private Label wlEndpoint;
	private TextVar wEndpoint;
	private FormData fdlEndpoint, fdEndpoint;

	/*
	 * AccessKey
	 */
	private Label wlAccessKey;
	private TextVar wAccessKey;
	private FormData fdlAccessKey, fdAccessKey;

	/*
	 * SecureKey
	 */
	private Label wlSecureKey;
	private TextVar wSecureKey;
	private FormData fdlSecureKey, fdSecureKey;

	/*
	 * Bucket
	 */
	private Label wlBucket;
	private TextVar wBucket;
	private FormData fdlBucket, fdBucket;

	/*
	 * FilenameAsPrevious
	 */
	private Label wlFilenameAsPrevious;
	private Button wFilenameAsPrevious;
	private FormData fdlFilenameAsPrevious, fdFilenameAsPrevious;

	/*
	 * SourceFilePath
	 */
	private Label wlFileName;
	private FormData fdFileName;
	private TextVar wFileName;
	private FormData fdlFileName;

	/*
	 * LowerLimitMarker
	 */
	private Label wlLowerLimitMarker;
	private FormData fdLowerLimitMarker;
	private TextVar wLowerLimitMarker;
	private FormData fdlLowerLimitMarker;

	/*
	 * downloadDir
	 */
	private Label wlDownloadDir;
	private FormData fdDownloadDir;
	private TextVar wDownloadDir;
	private FormData fdlDownloadDir;

	/*
	 * DeleteOss
	 */
	private Label wlDeleteOss;
	private Button wDeleteOss;
	private FormData fdlDeleteOss, fdDeleteOss;

	private boolean getpreviousFields = false;

	public OssDownloaderDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		input = (OssDownloaderMeta) in;
	}

	public String open() {
		// Layout ...
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
		props.setLook(shell);
		setShellImage(shell, input);

		ModifyListener lsMod = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				input.setChanged();
			}
		};

		changed = input.hasChanged();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(msgProp("OssDownloader.Shell.Title"));

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

		// Stepname line ...
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(msgProp("System.Label.StepName"));
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);

		// Endpoint ...
		wlEndpoint = new Label(shell, SWT.RIGHT);
		wlEndpoint.setText(msgProp("OssDownloader.Endpoint.Label"));
		props.setLook(wlEndpoint);
		fdlEndpoint = new FormData();
		fdlEndpoint.left = new FormAttachment(0, 0);
		fdlEndpoint.right = new FormAttachment(middle, -margin);
		fdlEndpoint.top = new FormAttachment(wStepname, margin);
		wlEndpoint.setLayoutData(fdlEndpoint);
		wEndpoint = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wEndpoint);
		wEndpoint.addModifyListener(lsMod);
		fdEndpoint = new FormData();
		fdEndpoint.left = new FormAttachment(middle, 0);
		fdEndpoint.top = new FormAttachment(wStepname, margin);
		fdEndpoint.right = new FormAttachment(100, 0);
		wEndpoint.setLayoutData(fdEndpoint);

		// AccessKey ...
		wlAccessKey = new Label(shell, SWT.RIGHT);
		wlAccessKey.setText(msgProp("OssDownloader.AccessKey.Label"));
		props.setLook(wlAccessKey);
		fdlAccessKey = new FormData();
		fdlAccessKey.left = new FormAttachment(0, 0);
		fdlAccessKey.right = new FormAttachment(middle, -margin);
		fdlAccessKey.top = new FormAttachment(wEndpoint, margin);
		wlAccessKey.setLayoutData(fdlAccessKey);
		wAccessKey = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wAccessKey);
		wAccessKey.addModifyListener(lsMod);
		fdAccessKey = new FormData();
		fdAccessKey.left = new FormAttachment(middle, 0);
		fdAccessKey.top = new FormAttachment(wEndpoint, margin);
		fdAccessKey.right = new FormAttachment(100, 0);
		wAccessKey.setLayoutData(fdAccessKey);

		// SecureKey ...
		wlSecureKey = new Label(shell, SWT.RIGHT);
		wlSecureKey.setText(msgProp("OssDownloader.SecureKey.Label"));
		props.setLook(wlSecureKey);
		fdlSecureKey = new FormData();
		fdlSecureKey.left = new FormAttachment(0, 0);
		fdlSecureKey.right = new FormAttachment(middle, -margin);
		fdlSecureKey.top = new FormAttachment(wAccessKey, margin);
		wlSecureKey.setLayoutData(fdlSecureKey);
		wSecureKey = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wSecureKey);
		wSecureKey.addModifyListener(lsMod);
		fdSecureKey = new FormData();
		fdSecureKey.left = new FormAttachment(middle, 0);
		fdSecureKey.top = new FormAttachment(wAccessKey, margin);
		fdSecureKey.right = new FormAttachment(100, 0);
		wSecureKey.setLayoutData(fdSecureKey);

		// Bucket ...
		wlBucket = new Label(shell, SWT.RIGHT);
		wlBucket.setText(msgProp("OssDownloader.Bucket.Label"));
		props.setLook(wlBucket);
		fdlBucket = new FormData();
		fdlBucket.left = new FormAttachment(0, 0);
		fdlBucket.right = new FormAttachment(middle, -margin);
		fdlBucket.top = new FormAttachment(wSecureKey, margin);
		wlBucket.setLayoutData(fdlBucket);
		wBucket = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wBucket);
		wBucket.addModifyListener(lsMod);
		fdBucket = new FormData();
		fdBucket.left = new FormAttachment(middle, 0);
		fdBucket.top = new FormAttachment(wSecureKey, margin);
		fdBucket.right = new FormAttachment(100, 0);
		wBucket.setLayoutData(fdBucket);

		// FilenameAsPrevious
		wlFilenameAsPrevious = new Label(shell, SWT.RIGHT);
		wlFilenameAsPrevious.setText(msgProp("OssDownloader.FilenameAsPrevious.Label"));
		props.setLook(wlFilenameAsPrevious);
		fdlFilenameAsPrevious = new FormData();
		fdlFilenameAsPrevious.left = new FormAttachment(0, 0);
		fdlFilenameAsPrevious.top = new FormAttachment(wBucket, margin);
		fdlFilenameAsPrevious.right = new FormAttachment(middle, -margin);
		wlFilenameAsPrevious.setLayoutData(fdlFilenameAsPrevious);
		wFilenameAsPrevious = new Button(shell, SWT.CHECK);
		props.setLook(wFilenameAsPrevious);
		fdFilenameAsPrevious = new FormData();
		fdFilenameAsPrevious.left = new FormAttachment(middle, 0);
		fdFilenameAsPrevious.top = new FormAttachment(wBucket, margin);
		fdFilenameAsPrevious.right = new FormAttachment(100, 0);
		wFilenameAsPrevious.setLayoutData(fdFilenameAsPrevious);

		// FileName ...
		wlFileName = new Label(shell, SWT.RIGHT);
		wlFileName.setText(msgProp("OssDownloader.FileName.Label"));
		props.setLook(wlFileName);
		fdlFileName = new FormData();
		fdlFileName.left = new FormAttachment(0, 0);
		fdlFileName.right = new FormAttachment(middle, -margin);
		fdlFileName.top = new FormAttachment(wFilenameAsPrevious, margin);
		wlFileName.setLayoutData(fdlFileName);
		wFileName = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wFileName);
		wFileName.addModifyListener(lsMod);
		fdFileName = new FormData();
		fdFileName.left = new FormAttachment(middle, 0);
		fdFileName.top = new FormAttachment(wFilenameAsPrevious, margin);
		fdFileName.right = new FormAttachment(100, 0);
		wFileName.setLayoutData(fdFileName);

		// LowerLimitMarker ...
		wlLowerLimitMarker = new Label(shell, SWT.RIGHT);
		wlLowerLimitMarker.setText(msgProp("OssDownloader.LowerLimitMarker.Label"));
		props.setLook(wlLowerLimitMarker);
		fdlLowerLimitMarker = new FormData();
		fdlLowerLimitMarker.left = new FormAttachment(0, 0);
		fdlLowerLimitMarker.right = new FormAttachment(middle, -margin);
		fdlLowerLimitMarker.top = new FormAttachment(wFileName, margin);
		wlLowerLimitMarker.setLayoutData(fdlLowerLimitMarker);
		wLowerLimitMarker = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wLowerLimitMarker);
		wLowerLimitMarker.addModifyListener(lsMod);
		fdLowerLimitMarker = new FormData();
		fdLowerLimitMarker.left = new FormAttachment(middle, 0);
		fdLowerLimitMarker.top = new FormAttachment(wFileName, margin);
		fdLowerLimitMarker.right = new FormAttachment(100, 0);
		wLowerLimitMarker.setLayoutData(fdLowerLimitMarker);

		// DownloadDir ...
		wlDownloadDir = new Label(shell, SWT.RIGHT);
		wlDownloadDir.setText(msgProp("OssDownloader.DownloadDir.Label"));
		props.setLook(wlDownloadDir);
		fdlDownloadDir = new FormData();
		fdlDownloadDir.left = new FormAttachment(0, 0);
		fdlDownloadDir.right = new FormAttachment(middle, -margin);
		fdlDownloadDir.top = new FormAttachment(wLowerLimitMarker, margin);
		wlDownloadDir.setLayoutData(fdlDownloadDir);
		wDownloadDir = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wDownloadDir);
		wDownloadDir.addModifyListener(lsMod);
		fdDownloadDir = new FormData();
		fdDownloadDir.left = new FormAttachment(middle, 0);
		fdDownloadDir.top = new FormAttachment(wLowerLimitMarker, margin);
		fdDownloadDir.right = new FormAttachment(100, 0);
		wDownloadDir.setLayoutData(fdDownloadDir);

		// DeleteOss
		wlDeleteOss = new Label(shell, SWT.RIGHT);
		wlDeleteOss.setText(msgProp("OssDownloader.DeleteOss.Label"));
		props.setLook(wlDeleteOss);
		fdlDeleteOss = new FormData();
		fdlDeleteOss.left = new FormAttachment(0, 0);
		fdlDeleteOss.top = new FormAttachment(wDownloadDir, margin);
		fdlDeleteOss.right = new FormAttachment(middle, -margin);
		wlDeleteOss.setLayoutData(fdlDeleteOss);
		wDeleteOss = new Button(shell, SWT.CHECK);
		props.setLook(wDeleteOss);
		fdDeleteOss = new FormData();
		fdDeleteOss.left = new FormAttachment(middle, 0);
		fdDeleteOss.top = new FormAttachment(wDownloadDir, margin);
		fdDeleteOss.right = new FormAttachment(100, 0);
		wDeleteOss.setLayoutData(fdDeleteOss);

		// OK and cancel buttons ...
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

		BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, wDeleteOss);

		// Add listeners
		lsCancel = new Listener() {
			public void handleEvent(Event e) {
				cancel();
			}
		};
		lsOK = new Listener() {
			public void handleEvent(Event e) {
				ok();
			}
		};

		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);

		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};

		wStepname.addSelectionListener(lsDef);
		wEndpoint.addSelectionListener(lsDef);
		wAccessKey.addSelectionListener(lsDef);
		wSecureKey.addSelectionListener(lsDef);
		wBucket.addSelectionListener(lsDef);
		wFilenameAsPrevious.addSelectionListener(lsDef);
		wFileName.addSelectionListener(lsDef);
		wLowerLimitMarker.addSelectionListener(lsDef);
		wDownloadDir.addSelectionListener(lsDef);
		wDeleteOss.addSelectionListener(lsDef);

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

		// Set the shell size, based upon previous time...
		setSize();

		getData();
		input.setChanged(changed);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return stepname;
	}

	/**
	 * 获取前一个步骤的字段
	 */
	protected void getPreviousFields() {
//		if (getpreviousFields) {
//			return;
//		}
//		getpreviousFields = true;
//		try {
//			// File path ...
//			String sourceFilePath = null;
//			if (wSourceFilePath != null) {
//				sourceFilePath = wSourceFilePath.getText();
//			}
//			wSourceFilePath.removeAll();
//
//			RowMetaInterface r = transMeta.getPrevStepFields(stepname);
//			if (r != null) {
//				String[] fieldnames = r.getFieldNames();
//				wSourceFilePath.setItems(fieldnames);
//			}
//
//			if (sourceFilePath != null) {
//				wSourceFilePath.setText(sourceFilePath);
//			}
//		} catch (KettleException ke) {
//			new ErrorDialog(shell, BaseMessages.getString(PKG, "OssDownloaderDialog.FailedToGetFields.DialogTitle"),
//					BaseMessages.getString(PKG, "OssDownloaderDialog.FailedToGetFields.DialogMessage"), ke);
//		}
	}

	private String msgProp(String propKey) {
		return BaseMessages.getString(PKG, propKey);
	}

	// Read data and place it in the dialog
	public void getData() {
		wEndpoint.setText(Const.NVL(input.getEndpoint(), ""));
		wAccessKey.setText(Const.NVL(input.getAccessKey(), ""));
		wSecureKey.setText(Const.NVL(input.getSecureKey(), ""));
		wBucket.setText(Const.NVL(input.getBucket(), ""));
		wFilenameAsPrevious.setSelection(input.isFilenameAsPrevious());
		wFileName.setText(Const.NVL(input.getFileName(), ""));
		wLowerLimitMarker.setText(Const.NVL(input.getLowerLimitMarker(), ""));
		wDownloadDir.setText(Const.NVL(input.getDownloadDir(), ""));
		wDeleteOss.setSelection(input.isDeleteOss());

		wStepname.selectAll();
		wStepname.setFocus();
	}

	private void cancel() {
		stepname = null;
		input.setChanged(changed);
		dispose();
	}

	// let the plugin know about the entered data
	private void ok() {
		stepname = wStepname.getText(); // return value
		getInfo(input, false);
		dispose();
	}

	/**
	 * 获取表单信息
	 * 
	 * @param meta
	 * @param preview
	 */
	private void getInfo(OssDownloaderMeta meta, boolean preview) {
		meta.setEndpoint(wEndpoint.getText());
		meta.setAccessKey(wAccessKey.getText());
		meta.setSecureKey(wSecureKey.getText());
		meta.setBucket(wBucket.getText());
		meta.setFilenameAsPrevious(wFilenameAsPrevious.getSelection());
		meta.setFileName(wFileName.getText());
		meta.setLowerLimitMarker(wLowerLimitMarker.getText());
		meta.setDeleteOss(wDeleteOss.getSelection());
		meta.setDownloadDir(wDownloadDir.getText());
	}
}
