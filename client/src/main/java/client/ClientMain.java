package client;

import chess.*;
import ui.ChessBoardUI;

public class ClientMain {
    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        
        System.out.println("WHITE PERSPECTIVE");
        ChessBoardUI.displayBoardWhitePerspective(game);
        
        System.out.println("BLACK PERSPECTIVE");
        ChessBoardUI.displayBoardBlackPerspective(game);
    }
}
