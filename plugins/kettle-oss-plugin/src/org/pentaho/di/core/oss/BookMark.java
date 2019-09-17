package org.pentaho.di.core.oss;

import java.util.List;

public class BookMark {

	private List<String> filenames; // 要读的所有文件名
	private int readFileIndex = 0; // 读到第几个文件(从0开始)
	private int readLineIndex = 0; // 读到第几行(从0开始)

	public BookMark(List<String> filenames) {
		super();
		this.filenames = filenames;
	}

	public String readLine() {
		readLineIndex++;
		return null;
	}
}
