import javax.swing.*;
import java.io.Serializable;

public class GamePacket implements Serializable {
    public static final int MODE_LOGIN = 0x1;
    public static final int MODE_LOGOUT = 0x2;

    public static final int MODE_TX_STRING = 0x10;
    public static final int MODE_BROAD_STRING = 0x11;

    public static final int MODE_TX_IMAGE = 0x30;

    public static final int MODE_ROOM_ADD = 0x40;
    public static final int MODE_ROOM_COUNT = 0x41;
    public static final int MODE_ROOM_JOIN = 0x42;
    public static final int MODE_ROOM_READY = 0x43;
    public static final int MODE_ROOM_INFO = 0x44;
    public static final int MODE_ROOM_DELETE = 0x45;

    public static final int MODE_UNO_START = 0x50;
    public static final int MODE_UNO_UPDATE = 0x51;
    public static final int MODE_UNO_GAME_OVER = 0x52;

    private String userID;
    private int mode;
    private String message;
    private ImageIcon image;
    private UnoGame uno;
    private int roomCount;
    private Integer roomReady;
    private Integer roomJoin;
    private int roomNum;
    private int participantsCount;

    public GamePacket(String userID, int mode, String message, ImageIcon image, UnoGame uno, int roomCount, Integer roomReady, Integer roomJoin, int roomNum, int participantsCount) {
        this.userID = userID;
        this.mode = mode;
        this.message = message;
        this.image = image;
        this.uno = uno;
        this.roomCount = roomCount;
        this.roomReady = roomReady;
        this.roomJoin = roomJoin;
        this.roomNum = roomNum;
        this.participantsCount = participantsCount;
    }

    public GamePacket(String userID, int mode, String message, int roomNum) {
        this(userID, mode, message, null, null, 0, 0, 0, roomNum, 0);
    }

    public GamePacket(String userID, int mode, String message, ImageIcon image, int roomNum) {
        this(userID, mode, message, image, null, 0, 0, 0, roomNum, 0);
    }

    public GamePacket(String userID, int mode, UnoGame uno, int roomNum) {
        this(userID, mode, null, null, uno, 0, 0, 0, roomNum, 0);
    }

    public GamePacket(String userID, int mode, Integer roomReady, Integer roomJoin, int roomNum) {
        this(userID, mode, null, null, null, 0, roomReady, roomJoin, roomNum, 0);
    }

    // Getter
    public String getUserID() {
        return userID;
    }

    public int getMode() {
        return mode;
    }

    public String getMessage() {
        return message;
    }

    public ImageIcon getImage() {
        return image;
    }

    public UnoGame getUno() {
        return uno;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public Integer getRoomReady() {
        return roomReady;
    }

    public Integer getRoomJoin() {
        return roomJoin;
    }

    public int getRoomNum() {
        return roomNum;
    }

    public int getParticipantsCount() {
        return participantsCount;
    }
}
