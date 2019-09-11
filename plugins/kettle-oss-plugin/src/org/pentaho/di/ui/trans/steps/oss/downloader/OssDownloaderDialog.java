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
	 * Cover file
	 */
	private Label wlCoverFile;
	private Button wCoverFile;
	private FormData fdlCoverFile, fdCoverFile;

	/*
	 * TargetFileName
	 */
	private Label wlTargetFileName;
	private TextVar wTargetFileName;
	private FormData fdlTargetFileName, fdTargetFileName;

	/*
	 * SourceFilePath
	 */
	private Label wlSourceFilePath;
	private FormData fdSourceFilePath;
	private CCombo wSourceFilePath;
	private FormData fdlSourceFilePath;

	/*
	 * SourceFileName
	 */
	private Label wlSourceFileName;
	private FormData fdSourceFileName;
	private CCombo wSourceFileName;
	private FormData fdlSourceFileName;

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

		// Cover file
		wlCoverFile = new Label(shell, SWT.RIGHT);
		wlCoverFile.setText(msgProp("OssDownloader.CoverFile.Label"));
		props.setLook(wlCoverFile);
		fdlCoverFile = new FormData();
		fdlCoverFile.left = new FormAttachment(0, 0);
		fdlCoverFile.top = new FormAttachment(wBucket, margin);
		fdlCoverFile.right = new FormAttachment(middle, -margin);
		wlCoverFile.setLayoutData(fdlCoverFile);
		wCoverFile = new Button(shell, SWT.CHECK);
		props.setLook(wCoverFile);
		fdCoverFile = new FormData();
		fdCoverFile.left = new FormAttachment(middle, 0);
		fdCoverFile.top = new FormAttachment(wBucket, margin);
		fdCoverFile.right = new FormAttachment(100, 0);
		wCoverFile.setLayoutData(fdCoverFile);

		// Target file name ...
		wlTargetFileName = new Label(shell, SWT.RIGHT);
		wlTargetFileName.setText(msgProp("OssDownloader.TargetFileName.Label"));
		props.setLook(wlTargetFileName);
		fdlTargetFileName = new FormData();
		fdlTargetFileName.left = new FormAttachment(0, 0);
		fdlTargetFileName.right = new FormAttachment(middle, -margin);
		fdlTargetFileName.top = new FormAttachment(wCoverFile, margin);
		wlTargetFileName.setLayoutData(fdlTargetFileName);
		wTargetFileName = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wTargetFileName);
		wTargetFileName.addModifyListener(lsMod);
		fdTargetFileName = new FormData();
		fdTargetFileName.left = new FormAttachment(middle, 0);
		fdTargetFileName.top = new FormAttachment(wCoverFile, margin);
		fdTargetFileName.right = new FormAttachment(100, 0);
		wTargetFileName.setLayoutData(fdTargetFileName);

		// Source file path ...
		wlSourceFilePath = new Label(shell, SWT.RIGHT);
		wlSourceFilePath.setText(BaseMessages.getString(PKG, "OssDownloader.SourceFilePath.Label"));
		props.setLook(wlSourceFilePath);
		fdlSourceFilePath = new FormData();
		fdlSourceFilePath.left = new FormAttachment(0, -margin);
		fdlSourceFilePath.top = new FormAttachment(wTargetFileName, 2 * margin);
		fdlSourceFilePath.right = new FormAttachment(middle, -2 * margin);
		wlSourceFilePath.setLayoutData(fdlSourceFilePath);

		wSourceFilePath = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
		wSourceFilePath.setEditable(true);
		props.setLook(wSourceFilePath);
		wSourceFilePath.addModifyListener(lsMod);
		fdSourceFilePath = new FormData();
		fdSourceFilePath.left = new FormAttachment(middle, -margin);
		fdSourceFilePath.top = new FormAttachment(wTargetFileName, 2 * margin);
		fdSourceFilePath.right = new FormAttachment(100, -margin);
		wSourceFilePath.setLayoutData(fdSourceFilePath);
		wSourceFilePath.addFocusListener(new FocusListener() {
			public void focusLost(org.eclipse.swt.events.FocusEvent e) {
			}

			public void focusGained(org.eclipse.swt.events.FocusEvent e) {
				Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
				shell.setCursor(busy);
				getPreviousFields();
				shell.setCursor(null);
				busy.dispose();
			}
		});

		// Source file name ...
		wlSourceFileName = new Label(shell, SWT.RIGHT);
		wlSourceFileName.setText(BaseMessages.getString(PKG, "OssDownloader.SourceFileName.Label"));
		props.setLook(wlSourceFileName);
		fdlSourceFileName = new FormData();
		fdlSourceFileName.left = new FormAttachment(0, -margin);
		fdlSourceFileName.top = new FormAttachment(wSourceFilePath, 2 * margin);
		fdlSourceFileName.right = new FormAttachment(middle, -2 * margin);
		wlSourceFileName.setLayoutData(fdlSourceFileName);

		wSourceFileName = new CCombo(shell, SWT.BORDER | SWT.READ_ONLY);
		wSourceFileName.setEditable(true);
		props.setLook(wSourceFileName);
		wSourceFileName.addModifyListener(lsMod);
		fdSourceFileName = new FormData();
		fdSourceFileName.left = new FormAttachment(middle, -margin);
		fdSourceFileName.top = new FormAttachment(wSourceFilePath, 2 * margin);
		fdSourceFileName.right = new FormAttachment(100, -margin);
		wSourceFileName.setLayoutData(fdSourceFileName);
		wSourceFileName.addFocusListener(new FocusListener() {
			public void focusLost(org.eclipse.swt.events.FocusEvent e) {
			}

			public void focusGained(org.eclipse.swt.events.FocusEvent e) {
				Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
				shell.setCursor(busy);
				getPreviousFields();
				shell.setCursor(null);
				busy.dispose();
			}
		});

		// OK and cancel buttons ...
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

		BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, wSourceFileName);

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
		wTargetFileName.addSelectionListener(lsDef);
		wSourceFilePath.addSelectionListener(lsDef);
		wSourceFileName.addSelectionListener(lsDef);
		wCoverFile.addSelectionListener(lsDef);

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

	protected void getPreviousFields() {
		if (getpreviousFields) {
			return;
		}
		getpreviousFields = true;
		try {
			// File path ...
			String sourceFilePath = null;
			if (wSourceFilePath != null) {
				sourceFilePath = wSourceFilePath.getText();
			}
			wSourceFilePath.removeAll();

			// File name ...
			String siurceFileName = null;
			if (wSourceFileName != null) {
				siurceFileName = wSourceFileName.getText();
			}
			wSourceFileName.removeAll();

			RowMetaInterface r = transMeta.getPrevStepFields(stepname);
			if (r != null) {
				String[] fieldnames = r.getFieldNames();
				wSourceFilePath.setItems(fieldnames);
				wSourceFileName.setItems(fieldnames);
			}

			if (sourceFilePath != null) {
				wSourceFilePath.setText(sourceFilePath);
			}
			if (siurceFileName != null) {
				wSourceFileName.setText(siurceFileName);
			}
		} catch (KettleException ke) {
			new ErrorDialog(shell, BaseMessages.getString(PKG, "OssDownloaderDialog.FailedToGetFields.DialogTitle"),
					BaseMessages.getString(PKG, "OssDownloaderDialog.FailedToGetFields.DialogMessage"), ke);
		}
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
		wTargetFileName.setText(Const.NVL(input.getTargetFileName(), ""));
		wSourceFilePath.setText(Const.NVL(input.getSourceFilePath(), ""));
		wSourceFileName.setText(Const.NVL(input.getSourceFileName(), ""));
		wCoverFile.setSelection(input.isCoverFile());

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
		meta.setTargetFileName(wTargetFileName.getText());
		meta.setSourceFilePath(wSourceFilePath.getText());
		meta.setSourceFileName(wSourceFileName.getText());
		meta.setCoverFile(wCoverFile.getSelection());
	}
}
