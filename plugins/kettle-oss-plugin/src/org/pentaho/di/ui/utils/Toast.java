package org.pentaho.di.ui.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class Toast {

	public static void error(Shell shell, Class<?> PKG, String title, String message) {
		MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
		mb.setMessage(message);
		mb.setText(title);
		mb.open();
	}
}
