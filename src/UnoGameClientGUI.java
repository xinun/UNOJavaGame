import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.*;
import java.util.List;
import javax.swing.*;
import java.awt.*;

public class UnoGameClientGUI extends JPanel {
    private UnoGame unoGame;
    private JPanel gamePanel;
    private JPanel gameCenterPanel;
    private String uid;
    private int myNum;
    private HashMap<Integer, String> userMap;
    private ClientGUI uc;
    private String currentTurn;
    Color darkOrange = new Color(255, 120, 0); // RGB 값으로 진한 오렌지 생성

    public UnoGameClientGUI(UnoGame unoGame, String uid, ClientGUI uc) {
        setLayout(new BorderLayout()); // 기존의 레이아웃 설정
        setPreferredSize(new Dimension(615, 830));

        // 게임 패널 설정
        gamePanel = new BackgroundPanel(this.getClass().getClassLoader().getResource("assets/unotable.png")); // 이미지 경로
        gamePanel.setLayout(new BorderLayout());  // 전체 화면을 BorderLayout으로 설정
        add(gamePanel, BorderLayout.CENTER); // 패널 추가


        this.uid = uid;
        this.unoGame = unoGame;
        this.uc = uc;
        setMyNum();

        updateGamePanel();

        setVisible(true);
    }

    public void setMyNum(){
        userMap = unoGame.getPlayerNumMap();
        for (Map.Entry<Integer, String> entry : userMap.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();

            if (Objects.equals(value, uid)) {
                myNum = key;
            }
        }
    }

    private void updateGamePanel() {
        gamePanel.removeAll();  // 기존 내용 제거

        JPanel player1Panel = new JPanel(new BorderLayout());
        player1Panel.setOpaque(true); // 배경색이 보이도록 설정
        player1Panel.setBackground(darkOrange); // 배경색 설정
        // 상단에 플레이어 덱 (Player 1, 2, 3, 4) 배치
        JPanel playersPanel = new JPanel(new BorderLayout());
        player1Panel.add(displayMyCards(unoGame.getPlayerCards(myNum), myNum), BorderLayout.CENTER); // Player 1 덱
        player1Panel.add(displayActionButtons(), BorderLayout.EAST);  // 플레이어 1 버튼은 오른쪽에 배치
        player1Panel.setPreferredSize(new Dimension(0, 150));  // 남쪽 패널 크기 고정
        // 동쪽(WEST)과 서쪽(EAST) 패널의 크기 고정


        JPanel player2Panel = displayPlayerCards(unoGame.getPlayerCards((myNum+1)%4), (myNum+1)%4);
        player2Panel.setPreferredSize(new Dimension(120, 0));  // 서쪽 패널 크기 고정
        playersPanel.add(player2Panel, BorderLayout.WEST); // Player 2 덱

        JPanel player4Panel = displayPlayerCards(unoGame.getPlayerCards((myNum+2)%4), (myNum+2)%4);
        player4Panel.setPreferredSize(new Dimension(0, 150));  // 북쪽 패널 크기 고정
        playersPanel.add(player4Panel, BorderLayout.NORTH);  // Player 3 덱

        JPanel player3Panel = displayPlayerCards(unoGame.getPlayerCards((myNum+3)%4), (myNum+3)%4);
        player3Panel.setPreferredSize(new Dimension(120, 0));  // 동쪽 패널 크기 고정
        playersPanel.add(player3Panel, BorderLayout.EAST);  // Player 3 덱

        playersPanel.add(player1Panel, BorderLayout.SOUTH); // Player 1 덱


        // Top Card 표시 (중앙)
        playersPanel.add(GameCenterPanel(), BorderLayout.CENTER);

        gamePanel.add(playersPanel, BorderLayout.CENTER);  // 플레이어
        // 덱을 게임 패널 중앙에 배치
       // player1Panel.setOpaque(false); // 투명도를 위해 기본 불투명 설정 해제
        player3Panel.setOpaque(false); // 투명도를 위해 기본 불투명 설정 해제
        player2Panel.setOpaque(false); // 투명도를 위해 기본 불투명 설정 해제
        player4Panel.setOpaque(false); // 투명도를 위해 기본 불투명 설정 해제
        playersPanel.setBackground(darkOrange);

        // 게임 패널 재배치
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    private JPanel displayActionButtons() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1));  // 3개의 버튼을 세로로 배치 (3행, 1열)
        buttonPanel.setPreferredSize(new Dimension(200, 100)); // 패널 크기 설정

        // Draw, Next, UNO 버튼들 생성
        JButton drawButton = new JButton("Draw");
        JButton unoButton = new JButton("UNO");
        buttonPanel.setBackground(darkOrange); // 배경색 설정
        drawButton.setBackground(Color.white); // 배경색 설정
        unoButton.setBackground(Color.white); // 배경색 설정

        // 각 버튼에 액션 리스너 추가
        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawCardUpdate(myNum);
                unoGame.nextTurn();
                uc.sendUnoUpdate(uid, unoGame);
            }
        });
        if(!(Objects.equals(unoGame.getTurn().get(0), uid))){
            drawButton.setEnabled(false);
        }

        unoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> currentPlayerList = unoGame.getPlayerCards(myNum);

                if (currentPlayerList.size() == 1) {
                    // 카드가 하나 남았으면 UNO 버튼을 눌러야 함
                    unoGame.getIsUNO().put(uid, true);  // UNO를 외쳤다고 설정
                    uc.send(new GamePacket(uid, GamePacket.MODE_TX_STRING, "UNO!!!", uc.getMyRoomNumber()));
                    uc.sendUnoUpdate(uid, unoGame);
                    unoGame.nextTurn();
                } else {
                    // 카드가 하나 남지 않으면 UNO 버튼을 눌러도 플래그 변경 없음
                    uc.printDisplay( "UNO를 외칠 수 없습니다.");
                }

                // 모든 플레이어의 덱을 확인하고, UNO를 외쳤는지 판단
                for (Map.Entry<Integer, String> entry : userMap.entrySet()) {
                    int playerNumber = entry.getKey();
                    String playerUid = entry.getValue();
                    List<String> playerList = unoGame.getPlayerCards(playerNumber);

                    if (playerList.size() == 1 && !unoGame.getIsUNO().get(playerUid)) {
                        // 플레이어가 카드 1장을 남겼고 UNO를 외치지 않았다면, 한 장 더 뽑아야 함
                        uc.send(new GamePacket(uid, GamePacket.MODE_TX_STRING, "UNO!!! " + playerUid + "가 한 장 더 뽑습니다!!", uc.getMyRoomNumber()));
                        drawCardUpdate(playerNumber);  // 한 장 더 뽑기
                        uc.sendUnoUpdate(uid, unoGame);
                    }
                }
            }
        });

        // 버튼을 버튼 패널에 추가
        buttonPanel.add(drawButton);
        buttonPanel.add(unoButton);

        return buttonPanel;
    }

    private JPanel displayPlayerCards(List<String> playerList, int playerIndex) {
        JPanel playerPanel = new JPanel();
        playerPanel.setBorder(BorderFactory.createTitledBorder(unoGame.getPlayerNumMap().get(playerIndex)));
        playerPanel.setLayout(new BorderLayout());

        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new FlowLayout(FlowLayout.LEFT));  // 카드 버튼들을 왼쪽에 배치
        cardPanel.setOpaque(false); // 투명도를 위해 기본 불투명 설정 해제

        // 동쪽, 서쪽, 북쪽 패널에 따라 다른 이미지 경로와 크기 설정
        URL imagePath ;   // 카드 이미지 경로를 동적으로 설정
        int cardWidth = 90;
        int cardHeight = 30;

        if (playerIndex == (myNum + 1) % 4) {
            imagePath = this.getClass().getClassLoader().getResource("assets/cards/uno1.png");  // 서쪽 패널
        } else if (playerIndex == (myNum + 3) % 4) {
            imagePath = this.getClass().getClassLoader().getResource("assets/cards/uno3.png");  // 동쪽 패널
        } else if (playerIndex == (myNum + 2) % 4) {
            imagePath = this.getClass().getClassLoader().getResource("assets/cards/uno2.png");  // 북쪽 패널
            cardWidth = 30;  // 북쪽은 세로로 표시
            cardHeight = 90;
        } else {
            imagePath = this.getClass().getClassLoader().getResource("assets/cards/uno1.png");  // 기본 이미지, 필요시 변경
        }

        // 각 카드에 대해 이미지를 버튼으로 설정
        for (String card : playerList) {
            ImageIcon cardImage = new ImageIcon(imagePath);  // 이미지 로드
            Image scaledImage = cardImage.getImage().getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH);  // 크기 조정

            JButton cardButton = new JButton(new ImageIcon(scaledImage));  // 이미지로 버튼 생성
            cardButton.setPreferredSize(new Dimension(cardWidth, cardHeight));  // 버튼 크기 조정

            // 카드 클릭 시 처리할 액션 리스너 추가 (필요시 코드 추가)
            cardButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                }
            });

            cardPanel.add(cardButton);
        }

        playerPanel.add(cardPanel, BorderLayout.CENTER);  // 카드 패널을 중앙에 배치

        return playerPanel;
    }

    private JPanel displayMyCards(List<String> playerList, int playerIndex) {
        JPanel playerPanel = new JPanel();
        playerPanel.setBorder(BorderFactory.createTitledBorder(unoGame.getPlayerNumMap().get(playerIndex)));
        playerPanel.setLayout(new BorderLayout());
        playerPanel.setBackground(darkOrange); // 배경색 설정
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new FlowLayout(FlowLayout.LEFT));  // 카드 버튼들을 왼쪽에 배치
        cardPanel.setBackground(darkOrange); // 배경색 설정
        for (String card : playerList) {
            // 카드 이름을 색상과 값으로 나누어 표시
            String[] cardParts = card.split(" ");
            String color = cardParts[0]; // 색상
            String value = cardParts[1]; // 값

            // 예시 경로 : "assets/cards/Red_1.png"
            URL imagePath = this.getClass().getClassLoader().getResource("assets/cards/" + color + "_" + value + ".png");

            // 이미지 로드
            ImageIcon cardImage = new ImageIcon(imagePath);

            // JButton 생성하여 카드 이미지로 표시
            Image scaledImage = cardImage.getImage().getScaledInstance(35, 80, Image.SCALE_SMOOTH);
            JButton cardButton = new JButton(new ImageIcon(scaledImage));
            cardButton.setPreferredSize(new Dimension(35, 80));

            // 카드 클릭 시 이벤트 처리
            final String currentCard = card;
            cardButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 카드 클릭 시 처리할 코드
                    playCardUpdate(currentCard, playerIndex);
                    uc.sendUnoUpdate(uid, unoGame);
                }
            });

            // 해당 플레이어가 아닌 경우 버튼 비활성화
            if (!(Objects.equals(unoGame.getTurn().get(0), uid))) {
                cardButton.setEnabled(false);
            }

            cardPanel.setPreferredSize(new Dimension(cardPanel.getPreferredSize().width, 200));
            // 카드 버튼을 카드 패널에 추가
            cardPanel.add(cardButton);
        }

        playerPanel.add(cardPanel, BorderLayout.CENTER);  // 카드 패널을 중앙에 배치

        return playerPanel;
    }

    private JPanel GameCenterPanel() {

        gameCenterPanel = new JPanel(new BorderLayout());
        gameCenterPanel.add(displayTopCardPanel(), BorderLayout.CENTER);
        gameCenterPanel.setOpaque(false); // 투명도를 위해 기본 불투명 설정 해제
        gameCenterPanel.setBackground(darkOrange); // 투명도를 위해 기본 불투명 설정 해제

        // 플레이어 차례 패널 생성
        JPanel Player1Turn = new JPanel();  // 남쪽
        JPanel Player2Turn = new JPanel();  // 서쪽
        JPanel Player3Turn = new JPanel();  // 북쪽
        JPanel Player4Turn = new JPanel();  // 동쪽

        Player1Turn.setBackground(Color.LIGHT_GRAY);
        Player2Turn.setBackground(Color.LIGHT_GRAY);
        Player3Turn.setBackground(Color.LIGHT_GRAY);
        Player4Turn.setBackground(Color.LIGHT_GRAY);


        String currentPlayerName = unoGame.getTurn().get(0);
        if(Objects.equals(unoGame.getPlayerNumMap().get(myNum), currentPlayerName)){
            Player1Turn.setBackground(Color.YELLOW);
        }else if(Objects.equals(unoGame.getPlayerNumMap().get((myNum + 1) % 4), currentPlayerName)){
            Player2Turn.setBackground(Color.YELLOW);
        }else if(Objects.equals(unoGame.getPlayerNumMap().get((myNum + 2) % 4), currentPlayerName)){
            Player3Turn.setBackground(Color.YELLOW);
        }else{
            Player4Turn.setBackground(Color.YELLOW);
        }

        gameCenterPanel.add(Player1Turn, BorderLayout.SOUTH);
        gameCenterPanel.add(Player2Turn, BorderLayout.WEST);
        gameCenterPanel.add(Player3Turn, BorderLayout.NORTH);
        gameCenterPanel.add(Player4Turn, BorderLayout.EAST);

        return gameCenterPanel;
    }

    private JPanel displayTopCardPanel() {
        // 메인 패널을 BorderLayout으로 설정
        JPanel topCardPanel = new BackgroundPanel(this.getClass().getClassLoader().getResource("assets/unotable.png"));
        topCardPanel.setLayout(new BorderLayout());
        topCardPanel.setLayout(new BorderLayout());  // BorderLayout을 사용하여 상단과 중앙에 패널을 배치
        topCardPanel.setOpaque(false); // displayTopCardPanel의 최상위 패널
        topCardPanel.setBackground(darkOrange);


        // "현재 차례"를 표시하는 패널을 상단에 추가
        if (unoGame.getTurn() != null) {
            JPanel currentTurnPanel = new JPanel();
            currentTurnPanel.setBorder(BorderFactory.createTitledBorder("현재 차례"));

            // 현재 차례를 가져와 라벨에 표시
            JLabel currentTurnLabel = new JLabel(unoGame.getTurn().get(0));  // 'getFirst()'가 현재 플레이어 이름을 반환한다고 가정
            currentTurnPanel.add(currentTurnLabel);

            // "현재 차례" 패널을 상단에 배치
            topCardPanel.add(currentTurnPanel, BorderLayout.NORTH);

            currentTurnPanel.setOpaque(false);
            currentTurnPanel.setBackground(Color.white);

        }

        // 상단 카드가 null이 아닌 경우 처리
        if (unoGame.getTopCard() != null) {
            // topCard를 카드 이름과 값으로 분리
            String[] topCardParts = unoGame.getTopCard().split(" ");
            String topColor = topCardParts[0];  // 색상
            String topValue = topCardParts[1];  // 값

            // 이미지 경로 생성 (예: "assets/cards/Red_1.png")
            URL imagePath = this.getClass().getClassLoader().getResource("assets/cards/" + topColor + "_" + topValue + ".png");

            // topCard 이미지 로드
            ImageIcon topCardImage = new ImageIcon(imagePath);
            Image scaledTopCardImage = topCardImage.getImage().getScaledInstance(80, 200, Image.SCALE_SMOOTH);

            // topCard 버튼 생성
            JButton topCardButton = new JButton(new ImageIcon(scaledTopCardImage));
            topCardButton.setPreferredSize(new Dimension(80, 200));

            // BackButton 이미지 로드 (예: "uno1.png")
            URL backButtonImagePath = this.getClass().getClassLoader().getResource("assets/cards/uno4.png");  // 경로 변경 가능
            ImageIcon backButtonImage = new ImageIcon(backButtonImagePath);
            Image scaledBackButtonImage = backButtonImage.getImage().getScaledInstance(80, 200, Image.SCALE_SMOOTH);

            // BackButton 생성
            JButton backButton = new JButton(new ImageIcon(scaledBackButtonImage));
            backButton.setPreferredSize(new Dimension(80, 200));

            // 두 버튼을 중앙에 배치하기 위해 JPanel 생성
            JPanel cardPanel = new JPanel();
            cardPanel.setLayout(new FlowLayout(FlowLayout.CENTER));  // 중앙 정렬
            cardPanel.add(topCardButton);
            cardPanel.add(backButton);

            // 카드 패널을 중앙에 배치
            topCardPanel.add(cardPanel, BorderLayout.CENTER);
            cardPanel.setOpaque(false);
            cardPanel.setBackground(Color.white);
        }

        return topCardPanel;
    }


    private void playCardUpdate(String card, int playerIndex) {
        if (!unoGame.playCard(card, playerIndex)) {
            JOptionPane.showMessageDialog(this, "이 카드는 플레이할 수 없습니다. 색상 또는 숫자가 일치하지 않습니다.");
        }
        updateGamePanel();
    }

    private void drawCardUpdate(int playerIndex) {
        // 덱에서 한 장의 카드를 뽑아 해당 플레이어에게 추가
        unoGame.drawCard(playerIndex);

        // 게임 화면 갱신
        updateGamePanel();
    }
}