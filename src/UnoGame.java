import java.io.Serializable;
import java.util.*;

public class UnoGame implements Serializable {

    private static final String[] COLORS = {"Red", "Green", "Blue", "Yellow"};
    private static final String[] VALUES = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "Skip", "Reverse", "Draw2"};

    private List<String> deck; // 전체 덱
    private List<String> player1List, player2List, player3List, player4List; // 플레이어 덱
    private String topCard; // 탑카드
    private HashMap<Integer, String> playerNum; // 플레이어 넘버 ex < 1 , guest123 >
    private List<String> turn; // turn 0번 인덱스가 현제 차례
    private HashMap<String, Boolean> isUNO; // 우노 플래그 ex < guest123, false >, 1장 남았을 떄 눌러야함

    public UnoGame() {
        // 초기화
        deck = new ArrayList<>();

        // 각 플레이어 카드 리스트 초기화
        player1List = new ArrayList<>();
        player2List = new ArrayList<>();
        player3List = new ArrayList<>();
        player4List = new ArrayList<>();
    }

    public void setPlayers(List<String> players) {
        turn = players;
        playerNum = new HashMap<Integer, String>();
        for (int i = 0; i < players.size(); i++) {
            playerNum.put(i, players.get(i));
        }
        initializeIsUNO();
    }

    // 우노 플레그 초기화
    public void initializeIsUNO() {
        isUNO = new HashMap<>(); // 새로운 HashMap 생성

        // playerNum의 모든 엔트리를 순회하면서 isUNO에 초기값 false 설정
        for (Map.Entry<Integer, String> entry : playerNum.entrySet()) {
            String playerUid = entry.getValue();  // playerNum의 값(플레이어 UID)
            isUNO.put(playerUid, false); // isUNO에서 해당 UID에 대해 false로 설정
        }
    }

    public boolean startGame(){
        if(turn != null){
            dealCards();
            return true;
        }
        return false;
    }

    public void dealCards() {
        // 덱을 초기화하고 카드 섞기
        deck = new ArrayList<>();

        // 각 플레이어 카드 리스트 초기화
        player1List = new ArrayList<>();
        player2List = new ArrayList<>();
        player3List = new ArrayList<>();
        player4List = new ArrayList<>();

        // 덱 생성
        for (String color : COLORS) {
            for (String value : VALUES) {
                deck.add(color + " " + value);
                deck.add(color + " " + value);  // 각 카드는 2번씩 존재
            }
        }

        // 덱 섞기
        Collections.shuffle(deck);

        // 4명의 플레이어에게 7장씩 나누어 주기
        for (int i = 0; i < 7; i++) {  // 각 플레이어에게 7장
            player1List.add(deck.remove(0));
            player2List.add(deck.remove(0));
            player3List.add(deck.remove(0));
            player4List.add(deck.remove(0));
        }

        // 덱에서 한 장의 카드를 뽑아서 topCard 설정
        if (!deck.isEmpty()) {
            topCard = deck.remove(0);
        }
    }

    public List<String> getDeck(){
        return deck;
    }

    public String getTopCard() {
        return topCard;
    }

    public List<String> getPlayerCards(int playerIndex) {
        switch (playerIndex) {
            case 0: return player1List;
            case 1: return player2List;
            case 2: return player3List;
            case 3: return player4List;
            default: return new ArrayList<>();
        }
    }

    public List<String> getTurn() {
        return turn;
    }
    public HashMap<String, Boolean> getIsUNO() {return isUNO;}

    public boolean playCard(String card, int playerIndex) {
        String[] cardParts = card.split(" ");
        String color = cardParts[0];
        String value = cardParts[1];

        String[] topCardParts = topCard.split(" ");
        String topColor = topCardParts[0];
        String topValue = topCardParts[1];

        if (color.equals(topColor) || value.equals(topValue)) {
            // 카드가 일치하면 플레이하고, 해당 카드 삭제
            switch (playerIndex) {
                case 0: player1List.remove(card); break;
                case 1: player2List.remove(card); break;
                case 2: player3List.remove(card); break;
                case 3: player4List.remove(card); break;
            }

            if (value.equals("Skip")) {
                jumpTurn();  // 턴 반전 호출
            }
            else if (value.equals("Reverse")) {
                reverseTurn();  // 턴 반전 호출
            }
            else if (value.equals("Draw2")) {
                String nextTurn = turn.get(1);
                int nextTurnNum = 4;

                for (Map.Entry<Integer, String> entry : playerNum.entrySet()) {
                    if (entry.getValue().equals(nextTurn)) {
                        nextTurnNum = entry.getKey(); // 번호(키)를 반환
                    }
                }

                switch (nextTurnNum) {
                    case 0:
                        player1List.add(deck.remove(0));
                        player1List.add(deck.remove(0));
                        break;
                    case 1:
                        player2List.add(deck.remove(0));
                        player2List.add(deck.remove(0));
                        break;
                    case 2:
                        player3List.add(deck.remove(0));
                        player3List.add(deck.remove(0));
                        break;
                    case 3:
                        player4List.add(deck.remove(0));
                        player4List.add(deck.remove(0));
                        break;
                }
                nextTurn();
            }else {
                nextTurn();
            }

            // topCard 갱신
            topCard = card;
            return true;
        } else {
            return false;
        }
    }

    public HashMap<Integer, String> getPlayerNumMap(){
        return playerNum;
    }

    public void drawCard(int playerIndex) {
        if (!deck.isEmpty()) {
            String drawnCard = deck.remove(0);
            switch (playerIndex) {
                case 0: player1List.add(drawnCard); break;
                case 1: player2List.add(drawnCard); break;
                case 2: player3List.add(drawnCard); break;
                case 3: player4List.add(drawnCard); break;
            }
        }
    }

    public void nextTurn() {
        // 턴 변경: turn 리스트에서 첫 번째 아이템을 맨 뒤로 보냄
        String firstPlayer = turn.get(0);
        turn.remove(0);  // 첫 번째 요소를 리스트에서 제거
        turn.add(firstPlayer);  // 첫 번째 플레이어를 리스트의 마지막에 추가
    }

    public void jumpTurn() {
        nextTurn();
        nextTurn();
    }

    public void reverseTurn() {
        // turn 리스트를 뒤집음
        Collections.reverse(turn);
    }
}