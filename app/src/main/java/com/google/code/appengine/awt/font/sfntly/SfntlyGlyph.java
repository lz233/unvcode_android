package com.google.code.appengine.awt.font.sfntly;

import com.google.code.appengine.awt.Shape;
import com.google.code.appengine.awt.font.GlyphMetrics;
import com.google.code.appengine.awt.geom.GeneralPath;
import com.google.code.appengine.awt.geom.Rectangle2D;
import com.google.typography.font.sfntly.table.truetype.Glyph;
import com.google.typography.font.sfntly.table.truetype.SimpleGlyph;

public class SfntlyGlyph extends org.apache.harmony.awt.gl.font.Glyph {

	private Glyph glyph;
	private float scale;
	private int yMax;
	private int yMin;
	private int fontSize;

	public SfntlyGlyph(double advance, double width, double height, Glyph glyph) {
		this.glPointMetrics = new GlyphMetrics((float) advance, 
				new Rectangle2D.Double(0,0,width,height), GlyphMetrics.STANDARD);
		
		this.glMetrics = new GlyphMetrics((float) advance, 
				new Rectangle2D.Double(0,0,width,height), GlyphMetrics.STANDARD);
		this.glyph = glyph;
	}
	
	void setScale(float scale) {
		this.scale = scale;
	}

	void setYBounds(int min, int max) {
		this.yMin = min;
		this.yMax = max;
	}
	
	void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}


	void setChar(char ch) {
		this.glChar = ch;
	}

	@Override
	public byte[] getBitmap() {
		throw new UnsupportedOperationException();
//		return null;
	}

	@Override
	public Shape initOutline(char c) {
		GeneralPath path = new GeneralPath();
		if(glyph instanceof SimpleGlyph) {
			SimpleGlyph simpleGlyph = (SimpleGlyph) glyph;
			int numContours = simpleGlyph.numberOfContours();
			for(int iContour=0;iContour!=numContours;++iContour) {
				int numPoints = simpleGlyph.numberOfPoints(iContour);
				for(int iPoint=0;iPoint!=numPoints;++iPoint) {
					int x = simpleGlyph.xCoordinate(iContour, iPoint);
					int y = simpleGlyph.yCoordinate(iContour, iPoint);
					float fx = scale * x;
					float fy = - (scale * y) - fontSize;
					if(iPoint == 0) {
						path.moveTo(fx, fy);
					} else {
						path.lineTo(fx, fy);
					}
				}
			}
		}
		
		return path;
	}

}
