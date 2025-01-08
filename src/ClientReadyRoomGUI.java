import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientReadyRoomGUI extends JPanel {

    private int roomNumber;
    private ClientGUI uc;
    private int readyProgress;
    private int joinProgress;
    private boolean isReady = false; // 초기 상태는 레디가 아님

    public ClientReadyRoomGUI(ClientGUI uc, int roomNumber, int readyProgress, int joinProgress) {
        this.uc = uc;
        this.roomNumber = roomNumber;
        this.readyProgress = readyProgress;
        this.joinProgress = joinProgress;
        setLayout(new BorderLayout());
        createReadyRoomPanel();
    }

    public void handleRoomInfo(GamePacket packet) {
        int roomNumber = packet.getRoomNum();
        int participantsCount = packet.getRoomJoin();

        if (this.roomNumber == roomNumber) {
            setRoomParticipants(participantsCount);
        }
    }

    private void setRoomParticipants(int participantsCount) {
        JLabel titleLabel = (JLabel) ((JPanel) getComponent(0)).getComponent(0);
        titleLabel.setText("[ Room: " + roomNumber + " ] Participants: " + participantsCount + "/4");
        revalidate();
        repaint();
    }

    private void createReadyRoomPanel() {
        // 네모와 버튼을 가로로 배치하는 패널
        JPanel boxesPanel = new BackgroundPanel(this.getClass().getClassLoader().getResource("assets/uno1.png")); // 이미지 경로

        boxesPanel.setLayout(new GridLayout(2, 4, 10, 10)); // 1x4 그리드
        //boxesPanel.setBackground(Color.YELLOW); // boxesPanel 배경색 주황색으로 설정

        for (int i = 0; i < 4; i++) {
            // 개별 네모와 버튼을 포함하는 패널 생성
            JPanel boxPanel = new JPanel(new BorderLayout());
            JLabel uidPanel = new JLabel("Player " + (i + 1)); // 플레이어가 없는 경우 빈 JLabel

            uidPanel.setPreferredSize(new Dimension(100, 20));
            boxPanel.add(uidPanel, BorderLayout.NORTH);
            uidPanel.setOpaque(false); // 투명도를 위해 기본 불투명 설정 해제
            boxPanel.setOpaque(false); // 투명도를 위해 기본 불투명 설정 해제

            // 네모 생성
            JPanel box = new JPanel();
            box.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            box.setPreferredSize(new Dimension(100, 100));
            box.setOpaque(false); // 투명도를 위해 기본 불투명 설정 해제


            if (joinProgress > i) {
                // 이미지 아이콘을 생성하여 JLabel에 설정
                ImageIcon imageIcon = new ImageIcon(this.getClass().getClassLoader().getResource("assets/pro.png"));
                Image scaledImage = imageIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH); // 크기 조정
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

                // JLabel을 stateBoxPanel에 추가
                box.removeAll(); // 이미지 제거
                box.add(imageLabel);
            } else {
                box.removeAll(); // 이미지 제거
            }

            boxPanel.add(box, BorderLayout.CENTER);
            // 메인 패널에 추가
            boxesPanel.add(boxPanel);
        }

        // StatePanel 생성 (ready state 제목과 4개의 박스를 포함)
        JPanel statePanel = new JPanel();
        statePanel.setLayout(new BorderLayout());

        // "Ready State" 제목을 추가
        JLabel titleLabel = new JLabel("[ Room: " + roomNumber + " ] Ready State", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statePanel.add(titleLabel, BorderLayout.NORTH);

        // StatePanel 내에 4개의 박스를 추가
        JPanel stateBoxesPanel = new JPanel(new GridLayout(1, 4, 10, 10)); // 1x4 그리드
        Color darkOrange = new Color(255, 120, 0); // RGB 값으로 진한 오렌지 생성

// stateBoxesPanel에 진한 오렌지 색상 설정
        stateBoxesPanel.setBackground(darkOrange);
        for (int i = 0; i < 4; i++) {
            JPanel stateBoxPanel = new JPanel();
            stateBoxPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            stateBoxPanel.setPreferredSize(new Dimension(100, 50));

            if (joinProgress > i) {
                if (readyProgress > i) {
                    // 접속하고 레디 했으면 초록색
                    stateBoxPanel.setBackground(Color.GREEN);
                } else {
                    // 접속했지만 레디하지 않았으면 주황색
                    stateBoxPanel.setBackground(Color.YELLOW);
                }
            } else {
                // 접속하지 않았으면 하얀색
                stateBoxPanel.setBackground(Color.WHITE);
            }

            stateBoxesPanel.add(stateBoxPanel);
        }

        statePanel.add(stateBoxesPanel, BorderLayout.CENTER);
        statePanel.setBackground(darkOrange);

        // 버튼 생성
        JButton readyButton = new JButton("READY");
        readyButton.setPreferredSize(new Dimension(100, 40));
        readyButton.setBackground(Color.GREEN); // READY 상태일 때 배경을 초록색으로 변경

        readyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // 상태에 따라 버튼 텍스트와 색상 변경
                if (isReady) {
                    readyButton.setText("READY");
                    readyButton.setBackground(Color.GREEN); // READY 상태 배경 색
                    readyButton.setForeground(Color.BLACK); // 글자색 기본값
                    isReady = false;
                    uc.sendReady(roomNumber); // 서버로 레디 취소 요청 전송
                } else {
                    readyButton.setText("CANCEL");
                    readyButton.setBackground(Color.RED); // CANCEL 상태 배경 색
                    readyButton.setForeground(Color.WHITE); // 글자색 하얀색
                    isReady = true;
                    uc.sendReady(roomNumber); // 서버로 레디 요청 전송
                }
            }
        });

        // 레이아웃에 추가
        setLayout(new BorderLayout());
        add(statePanel, BorderLayout.NORTH); // statePanel을 상단에 배치
        add(boxesPanel, BorderLayout.CENTER);
        add(readyButton, BorderLayout.SOUTH);
    }

    // 외부에서 readyProgress를 변경하는 메서드 추가
    public void setReadyProgress(int newReadyProgress, int newJoinProgress) {
        this.readyProgress = newReadyProgress;
        this.joinProgress = newJoinProgress;

        // 레디 상태를 기반으로 UI 업데이트
        updatePanel();
    }



    private void updatePanel() {
        // boxesPanel과 statePanel 모두에 대해 색상을 업데이트합니다.

        // boxesPanel 업데이트
        for (int i = 0; i < 4; i++) {
            JPanel boxPanel = (JPanel) ((JPanel) getComponent(1)).getComponent(i); // boxesPanel에서 각 boxPanel을 가져옴
            JPanel box = (JPanel) boxPanel.getComponent(1); // boxPanel에서 네모 패널 가져옴

            if (joinProgress > i) {
                // 이미지 아이콘을 생성하여 JLabel에 설정
                ImageIcon imageIcon = new ImageIcon(this.getClass().getClassLoader().getResource("assets/pro.png"));
                Image scaledImage = imageIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH); // 크기 조정
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

                // JLabel을 stateBoxPanel에 추가
                box.removeAll(); // 이미지 제거
                box.add(imageLabel);
            } else {
                box.removeAll(); // 이미지 제거
            }
        }

        // statePanel 업데이트
        JPanel statePanel = (JPanel) getComponent(0); // StatePanel을 가져옴
        JPanel stateBoxesPanel = (JPanel) statePanel.getComponent(1); // statePanel에서 stateBoxesPanel을 가져옴

        for (int i = 0; i < 4; i++) {
            JPanel stateBoxPanel = (JPanel) stateBoxesPanel.getComponent(i); // 각 박스를 가져옴

            if (joinProgress > i) {
                if (readyProgress > i) {
                    // 접속하고 레디 했으면 초록색
                    stateBoxPanel.setBackground(Color.GREEN);
                } else {
                    // 접속했지만 레디하지 않았으면 주황색
                    stateBoxPanel.setBackground(Color.YELLOW);
                }
            } else {
                // 접속하지 않았으면 하얀색
                stateBoxPanel.setBackground(Color.WHITE);
            }
        }

        revalidate();
        repaint();
    }

}