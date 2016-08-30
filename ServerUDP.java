import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerUDP {
    private ArrayList players;
    private JPanel mainPanel;
    private JButton startServer;
    private JTextField serverAt, port;
    private JTable information;
    private static boolean Online;
    private DatagramSocket serverSocket = null;
    private Thread mThread;
    private InetAddress servIP;
    private long counter, exeption_counter;
    private Date startTime, endTime, currentTime;
    private String[] columnNames = {"Property",
            "Value"};
    private Object[][] data = {
            {"Status", ""},
            {"Time Up", ""},
            {"Started at", ""},
            {"Finished at", ""},
            {"Exceptions", ""},
            {"AVG packets per second", ""},
            {"Active connections",""}};

    public void setDataInTable() {
        long seconds = (currentTime.getTime() - startTime.getTime()) / 1000;
        data[0][1] = Online ? "Online" : "Offline";
        data[1][1] = seconds;
        data[2][1] = startTime.toString();
        data[3][1] = endTime == null ? "" : endTime.toString();
        data[4][1] = exeption_counter;//Thread.activeCount();
        data[5][1] = counter / seconds;
        data[6][1] = players.size();
        information.updateUI();
    }

    public void WriteInLog(String s) {
        exeption_counter++;
        System.out.println(new Date().toString() + " - " + s);
    }

    public ServerUDP() {
        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    try {
                        servIP = InetAddress.getByName(serverAt.getText());
                    } catch (UnknownHostException exp) {
                        WriteInLog(exp.getMessage());
                    }
                    if (Online) {
                        serverSocket.close();
                        endTime = new Date();
                        Online = false;
                        players.clear();
                    } else {
                        serverSocket = new DatagramSocket(Integer.parseInt(port.getText()), servIP);
                        startTime = new Date();
                        endTime = null;
                        players = new ArrayList();
                        Online = true;
                    }
                    information.updateUI();
                } catch (SocketException exep) {
                    WriteInLog(exep.getMessage());
                    Online = false;
                } finally {
                    startServer.setText(Online ? "Stop" : "Start");
                    if (Online) {
                        exeption_counter = 0;
                        counter = 0;
                        mThread = new Thread(new Runnable() {
                            public void run() {
                                byte[] receiveData = new byte[64];
                                //byte[] sendData = new byte[64];
                                while (true) {
                                    counter++;
                                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                                    try {
                                        serverSocket.receive(receivePacket);
                                    } catch (IOException e) {
                                        WriteInLog(e.getMessage());
                                    }
                                    String sentence = new String(receivePacket.getData());
                                    // Here starts logic, example - login user with nickname
                                    // example string - login/MyNickname/password/
                                    if (sentence.substring(0,5).equalsIgnoreCase("login")) {
                                        Player pl = new Player(sentence.substring(6,31), receivePacket.getAddress(),receivePacket.getPort());
                                        players.add(pl);
                                        players.indexOf(pl);
                                        sentence = String.valueOf(players.indexOf(pl))+pl.position();
                                    }
                                    InetAddress IPAddress = receivePacket.getAddress();
                                    int port = receivePacket.getPort();
                                    DatagramPacket sendPacket =
                                            new DatagramPacket(sentence.getBytes(), sentence.length(), IPAddress, port);
                                    try {
                                        serverSocket.send(sendPacket);
                                    } catch (IOException e) {
                                        WriteInLog(e.getMessage());
                                    }
                                    receiveData = new byte[64];
                                }
                            }
                        });
                        mThread.start();
                        Thread UIThread = new Thread(new Runnable() {
                            public void run() {
                                while (Online) {
                                    try {
                                        Thread.sleep(1000);
                                        currentTime = new Date();
                                        setDataInTable();
                                    } catch (InterruptedException ex) {
                                        Thread.currentThread().interrupt();
                                    }
                                }
                                setDataInTable();
                            }
                        });
                        UIThread.start();
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Server");
        frame.setContentPane(new ServerUDP().mainPanel);
        frame.pack();
        frame.setSize(640, 400);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });
        Online = false;
    }

    private void createUIComponents() {
        information = new JTable(data, columnNames);
        information.setBackground(Color.black);
    }
}
