package org.pentaho.di.ui.trans.steps.oss.filesinput;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.oss.BookMark;
import org.pentaho.di.core.oss.OSSFileObject;
import org.pentaho.di.core.oss.OssConfig;
import org.pentaho.di.core.oss.OssWorker;
import org.pentaho.di.core.oss.OssWorkerUtils;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.TransPreviewFactory;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.steps.oss.filesinput.OssFilesInput;
import org.pentaho.di.trans.steps.oss.filesinput.OssFilesInputMeta;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputField;
import org.pentaho.di.ui.core.dialog.EnterNumberDialog;
import org.pentaho.di.ui.core.dialog.EnterTextDialog;
import org.pentaho.di.ui.core.dialog.PreviewRowsDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.dialog.TransPreviewProgressDialog;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

/**
 * 
 * @author xuejian
 *
 */
public class OssFilesInputDialog extends BaseStepDialog implements StepDialogInterface {

	private static Class<?> PKG = OssFilesInputMeta.class; // for i18n purposes

	private OssFilesInputMeta input;

	private ModifyListener lsMod;

	int middle, margin;

	// Build tabs folder
	private CTabFolder wTabFolder;
	private FormData fdTabFolder;

	// build tabs
	private CTabItem wOssConfigTab;
	private CTabItem wContentTab;
	private CTabItem wFieldsTab;

	// Oss config tab
	private Composite wOssConfigComp;
	private FormData fdOssConfigComp;

	// Content tab
	private Composite wContentComp;
	private FormData fdContentComp;

	// Fields tab
	private Composite wFieldsComp;
	private FormData fdFieldsComp;

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

	/*
	 * LowerLimitMarker
	 */
	private Label wlLowerLimitMarker;
	private TextVar wLowerLimitMarker;
	private FormData fdlLowerLimitMarker, fdLowerLimitMarker;

	/*
	 * FileType
	 */
	private Label wlFileType;
	private CCombo wFileType;
	private FormData fdlFileType, fdFileType;

	/*
	 * Separator
	 */
	private Label wlSeparator;
	private TextVar wSeparator;
	private FormData fdlSeparator, fdSeparator;

	/*
	 * Enclosure
	 */
	private Label wlEnclosure;
	private TextVar wEnclosure;
	private FormData fdlEnclosure, fdEnclosure;

	/*
	 * Format
	 */
	private Label wlFormat;
	private CCombo wFormat;
	private FormData fdlFormat, fdFormat;

	/*
	 * Charset
	 */
	private Label wlCharset;
	private CCombo wCharset;
	private FormData fdlCharset, fdCharset;

	/*
	 * Prev flag
	 */
	private Label wlHeadFlag;
	private Button wHeadFlag;
	private FormData fdlHeadFlag, fdHeadFlag;

	private TableView wFields;
	private FormData fdFields;

	private boolean getpreviousFields = false;
	private boolean gotEncodings = false;

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

		lsMod = new ModifyListener() {
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

		middle = props.getMiddlePct();
		margin = Const.MARGIN;

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

		addOssConfigTab(); // oss配置
		addContentTab(); // 上下文处理
		addFieldsTab(); // 字段信息

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
		lsGet = new Listener() {
			public void handleEvent(Event e) {
				get();
			}
		};
		lsPreview = new Listener() {
			public void handleEvent(Event e) {
				preview();
			}
		};

		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);
		wGet.addListener(SWT.Selection, lsGet);
		wPreview.addListener(SWT.Selection, lsPreview);

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
		wLowerLimitMarker.addSelectionListener(lsDef);

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

		wTabFolder.setSelection(0);
		// Set the shell size, based upon previous time...
		getData();

		// input.setChanged(changed);
		setSize();

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
		wOssConfigTab.setText(BaseMessages.getString(PKG, "OssFilesInputDialog.OssConfigTab.TabTitle"));

		wOssConfigComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wOssConfigComp);

		FormLayout fieldsLayout = new FormLayout();
		fieldsLayout.marginWidth = 3;
		fieldsLayout.marginHeight = 3;
		wOssConfigComp.setLayout(fieldsLayout);

		// Endpoint ...
		wlEndpoint = new Label(wOssConfigComp, SWT.RIGHT);
		wlEndpoint.setText(msgProp("OssFilesInput.Endpoint.Label"));
		props.setLook(wlEndpoint);
		fdlEndpoint = new FormData();
		fdlEndpoint.left = new FormAttachment(0, 0);
		fdlEndpoint.right = new FormAttachment(middle, -margin);
		fdlEndpoint.top = new FormAttachment(margin, margin);
		wlEndpoint.setLayoutData(fdlEndpoint);

		wEndpoint = new TextVar(transMeta, wOssConfigComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wEndpoint);
		wEndpoint.addModifyListener(lsMod);
		fdEndpoint = new FormData();
		fdEndpoint.left = new FormAttachment(middle, 0);
		fdEndpoint.right = new FormAttachment(100, -margin);
		fdEndpoint.top = new FormAttachment(margin, margin);
		wEndpoint.setLayoutData(fdEndpoint);

		// AccessKey ...
		wlAccessKey = new Label(wOssConfigComp, SWT.RIGHT);
		wlAccessKey.setText(msgProp("OssFilesInput.AccessKey.Label"));
		props.setLook(wlAccessKey);
		fdlAccessKey = new FormData();
		fdlAccessKey.left = new FormAttachment(0, 0);
		fdlAccessKey.right = new FormAttachment(middle, -margin);
		fdlAccessKey.top = new FormAttachment(wEndpoint, margin);
		wlAccessKey.setLayoutData(fdlAccessKey);
		wAccessKey = new TextVar(transMeta, wOssConfigComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wAccessKey);
		wAccessKey.addModifyListener(lsMod);
		fdAccessKey = new FormData();
		fdAccessKey.left = new FormAttachment(middle, 0);
		fdAccessKey.right = new FormAttachment(100, 0);
		fdAccessKey.top = new FormAttachment(wEndpoint, margin);
		wAccessKey.setLayoutData(fdAccessKey);

		// SecureKey ...
		wlSecureKey = new Label(wOssConfigComp, SWT.RIGHT);
		wlSecureKey.setText(msgProp("OssFilesInput.SecureKey.Label"));
		props.setLook(wlSecureKey);
		fdlSecureKey = new FormData();
		fdlSecureKey.left = new FormAttachment(0, 0);
		fdlSecureKey.right = new FormAttachment(middle, -margin);
		fdlSecureKey.top = new FormAttachment(wAccessKey, margin);
		wlSecureKey.setLayoutData(fdlSecureKey);
		wSecureKey = new TextVar(transMeta, wOssConfigComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wSecureKey);
		wSecureKey.addModifyListener(lsMod);
		fdSecureKey = new FormData();
		fdSecureKey.left = new FormAttachment(middle, 0);
		fdSecureKey.right = new FormAttachment(100, 0);
		fdSecureKey.top = new FormAttachment(wAccessKey, margin);
		wSecureKey.setLayoutData(fdSecureKey);

		// Bucket ...
		wlBucket = new Label(wOssConfigComp, SWT.RIGHT);
		wlBucket.setText(msgProp("OssFilesInput.Bucket.Label"));
		props.setLook(wlBucket);
		fdlBucket = new FormData();
		fdlBucket.left = new FormAttachment(0, 0);
		fdlBucket.right = new FormAttachment(middle, -margin);
		fdlBucket.top = new FormAttachment(wSecureKey, margin);
		wlBucket.setLayoutData(fdlBucket);
		wBucket = new TextVar(transMeta, wOssConfigComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wBucket);
		wBucket.addModifyListener(lsMod);
		fdBucket = new FormData();
		fdBucket.left = new FormAttachment(middle, 0);
		fdBucket.right = new FormAttachment(100, 0);
		fdBucket.top = new FormAttachment(wSecureKey, margin);
		wBucket.setLayoutData(fdBucket);

		// Prev flag
		wlPrevFlag = new Label(wOssConfigComp, SWT.RIGHT);
		wlPrevFlag.setText(msgProp("OssFilesInput.PrevFlag.Label"));
		props.setLook(wlPrevFlag);
		fdlPrevFlag = new FormData();
		fdlPrevFlag.left = new FormAttachment(0, 0);
		fdlPrevFlag.right = new FormAttachment(middle, -margin);
		fdlPrevFlag.top = new FormAttachment(wBucket, margin);
		wlPrevFlag.setLayoutData(fdlPrevFlag);
		wPrevFlag = new Button(wOssConfigComp, SWT.CHECK);
		props.setLook(wPrevFlag);
		fdPrevFlag = new FormData();
		fdPrevFlag.left = new FormAttachment(middle, 0);
		fdPrevFlag.right = new FormAttachment(100, 0);
		fdPrevFlag.top = new FormAttachment(wBucket, margin);
		wPrevFlag.setLayoutData(fdPrevFlag);

		// File name ...
		wlFileName = new Label(wOssConfigComp, SWT.RIGHT);
		wlFileName.setText(msgProp("OssFilesInput.FileName.Label"));
		props.setLook(wlFileName);
		fdlFileName = new FormData();
		fdlFileName.left = new FormAttachment(0, 0);
		fdlFileName.right = new FormAttachment(middle, -margin);
		fdlFileName.top = new FormAttachment(wPrevFlag, margin);
		wlFileName.setLayoutData(fdlFileName);
		wFileName = new TextVar(transMeta, wOssConfigComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wFileName);
		wFileName.addModifyListener(lsMod);
		fdFileName = new FormData();
		fdFileName.left = new FormAttachment(middle, 0);
		fdFileName.right = new FormAttachment(100, 0);
		fdFileName.top = new FormAttachment(wPrevFlag, margin);
		wFileName.setLayoutData(fdFileName);

		// LowerLimitMarker ...
		wlLowerLimitMarker = new Label(wOssConfigComp, SWT.RIGHT);
		wlLowerLimitMarker.setText(msgProp("OssFilesInput.LowerLimitMarker.Label"));
		props.setLook(wlLowerLimitMarker);
		fdlLowerLimitMarker = new FormData();
		fdlLowerLimitMarker.left = new FormAttachment(0, 0);
		fdlLowerLimitMarker.right = new FormAttachment(middle, -margin);
		fdlLowerLimitMarker.top = new FormAttachment(wFileName, margin);
		wlLowerLimitMarker.setLayoutData(fdlLowerLimitMarker);
		wLowerLimitMarker = new TextVar(transMeta, wOssConfigComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wLowerLimitMarker);
		wLowerLimitMarker.addModifyListener(lsMod);
		fdLowerLimitMarker = new FormData();
		fdLowerLimitMarker.left = new FormAttachment(middle, 0);
		fdLowerLimitMarker.right = new FormAttachment(100, 0);
		fdLowerLimitMarker.top = new FormAttachment(wFileName, margin);
		wLowerLimitMarker.setLayoutData(fdLowerLimitMarker);

		fdOssConfigComp = new FormData();
		fdOssConfigComp.left = new FormAttachment(0, 0);
		fdOssConfigComp.top = new FormAttachment(0, 0);
		fdOssConfigComp.right = new FormAttachment(100, 0);
		fdOssConfigComp.bottom = new FormAttachment(100, 0);
		wOssConfigComp.setLayoutData(fdOssConfigComp);

		wOssConfigComp.layout();
		wOssConfigTab.setControl(wOssConfigComp);
	}

	private void addContentTab() {
		// OSS config tab
		wContentTab = new CTabItem(wTabFolder, SWT.NONE);
		wContentTab.setText(BaseMessages.getString(PKG, "OssFilesInputDialog.ContentTab.TabTitle"));

		wContentComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wContentComp);

		FormLayout fieldsLayout = new FormLayout();
		fieldsLayout.marginWidth = 3;
		fieldsLayout.marginHeight = 3;
		wContentComp.setLayout(fieldsLayout);

		// FileType ...

		// Filetype line
		wlFileType = new Label(wContentComp, SWT.RIGHT);
		wlFileType.setText(BaseMessages.getString(PKG, "OssFilesInputDialog.FileType.Label"));
		props.setLook(wlFileType);
		fdlFileType = new FormData();
		fdlFileType.left = new FormAttachment(0, 0);
		fdlFileType.right = new FormAttachment(middle, -margin);
		fdlFileType.top = new FormAttachment(margin, margin);
		wlFileType.setLayoutData(fdlFileType);
		wFileType = new CCombo(wContentComp, SWT.BORDER | SWT.READ_ONLY);
		wFileType.setText(BaseMessages.getString(PKG, "OssFilesInputDialog.FileType.Label"));
		props.setLook(wFileType);
		wFileType.add("CSV");
		// wFileType.add( "Fixed" );
		wFileType.select(0);
		wFileType.addModifyListener(lsMod);
		fdFileType = new FormData();
		fdFileType.left = new FormAttachment(middle, 0);
		fdFileType.right = new FormAttachment(100, -margin);
		fdFileType.top = new FormAttachment(margin, margin);
		wFileType.setLayoutData(fdFileType);

		// Separator ...
		wlSeparator = new Label(wContentComp, SWT.RIGHT);
		wlSeparator.setText(msgProp("OssFilesInput.Separator.Label"));
		props.setLook(wlSeparator);
		fdlSeparator = new FormData();
		fdlSeparator.left = new FormAttachment(0, 0);
		fdlSeparator.right = new FormAttachment(middle, -margin);
		fdlSeparator.top = new FormAttachment(wFileType, margin);
		wlSeparator.setLayoutData(fdlSeparator);

		wSeparator = new TextVar(transMeta, wContentComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wSeparator);
		wSeparator.addModifyListener(lsMod);
		fdSeparator = new FormData();
		fdSeparator.left = new FormAttachment(middle, 0);
		fdSeparator.right = new FormAttachment(100, -margin);
		fdSeparator.top = new FormAttachment(wFileType, margin);
		wSeparator.setLayoutData(fdSeparator);

		// Enclosure ...
		wlEnclosure = new Label(wContentComp, SWT.RIGHT);
		wlEnclosure.setText(msgProp("OssFilesInput.Enclosure.Label"));
		props.setLook(wlEnclosure);
		fdlEnclosure = new FormData();
		fdlEnclosure.left = new FormAttachment(0, 0);
		fdlEnclosure.right = new FormAttachment(middle, -margin);
		fdlEnclosure.top = new FormAttachment(wSeparator, margin);
		wlEnclosure.setLayoutData(fdlEnclosure);

		wEnclosure = new TextVar(transMeta, wContentComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wEnclosure);
		wEnclosure.addModifyListener(lsMod);
		fdEnclosure = new FormData();
		fdEnclosure.left = new FormAttachment(middle, 0);
		fdEnclosure.right = new FormAttachment(100, -margin);
		fdEnclosure.top = new FormAttachment(wSeparator, margin);
		wEnclosure.setLayoutData(fdEnclosure);

		// Format ...
		wlFormat = new Label(wContentComp, SWT.RIGHT);
		wlFormat.setText(msgProp("OssFilesInput.Format.Label"));
		props.setLook(wlFormat);
		fdlFormat = new FormData();
		fdlFormat.left = new FormAttachment(0, 0);
		fdlFormat.right = new FormAttachment(middle, -margin);
		fdlFormat.top = new FormAttachment(wEnclosure, margin);
		wlFormat.setLayoutData(fdlFormat);

		wFormat = new CCombo(wContentComp, SWT.BORDER | SWT.READ_ONLY);
		props.setLook(wFormat);
		wFormat.add("DOS");
		wFormat.add("Unix");
		wFormat.add("mixed");
		wFormat.select(0);
		wFormat.addModifyListener(lsMod);
		fdFormat = new FormData();
		fdFormat.left = new FormAttachment(middle, 0);
		fdFormat.right = new FormAttachment(100, -margin);
		fdFormat.top = new FormAttachment(wEnclosure, margin);
		wFormat.setLayoutData(fdFormat);

		// Charset ...
		wlCharset = new Label(wContentComp, SWT.RIGHT);
		wlCharset.setText(msgProp("OssFilesInput.Charset.Label"));
		props.setLook(wlCharset);
		fdlCharset = new FormData();
		fdlCharset.left = new FormAttachment(0, 0);
		fdlCharset.right = new FormAttachment(middle, -margin);
		fdlCharset.top = new FormAttachment(wFormat, margin);
		wlCharset.setLayoutData(fdlCharset);

		wCharset = new CCombo(wContentComp, SWT.BORDER | SWT.READ_ONLY);
		wCharset.setEditable(true);
		props.setLook(wCharset);
		wCharset.addModifyListener(lsMod);
		fdCharset = new FormData();
		fdCharset.left = new FormAttachment(middle, 0);
		fdCharset.right = new FormAttachment(100, -margin);
		fdCharset.top = new FormAttachment(wFormat, margin);
		wCharset.setLayoutData(fdCharset);
		wCharset.addFocusListener(new FocusListener() {
			public void focusLost(org.eclipse.swt.events.FocusEvent e) {
			}

			public void focusGained(org.eclipse.swt.events.FocusEvent e) {
				Cursor busy = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
				shell.setCursor(busy);
				setEncodings();
				shell.setCursor(null);
				busy.dispose();
			}
		});

		// HeadFlag
		wlHeadFlag = new Label(wContentComp, SWT.RIGHT);
		wlHeadFlag.setText(msgProp("OssFilesInput.HeadFlag.Label"));
		props.setLook(wlHeadFlag);
		fdlHeadFlag = new FormData();
		fdlHeadFlag.left = new FormAttachment(0, 0);
		fdlHeadFlag.right = new FormAttachment(middle, -margin);
		fdlHeadFlag.top = new FormAttachment(wCharset, margin);
		wlHeadFlag.setLayoutData(fdlHeadFlag);
		wHeadFlag = new Button(wContentComp, SWT.CHECK);
		props.setLook(wHeadFlag);
		fdHeadFlag = new FormData();
		fdHeadFlag.left = new FormAttachment(middle, 0);
		fdHeadFlag.right = new FormAttachment(100, 0);
		fdHeadFlag.top = new FormAttachment(wCharset, margin);
		wHeadFlag.setLayoutData(fdHeadFlag);

		fdContentComp = new FormData();
		fdContentComp.left = new FormAttachment(0, 0);
		fdContentComp.top = new FormAttachment(0, 0);
		fdContentComp.right = new FormAttachment(100, 0);
		fdContentComp.bottom = new FormAttachment(100, 0);
		wContentComp.setLayoutData(fdContentComp);

		wContentComp.layout();
		wContentTab.setControl(wContentComp);
	}

	private void addFieldsTab() {
		// Fields tab...
		//
		wFieldsTab = new CTabItem(wTabFolder, SWT.NONE);
		wFieldsTab.setText(BaseMessages.getString(PKG, "OssFilesInputDialog.FieldsTab.TabTitle"));

		FormLayout fieldsLayout = new FormLayout();
		fieldsLayout.marginWidth = Const.FORM_MARGIN;
		fieldsLayout.marginHeight = Const.FORM_MARGIN;

		wFieldsComp = new Composite(wTabFolder, SWT.NONE);
		wFieldsComp.setLayout(fieldsLayout);
		props.setLook(wFieldsComp);

		wGet = new Button(wFieldsComp, SWT.PUSH);
		wGet.setText(BaseMessages.getString(PKG, "System.Button.GetFields"));
		fdGet = new FormData();
		fdGet.left = new FormAttachment(50, 0);
		fdGet.bottom = new FormAttachment(100, 0);
		wGet.setLayoutData(fdGet);

		final int FieldsRows = input.getInputFields().length;

		ColumnInfo[] colinf = new ColumnInfo[] {
				new ColumnInfo(BaseMessages.getString(PKG, "OssFilesInputDialog.NameColumn.Column"),
						ColumnInfo.COLUMN_TYPE_TEXT, false),
				new ColumnInfo(BaseMessages.getString(PKG, "OssFilesInputDialog.TypeColumn.Column"),
						ColumnInfo.COLUMN_TYPE_CCOMBO, ValueMeta.getTypes(), true),
				new ColumnInfo(BaseMessages.getString(PKG, "OssFilesInputDialog.FormatColumn.Column"),
						ColumnInfo.COLUMN_TYPE_FORMAT, 2),
				new ColumnInfo(BaseMessages.getString(PKG, "OssFilesInputDialog.PositionColumn.Column"),
						ColumnInfo.COLUMN_TYPE_TEXT, false),
				new ColumnInfo(BaseMessages.getString(PKG, "OssFilesInputDialog.LengthColumn.Column"),
						ColumnInfo.COLUMN_TYPE_TEXT, false),
				new ColumnInfo(BaseMessages.getString(PKG, "OssFilesInputDialog.PrecisionColumn.Column"),
						ColumnInfo.COLUMN_TYPE_TEXT, false),
				new ColumnInfo(BaseMessages.getString(PKG, "OssFilesInputDialog.CurrencyColumn.Column"),
						ColumnInfo.COLUMN_TYPE_TEXT, false),
				new ColumnInfo(BaseMessages.getString(PKG, "OssFilesInputDialog.DecimalColumn.Column"),
						ColumnInfo.COLUMN_TYPE_TEXT, false),
				new ColumnInfo(BaseMessages.getString(PKG, "OssFilesInputDialog.GroupColumn.Column"),
						ColumnInfo.COLUMN_TYPE_TEXT, false),
				new ColumnInfo(BaseMessages.getString(PKG, "OssFilesInputDialog.NullIfColumn.Column"),
						ColumnInfo.COLUMN_TYPE_TEXT, false),
				new ColumnInfo(BaseMessages.getString(PKG, "OssFilesInputDialog.IfNullColumn.Column"),
						ColumnInfo.COLUMN_TYPE_TEXT, false),
				new ColumnInfo(BaseMessages.getString(PKG, "OssFilesInputDialog.TrimTypeColumn.Column"),
						ColumnInfo.COLUMN_TYPE_CCOMBO, ValueMeta.trimTypeDesc, true),
				new ColumnInfo(BaseMessages.getString(PKG, "OssFilesInputDialog.RepeatColumn.Column"),
						ColumnInfo.COLUMN_TYPE_CCOMBO, new String[] { BaseMessages.getString(PKG, "System.Combo.Yes"),
								BaseMessages.getString(PKG, "System.Combo.No") },
						true) };

		colinf[12].setToolTip(BaseMessages.getString(PKG, "OssFilesInputDialog.RepeatColumn.Tooltip"));

		wFields = new TableView(transMeta, wFieldsComp, SWT.FULL_SELECTION | SWT.MULTI, colinf, FieldsRows, lsMod,
				props);

		fdFields = new FormData();
		fdFields.left = new FormAttachment(0, 0);
		fdFields.top = new FormAttachment(0, 0);
		fdFields.right = new FormAttachment(100, 0);
		fdFields.bottom = new FormAttachment(wGet, -margin);
		wFields.setLayoutData(fdFields);

		fdFieldsComp = new FormData();
		fdFieldsComp.left = new FormAttachment(0, 0);
		fdFieldsComp.top = new FormAttachment(0, 0);
		fdFieldsComp.right = new FormAttachment(100, 0);
		fdFieldsComp.bottom = new FormAttachment(100, 0);
		wFieldsComp.setLayoutData(fdFieldsComp);

		wFieldsComp.layout();
		wFieldsTab.setControl(wFieldsComp);
	}

	private void setEncodings() {
		// Encoding of the text file:
		if (!gotEncodings) {
			gotEncodings = true;

			wCharset.removeAll();
			List<Charset> values = new ArrayList<Charset>(Charset.availableCharsets().values());
			for (Charset charSet : values) {
				wCharset.add(charSet.displayName());
			}

			// Now select the default!
			String defEncoding = Const.getEnvironmentVariable("file.encoding", "UTF-8");
			int idx = Const.indexOfString(defEncoding, wCharset.getItems());
			if (idx >= 0) {
				wCharset.select(idx);
			}
		}
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
		// oss config
		wEndpoint.setText(Const.NVL(input.getEndpoint(), ""));
		wAccessKey.setText(Const.NVL(input.getAccessKey(), ""));
		wSecureKey.setText(Const.NVL(input.getSecureKey(), ""));
		wBucket.setText(Const.NVL(input.getBucket(), ""));
		wFileName.setText(Const.NVL(input.getFileName(), ""));
		wPrevFlag.setSelection(input.isPrevFlag());
		wLowerLimitMarker.setText(Const.NVL(input.getLowerLimitMarker(), ""));

		// content
		wFileType.setText(Const.NVL(input.getFileType(), ""));
		wSeparator.setText(Const.NVL(input.getSeparator(), ""));
		wEnclosure.setText(Const.NVL(input.getEnclosure(), ""));
		wFormat.setText(Const.NVL(input.getFormat(), ""));
		wCharset.setText(Const.NVL(input.getCharset(), ""));
		wHeadFlag.setSelection(input.isHeadFlag());

		// fields
		getFieldsData(input, false);

		wFields.removeEmptyRows();
		wFields.setRowNums();
		wFields.optWidth(true);

		wStepname.selectAll();
		wStepname.setFocus();
	}

	private void getFieldsData(OssFilesInputMeta in, boolean insertAtTop) {
		for (int i = 0; i < in.getInputFields().length; i++) {
			TextFileInputField field = in.getInputFields()[i];

			TableItem item;

			if (insertAtTop) {
				item = new TableItem(wFields.table, SWT.NONE, i);
			} else {
				if (i >= wFields.table.getItemCount()) {
					item = wFields.table.getItem(i);
				} else {
					item = new TableItem(wFields.table, SWT.NONE);
				}
			}

			item.setText(1, Const.NVL(field.getName(), ""));
			String type = field.getTypeDesc();
			String format = field.getFormat();
			String position = "" + field.getPosition();
			String length = "" + field.getLength();
			String prec = "" + field.getPrecision();
			String curr = field.getCurrencySymbol();
			String group = field.getGroupSymbol();
			String decim = field.getDecimalSymbol();
			String def = field.getNullString();
			String ifNull = field.getIfNullValue();
			String trim = field.getTrimTypeDesc();
			String rep = field.isRepeated() ? BaseMessages.getString(PKG, "System.Combo.Yes")
					: BaseMessages.getString(PKG, "System.Combo.No");

			if (type != null) {
				item.setText(2, type);
			}
			if (format != null) {
				item.setText(3, format);
			}
			if (position != null && !"-1".equals(position)) {
				item.setText(4, position);
			}
			if (length != null && !"-1".equals(length)) {
				item.setText(5, length);
			}
			if (prec != null && !"-1".equals(prec)) {
				item.setText(6, prec);
			}
			if (curr != null) {
				item.setText(7, curr);
			}
			if (decim != null) {
				item.setText(8, decim);
			}
			if (group != null) {
				item.setText(9, group);
			}
			if (def != null) {
				item.setText(10, def);
			}
			if (ifNull != null) {
				item.setText(11, ifNull);
			}
			if (trim != null) {
				item.setText(12, trim);
			}
			if (rep != null) {
				item.setText(13, rep);
			}
		}

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
		// oss config
		meta.setEndpoint(wEndpoint.getText());
		meta.setAccessKey(wAccessKey.getText());
		meta.setSecureKey(wSecureKey.getText());
		meta.setBucket(wBucket.getText());
		meta.setFileName(wFileName.getText());
		meta.setPrevFlag(wPrevFlag.getSelection());
		meta.setLowerLimitMarker(wLowerLimitMarker.getText());

		// content
		meta.setFileType(wFileType.getText());
		meta.setSeparator(wSeparator.getText());
		meta.setEnclosure(wEnclosure.getText());
		meta.setFormat(wFormat.getText());
		meta.setCharset(wCharset.getText());
		meta.setHeadFlag(wHeadFlag.getSelection());

		// fields
		int nrfields = wFields.nrNonEmpty();
		meta.allocate(nrfields);

		for (int i = 0; i < nrfields; i++) {
			TextFileInputField field = new TextFileInputField();

			TableItem item = wFields.getNonEmpty(i);
			field.setName(item.getText(1));
			field.setType(ValueMeta.getType(item.getText(2)));
			field.setFormat(item.getText(3));
			field.setPosition(Const.toInt(item.getText(4), -1));
			field.setLength(Const.toInt(item.getText(5), -1));
			field.setPrecision(Const.toInt(item.getText(6), -1));
			field.setCurrencySymbol(item.getText(7));
			field.setDecimalSymbol(item.getText(8));
			field.setGroupSymbol(item.getText(9));
			field.setNullString(item.getText(10));
			field.setIfNullValue(item.getText(11));
			field.setTrimType(ValueMeta.getTrimTypeByDesc(item.getText(12)));
			field.setRepeated(BaseMessages.getString(PKG, "System.Combo.Yes").equalsIgnoreCase(item.getText(13)));

			// CHECKSTYLE:Indentation:OFF
			meta.getInputFields()[i] = field;
		}
	}

	private void get() {
		if (wFileType.getText().equalsIgnoreCase("CSV")) {
			getCSV();
		} else {
			// getFixed();
		}
	}

	// Get the data layout
	private void getCSV() {
		logBasic("CSV...");

		// Dialog inputs
		OssFilesInputMeta meta = new OssFilesInputMeta();
		getInfo(meta, false);
		OssFilesInputMeta previousMeta = (OssFilesInputMeta) meta.clone();

		// Get datas from meta
		String endpoint = transMeta.environmentSubstitute(meta.getEndpoint());
		String accessKey = transMeta.environmentSubstitute(meta.getAccessKey());
		String secureKey = transMeta.environmentSubstitute(meta.getSecureKey());
		String bucket = transMeta.environmentSubstitute(meta.getBucket());
		String fileName = transMeta.environmentSubstitute(meta.getFileName());
		String lowerLimitMarker = transMeta.environmentSubstitute(meta.getLowerLimitMarker());

		String fileType = transMeta.environmentSubstitute(meta.getFileType());
		String separator = transMeta.environmentSubstitute(meta.getSeparator());
		String enclosure = transMeta.environmentSubstitute(meta.getEnclosure());
		String format = transMeta.environmentSubstitute(meta.getFormat());
		String charset = transMeta.environmentSubstitute(meta.getCharset());

		OssConfig ossConfig = new OssConfig(endpoint, accessKey, secureKey, bucket);
		BookMark bookMark = OssWorkerUtils.createBookMark(ossConfig, fileName, lowerLimitMarker, meta.nameIsPrevious(),
				10);

		// 读到的文件集合为空
		if (bookMark.hasNoBooks()) {
			MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
			mb.setMessage(BaseMessages.getString(PKG, "TextFileInputDialog.NoValidFileFound.DialogMessage"));
			mb.setText(BaseMessages.getString(PKG, "System.Dialog.Error.Title"));
			mb.open();
			return;
		}

		int clearFields = meta.hasHeader() ? SWT.YES : SWT.NO;
		int nrInputFields = meta.getInputFields().length;

		if (meta.hasHeader() && nrInputFields > 0) {
			MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION);
			mb.setMessage(BaseMessages.getString(PKG, "TextFileInputDialog.ClearFieldList.DialogMessage"));
			mb.setText(BaseMessages.getString(PKG, "TextFileInputDialog.ClearFieldList.DialogTitle"));
			clearFields = mb.open();
			if (clearFields == SWT.CANCEL) {
				return;
			}
		}

		wFields.table.removeAll();
		Table table = wFields.table;

		if (!(clearFields == SWT.YES || !meta.hasHeader() || nrInputFields > 0)) {
			MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
			mb.setMessage(BaseMessages.getString(PKG, "TextFileInputDialog.UnableToReadHeaderLine.DialogMessage"));
			mb.setText(BaseMessages.getString(PKG, "System.Dialog.Error.Title"));
			mb.open();
			return;
		}

		String currentBook = bookMark.getCurrentBook();
		OssWorker ossWorker = null;
		int fileFormatTypeNr = meta.getFileFormatTypeNr();

		try {
			ossWorker = new OssWorker(ossConfig);
			OSSFileObject ossFileObject = ossWorker.getOSSFileObject(currentBook);
			bookMark.openBook(ossFileObject.getContent(), ossFileObject.getEncoding(), fileFormatTypeNr);
			String firstLine = bookMark.readLine();
			if (StringUtils.isEmpty(firstLine)) {
				throw new Exception("首行为空");
			}
			String[] fields = OssFilesInput.guessStringsFromLine(transMeta, log, firstLine, meta, separator, enclosure,
					null);
			if (meta.hasHeader()) {
				// 如果是头 读出第一行作为title
				for (int i = 0; i < fields.length; i++) {
					String field = fields[i];
					if (StringUtils.isEmpty(field)) {
						field = "Field" + (i + 1);
					} else {
						// Trim the field
						field = Const.trim(field);
						// Replace all spaces & - with underscore _
						field = Const.replace(field, " ", "_");
						field = Const.replace(field, "-", "_");
					}
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(1, field);
					item.setText(2, "String"); // The default type is String...
				}
			} else {
				// 沒有title
				for (int i = 0; i < fields.length; i++) {
					String field = "Field" + (i + 1);
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText(1, field);
					item.setText(2, "String"); // The default type is String...
				}
			}
			wFields.setRowNums();
			wFields.optWidth(true);

			// Copy it...
			getInfo(meta, false);
			// Sample a few lines to determine the correct type of the fields...
			String shellText = BaseMessages.getString(PKG, "TextFileInputDialog.LinesToSample.DialogTitle");
			String lineText = BaseMessages.getString(PKG, "TextFileInputDialog.LinesToSample.DialogMessage");
			EnterNumberDialog end = new EnterNumberDialog(shell, 100, shellText, lineText);
			int samples = end.open();

			if (samples < 0) {
				MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
				mb.setMessage(BaseMessages.getString(PKG, "TextFileInputDialog.UnableToReadHeaderLine.DialogMessage"));
				mb.setText(BaseMessages.getString(PKG, "System.Dialog.Error.Title"));
				mb.open();
				return;
			}

			getInfo(meta, false);

			// guess fields

		} catch (Throwable thr) {
			thr.printStackTrace();
			wFields.removeAll();

			// OK, what's the result of our search?
			getData();

			// If we didn't want the list to be cleared, we need to re-inject the previous
			// values...
			//
			if (clearFields == SWT.NO) {
				getFieldsData(previousMeta, true);
				wFields.table.setSelection(previousMeta.getInputFields().length, wFields.table.getItemCount() - 1);
			}

			wFields.removeEmptyRows();
			wFields.setRowNums();
			wFields.optWidth(true);

			EnterTextDialog etd = new EnterTextDialog(shell,
					BaseMessages.getString(PKG, "TextFileInputDialog.ScanResults.DialogTitle"),
					BaseMessages.getString(PKG, "TextFileInputDialog.ScanResults.DialogMessage"), thr.getMessage(),
					true);
			etd.setReadOnly();
			etd.open();
		} finally {
			ossWorker.close();
			bookMark.closeBook();
			try {
				bookMark.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void preview() {
		logBasic("Preview...");

	    // Create the XML input step
	    OssFilesInputMeta oneMeta = new OssFilesInputMeta();
	    getInfo( oneMeta, false );

	    TransMeta previewMeta = TransPreviewFactory.generatePreviewTransformation( transMeta, oneMeta,
	      wStepname.getText() );

	    EnterNumberDialog numberDialog =
	        new EnterNumberDialog( shell, props.getDefaultPreviewSize(), BaseMessages.getString( PKG,
	            "TextFileInputDialog.PreviewSize.DialogTitle" ), BaseMessages.getString( PKG,
	            "TextFileInputDialog.PreviewSize.DialogMessage" ) );
	    int previewSize = numberDialog.open();
	    if ( previewSize > 0 ) {
	      TransPreviewProgressDialog progressDialog =
	          new TransPreviewProgressDialog( shell, previewMeta, new String[] { wStepname.getText() },
	              new int[] { previewSize } );
	      progressDialog.open();

	      Trans trans = progressDialog.getTrans();
	      String loggingText = progressDialog.getLoggingText();

	      if ( !progressDialog.isCancelled() ) {
	        if ( trans.getResult() != null && trans.getResult().getNrErrors() > 0 ) {
	          EnterTextDialog etd =
	              new EnterTextDialog( shell, BaseMessages.getString( PKG, "System.Dialog.PreviewError.Title" ),
	                  BaseMessages.getString( PKG, "System.Dialog.PreviewError.Message" ), loggingText, true );
	          etd.setReadOnly();
	          etd.open();
	        }
	      }

	      PreviewRowsDialog prd =
	          new PreviewRowsDialog( shell, transMeta, SWT.NONE, wStepname.getText(), progressDialog
	              .getPreviewRowsMeta( wStepname.getText() ), progressDialog.getPreviewRows( wStepname.getText() ),
	              loggingText );
	      prd.open();
	    }
	}
}
