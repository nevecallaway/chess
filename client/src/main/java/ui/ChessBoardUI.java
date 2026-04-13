package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.ChessMove;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ChessBoardUI {
    
    /**
     * Displays the chess board from white's perspective (a1 at bottom-left)
     */
    public static void displayBoardWhitePerspective(ChessGame game) {
        displayBoard(game, ChessGame.TeamColor.WHITE);
    }
    
    /**
     * Displays the chess board from black's perspective (a1 at upper-right)
     */
    public static void displayBoardBlackPerspective(ChessGame game) {
        displayBoard(game, ChessGame.TeamColor.BLACK);
    }
    
    private static void displayBoard(ChessGame game, ChessGame.TeamColor perspective) {
        ChessBoard board = game.getBoard();
        
        if (perspective == ChessGame.TeamColor.WHITE) {
            displayWhiteBoard(board);
        } else {
            displayBlackBoard(board);
        }
    }
    
    private static void displayWhiteBoard(ChessBoard board) {
        System.out.println();  // Blank line for spacing
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "    a  b  c  d  e  f  g  h" + 
                         EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
        
        // Rows 8 down to 1
        for (int row = 8; row >= 1; row--) {
            displayBoardRow(board, row, false);
        }
        
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "    a  b  c  d  e  f  g  h" + 
                         EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
        System.out.println();  // Blank line for spacing
    }
    
    private static void displayBlackBoard(ChessBoard board) {
        System.out.println();  // Blank line for spacing
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "    h  g  f  e  d  c  b  a" + 
                         EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
        
        // Rows 1 to 8 (reversed from white perspective)
        for (int row = 1; row <= 8; row++) {
            displayBoardRow(board, row, true);
        }
        
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "    h  g  f  e  d  c  b  a" + 
                         EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
        System.out.println();  // Blank line for spacing
    }
    
    private static void displayBoardRow(ChessBoard board, int row, boolean reversed) {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + " " + row + " " + EscapeSequences.RESET_TEXT_COLOR);
        
        int startCol = reversed ? 8 : 1;
        int endCol = reversed ? 0 : 9;
        int increment = reversed ? -1 : 1;
        
        for (int col = startCol; col != endCol; col += increment) {
            ChessPosition position = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(position);
            
            String bgColor = ((row + col) % 2 == 0) ? 
                EscapeSequences.SET_BG_COLOR_DARK_GREY : EscapeSequences.SET_BG_COLOR_WHITE;
            String pieceDisplay = getPieceDisplay(piece);
            
            System.out.print(bgColor + pieceDisplay + EscapeSequences.RESET_BG_COLOR);
        }
        
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + " " + row + EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
    }
    
    private static String getPieceDisplay(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }
        
        String textColor = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 
            EscapeSequences.SET_TEXT_COLOR_RED : EscapeSequences.SET_TEXT_COLOR_BLUE;
        
        String pieceSymbol = switch (piece.getPieceType()) {
            case KING -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 
                EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 
                EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case ROOK -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 
                EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case BISHOP -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 
                EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 
                EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case PAWN -> (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 
                EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
        
        return textColor + pieceSymbol + EscapeSequences.RESET_TEXT_COLOR;
    }
    
    /**
     * Displays the chess board with highlighted squares from white's perspective.
     * The piece's current square is highlighted in yellow, legal moves in green.
     */
    public static void displayBoardWithHighlightsWhitePerspective(ChessGame game, ChessPosition piecePos, Collection<ChessMove> validMoves) {
        displayBoardWithHighlights(game, ChessGame.TeamColor.WHITE, piecePos, validMoves);
    }
    
    /**
     * Displays the chess board with highlighted squares from black's perspective.
     * The piece's current square is highlighted in yellow, legal moves in green.
     */
    public static void displayBoardWithHighlightsBlackPerspective(ChessGame game, ChessPosition piecePos, Collection<ChessMove> validMoves) {
        displayBoardWithHighlights(game, ChessGame.TeamColor.BLACK, piecePos, validMoves);
    }
    
    private static void displayBoardWithHighlights(ChessGame game, ChessGame.TeamColor perspective, 
                                                   ChessPosition piecePos, Collection<ChessMove> validMoves) {
        ChessBoard board = game.getBoard();
        Set<ChessPosition> highlightedPositions = new HashSet<>();
        
        // Add all destination positions to the highlight set
        for (ChessMove move : validMoves) {
            highlightedPositions.add(move.getEndPosition());
        }
        
        if (perspective == ChessGame.TeamColor.WHITE) {
            displayWhiteBoardWithHighlights(board, piecePos, highlightedPositions);
        } else {
            displayBlackBoardWithHighlights(board, piecePos, highlightedPositions);
        }
    }
    
    private static void displayWhiteBoardWithHighlights(ChessBoard board, ChessPosition piecePos, Set<ChessPosition> highlightedPositions) {
        System.out.println();  // Blank line for spacing
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "    a  b  c  d  e  f  g  h" + 
                         EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
        
        // Rows 8 down to 1
        for (int row = 8; row >= 1; row--) {
            displayBoardRowWithHighlights(board, row, false, piecePos, highlightedPositions);
        }
        
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "    a  b  c  d  e  f  g  h" + 
                         EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
        System.out.println();  // Blank line for spacing
    }
    
    private static void displayBlackBoardWithHighlights(ChessBoard board, ChessPosition piecePos, Set<ChessPosition> highlightedPositions) {
        System.out.println();  // Blank line for spacing
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "    h  g  f  e  d  c  b  a" + 
                         EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
        
        // Rows 1 to 8 (reversed from white perspective)
        for (int row = 1; row <= 8; row++) {
            displayBoardRowWithHighlights(board, row, true, piecePos, highlightedPositions);
        }
        
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + "    h  g  f  e  d  c  b  a" + 
                         EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
        System.out.println();  // Blank line for spacing
    }
    
    private static void displayBoardRowWithHighlights(ChessBoard board, int row, boolean reversed, 
                                                     ChessPosition piecePos, Set<ChessPosition> highlightedPositions) {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + " " + row + " " + EscapeSequences.RESET_TEXT_COLOR);
        
        int startCol = reversed ? 8 : 1;
        int endCol = reversed ? 0 : 9;
        int increment = reversed ? -1 : 1;
        
        for (int col = startCol; col != endCol; col += increment) {
            ChessPosition position = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(position);
            
            // Determine background color
            String bgColor;
            if (position.equals(piecePos)) {
                // Selected piece's current square - highlight in yellow
                bgColor = EscapeSequences.SET_BG_COLOR_YELLOW;
            } else if (highlightedPositions.contains(position)) {
                // Legal destination square - highlight in green
                bgColor = EscapeSequences.SET_BG_COLOR_GREEN;
            } else {
                // Normal chess board coloring
                bgColor = ((row + col) % 2 == 0) ? 
                    EscapeSequences.SET_BG_COLOR_DARK_GREY : EscapeSequences.SET_BG_COLOR_WHITE;
            }
            
            String pieceDisplay = getPieceDisplay(piece);
            System.out.print(bgColor + pieceDisplay + EscapeSequences.RESET_BG_COLOR);
        }
        
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + " " + row + EscapeSequences.RESET_TEXT_COLOR);
        System.out.println();
    }
}
