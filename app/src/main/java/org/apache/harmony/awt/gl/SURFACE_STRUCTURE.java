/**
 * 
 */
package org.apache.harmony.awt.gl;

import com.google.code.appengine.awt.Transparency;
import com.google.code.appengine.awt.image.BufferedImage;
import com.google.code.appengine.awt.image.DataBuffer;

public class SURFACE_STRUCTURE
{
	private int ss_type;
	private int width;
	private int height;
	private int cm_type;
	private int cs_type;
	private int data_type;
	private int num_components;
	private int pixel_stride;
	private int scanline_stride;
	private int offset;
	private boolean has_alpha;
	private boolean isAlphaPre;
	private int transparency;
	private int scanline_stride_byte;
	private int red_mask;
	private int green_mask;
	private int blue_mask;
	private int alpha_mask;
	private int red_sht;
	private int max_red;
	private int green_sht;
	private int max_green;
	private int blue_sht;
	private int max_blue;
	private int alpha_sht;
	private int max_alpha;
	private int colormap_size;
	private int transparent_pixel;
	private boolean isGrayPallete;
	private int[] colormap;
	private int[] bits;
	private int[] bank_indexes;
	private int[] band_offsets;
	private boolean invalidated;
	private int bmp_byte_stride;
	private Object bmpData;
	private boolean hasRealAlpha;

	public SURFACE_STRUCTURE(int surfType, int width, int height, int cmType, 
		    int csType, int smType, int dataType, int numComponents, int pixelStride, 
		    int scanlineStride, int[] bits, int[] masks, int colorMapSize, 
		    int[] colorMap, int transpPixel, boolean isGrayPalette, int[] bankIndeces, 
		    int[] bandOffsets, int offset, boolean hasAlpha, boolean isAlphaPre, 
		    int transparency)
	{
            ss_type = surfType;
            this.width = width;
            this.height = height;
            cm_type = cmType;
            cs_type = csType;
            data_type = dataType;
            num_components = numComponents;
            pixel_stride = pixelStride;
            scanline_stride = scanlineStride;
            this.offset = offset;
            has_alpha = hasAlpha;
            this.isAlphaPre = isAlphaPre;
            this.transparency = transparency;

            if(dataType == DataBuffer.TYPE_BYTE){
                scanline_stride_byte = scanlineStride;
            }else if(dataType == DataBuffer.TYPE_USHORT){
                scanline_stride_byte = scanlineStride << 1;
            }else if(dataType == DataBuffer.TYPE_INT){
                scanline_stride_byte = scanlineStride << 2;
            }

            int i;

            switch(cmType){
                case Surface.DCM:
                    this.bits = new int[num_components];
                    for(i = 0; i < numComponents; i++){
                        this.bits[i] = bits[i];
                    }

                    red_mask = masks[i++];
                    green_mask = masks[i++];
                    blue_mask = masks[i++];
                    if(hasAlpha){
                        alpha_mask = masks[i];
                    }

                    red_sht = getShift(red_mask);
                    max_red = (1 << bits[0]) - 1;
                    green_sht = getShift(green_mask);
                    max_green = (1 << bits[1]) - 1;
                    blue_sht = getShift(blue_mask);
                    max_blue = (1 << bits[2]) - 1;
                    if(hasAlpha){
                        alpha_sht = getShift(alpha_mask);
                        max_alpha = ( 1 << bits[3]) - 1;
                    }
                    break;

                case Surface.ICM:
                    colormap_size = colorMapSize;
                    transparent_pixel = transpPixel;
                    isGrayPallete = isGrayPalette;
                    colormap = new int[colorMapSize];

                    for (i = 0; i < colorMapSize; ++i){
                    	colormap[i] = colorMap[i];
                    }
                    break;

                case Surface.CCM:
                    bank_indexes = new int[numComponents];
                    for (i = 0; i < numComponents; ++i){
                    	bank_indexes[i] = bankIndeces[i];
                    }
                    
                    band_offsets = new int[numComponents];
                    for (i = 0; i < numComponents; ++i){
                    	band_offsets[i] = bandOffsets[i];
                    }
                    break;
            }
            invalidated = true;
            bmp_byte_stride = width << 2;
            
            createBuffer();
            // TODO create the appropriate type based on ss_type
            bmpData = new byte[bmp_byte_stride  * height];
	}
	
	int getShift(int mask){
	    int shift = 0;
	    if (mask != 0) {
	        while ((mask & 1) == 0) {
	            mask >>= 1;
	            shift++;
	        }
	    }
	    return shift;
	}

	public void setImageSize(int width, int height)
	{
	    scanline_stride = scanline_stride / width * width;
	    scanline_stride_byte = scanline_stride_byte / width * width;
	    this.width = width;
	    this.height = height;
	}

	public void dispose()
	{
        bits = null;
        colormap = null;
        bank_indexes = null;
        band_offsets = null;
        bmpData = null;
	}

	public Object updateCache(Object data, boolean alphaPre)
	{
	    updateCache(data, alphaPre, 0, 0, width, height);
	    return bmpData;
	}
	
	void updateCache(Object srcData, boolean alphaPre, int x, int y, int width, int height)
	{
	    int src_stride, dst_stride;
	    int sidx, didx;
	    int h = height;
	    int w = width;


	    switch(ss_type){

	        case BufferedImage.TYPE_INT_RGB:
	            {
	                int[] src, dst;

	                src_stride = scanline_stride;
	                dst_stride = width;

	                sidx = y * src_stride + x;
	                didx = y * dst_stride + x;
	                src = (int[]) srcData;
	                dst = (int[]) bmpData;

	                for(int _y = 0; _y < h; _y++, sidx += src_stride, didx += dst_stride){
	                	int s = sidx;
	                	int d = didx;
	                    for(int _x = 0; _x < w; _x++){
	                        dst[d++] = 0xff000000 | src[s++];
	                    }
	                }
	            }
	            break;

	        case BufferedImage.TYPE_INT_ARGB:
	            {
	                if(alphaPre){
	                	byte[] src, dst;
	                	byte sa;

	                    src_stride = scanline_stride_byte;
	                    dst_stride = width << 2;

	                    sidx = y * src_stride + ((x + w) << 2) - 1;
	                    didx = y * dst_stride + ((x + w) << 2) - 1;
	                    src = (byte[])srcData;
	                    dst = (byte[])bmpData;

	                    for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                        int s = sidx;
	                        int d = didx;

	                        for(int _x = w; _x > 0; _x--){
	                            sa = src[s--];
	                            dst[d--] = sa;
	                            if(sa != 255){
	                            	dst[d--] = LUTTables.MUL(sa, src[s--]);
	                            	dst[d--] = LUTTables.MUL(sa, src[s--]);
	                            	dst[d--] = LUTTables.MUL(sa, src[s--]);
	                                hasRealAlpha = true;
	                            }else{
	                            	dst[d--] = src[s--];
	                            	dst[d--] = src[s--];
	                            	dst[d--] = src[s--];
	                            }
	                        }
	                    }

	                    isAlphaPre = true;
	                }else{
	                    int[] src, dst;

	                    src_stride = scanline_stride;
	                    dst_stride = width;

	                    sidx = y * src_stride + x;
	                    didx = y * dst_stride + x;
	                    src = (int[])srcData;
	                    dst = (int[])bmpData;

	                    for(int _y = 0; _y < h; _y++, sidx += src_stride, didx += dst_stride){
	                    	int d = didx;
	                    	int s = sidx;
	                    	for (int _x = 0; _x < w; _x++){
	                    		dst[d++] = src[s++];
	                    	}
	                    }

	                    isAlphaPre = false;
	                }
	            }
	            break;

	        case BufferedImage.TYPE_INT_ARGB_PRE:
	            {
	                byte[] src, dst;
	                byte sa;

	                src_stride = scanline_stride_byte;
	                dst_stride = width << 2;

	                sidx = y * src_stride + ((x + w) << 2) - 1;
	                didx = y * dst_stride + ((x + w) << 2) - 1;
	                src = (byte[])srcData;
	                dst = (byte[])bmpData;

	                if(alphaPre){
	                    for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                        int s = sidx;
	                        int d = didx;

	                        for(int _x = w; _x > 0; _x--){
	                            sa = src[s--];
	                            dst[d--] = sa;
	                            dst[d--] = src[s--];
	                            dst[d--] = src[s--];
	                            dst[d--] = src[s--];
	                            if(sa != 255){
	                                hasRealAlpha = true;
	                            }
	                        }
	                    }
	                    isAlphaPre = true;
	                }else{
	                    for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                        int s = sidx;
	                        int d = didx;

	                        for(int _x = w; _x > 0; _x--){
	                            sa = src[s--];
	                            dst[d--] = sa;
	                            dst[d--] = LUTTables.DIV(sa, src[s--]);
	                            dst[d--] = LUTTables.DIV(sa, src[s--]);
	                            dst[d--] = LUTTables.DIV(sa, src[s--]);
	                        }
	                    }
	                    isAlphaPre = false;
	                }
	            }
	            break;

	        case BufferedImage.TYPE_INT_BGR:
	            {
	                byte[] src, dst;

	                src_stride = scanline_stride_byte;
	                dst_stride = width << 2;

	                sidx = y * src_stride + ((x + w) << 2) - 1;
	                didx = y * dst_stride + ((x + w) << 2) - 1;
	                src = (byte[])srcData;
	                dst = (byte[])bmpData;

	                for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                    int s = sidx;
	                    int d = didx;

	                    for(int _x = w; _x > 0; _x--){
	                        dst[d] = (byte)255;
	                        s--;
	                        dst[(d - 3)] = src[s--];
	                        dst[(d - 2)] = src[s--];
	                        dst[(d - 1)] = src[s--];
	                        d -= 4;
	                    }
	                }
	            }
	            break;

	        case BufferedImage.TYPE_3BYTE_BGR:
	            {
	                byte[] src, dst;

	                src_stride = scanline_stride_byte;
	                dst_stride = width << 2;

	                sidx = y * src_stride + (x + w) * 3 - 1;
	                didx = y * dst_stride + ((x + w) << 2) - 1;
	                src = (byte[])srcData;
	                dst = (byte[])bmpData;

	                for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                    int s = sidx;
	                    int d = didx;

	                    for(int _x = w; _x > 0; _x--){
	                        dst[d--] = (byte) 255;
	                        dst[d--] = src[s--];
	                        dst[d--] = src[s--];
	                        dst[d--] = src[s--];
	                    }
	                }
	            }
	            break;

	        case BufferedImage.TYPE_4BYTE_ABGR:
	            {
	                byte[] src, dst;
	                byte a, r, g, b;

	                src_stride = scanline_stride_byte;
	                dst_stride = width << 2;

	                sidx = y * src_stride + ((x + w) << 2) - 1;
	                didx = y * dst_stride + ((x + w) << 2) - 1;
	                src = (byte[])srcData;
	                dst = (byte[])bmpData;

	                if(alphaPre){
	                    for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                        int s = sidx;
	                        int d = didx;

	                        for(int _x = w; _x > 0; _x--){
	                            r = src[s--];
	                            g = src[s--];
	                            b = src[s--];
	                            a = src[s--];
	                            dst[d--] = a;
	                            if(a != 255){
	                                dst[d--] = LUTTables.MUL(a, r);
	                                dst[d--] = LUTTables.MUL(a, g);
	                                dst[d--] = LUTTables.MUL(a, b);
	                                hasRealAlpha = true;
	                            }else{
	                                dst[d--] = r;
	                                dst[d--] = g;
	                                dst[d--] = b;
	                            }
	                        }
	                    }
	                    isAlphaPre = true;
	                }else{
	                    for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                        int s = sidx;
	                        int d = didx;

	                        for(int _x = w; _x > 0; _x--){
	                            r = src[s--];
	                            g = src[s--];
	                            b = src[s--];
	                            a = src[s--];
	                            dst[d--] = a;
	                            dst[d--] = r;
	                            dst[d--] = g;
	                            dst[d--] = b;
	                        }
	                    }
	                    isAlphaPre = false;
	                }
	            }
	            break;

	        case BufferedImage.TYPE_4BYTE_ABGR_PRE:
	            {
	                byte[] src, dst;
	                byte a, r, g, b;

	                src_stride = scanline_stride_byte;
	                dst_stride = width << 2;

	                sidx = y * src_stride + ((x + w) << 2) - 1;
	                didx = y * dst_stride + ((x + w) << 2) - 1;
	                src = (byte[])srcData;
	                dst = (byte[])bmpData;

	                if(alphaPre){
	                    for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                        int s = sidx;
	                        int d = didx;
	 
	                        for(int _x =  w; _x > 0; _x--){
	                            r = src[s--];
	                            g = src[s--];
	                            b = src[s--];
	                            a = src[s--];
	                            if(a != 255){
	                                hasRealAlpha = true;
	                            }
	                            dst[d--] = a;
	                            dst[d--] = r;
	                            dst[d--] = g;
	                            dst[d--] = b;
	                        }
	                    }
	                    isAlphaPre = true;
	                }else{
	                    for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                        int s = sidx;
	                        int d = didx;
	  
	                        for(int _x = w; _x > 0; _x--){
	                            r = src[s--];
	                            g = src[s--];
	                            b = src[s--];
	                            a = src[s--];
	                            dst[d--] = a;
	                            if(a != 255){
	                                dst[d--] = LUTTables.DIV(a, r);
	                                dst[d--] = LUTTables.DIV(a, g);
	                                dst[d--] = LUTTables.DIV(a, b);
	                            }else{
	                                dst[d--] = r;
	                                dst[d--] = g;
	                                dst[d--] = b;
	                            }
	                        }
	                    }
	                    isAlphaPre = false;
	                }
	            }
	            break;

	        case BufferedImage.TYPE_USHORT_555_RGB:
	        case BufferedImage.TYPE_USHORT_565_RGB:
	            {
	                byte[] dst;
	                short[] src;
	                short pixel;

	                int mr = max_red;
	                int mg = max_green;
	                int mb = max_red;
	                int rm = red_mask;
	                int gm = green_mask;
	                int bm = blue_mask;
	                int rs = red_sht;
	                int gs = green_sht;
	                int bs = blue_sht;

	                src_stride = scanline_stride;
	                dst_stride = width << 2;

	                sidx = y * src_stride + x + w - 1;
	                didx = y * dst_stride + ((x + w) << 2) - 1;
	                src = (short[])srcData;
	                dst = (byte[])bmpData;

	                for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                    int d = didx;
	                    int s = sidx;
	                    for(int _x = w; _x > 0; _x--){
	                        pixel = src[s--];
	                        dst[d--] = (byte) 255;
	                        dst[d--] = LUTTables.DIV(mb, ((pixel & rm) >> rs));
	                        dst[d--] = LUTTables.DIV(mg, ((pixel & gm) >> gs));
	                        dst[d--] = LUTTables.DIV(mr, ((pixel & bm) >> bs));
	                    }
	                }
	            }
	            break;

	        case BufferedImage.TYPE_USHORT_GRAY:
	            {
	                byte[]dst;
	                byte pixel;
	                short[] src;

	                src_stride = scanline_stride;
	                dst_stride = width << 2;

	                sidx = y * src_stride + (x << 1);
	                didx = y * dst_stride + (x << 2);
	                src = (short[])srcData;
	                dst = (byte[])bmpData;

	                for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                    int s = sidx;
	                    int d = didx;
	                    for(int _x =  w; _x > 0; _x--){
	                        pixel = (byte)(src[s++] / 257);
	                        dst[d++] = pixel;
	                        dst[d++] = pixel;
	                        dst[d++] = pixel;
	                        dst[d++] = (byte) 255;
	                    }
	                }
	            }
	            break;

	        case BufferedImage.TYPE_BYTE_BINARY:
	            {
	                byte[] src;
	                int[] dst;
	                int pixel, bitnum, elem, shift, bitMask;

	                int pixelBits = pixel_stride;
	                int[] cm = colormap;

	                src_stride = scanline_stride;
	                dst_stride = width;

	                sidx = y * src_stride;
	                didx = y * dst_stride + x;
	                src = (byte[])srcData;
	                dst = (int[])bmpData;

	                for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                    int d = didx;

	                    for(int _x = 0; _x < w; _x++){
	                        bitnum = _x * pixelBits;
	                        int s = bitnum / 8;
	                        elem = src[s];
	                        shift = 8 - (bitnum & 7) - pixelBits;
	                        bitMask = (1 << pixelBits) - 1;
	                        pixel = (elem >> shift) & bitMask;
	                        dst[d++] = 0xff000000 | (cm[pixel]);
	                    }
	                }
	            }
	            break;

	        case BufferedImage.TYPE_BYTE_INDEXED:
	            {
	                int transparency = this.transparency;
	                byte[] src;
	                int[] dst;
	                int pixel, r, g, b, a;
	                int[] cm = colormap;
	                int tp = transparent_pixel;

	                src_stride = scanline_stride;
	                dst_stride = width;

	                sidx = y * src_stride + x + w - 1;
	                didx = y * dst_stride + x + w - 1;
	                src = (byte[])srcData;
	                dst = (int[])bmpData;


	                if(transparency == Transparency.OPAQUE){
	                    for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                        int s = sidx;
	                        int d = didx;

	                        for(int _x = w; _x > 0; _x--){
	                            dst[d--] = 0xff000000 | (cm[src[s--]]);
	                        }
	                    }
	                }else if(transparency == Transparency.BITMASK){
	                    for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                        int s = sidx;
	                        int d = didx;

	                        for(int _x = w; _x > 0; _x--){
	                            pixel = src[s--];
	                            if(pixel != tp){
	                                dst[d--] = 0xff000000 | (cm[pixel]);
	                            }else{
	                                hasRealAlpha = true;
	                                dst[d--] = 0;
	                            }
	                        }
	                    }
	                }else{
	                    for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                        int s = sidx;
	                        int d = didx;

	                        for(int _x = w; _x > 0; _x--){
	                            pixel = (cm[src[s--]]);
	                            a = (pixel >> 24) & 0xff;
	                            if(alphaPre){
	                                if(a == 255){
	                                    dst[d--] = pixel;
	                                }else{
	                                    r = (pixel >> 16) & 0xff;
	                                    g = (pixel >> 8) & 0xff;
	                                    b = pixel & 0xff;
	                                    r = LUTTables.MUL(a, r);
	                                    g = LUTTables.MUL(a, g);
	                                    b = LUTTables.MUL(a, b);
	                                    dst[d--] = (a << 24) | (r << 16) | (g << 8) | b;
	                                }
	                                isAlphaPre = true;
	                            }else{
	                                if(a == 0) dst[d--] = 0;
	                                else dst[d--] = pixel;
	                                isAlphaPre = false;
	                            }
	                        }
	                    }
	                }
	            }
	            break;

	        case BufferedImage.TYPE_BYTE_GRAY:
	            {
	                byte[] src, dst;
	                byte pixel;
	                src_stride = scanline_stride;
	                dst_stride = width << 2;

	                sidx = y * src_stride + x;
	                didx = y * dst_stride + (x << 2);
	                src = (byte[])srcData;
	                dst = (byte[])bmpData;

	                for(int _y = h; _y > 0; _y--, sidx += src_stride, didx += dst_stride){
	                    int s = sidx;
	                    int d = didx;

	                    for(int _x = w; _x > 0; _x--){
	                        pixel = src[s++];
	                        dst[d++] = pixel;
	                        dst[d++] = pixel;
	                        dst[d++] = pixel;
	                        dst[d++] = (byte) 255;
	                    }
	                }
	            }
	            break;
	    }
	}
	
	void createBuffer()
	{
	    switch(ss_type){

	        case BufferedImage.TYPE_INT_RGB:
	            bmpData = new int[1];
	            break;

	        case BufferedImage.TYPE_INT_ARGB:
                if(isAlphaPre){
                    bmpData = new byte[1];
                }else{
                    bmpData = new int[1];
                }
	            break;

	        case BufferedImage.TYPE_INT_ARGB_PRE:
	            bmpData = new byte[1];
	            break;

	        case BufferedImage.TYPE_INT_BGR:
	            bmpData = new byte[1];
	            break;

	        case BufferedImage.TYPE_3BYTE_BGR:
	            bmpData = new byte[1];
	            break;

	        case BufferedImage.TYPE_4BYTE_ABGR:
	            bmpData = new byte[1];
	            break;

	        case BufferedImage.TYPE_4BYTE_ABGR_PRE:
	            bmpData = new byte[1];
	            break;

	        case BufferedImage.TYPE_USHORT_555_RGB:
	        case BufferedImage.TYPE_USHORT_565_RGB:
	            bmpData = new byte[1];
	            break;

	        case BufferedImage.TYPE_USHORT_GRAY:
	            bmpData = new byte[1];
	            break;

	        case BufferedImage.TYPE_BYTE_BINARY:
	            bmpData = new int[1];
	            break;

	        case BufferedImage.TYPE_BYTE_INDEXED:
	            bmpData = new int[1];
	            break;

	        case BufferedImage.TYPE_BYTE_GRAY:
	            bmpData = new byte[1];
	            break;
	    }
	}
}
