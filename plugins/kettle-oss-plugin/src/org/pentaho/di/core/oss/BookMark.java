package org.pentaho.di.core.oss;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.pentaho.di.trans.steps.textfileinput.EncodingType;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputMeta;

public class BookMark implements Closeable {

	private static int DEFAULT_BUFFER_SIZE = 8192;

	private List<String> bookNames; // 要读的所有文件名
	private int bufferSize;
	private int readFileIndex = 0; // 读到第几个文件(从0开始)
	private Map<String, Integer> readLinesMap = new HashMap<String, Integer>(); // 读到第几行(从0开始)
	private Map<String, Boolean> readedMap = new HashMap<String, Boolean>(); // 阅读标记
	private InputStreamReader reader = null;
	private EncodingType encodingType;
	private int formatNr;
	private boolean needNextBook = true;
	private StringBuilder line = new StringBuilder(256);

	public BookMark(List<String> bookNames, int bufferSize) {
		super();
		this.bookNames = bookNames;
		if (bufferSize <= 0) {
			bufferSize = DEFAULT_BUFFER_SIZE;
		}
		this.bufferSize = bufferSize;
		for (String bookName : bookNames) {
			readedMap.put(bookName, false);
		}
	}

	public List<String> getBookNames() {
		return bookNames;
	}

	public int getReadFileIndex() {
		return readFileIndex;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public int getFormatNr() {
		return formatNr;
	}

	public InputStreamReader getReader() {
		return reader;
	}

	public boolean isNeedNextBook() {
		return needNextBook;
	}

	public Map<String, Integer> getReadLinesMap() {
		return readLinesMap;
	}

	/**
	 * 获取当前正在读的书
	 * 
	 * @return
	 */
	public String getCurrentBook() {
		return bookNames.get(readFileIndex);
	}

	/**
	 * 获取当前正在读的行数
	 * 
	 * @return
	 */
	public int getCurrentReadLineIndex() {
		Integer index = readLinesMap.get(getCurrentBook());
		if (index == null) {
			return 0;
		}
		return index;
	}

	public void openBook(InputStream content, String encoding, int formatNr) throws UnsupportedEncodingException {
		if (StringUtils.isEmpty(encoding)) {
			reader = new InputStreamReader(new BufferedInputStream(content, bufferSize));
		} else {
			reader = new InputStreamReader(new BufferedInputStream(content, bufferSize), encoding);
		}

		encodingType = EncodingType.guessEncodingType(encoding);
		this.formatNr = formatNr;
	}

	public void closeBook() {
		// 书已读完
		if (readFileIndex < this.bookNames.size()) {
			System.out.println("已读完: " + getCurrentBook());
			readedMap.put(getCurrentBook(), true);
		}
		if (reader != null) {
			try {
				reader.close();
			} catch (Exception e) {
				// do nothing
			}
			reader = null;
		}

	}

	public String readLine() throws Exception {
		if (this.needNextBook = true) {
			// 不需要换下一本书
			this.needNextBook = false;
		}
		int c = 0;
		line.setLength(0);
		try {
			switch (formatNr) {
			case TextFileInputMeta.FILE_FORMAT_DOS:
				while (c >= 0) {
					c = reader.read();

					if (encodingType.isReturn(c) || encodingType.isLinefeed(c)) {
						c = reader.read(); // skip \n and \r
						if (!encodingType.isReturn(c) && !encodingType.isLinefeed(c)) {
							// make sure its really a linefeed or cariage return
							// raise an error this is not a DOS file
							// so we have pulled a character from the next line
							throw new Exception(
									"DOS format was specified but only a single line feed character was found, not 2");
						}
						addReadLine();
						return line.toString();
					}
					if (c >= 0) {
						line.append((char) c);
					}
				}
				break;
			case TextFileInputMeta.FILE_FORMAT_UNIX:
				while (c >= 0) {
					c = reader.read();

					if (encodingType.isLinefeed(c) || encodingType.isReturn(c)) {
						addReadLine();
						return line.toString();
					}
					if (c >= 0) {
						line.append((char) c);
					}
				}
				break;
			case TextFileInputMeta.FILE_FORMAT_MIXED:
				// in mixed mode we suppose the LF is the last char and CR is ignored
				// not for MAC OS 9 but works for Mac OS X. Mac OS 9 can use UNIX-Format
				while (c >= 0) {
					c = reader.read();

					if (encodingType.isLinefeed(c)) {
						addReadLine();
						return line.toString();
					} else if (!encodingType.isReturn(c)) {
						if (c >= 0) {
							line.append((char) c);
						}
					}
				}
				break;
			default:
				break;
			}
		} catch (Exception e) {
			if (line.length() == 0) {
				throw e;
			}
			addReadLine();
			return line.toString();
		}
		if (line.length() > 0) {
			addReadLine();
			return line.toString();
		}
		// 读完书换下一本
		closeBook();
		nextBook();

		return null;
	}

	/**
	 * 加行号
	 */
	private void addReadLine() {
		readLinesMap.put(getCurrentBook(), getCurrentReadLineIndex() + 1);
	}

	/**
	 * 加书的序号
	 */
	private void nextBook() {
		if (readFileIndex == bookNames.size()) {
			return;
		}
		readFileIndex++;
		this.needNextBook = true;
	}

	@Override
	public void close() throws IOException {
		closeBook();
	}

	public boolean hasBooks() {
		return !hasNoBooks();
	}

	public boolean hasNoBooks() {
		return CollectionUtils.isEmpty(this.bookNames);
	}

	/**
	 * 所有书都已读
	 * 
	 * @return
	 */
	public boolean allReaded() {
		for (Boolean readed : readedMap.values()) {
			if (!readed) {
				return false;
			}
		}
		return true;
	}
}
