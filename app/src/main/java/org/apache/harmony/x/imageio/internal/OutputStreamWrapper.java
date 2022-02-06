package org.apache.harmony.x.imageio.internal;

import java.io.IOException;
import java.io.OutputStream;

import com.google.code.appengine.imageio.stream.ImageOutputStream;

public class OutputStreamWrapper extends OutputStream {
	
	private ImageOutputStream out;

	public OutputStreamWrapper(ImageOutputStream out) {
		super();
		this.out = out;
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}

	@Override
	public void close() throws IOException {
		//out.close();
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
	}
}
