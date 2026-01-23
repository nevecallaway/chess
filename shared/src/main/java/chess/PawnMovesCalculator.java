package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements ChessMovesCalculator {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(position);
        ChessGame.TeamColor color = piece.getTeamColor();

        int direction = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (color == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (color == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int row = position.getRow();
        int col = position.getColumn();

        //Forward moves
        if (isEmpty(board, row + direction, col)) {
            if (row + direction == promotionRow) {
                addPromotion(position, row + direction, col, validMoves);
            } else {
                addValidMoves(position, row + direction, col, validMoves);
                if (row == startRow && isEmpty(board, row + direction * 2, col)) {
                    addValidMoves(position, row + direction * 2, col, validMoves);
                }
            }
        }

        //Diagonal captures
        int[] colMoves = {-1, 1};
        for (int i = 0; i < 2; i++) {
            if(ChessPiece.onBoard(row + direction, col + colMoves[i])) {
                ChessPosition newPosition = new ChessPosition(row + direction, col + colMoves[i]);
                ChessPiece capturePiece = board.getPiece(newPosition);
                if (capturePiece != null && capturePiece.getTeamColor() != color) {
                    if (row + direction == promotionRow) {
                        addPromotion(position, row + direction, col + colMoves[i], validMoves);
                    } else {
                        addValidMoves(position, row + direction, col + colMoves[i], validMoves);
                    }
                }
            }
        }

        return validMoves;
    }

    public boolean isEmpty (ChessBoard board, int row, int col) {
        if (ChessPiece.onBoard(row, col)) {
            ChessPosition newPosition = new ChessPosition(row, col);
            return board.getPiece(newPosition) == null;
        }
        return false;
    }

    public void addValidMoves (ChessPosition position, int row, int col, Collection<ChessMove> validMoves) {
        if (ChessPiece.onBoard(row, col)) {
            ChessPosition newPosition = new ChessPosition(row, col);
            validMoves.add(new ChessMove(position, newPosition, null));
        }
    }

    public void addPromotion(ChessPosition position, int row, int col, Collection<ChessMove> validMoves) {
        if (ChessPiece.onBoard(row, col)) {
            ChessPosition newPosition = new ChessPosition(row, col);
            validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
        }
    }
}
