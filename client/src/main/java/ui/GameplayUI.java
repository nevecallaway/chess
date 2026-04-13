package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPiece;
import client.WebSocketManager;
import websocket.messages.LoadGameMessage;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;

import java.util.Scanner;
import java.util.Collection;

/**
 * Handles the interactive gameplay UI for the chess client.
 * Displays the board and prompts for user commands.
 */
public class GameplayUI implements WebSocketManager.ServerMessageListener {
    private final WebSocketManager wsManager;
    private final Scanner scanner;
    private final String username;
    private final String authToken;
    private final int gameID;
    private final String playerColor; // "WHITE", "BLACK", or "OBSERVER"
    
    private ChessGame currentGame;
    private boolean gameActive = true;
    private boolean gameOver = false;

    public GameplayUI(WebSocketManager wsManager, Scanner scanner, String username, String authToken, 
                      int gameID, String playerColor) {
        this.wsManager = wsManager;
        this.scanner = scanner;
        this.username = username;
        this.authToken = authToken;
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.wsManager.addListener(this);
    }

    /**
     * Main gameplay loop - displays board and processes user commands.
     */
    public boolean run() {
        displayHelp();
        
        while (gameActive) {
            try {
                System.out.print("\n> ");
                String input = scanner.nextLine().trim().toLowerCase();
                
                if (input.isEmpty()) {
                    continue;
                }

                String[] tokens = input.split("\\s+");
                String command = tokens[0];

                switch (command) {
                    case "help":
                        displayHelp();
                        break;
                    case "redraw":
                        redrawBoard();
                        break;
                    case "move":
                        handleMove(tokens);
                        break;
                    case "resign":
                        handleResign();
                        break;
                    case "leave":
                        handleLeave();
                        break;
                    case "highlight":
                        handleHighlight(tokens);
                        break;
                    default:
                        System.out.println("Unknown command: " + command + ". Type 'help' for available commands.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        wsManager.removeListener(this);
        return !gameOver;  // Return successful exit status
    }

    /**
     * Display help text for available commands.
     */
    private void displayHelp() {
        System.out.println("\nAvailable commands:");
        System.out.println("  help              - Display this help text");
        System.out.println("  redraw            - Redraw the chess board");
        System.out.println("  move <from> <to>  - Make a move (e.g., 'move e2 e4')");
        System.out.println("  highlight <pos>   - Show legal moves for any piece (e.g., 'highlight e2')");
        System.out.println("  resign            - Resign from the game");
        System.out.println("  leave             - Leave the game");
    }

    /**
     * Redraw the current game board.
     */
    private void redrawBoard() {
        if (currentGame == null) {
            System.out.println("Game not yet loaded");
            return;
        }

        System.out.println();
        if (playerColor.equals("BLACK")) {
            ChessBoardUI.displayBoardBlackPerspective(currentGame);
        } else {
            ChessBoardUI.displayBoardWhitePerspective(currentGame);
        }
    }

    /**
     * Handle make move command.
     */
    private void handleMove(String[] tokens) throws Exception {
        if (playerColor.equals("OBSERVER")) {
            System.out.println("Observers cannot make moves");
            return;
        }

        if (gameOver) {
            System.out.println("Game is over, no more moves can be made");
            return;
        }

        if (tokens.length < 3) {
            System.out.println("Usage: move <from> <to>");
            System.out.println("Example: move e2 e4");
            return;
        }

        try {
            String fromStr = tokens[1];
            String toStr = tokens[2];
            String promotionStr = tokens.length > 3 ? tokens[3].toUpperCase() : null;

            ChessPosition from = parsePosition(fromStr);
            ChessPosition to = parsePosition(toStr);

            ChessPiece.PieceType promotionPiece = null;
            if (promotionStr != null) {
                try {
                    promotionPiece = ChessPiece.PieceType.valueOf(promotionStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid promotion piece: " + promotionStr);
                    return;
                }
            }

            ChessMove move = new ChessMove(from, to, promotionPiece);
            wsManager.makeMove(authToken, gameID, move);

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid position format. Use letters a-h and numbers 1-8 (e.g., e2 e4)");
        }
    }

    /**
     * Handle highlight legal moves command.
     * Shows legal moves for any piece on the board (own or opponent).
     */
    private void handleHighlight(String[] tokens) {
        if (currentGame == null) {
            System.out.println("Game not yet loaded");
            return;
        }

        if (tokens.length < 2) {
            System.out.println("Usage: highlight <position>");
            System.out.println("Example: highlight e2");
            return;
        }

        try {
            ChessPosition position = parsePosition(tokens[1]);
            ChessPiece piece = currentGame.getBoard().getPiece(position);

            if (piece == null) {
                System.out.println("No piece at position " + tokens[1]);
                return;
            }

            // Get valid moves for the piece (works for any piece)
            Collection<ChessMove> validMoves = currentGame.validMoves(position);

            if (validMoves == null || validMoves.isEmpty()) {
                System.out.println("No legal moves available for the piece at " + tokens[1]);
                return;
            }

            // Display piece info and legal moves
            String colorStr = piece.getTeamColor().toString();
            String typeStr = piece.getPieceType().toString();
            System.out.println("\nHighlighting moves for " + colorStr + " " + typeStr + " at " + tokens[1] + ":");
            displayBoardWithHighlights(position, validMoves);

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid position format. Use letters a-h and numbers 1-8 (e.g., e2)");
        }
    }

    /**
     * Display board with highlighted squares for valid moves.
     */
    private void displayBoardWithHighlights(ChessPosition piecePos, Collection<ChessMove> validMoves) {
        String colorStr = currentGame.getBoard().getPiece(piecePos).getTeamColor().toString();
        String typeStr = currentGame.getBoard().getPiece(piecePos).getPieceType().toString();
        System.out.println("\nHighlighting legal moves for " + colorStr + " " + typeStr + " at " + positionToString(piecePos) + ":");
        
        // Display board with visual highlights
        if (playerColor.equals("BLACK")) {
            ChessBoardUI.displayBoardWithHighlightsBlackPerspective(currentGame, piecePos, validMoves);
        } else {
            ChessBoardUI.displayBoardWithHighlightsWhitePerspective(currentGame, piecePos, validMoves);
        }
        
        // Also display the destinations as a list for reference
        System.out.println("Legal destination squares:");
        for (ChessMove move : validMoves) {
            System.out.print("  → " + positionToString(move.getEndPosition()));
            
            // Check if destination has an opponent piece
            ChessPiece targetPiece = currentGame.getBoard().getPiece(move.getEndPosition());
            if (targetPiece != null) {
                System.out.print(" (captures " + targetPiece.getTeamColor() + " " + targetPiece.getPieceType() + ")");
            }
            
            // Check if this is a promotion move
            if (move.getPromotionPiece() != null) {
                System.out.print(" (promotes to " + move.getPromotionPiece() + ")");
            }
            
            System.out.println();
        }
    }

    /**
     * Handle resign command.
     */
    private void handleResign() throws Exception {
        if (playerColor.equals("OBSERVER")) {
            System.out.println("Observers cannot resign");
            return;
        }

        System.out.print("Are you sure you want to resign? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes")) {
            wsManager.resign(authToken, gameID);
            gameActive = false;
            gameOver = true;
        } else {
            System.out.println("Resignation cancelled");
        }
    }

    /**
     * Handle leave command.
     */
    private void handleLeave() throws Exception {
        wsManager.leave(authToken, gameID);
        gameActive = false;
    }

    /**
     * Parse chess notation position (e.g., "e2") to ChessPosition.
     * @throws IllegalArgumentException if position is invalid
     */
    private ChessPosition parsePosition(String notation) throws IllegalArgumentException {
        if (notation == null || notation.length() != 2) {
            throw new IllegalArgumentException("Invalid position: " + notation);
        }

        char colChar = notation.toLowerCase().charAt(0);
        char rowChar = notation.charAt(1);

        if (colChar < 'a' || colChar > 'h') {
            throw new IllegalArgumentException("Column must be a-h");
        }

        if (rowChar < '1' || rowChar > '8') {
            throw new IllegalArgumentException("Row must be 1-8");
        }

        int col = colChar - 'a' + 1;  // 'a'→1, 'b'→2, ..., 'h'→8
        int row = rowChar - '0';       // '1'→1, '2'→2, ..., '8'→8

        return new ChessPosition(row, col);
    }

    /**
     * Convert ChessPosition to chess notation string (e.g., "e2").
     */
    private String positionToString(ChessPosition pos) {
        char col = (char) ('a' + pos.getColumn() - 1);
        return "" + col + pos.getRow();
    }

    // WebSocketManager.ServerMessageListener Implementation

    @Override
    public void onLoadGame(LoadGameMessage message) {
        currentGame = (ChessGame) message.getGame();
        System.out.println("\n[Board updated]");
        redrawBoard();
    }

    @Override
    public void onError(ErrorMessage message) {
        System.out.println("\nError: " + message.getErrorMessage());
    }

    @Override
    public void onNotification(NotificationMessage message) {
        System.out.println("\n[" + message.getMessage() + "]");
        
        // Check if game is over based on notification content
        String msg = message.getMessage().toLowerCase();
        if (msg.contains("resigned") || msg.contains("checkmate") || msg.contains("stalemate")) {
            gameOver = true;
        }
    }
}
