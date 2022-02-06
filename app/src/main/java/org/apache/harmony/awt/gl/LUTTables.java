package org.apache.harmony.awt.gl;

public class LUTTables
{
	private static final byte[][] mulLUT = new byte[256][256];  /* Multiply Lookup table */
	private static final byte[][] divLUT = new byte[256][256];  /* Divide Lookup table   */

	static {
		init_mulLUT();
		init_divLUT();
	}
	
	private static void init_mulLUT(){
	    int i, j;
	    for(i = 0; i < 256; i++){
	        for(j = 0; j < 256; j++){
	            mulLUT[i][j] = (byte)(((float)i * j) / 255 + 0.5);
	        }
	    }
	}

	private static void init_divLUT(){
	    int i, j;
//	    memset(divLUT[0], 0, 256);
	    for(i = 1; i < 256; i++){
	        for(j = 0; j <= i; j++){
	            divLUT[i][j] = (byte)(((float)j) / i * 255 + 0.5);
	        }
	        for(; j < 256; j++){
	            divLUT[i][j] = 0;
	        }
	    }
	}

	public static byte MUL(int a, int b)
	{
		return mulLUT[a][b];
	}

	public static byte DIV(int a, int b)
	{
		return divLUT[a][b];
	}
}
