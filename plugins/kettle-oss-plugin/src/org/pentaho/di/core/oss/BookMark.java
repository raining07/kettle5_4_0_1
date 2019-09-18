package org.pentaho.di.core.oss;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.trans.steps.textfileinput.EncodingType;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputMeta;

public class BookMark implements Closeable {

	private static int DEFAULT_BUFFER_SIZE = 8192;

	private List<String> filenames; // 要读的所有文件名
	private int bufferSize;
	private int readFileIndex = 0; // 读到第几个文件(从0开始)
	private Map<String, Integer> readLinesMap = new HashMap<>(); // 读到第几行(从0开始)
	private InputStreamReader reader = null;
	private EncodingType encodingType;
	private int formatNr;

	public BookMark(List<String> filenames, int bufferSize) {
		super();
		this.filenames = filenames;
		if (bufferSize <= 0) {
			bufferSize = DEFAULT_BUFFER_SIZE;
		}
		this.bufferSize = bufferSize;
	}

	public List<String> getFilenames() {
		return filenames;
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

	/**
	 * 获取当前正在读的书
	 * 
	 * @return
	 */
	public String getCurrentBook() {
		return filenames.get(readFileIndex);
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

	/**
	 * 加行号
	 */
	private void addReadLine() {
		readLinesMap.put(getCurrentBook(), getCurrentReadLineIndex() + 1);
	}

	/**
	 * 加书的序号
	 */
	private void addBookIndex() {
		if (readFileIndex == filenames.size()) {
			// 书已读完
			return;
		}
		readFileIndex++;
	}

	public void openBook(InputStream content, String encoding, int formatNr) {
		reader = new InputStreamReader(new BufferedInputStream(content, bufferSize));
		encodingType = EncodingType.guessEncodingType(encoding);
		this.formatNr = formatNr;
	}

	public void closeBook() {
		if (reader != null) {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			reader = null;
		}
	}

	public String readLine() throws Exception {
		int c = 0;
		StringBuilder line = new StringBuilder(256);
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

		addBookIndex();
		return null;
	}

	@Override
	public void close() throws IOException {

	}

}
