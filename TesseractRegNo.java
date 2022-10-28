/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.*;
import net.sourceforge.tess4j.*;
import java.sql.*;
import java.sql.PreparedStatement;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TesseractRegNo {

    JFrame CarFinder = new JFrame();
    JPanel Top = new JPanel(new FlowLayout());
    JPanel Middle = new JPanel(new FlowLayout());
    JPanel Bottom = new JPanel(new FlowLayout());
    JLabel lblHeader = new JLabel("Stolen Vehicle Finder");
    JLabel lblRegNo = new JLabel("Vehicle plane number:");
    JLabel lblStatus = new JLabel("STATUS");
    JTextField txtNumPlate = new JTextField(20);
    JTextField txtMessage = new JTextField(15);
    JButton btnVerify = new JButton("Verify");
    JButton btnNext = new JButton("Next");
//Creating user interface

    public void ShowInterface() {
        Top.add(lblHeader);
        Middle.add(lblRegNo);
        Middle.add(txtNumPlate);
        Middle.add(btnVerify);
        Middle.add(btnNext);
        Bottom.add(lblStatus);
        Bottom.add(txtMessage);
        txtMessage.setEditable(false);
        btnVerify.addActionListener(new Verify());
        btnVerify.setToolTipText("Press this button to verify license number");
        btnNext.addActionListener(new Next());
        btnVerify.setToolTipText("Press this button to clear input field");

        //Adding panels to frame
        CarFinder.add(Top, BorderLayout.NORTH);
        CarFinder.add(Middle, BorderLayout.CENTER);
        CarFinder.add(Bottom, BorderLayout.SOUTH);
        //Set Frame visibility
        CarFinder.setVisible(true);
        CarFinder.setSize(800, 200);
        CarFinder.setLocation(100, 100);
        CarFinder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private class Next implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            txtNumPlate.setText("");
        }
    }

    private class Verify implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            File imageFile = new File("C:\\Users\\Kamva Cewu\\Documents\\NetBeansProjects\\TesseractRegNo\\VehiclePlate.jpg");
            ITesseract instance = new Tesseract();
            try {
                String result = instance.doOCR(imageFile);
                txtNumPlate.setText(result);
            } catch (TesseractException e) {
                System.err.println(e.getMessage());
            }
            String RegNumber = txtNumPlate.getText().trim();
            //Recipients email
            String to = "tomail@gmail.com";
            // Sender's email
            String from = "frommaiil@gmail.com";
            // SMTP
            String host = "smtp.something.com";
            // Get system properties
            Properties properties = System.getProperties();
            // Setup mail server
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");
            // Get the Session object.// and pass username and password
            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("username", "password");
                }

            });
            // Used to debug SMTP issues
            session.setDebug(true);
            //Database connection
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                String Constrr = "jdbc:sqlserver://localhost:1433;databaseName=RegNo;integratedSecurity=true;encrypt=true;trustServerCertificate=true";
                Connection con = DriverManager.getConnection(Constrr);
                //Retrieving all Registrations
                String sql = "SELECT * FROM NumPlate WHERE Registration=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, RegNumber);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    MimeMessage message = new MimeMessage(session);
                    // Set From: header field of the header.
                    message.setFrom(new InternetAddress(from));
                    // Set To: header field of the header.
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                    // Set Subject: header field
                    message.setSubject("Stolen Motor-vehicle found!");
                    // The actual email message
                    message.setText("Dear SAPS Head, \n\n A stolen vehicle with Registration Number " + RegNumber + " has been spotted. Please contact 0798304155 for more details.\n\n Regards, \n\n Z.T");
                    // Send message
                    Transport.send(message);
                    txtMessage.setText("Stolen Vehicle!!");
                    txtMessage.setBackground(Color.RED);
                } else {
                    txtMessage.setText("Compliant vehicle");
                    txtMessage.setBackground(Color.GREEN);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TesseractRegNo ObjShow = new TesseractRegNo();
        ObjShow.ShowInterface();

    }

}
