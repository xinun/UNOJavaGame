import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;

public class LoginGUI extends JFrame {
    private String serverAddress;
    private int serverPort;
    private JTextField t_userID;
    private JTextField t_hostAddr;
    private JTextField t_portNum;
    private JButton b_start, b_exit;
    private String uid;

    public LoginGUI() {
        setServerConfig();
        buildGUI();
        this.setSize(800, 800);
        this.setTitle("UNO Login");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void buildGUI() {
        // JLayeredPane 생성
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 800));
        this.add(layeredPane);

        // 배경 이미지 설정
        JLabel backgroundLabel = new JLabel(new ImageIcon(new ImageIcon(this.getClass().getClassLoader().getResource("assets/unotitle.png"))
                .getImage().getScaledInstance(800, 800, Image.SCALE_SMOOTH)));
        backgroundLabel.setBounds(0, 0, 800, 800); // 배경 이미지 크기와 위치
        layeredPane.add(backgroundLabel, Integer.valueOf(0)); // 배경을 0번 레이어에 추가

        // 입력 및 버튼 패널
        JPanel inputPanel = createInputPanel();
        inputPanel.setOpaque(false); // 배경 투명
        inputPanel.setBounds(150, 500, 500, 200); // 패널 위치 조정
        layeredPane.add(inputPanel, Integer.valueOf(1)); // 입력 패널을 1번 레이어에 추가
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(); // 기본 패널 생성
        inputPanel.setOpaque(false); // 투명 배경
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS)); // 세로로 컴포넌트를 정렬

        // 로그인 정보와 버튼을 포함한 수평 패널 생성
        JPanel horizontalPanel = new JPanel(new GridBagLayout()); // GridBagLayout으로 중앙 정렬
        horizontalPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5); // 여백 설정

        // 로그인 정보 패널 추가
        horizontalPanel.add(createInfoPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(15, 5, 5, 5); // 버튼 위 여백 추가
        horizontalPanel.add(createControlPanel(), gbc);

        inputPanel.add(horizontalPanel);

        return inputPanel;
    }

    private String getLocalAddr() {
        try {
            InetAddress local = InetAddress.getLocalHost();
            return local.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setOpaque(false); // 투명 배경

        // 폰트 설정
      /*  Font labelFont = new Font("Arial", Font.BOLD, 16); // JLabel 폰트
        Font textFieldFont = new Font("Arial", Font.PLAIN, 15); // JTextField 폰트
*/
        // JTextField 생성 및 설정
        t_userID = new JTextField(7);
        //t_userID.setFont(textFieldFont); // 폰트 설정

        t_hostAddr = new JTextField(12);
        //t_hostAddr.setFont(textFieldFont);

        t_portNum = new JTextField(5);
        //t_portNum.setFont(textFieldFont);

        // 기본값 설정
        t_userID.setText("guest" + getLocalAddr().split("\\.")[3]);
        t_hostAddr.setText(this.serverAddress);
        t_portNum.setText(String.valueOf(this.serverPort));

        // JLabel 생성 및 설정
        JLabel userIDLabel = new JLabel("아이디:");
     //   userIDLabel.setFont(labelFont);

        JLabel hostAddrLabel = new JLabel("서버주소:");
       // hostAddrLabel.setFont(labelFont);

        JLabel portNumLabel = new JLabel("포트번호:");
        //portNumLabel.setFont(labelFont);

        // 컴포넌트 추가
        panel.add(userIDLabel);
        panel.add(t_userID);
        panel.add(hostAddrLabel);
        panel.add(t_hostAddr);
        panel.add(portNumLabel);
        panel.add(t_portNum);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setOpaque(false); // 투명 배경

        b_start = new JButton("접속하기");
        b_exit = new JButton("종료하기");
        Dimension buttonSize = new Dimension(150, 50);
        b_start.setPreferredSize(buttonSize);
        b_exit.setPreferredSize(buttonSize);
        b_exit.setBackground(Color.white); // READY 상태일 때 배경을 초록색으로 변경
        b_start.setBackground(Color.white); // READY 상태일 때 배경을 초록색으로 변경

        // Font buttonFont = new Font("Arial", Font.BOLD, 15);
        //b_start.setFont(buttonFont);
        //b_exit.setFont(buttonFont);
        b_start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                serverAddress = t_hostAddr.getText();
                serverPort = Integer.parseInt(t_portNum.getText());
                uid = t_userID.getText();

                SwingUtilities.invokeLater(() -> new ClientGUI(uid, serverAddress, serverPort));
                System.out.println(uid + " " + serverAddress + " " + serverPort);

                dispose();
            }
        });

        b_exit.addActionListener(e -> System.exit(0));

        panel.add(b_start);
        panel.add(b_exit);

        return panel;
    }

    private void setServerConfig(){
        String text = null;

        // server.txt에서 서버 설정 읽기
        try (BufferedReader br = new BufferedReader(new FileReader("server.txt"))) {
            serverAddress = br.readLine(); // 첫 번째 줄 ip 주소
            serverPort = Integer.parseInt(br.readLine()); // 두 번째 줄 포트 번호

            text = "서버 ip: " + serverAddress + ", 포트번호: " + serverPort;

            System.out.println(text);
        } catch (IOException e) {
            text = "server.txt 파일을 읽을 수 없어 기본 설정을 사용.\n서버 ip: " + serverAddress + ", 포트번호: " + serverPort;

            System.err.println(text);
        }
    }

    public static void main(String[] args) {
        new LoginGUI();
    }
}
