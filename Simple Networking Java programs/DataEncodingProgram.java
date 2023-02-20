import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import static java.awt.Color.green;

/**
 * AAYUSH SHRESTHA
 * B00906766
 * */

public class DataEncodingProgram extends JFrame implements ActionListener {
    int xp = 300, yp=300, xn = 300, yn = 500, ym=400;
    static int xm=300;
    static JButton encodeButton;
    JTextField inputData = new JTextField(15);
    JComboBox<String> encodingTechnique = null;
    static JPanel drawPanel = new JPanel();
    public DataEncodingProgram() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(100000,40000);
        setTitle("Assignment 2, Program 1 : Data Encoding By AAYUSH SHRESTHA (B00906766)");
        initComponents();
    }

    private void initComponents() {
        JPanel jPanel = new JPanel();
        jPanel.add(new JLabel("Input Data:"));
        jPanel.add(inputData);
        String[] list = {"NRZ-L","Manchester","Unipolar"};
        encodingTechnique = new JComboBox<String>(list);
        jPanel.add(new JLabel("Technique:"));
        jPanel.add(encodingTechnique);
        encodeButton = new JButton("Encode");
        JButton clrBtn = new JButton("Exit");
        jPanel.add(encodeButton);
        encodeButton.addActionListener(this);
        jPanel.add(clrBtn);
        clrBtn.addActionListener(this);
        drawPanel.setBackground(Color.WHITE);
        add(drawPanel);
        add(jPanel,BorderLayout.NORTH);

    }

    public static void main(String[] args) {
        DataEncodingProgram f = new DataEncodingProgram();
        f.setVisible(true);
    }

    private static void drawReferenceAxes() {
        Graphics2D graphics2D = (Graphics2D) drawPanel.getGraphics();
        graphics2D.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));

        graphics2D.drawLine(300,200,300,600);
        graphics2D.drawLine(300,400,xm,400);
        graphics2D.drawString("0", 260, 410);
        graphics2D.drawLine(290,300,310,300);
        graphics2D.drawString("+V", 250, 300);
        graphics2D.drawLine(290,500,310,500);
        graphics2D.drawString("-V", 250, 500);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Encode")){
            dataEncode();
            encodeButton.setEnabled(false);
        }else if(e.getActionCommand().equals("Exit")){
            JComponent comp = (JComponent) e.getSource();
            Window win = SwingUtilities.getWindowAncestor(comp);
            win.dispose();
        }

    }


    private void dataEncode() {
        char[] bitStream = inputData.getText().toCharArray();

        Graphics2D graphics2D = (Graphics2D) drawPanel.getGraphics();
        graphics2D.setFont(graphics2D.getFont().deriveFont(17f));
        graphics2D.drawString("Data encoded using :  "+ encodingTechnique.getSelectedItem() + "  Encoding", 300, 150);
        graphics2D.setColor(green);
        graphics2D.setStroke(new BasicStroke(1,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL));

        //xs, ys -> starting x and y co-ordinate respectively
        int xs=0,ys=0;
        switch (String.valueOf(encodingTechnique.getSelectedItem())) {
            case "NRZ-L" -> dataEncodingForNRZLOrUniPolar(bitStream, true, graphics2D, xs, ys);
            case "Unipolar" -> dataEncodingForNRZLOrUniPolar(bitStream, false, graphics2D, xs, ys);
            default -> dataEncodingForManchester(bitStream, graphics2D, xs, ys);
        }
    }

    private void dataEncodingForManchester(char[] bitStream, Graphics2D graphics2D, int xs, int ys) {
        xm+=bitStream.length*53*2;
        drawReferenceAxes();
        for (int i=0;i< bitStream.length;i++) {
            if (bitStream[i] == '1') {
                xs = xn;
                ys = yn;

                graphics2D.drawLine(xs, ys, xs + 50, ys);
                graphics2D.drawLine(xs+50, yn, xs+50, yp);
                graphics2D.drawLine(xp+50, yp, xp + 100, yp);

            } else {
                xs = xp;
                ys = yp;

                graphics2D.drawLine(xs, ys, xs + 50, ys);
                graphics2D.drawLine(xs+50, yp, xs+50, yn);
                graphics2D.drawLine(xn+50, yn, xn + 100, yn);
            }
            if (i<bitStream.length-1 && bitStream[i] == bitStream[i+1])
                graphics2D.drawLine(xs+100, yp, xs+100, yn);
            xp += 100;
            xn += 100;
        }
    }


    private void dataEncodingForNRZLOrUniPolar(char[] bitStream, boolean nrzFlag, Graphics2D graphics2D, int xs, int ys) {
        xm+=bitStream.length*53;
        drawReferenceAxes();
        int value=0;
        for (int i=0;i< bitStream.length;i++) {
            if (bitStream[i] == '1') {
                xs = xp;
                ys = yp;
                value = nrzFlag?200:100;
            } else {
                xs = xn;
                value = nrzFlag ? -200 : -100;
                ys = nrzFlag ? yn : ym;
            }
            drawJoiningYaxisLine(bitStream,i,xs,ys,value, graphics2D);
            graphics2D.drawLine(xs, ys, xs+50, ys);
            xp += 50;
            xn += 50;
        }
    }

    private void drawJoiningYaxisLine(char[] bitStream, int i, int xs, int ys, int value,Graphics graphics) {
        if(i<bitStream.length-1 && !(bitStream[i]==bitStream[i+1]))
            graphics.drawLine(xs+50, ys, xs+50, ys+value);
    }

}