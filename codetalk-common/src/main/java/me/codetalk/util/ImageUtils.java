package me.codetalk.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

public class ImageUtils {

	static final Map<String, Integer> HEX_MAP = new HashMap<String, Integer>();
	static {
		HEX_MAP.put("0", 0);
		HEX_MAP.put("1", 1);
		HEX_MAP.put("2", 2);
		HEX_MAP.put("3", 3);
		HEX_MAP.put("4", 4);
		HEX_MAP.put("5", 5);
		HEX_MAP.put("6", 6);
		HEX_MAP.put("7", 7);
		HEX_MAP.put("8", 8);
		HEX_MAP.put("9", 9);
		HEX_MAP.put("a", 10);
		HEX_MAP.put("A", 10);
		HEX_MAP.put("b", 11);
		HEX_MAP.put("B", 11);
		HEX_MAP.put("c", 12);
		HEX_MAP.put("C", 12);
		HEX_MAP.put("d", 13);
		HEX_MAP.put("D", 13);
		HEX_MAP.put("e", 14);
		HEX_MAP.put("E", 14);
		HEX_MAP.put("f", 15);
		HEX_MAP.put("F", 15);
	}

	/**
	 * 
	 * @param text
	 *            验证码
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param bgColorHex
	 *            背景颜色
	 * @param textColorHex
	 *            文字颜色
	 * @return
	 */
	public static String base64CodeImg(String code, int width, int height, String bgColorHex, String codeColorHex,
			String format) throws IOException {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D)img.getGraphics();

		g2d.setPaint(hexToColor(bgColorHex));
		g2d.fillRect(0, 0, width, height);
		g2d.setColor(hexToColor(codeColorHex));

		Font baseFont = new Font("Fixedsys", Font.PLAIN, 12);
		float maxFontSize = calcMaxFontSize(g2d, code, baseFont, width, height),	// 最大font大小
			minFontSize = maxFontSize * (maxFontSize > 30 ? 0.5f : (maxFontSize > 20 ? 0.65f : 0.8f)); 	// 最小font大小
		float midFontSize = (maxFontSize + minFontSize) / 2;
		
		int ttlw = g2d.getFontMetrics().stringWidth(code); // total width 总宽度
		
//		int leftw = width - ttlw, padw = ( leftw > 10 ? 5 : leftw / 2 ), padh = (int)(height * 0.1); // 水平 和 垂直padding
		int leftw = width - ttlw, padw = ( leftw > 10 ? 5 : leftw / 2 ), 
			padh = (int)(height - midFontSize) / 2; // 水平 和 垂直padding
//		System.out.println("padw = " + padw + ", padh = " + padh);
		int pw = (width - padw * 2) / code.length(), ph = height - padh * 2; // 每个字母的宽度 和 高度
//		System.out.println("pw = " + pw + ", ph = " + ph);
		int maxAngle = 45; // 最大旋转角度
		Random rand = new Random();
		for(int i = 0; i < code.length(); i++) {
			String s = code.substring(i, i + 1);
			
			int anchorx = padw + pw / 2 * (2 * i + 1), anchory = ph / 2 + padh;
			
			int tmpAngle = rand.nextInt(maxAngle), 
					tmp = rand.nextInt(100),				// 1 or -1 
					angle = tmpAngle * (tmp > 50 ? 1 : -1);
			g2d.setTransform(AffineTransform.getRotateInstance(Math.toRadians(angle), anchorx, anchory));
			
			int fs1 = (int)maxFontSize, fs2 = (int)minFontSize;
			float randomFontSize = (float)(rand.nextInt(fs1 - fs2 + 1) + fs2);
			g2d.setFont(baseFont.deriveFont(randomFontSize)); // min - max fontSize
			
			tmp = rand.nextInt(100);
			int x = padw + pw * i, 
//					y = height - rand.nextInt(padh * 2 + 1);
					y = height - padh + (int)(padh * 0.1 * (tmp > 50 ? 1 : -1));
			g2d.drawString(s, x, y);
//			System.out.println("char : " + s + ", x = " + x + ", y = " + y + 
//					", fontSize = " + randomFontSize + ", angle = " + angle + ", anchorx = " + anchorx 
//					+ ", anchory = " + anchory);
		}
		
		g2d.dispose();

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(img, format, os);

		return Base64.getEncoder().encodeToString(os.toByteArray());
	}

	private static float calcMaxFontSize(Graphics2D g2d, String text, Font baseFont, int width, int height) {
		int initialSize = Math.min(width, height);
		
		for(int i = 9; i > 0; i--) {
			float fontSize = initialSize * i * 0.1f;
			g2d.setFont(baseFont.deriveFont(fontSize));
			
			int tmpw = g2d.getFontMetrics().stringWidth(text);
//			System.out.println("fontSize = " + fontSize + ", tmpw = " + tmpw + ", width = " + width);
			
			if(tmpw <= width) return fontSize;
		}
		
		return 0f;
	}
	
	/**
	 * 
	 * @param colorHex
	 *            eg: #000000 #000 000000 000
	 * @return
	 */
	public static Color hexToColor(String colorHex) {
		if (colorHex.startsWith("#"))
			colorHex = colorHex.substring(1);

		int r = 0, g = 0, b = 0;
		int len = colorHex.length();
		if (len == 3 || len == 4) {
			int c0 = HEX_MAP.get(String.valueOf(colorHex.charAt(0))),
					c1 = HEX_MAP.get(String.valueOf(colorHex.charAt(1))),
					c2 = HEX_MAP.get(String.valueOf(colorHex.charAt(2)));

			r = c0 * 16 + c0;
			g = c1 * 16 + c1;
			b = c2 * 16 + c2;
			
			if(len == 4) {
				int c3 = HEX_MAP.get(String.valueOf(colorHex.charAt(3))), 
						a = c3 * 16 + c3;
				return new Color(r, g, b, a);
			}
			
			return new Color(r, g, b);
		} else if (len == 6 || len == 8) {
			r = HEX_MAP.get(String.valueOf(colorHex.charAt(0))) * 16 + HEX_MAP.get(String.valueOf(colorHex.charAt(1)));
			g = HEX_MAP.get(String.valueOf(colorHex.charAt(2))) * 16 + HEX_MAP.get(String.valueOf(colorHex.charAt(3)));
			b = HEX_MAP.get(String.valueOf(colorHex.charAt(4))) * 16 + HEX_MAP.get(String.valueOf(colorHex.charAt(5)));
			
			if(len == 8) {
				int a = HEX_MAP.get(String.valueOf(colorHex.charAt(6))) * 16 + HEX_MAP.get(String.valueOf(colorHex.charAt(7)));

				return new Color(r, g, b, a);
			}
			
			return new Color(r, g, b);
		}

		return null;
	}

	public static void main(String[] args) throws Exception {
//		String code = StringUtils.randomNum(5);
//		
//		System.out.println(base64CodeImg(code, 120, 40, "FFF", "333", "png"));
	}

}
