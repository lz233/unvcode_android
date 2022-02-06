package org.apache.harmony.awt.gl;

import com.google.code.appengine.awt.Graphics2D;
import com.google.code.appengine.awt.font.GlyphVector;

public class NullTextRenderer extends TextRenderer {

	@Override
	public void drawString(Graphics2D g, String str, float x, float y) {
		
	}

	@Override
	public void drawGlyphVector(Graphics2D g, GlyphVector glyphVector, float x,
			float y) {
		
	}

}
