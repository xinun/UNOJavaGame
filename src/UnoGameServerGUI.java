import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnoGameServerGUI extends JPanel {
    private UnoGame unoGame;
    private JPanel gamePanel;
    private JLabel remainingCardsLabel;  // 덱에 남은 카드 수를 표시할 레이블

    public UnoGameServerGUI(UnoGame unoGame) {
    	setLayout(new BorderLayout()); // 기존의 레이아웃 설정
    	setPreferredSize(new Dimension(615, 830));
    	
        // 게임 패널 설정
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(6, 1)); // 한 행에 6개 항목을 표시
        add(gamePanel, BorderLayout.CENTER);

        // 덱에 남은 카드 수를 표시할 레이블 추가
        remainingCardsLabel = new JLabel("남은 카드 수 : 0");
        remainingCardsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(remainingCardsLabel, BorderLayout.NORTH);

        this.unoGame = unoGame;

        updateGamePanel();

        // 게임 시작 버튼
        JButton startButton = new JButton("게임 시작");
        startButton.addActionListener(e -> gameStartUp());
        add(startButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void gameStartUp() {
        unoGame.startGame();

        // 플레이어들의 카드 표시
        updateGamePanel();
    }

    private void updateRemainingCardsLabel() {
        // 덱에 남은 카드 수 표시
        remainingCardsLabel.setText("남은 카드 수 : " + unoGame.getDeck().size());
    }

    private void updateGamePanel() {
        // 남은 카드 수 업데이트
        updateRemainingCardsLabel();

        gamePanel.removeAll();  // 기존 내용 제거
        
        // 각 플레이어의 카드 표시        
        gamePanel.add(displayPlayerCards(unoGame.getPlayerCards(0), 0));
        gamePanel.add(displayPlayerCards(unoGame.getPlayerCards(1), 1));
        gamePanel.add(displayPlayerCards(unoGame.getPlayerCards(2), 2));
        gamePanel.add(displayPlayerCards(unoGame.getPlayerCards(3), 3));
        
        // 턴 패널 생성 및 중앙에 배치
        JPanel borderPanel = new JPanel(new BorderLayout());
        borderPanel.add(displayTopCardPanel(), BorderLayout.WEST);
        
        // displayTurnPanel을 중앙에 배치
        borderPanel.add(displayTurnPanel(), BorderLayout.CENTER);
        
        // 오른쪽에 버튼들 배치
        borderPanel.add(displayActionButtons(), BorderLayout.EAST);  // 동쪽에 버튼 패널 추가
        
        gamePanel.add(borderPanel);
        
        gamePanel.add(displayNumberOfCardsPanel());

        // 게임 패널 재배치
        gamePanel.revalidate();
        gamePanel.repaint();
    }
    
    private JPanel displayActionButtons() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1));  // 3개의 버튼을 세로로 배치 (3행, 1열)
        buttonPanel.setPreferredSize(new Dimension(200, 100)); // 패널 크기 설정

        // Draw, Next, Reverse 버튼들 생성
        JButton nextButton = new JButton("Next");
        JButton skipButton = new JButton("Skip");
        JButton reverseButton = new JButton("Reverse");

        // 각 버튼에 액션 리스너 추가
        nextButton.addActionListener(e -> nextTurnUpdate());
        
        // 스킵
        skipButton.addActionListener(e -> jumpTurnUpdate());
        
        // Reverse 버튼의 기능 구현 (turn 배열을 뒤집음)
        reverseButton.addActionListener(e -> reverseTurnUpdate());
        
        
        // 버튼을 버튼 패널에 추가
        buttonPanel.add(nextButton);
        buttonPanel.add(skipButton);
        buttonPanel.add(reverseButton);

        return buttonPanel;
    }

    private JPanel displayPlayerCards(List<String> playerList, int playerIndex) {
        JPanel playerPanel = new JPanel();
        playerPanel.setBorder(BorderFactory.createTitledBorder("Player " + playerIndex));
        playerPanel.setLayout(new BorderLayout());

        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new FlowLayout(FlowLayout.LEFT));  // 카드 버튼들을 왼쪽에 배치
        for (String card : playerList) {
            // 카드 이름을 색상과 값으로 나누어 표시
            String[] cardParts = card.split(" ");
            String color = cardParts[0]; // 색상
            String value = cardParts[1]; // 값

            // JButton 생성하여 카드로 표시
            JButton cardButton = new JButton(value);  // 카드 값만 텍스트로 표시
            cardButton.setPreferredSize(new Dimension(90, 30));  // 버튼 크기 조정

            // 카드 색상에 맞게 배경색 설정
            cardButton.setBackground(getColorForCard(color));
            
            // 색상에 따른 글자 색 설정
            if (color.equals("Green") || color.equals("Yellow")) {
                cardButton.setForeground(Color.BLACK);  // Green과 Yellow는 글자 색을 검은색으로 설정
            } else {
                cardButton.setForeground(Color.WHITE);  // 나머지 색은 흰색 글자
            }

            // 카드 클릭 시 이벤트 처리
            final String currentCard = card;
            cardButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 카드 클릭 시 처리할 코드
                    playCardUpdate(currentCard, playerIndex);
                }
            });

            cardPanel.add(cardButton);
        }

        // 오른쪽에 세로로 버튼 3개 추가 (GridLayout으로 변경)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1)); // 3개의 버튼을 세로로 배치 (3행, 1열)
        buttonPanel.setPreferredSize(new Dimension(100, 100)); // 패널 크기 설정

        // 버튼들 추가 (예: Draw, Next, UNO)
        JButton drawButton = new JButton("Draw");
        JButton nextButton = new JButton("Next");
        JButton unoButton = new JButton("UNO");

        // 버튼에 액션 리스너 추가 (기본적인 처리 방식)
        drawButton.addActionListener(e -> drawCardUpdate(playerIndex));
        nextButton.addActionListener(e -> System.out.println("player next action"));
        unoButton.addActionListener(e -> System.out.println("UNO action"));

        // 버튼을 버튼 패널에 추가
        buttonPanel.add(drawButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(unoButton);
        
        // 플레이어 패널에 카드 패널과 버튼 패널 추가
        playerPanel.add(cardPanel, BorderLayout.CENTER);
        playerPanel.add(buttonPanel, BorderLayout.EAST);  // 오른쪽에 버튼 패널 배치
        
        return playerPanel;
    }
    

    private JPanel displayTopCardPanel() {
        if (unoGame.getTopCard() != null) {
            JPanel topCardPanel = new JPanel();
            topCardPanel.setBorder(BorderFactory.createTitledBorder("Top Card"));
            
            // topCard를 JButton으로 만들기
            String[] topCardParts = unoGame.getTopCard().split(" ");
            String topColor = topCardParts[0];
            String topValue = topCardParts[1];

            // topCard 버튼 생성
            JButton topCardButton = new JButton(topValue);  
            topCardButton.setPreferredSize(new Dimension(130, 90));  // 버튼 크기 조정
            topCardButton.setBackground(getColorForCard(topColor)); // 카드 색상 설정

            // 글자 색 설정 (Yellow와 Green은 검은색, 나머지는 흰색)
            if (topColor.equals("Yellow") || topColor.equals("Green")) {
                topCardButton.setForeground(Color.BLACK);  // 글자 색을 검은색으로 설정
            } else {
                topCardButton.setForeground(Color.WHITE);  // 나머지 색상은 흰색
            }

            topCardPanel.add(topCardButton);

            return topCardPanel;
        }
        return new JPanel();
    }

    private void playCardUpdate(String card, int playerIndex) {
        if(!unoGame.playCard(card, playerIndex)){
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
    
    private JPanel displayTurnPanel() {
        JPanel turnPanel = new JPanel();
        turnPanel.setLayout(new GridLayout(unoGame.getTurn().size() + 1, 1));  // 각 플레이어를 세로로 나열
        
        JLabel nowTurn = new JLabel("현제 차례 : " + unoGame.getTurn().get(0));
        turnPanel.add(nowTurn);
        

        for (String player : unoGame.getTurn()) {
            JLabel playerLabel = new JLabel(player);
            playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            turnPanel.add(playerLabel);
        }

        return turnPanel;
    }

    private void nextTurnUpdate() {
        unoGame.nextTurn();

        // 게임 화면 업데이트
        updateGamePanel();
    }
    
    private void reverseTurnUpdate() {
        unoGame.reverseTurn();

        // 게임 화면 업데이트
        updateGamePanel();
    }

    private void jumpTurnUpdate() {
        unoGame.jumpTurn();

        // 게임 화면 업데이트
        updateGamePanel();
    }
    
    private JPanel displayNumberOfCardsPanel() {
        JPanel numberOfCardsPanel = new JPanel();
        numberOfCardsPanel.setBorder(BorderFactory.createTitledBorder("Number of Cards"));

        // 각 플레이어의 남은 카드 수를 표시하는 레이블 생성
        JLabel player1CardsLabel = new JLabel("Player 1: " + unoGame.getPlayerCards(0).size() + " cards");
        JLabel player2CardsLabel = new JLabel("Player 2: " + unoGame.getPlayerCards(1).size() + " cards");
        JLabel player3CardsLabel = new JLabel("Player 3: " + unoGame.getPlayerCards(2).size() + " cards");
        JLabel player4CardsLabel = new JLabel("Player 4: " + unoGame.getPlayerCards(3).size() + " cards");

        // 레이블들을 numberOfCardsPanel에 추가
        numberOfCardsPanel.add(player1CardsLabel);
        numberOfCardsPanel.add(player2CardsLabel);
        numberOfCardsPanel.add(player3CardsLabel);
        numberOfCardsPanel.add(player4CardsLabel);

        // 레이아웃 설정 (세로로 배치)
        numberOfCardsPanel.setLayout(new BoxLayout(numberOfCardsPanel, BoxLayout.Y_AXIS));

        return numberOfCardsPanel;
    }

    private Color getColorForCard(String color) {
        // 색상에 맞는 배경색을 반환
        switch (color) {
            case "Red": return Color.RED;
            case "Green": return Color.GREEN;
            case "Blue": return Color.BLUE;
            case "Yellow": return Color.YELLOW;
            default: return Color.GRAY;  // 기본 색상
        }
    }
}



