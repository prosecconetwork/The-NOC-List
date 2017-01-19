package twitterbotics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;


public class VennDiagram extends Frame {

    private String metaphor1, metaphor2, intersection1, intersection2;
    private Color colorLeft,colorRight,colorMixed;

    
    private static int WIDTH=1000, HEIGHT=700, HEIGHT_DIFF = 100;
    private static final int RADIUS=200;
    private static final int INTERSECTION_LENGTH=120;

    private static final int X1= WIDTH/2 - (6*RADIUS)/6 - INTERSECTION_LENGTH;
    private static       int Y1= HEIGHT/2 - (4*RADIUS)/4;
    private static final int X2 = WIDTH/2 - (6*RADIUS)/6 + INTERSECTION_LENGTH;
    private static       int Y2 = HEIGHT/2 - (4*RADIUS)/4;
    
    private static final int XM = WIDTH/2;
    private static final int Xd = 0;

    private static final int LINE_START=40;
    private static final int LINE_END=200;
    private static final int POINTS_R=5;

    private static final int LONG_STRING = 20;
    private static final int FONT_SIZE = 30;

    private static final Font FONT = new Font("Lucida", Font.BOLD, FONT_SIZE);
    private static Color LINE_COLOR = Color.black;
    
    private static int STROKE_THICKNESS = 6;
    private static int BALL_DIAMETER = 5;
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Constructors
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    public VennDiagram(String meta1, String meta2, String inter) {
        metaphor1 = meta1;
        metaphor2 = meta2;
        intersection1 = inter;
        prepareFrame();
        setRandomColor();
    }
    
    public VennDiagram(String meta1, String meta2, String inter1, String inter2) {
        metaphor1 = meta1;
        metaphor2 = meta2;
        intersection1 = inter1;
        intersection2 = inter2;
        prepareFrame();
        setRandomColor();
    }

 
    public void setRandomColor() {
        int r = (int)(Math.random() * 8);
        switch (r) {
            case 0:
                setColor(Color.yellow,Color.lightGray);
                break;
            case 1:
                setColor(Color.orange,Color.red);
                break;
            case 2:
                setColor(Color.orange,Color.magenta);
                break;
            case 3:
                setColor(Color.cyan,Color.orange);
                break;
            case 4:
                setColor(Color.lightGray,Color.pink);
                break;
            case 5:
                setColor(Color.white,Color.lightGray);
                break;
            case 6:
                setColor(Color.yellow,Color.cyan);
                break;
            default:
                setColor(Color.cyan,Color.yellow);
        }
    }

    
    private void prepareFrame() {
    	if (intersection2 == null)
    		HEIGHT = HEIGHT-HEIGHT_DIFF;
    	else
    	if (breakStrings(intersection1, LONG_STRING).size() > 3)
    	{
    		HEIGHT = HEIGHT+HEIGHT_DIFF;
    		Y1     = Y1 + HEIGHT_DIFF/2;
       		Y2     = Y2 + HEIGHT_DIFF/2;
       	}
    	
    	if (intersection2 != null && breakStrings(intersection2, LONG_STRING).size() > 3)
    	{
    		HEIGHT = HEIGHT+HEIGHT_DIFF/2;
    		Y1     = Y1 + HEIGHT_DIFF/4;
       		Y2     = Y2 + HEIGHT_DIFF/4;
       	}
    	
  		setSize(WIDTH, HEIGHT);   		
    }
    
    
    public void paint(Graphics g) {

        // Setting white background

        g.setColor(Color.WHITE);
        g.fillRect(0,0, WIDTH, HEIGHT);

        g.setColor(colorLeft);
        g.fillOval(X1, Y1, RADIUS*2, RADIUS*2);

        // Right circle
        g.setColor(colorRight);
        g.fillOval(X2, Y2, RADIUS*2, RADIUS*2);

        // Intersection
        g.setColor(colorMixed);
        // Looping over all pixels, finding those which lie in both circles and coloring them
        for (int x=0; x<WIDTH; x++) {
            for (int y=0; y<HEIGHT; y++) {
                if ( Math.hypot(x-(X1+RADIUS), y-(Y1+RADIUS)) < RADIUS &&
                        Math.hypot((x-(X2+RADIUS)), y-(Y2+RADIUS)) < RADIUS) {
                    g.fillRect(x,y,1,1);
                }
            }
        }

        // Line colors
        g.setColor(LINE_COLOR);
        
        // Little balls at end of line

        g.fillOval(X1 + (RADIUS-LINE_START-POINTS_R), Y1 + (RADIUS-LINE_START-POINTS_R), POINTS_R*BALL_DIAMETER, POINTS_R*BALL_DIAMETER);
        g.fillOval(X2 + (RADIUS+(LINE_START-POINTS_R))-BALL_DIAMETER*2, Y2 + (RADIUS-LINE_START-POINTS_R), POINTS_R*BALL_DIAMETER, POINTS_R*BALL_DIAMETER);
        g.fillOval(XM-POINTS_R - 3*BALL_DIAMETER/2, (HEIGHT/2 - LINE_START-POINTS_R), POINTS_R*BALL_DIAMETER, POINTS_R*BALL_DIAMETER);

        
        // Graphics2D package needed for thicker lines
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(STROKE_THICKNESS));

        g2.draw(new Line2D.Float(X1 + (RADIUS-LINE_START), Y1 + (RADIUS-LINE_START), X1 + (RADIUS-LINE_END), Y1 + (RADIUS-LINE_END) + 5));
        g2.draw(new Line2D.Float(X2 + (RADIUS+LINE_START) + 3, Y2 + (RADIUS-LINE_START), X2 + (RADIUS+LINE_END), Y2 + (RADIUS-LINE_END) + 5));
        g2.draw(new Line2D.Float(XM, (HEIGHT/2 - LINE_START), XM+Xd, Y1 + (RADIUS-LINE_END-30)));

        // Drawing circumference of circle

        g2.setStroke(new BasicStroke((STROKE_THICKNESS)));
        g2.drawOval(X1,Y1,RADIUS*2, RADIUS*2);
        g2.drawOval(X2, Y2, RADIUS*2, RADIUS*2);

        this.drawStrings(g);
    }

    
    private void drawStrings(Graphics g) {
        // Rough estimate of length of image of string

        g.setFont(FONT);

        // longest string len = 29
        // if string longer than this, then use newline breaks

        int lenMeta1 = FONT_SIZE/2 * metaphor1.length();
        int lenInter1 = FONT_SIZE/2 * intersection1.length();


        ArrayList<String> meta1Strings = breakStrings(metaphor1, LONG_STRING/2);

        int xPlace = (FONT_SIZE/2 * meta1Strings.get(meta1Strings.size()-1).length()) + FONT_SIZE;

        for (int i=0; i<meta1Strings.size(); i++) 
        {
            g.drawString(meta1Strings.get(i), Math.max(20, X1 - xPlace), Y1 - (FONT_SIZE * (meta1Strings.size()-i)) + (FONT_SIZE-5));
        }

        ArrayList<String> meta2Strings = breakStrings(metaphor2, LONG_STRING/2);
        
        
        for (int i=0; i<meta2Strings.size(); i++) 
        {
        	int labelSize = g.getFontMetrics().stringWidth(meta2Strings.get(i));
        	
            g.drawString(meta2Strings.get(i), Math.min(WIDTH-labelSize-10, X2 + RADIUS*2), Y2 - (FONT_SIZE * (meta2Strings.size()-i)) + (FONT_SIZE-5));
        }

        ArrayList<String> inter1Strings = breakStrings(intersection1, LONG_STRING);

        xPlace = (FONT_SIZE/2 * LONG_STRING) / 2;
        for (int i=0; i<inter1Strings.size(); i++) 
        {
        	int labelSize = g.getFontMetrics().stringWidth(inter1Strings.get(i));
        	
            g.drawString(inter1Strings.get(i), XM + Xd - labelSize/2, Y1 - (FONT_SIZE * (inter1Strings.size() - i)) - (FONT_SIZE-10));
        }

        Graphics2D g2 = (Graphics2D) g;

        if (intersection2 != null) {

            g.fillOval(XM-POINTS_R- 3*BALL_DIAMETER/2, (HEIGHT/2 + LINE_START-POINTS_R), POINTS_R*BALL_DIAMETER, POINTS_R*BALL_DIAMETER);
            g2.draw(new Line2D.Float(XM, (HEIGHT/2 + LINE_START), XM - Xd, Y1 + (RADIUS+LINE_END+30)));

            ArrayList<String> inter2Strings = breakStrings(intersection2, LONG_STRING);
            
            xPlace = (FONT_SIZE/2 * inter2Strings.get(inter2Strings.size()-1).length()) / 2;
            for (int i=0; i< inter2Strings.size(); i++) 
            {
              	int labelSize = g.getFontMetrics().stringWidth(inter2Strings.get(i));
            	
                g.drawString(inter2Strings.get(i), XM - Xd - labelSize/2, Y1 + RADIUS*2 + (i * FONT_SIZE) + 65);
            }
        }
    }

    
    
    static ArrayList<String> breakStrings(String string, int maxLen) {

        ArrayList<String> brokenStrings = new ArrayList<String>();

        String[] spaceSplit = string.split(" ");
        int len = 0;
        StringBuffer sb = new StringBuffer();
        
        for(String str:spaceSplit) {
            if (sb.length()+str.length() < maxLen) {
                if(sb.length() > 0)
                    sb.append(" ");
                sb.append(str);
            }
            else if(str.length() > maxLen) { //sb + str > LS
                brokenStrings.add(sb.toString());
                brokenStrings.add(str);
                sb = new StringBuffer();
            }
            else {
                brokenStrings.add(sb.toString());
                sb = new StringBuffer(str);
            }
        }

        if(sb.length() > 0)
            brokenStrings.add(sb.toString());

        return brokenStrings;
    }
    
    

    public void setColor(Color color1, Color color2) {
        double totalAlpha = color1.getAlpha() + color2.getAlpha();
        double w1 = color1.getAlpha() / totalAlpha;
        double w2 = color2.getAlpha() / totalAlpha;

        double r = w1 * color1.getRed() + w2 * color2.getRed();
        double g = w1 * color1.getGreen() + w2 * color2.getGreen();
        double b = w1 * color1.getBlue() + w2 * color2.getBlue();
        double a = Math.max(color2.getAlpha(), color2.getAlpha());

        colorMixed = new Color((int) r, (int) g, (int) b, (int) a);
        this.colorLeft = color1;
        this.colorRight = color2;
    }

    
    public File saveImage(String filename) {

        BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        this.paint(g);  //this == JComponent
        g.dispose();
        
        try {
        	File file = new File(filename);
        	
            ImageIO.write(bi, "jpg", file);
            
            return file;
        } catch (Exception e) {
        }
        
        return null;
    }

    
    public InputStream toInputStream() {
        BufferedImage bi = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        this.paint(g);  //this == JComponent
        g.dispose();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "jpg", os);
        } catch (Exception e) {
        }
        InputStream fis = new ByteArrayInputStream(os.toByteArray());
        return fis;
    }
    
    
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//
	// Main Stub
	//--------------------------------------------------------------------------//
	//--------------------------------------------------------------------------//

    public static void main(String[] args){
        VennDiagram m = new VennDiagram("atrocious strategists", "underachievers", 
        								 "Thugs who develop sympathetic muscles", 
        								"Sympathetic \"best friends\" who are roused by a rabble rouser");
        m.setRandomColor();
        m.saveImage("test.jpg");
        
        
    }
   

}
