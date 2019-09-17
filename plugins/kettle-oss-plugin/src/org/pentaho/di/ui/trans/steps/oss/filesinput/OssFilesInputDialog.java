package org.pentaho.di.ui.trans.steps.oss.filesinput;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.steps.oss.filesinput.OssFilesInputMeta;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

/**
 * 
 * @author xuejian
 *
 */
public class OssFilesInputDialog extends BaseStepDialog implements StepDialogInterface {

	private static Class<?> PKG = OssFilesInputMeta.class; // for i18n purposes

	private OssFilesInputMeta input;

	// Build tabs folder
	private CTabFolder wTabFolder;
	private FormData fdTabFolder;

	// build tabs
	private CTabItem wOssConfigTab;
	private CTabItem wFileMessageTab;
	private CTabItem wOutputTab;
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
	 * Prev flag
	 */
	private Label wlPrevFlag;
	private Button wPrevFlag;
	private FormData fdlPrevFlag, fdPrevFlag;

	/*
	 * FileName
	 */
	private Label wlFileName;
	private TextVar wFileName;
	private FormData fdlFileName, fdFileName;

	private boolean getpreviousFields = false;

	public OssFilesInputDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		input = (OssFilesInputMeta) in;
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
		shell.setText(msgProp("OssFilesInput.Shell.Title"));

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

		wTabFolder = new CTabFolder(shell, SWT.BORDER);
		props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);
		wTabFolder.setSimple(false);

		addOssConfigTab();

		fdTabFolder = new FormData();
		fdTabFolder.left = new FormAttachment(0, 0);
		fdTabFolder.top = new FormAttachment(wStepname, margin);
		fdTabFolder.right = new FormAttachment(100, 0);
		fdTabFolder.bottom = new FormAttachment(100, -50);
		wTabFolder.setLayoutData(fdTabFolder);

		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));

		wPreview = new Button(shell, SWT.PUSH);
		wPreview.setText(BaseMessages.getString(PKG, "OssFilesInputDialog.Preview.Button"));

		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

		setButtonPositions(new Button[] { wOK, wPreview, wCancel }, margin, wTabFolder);

		// OK and cancel buttons ...
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

		BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, wFileName);

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
		wFileName.addSelectionListener(lsDef);
		wPrevFlag.addSelectionListener(lsDef);

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

	private void addOssConfigTab() {
		// OSS config tab
		wOssConfigTab = new CTabItem(wTabFolder, SWT.NONE);
		wOssConfigTab.setText(BaseMessages.getString(PKG, "TextFileInputDialog.AdditionalFieldsTab.TabTitle"));

		wAdditionalFieldsComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wAdditionalFieldsComp);

		FormLayout fieldsLayout = new FormLayout();
		fieldsLayout.marginWidth = 3;
		fieldsLayout.marginHeight = 3;
		wAdditionalFieldsComp.setLayout(fieldsLayout);

		// Endpoint ...
		wlEndpoint = new Label(shell, SWT.RIGHT);
		wlEndpoint.setText(msgProp("OssFilesInput.Endpoint.Label"));
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
		wlAccessKey.setText(msgProp("OssFilesInput.AccessKey.Label"));
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
		wlSecureKey.setText(msgProp("OssFilesInput.SecureKey.Label"));
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
		wlBucket.setText(msgProp("OssFilesInput.Bucket.Label"));
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

		// Prev flag
		wlPrevFlag = new Label(shell, SWT.RIGHT);
		wlPrevFlag.setText(msgProp("OssFilesInput.PrevFlag.Label"));
		props.setLook(wlPrevFlag);
		fdlPrevFlag = new FormData();
		fdlPrevFlag.left = new FormAttachment(0, 0);
		fdlPrevFlag.top = new FormAttachment(wBucket, margin);
		fdlPrevFlag.right = new FormAttachment(middle, -margin);
		wlPrevFlag.setLayoutData(fdlPrevFlag);
		wPrevFlag = new Button(shell, SWT.CHECK);
		props.setLook(wPrevFlag);
		fdPrevFlag = new FormData();
		fdPrevFlag.left = new FormAttachment(middle, 0);
		fdPrevFlag.top = new FormAttachment(wBucket, margin);
		fdPrevFlag.right = new FormAttachment(100, 0);
		wPrevFlag.setLayoutData(fdPrevFlag);

		// File name ...
		wlFileName = new Label(shell, SWT.RIGHT);
		wlFileName.setText(msgProp("OssFilesInput.FileName.Label"));
		props.setLook(wlFileName);
		fdlFileName = new FormData();
		fdlFileName.left = new FormAttachment(0, 0);
		fdlFileName.right = new FormAttachment(middle, -margin);
		fdlFileName.top = new FormAttachment(wPrevFlag, margin);
		wlFileName.setLayoutData(fdlFileName);
		wFileName = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wFileName);
		wFileName.addModifyListener(lsMod);
		fdFileName = new FormData();
		fdFileName.left = new FormAttachment(middle, 0);
		fdFileName.top = new FormAttachment(wPrevFlag, margin);
		fdFileName.right = new FormAttachment(100, 0);
		wFileName.setLayoutData(fdFileName);

	}

	protected void getPreviousFields() {
		if (getpreviousFields) {
			return;
		}
		getpreviousFields = true;
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
		wFileName.setText(Const.NVL(input.getFileName(), ""));
		wPrevFlag.setSelection(input.isPrevFlag());

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
	private void getInfo(OssFilesInputMeta meta, boolean preview) {
		meta.setEndpoint(wEndpoint.getText());
		meta.setAccessKey(wAccessKey.getText());
		meta.setSecureKey(wSecureKey.getText());
		meta.setBucket(wBucket.getText());
		meta.setFileName(wFileName.getText());
		meta.setPrevFlag(wPrevFlag.getSelection());
	}
}
