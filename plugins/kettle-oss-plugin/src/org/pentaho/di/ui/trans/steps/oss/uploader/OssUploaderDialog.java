package org.pentaho.di.ui.trans.steps.oss.uploader;

import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.steps.oss.uploader.OssUploaderMeta;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

/**
 * 
 * @author xuejian
 *
 */
public class OssUploaderDialog extends BaseStepDialog implements StepDialogInterface {

	private static Class<?> PKG = OssUploaderMeta.class; // for i18n purposes

	private OssUploaderMeta input;

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
	private FormData fldAccessKey, fdAccessKey;

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

	public OssUploaderDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		input = (OssUploaderMeta) in;
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
		shell.setText(msgProp("OssUploader.Shell.Title"));

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
		wlEndpoint.setText(msgProp("OssUploader.Endpoint.Label"));
		props.setLook(wlEndpoint);
		fdlEndpoint = new FormData();
		fdlEndpoint.left = new FormAttachment(0, 0);
		fdlEndpoint.right = new FormAttachment(middle, -margin);
		fdlEndpoint.top = new FormAttachment(wlStepname, margin);
		wlEndpoint.setLayoutData(fdlEndpoint);
		wEndpoint = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wEndpoint);
		wEndpoint.addModifyListener(lsMod);
		fdEndpoint = new FormData();
		fdEndpoint.left = new FormAttachment(middle, 0);
		fdEndpoint.top = new FormAttachment(wlStepname, margin);
		fdEndpoint.right = new FormAttachment(100, 0);
		wEndpoint.setLayoutData(fdEndpoint);

		// OK and cancel buttons
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

		BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, wEndpoint);

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

	private String msgProp(String propKey) {
		return BaseMessages.getString(PKG, propKey);
	}

	// Read data and place it in the dialog
	public void getData() {
		wEndpoint.setText(Const.NVL(input.getEndpoint(), ""));

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
	private void getInfo(OssUploaderMeta meta, boolean preview) {
		meta.setEndpoint(wEndpoint.getText());
	}
}
