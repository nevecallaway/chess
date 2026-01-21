package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements ChessMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(position);
        ChessGame.TeamColor color = piece.getTeamColor();

        int direction = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (color == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (color == ChessGame.TeamColor.WHITE) ? 8 : 1;

        int row = position.getRow();
        int col = position.getColumn();

        // Forward (promotion, single, initial double)
        if (isEmpty(board, row + direction, col)) {
            if (row + direction == promotionRow) {
                addPromotion(position, validMoves, row + direction, col);
            } else {
                addValidMoves(position, validMoves, row + direction, col);
                if (row == startRow && isEmpty(board, row + direction * 2, col)) {
                    addValidMoves(position, validMoves, row + direction * 2, col);
                }
            }
        }
        // Diagonal captures
        int[] colMoves = {-1, 1};
        for (int i = 0; i < 2; i++) {
            if (ChessPiece.onBoard(row + direction, col + colMoves[i])) {
                ChessPosition capturePosition = new ChessPosition(row + direction, col + colMoves[i]);
                ChessPiece capturePiece = board.getPiece(capturePosition);
                if (capturePiece != null && capturePiece.getTeamColor() != color) {
                    if (row + direction == promotionRow) {
                        addValidPromotionMoves(position, validMoves, row + direction, col + colMoves[i]);
                    } else {
                        addValidMoves(position, validMoves, row + direction, col + colMoves[i]);
                    }
                }
            }
        }
        return validMoves;
    }

    private void addPromotion(ChessPosition start, Collection<ChessMove> validMoves, int row, int col) {
        if (ChessPiece.onBoard(row, col)) {
            ChessPosition newPosition = new ChessPosition(row, col);
            validMoves.add(new ChessMove(start, newPosition, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(start, newPosition, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(start, newPosition, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(start, newPosition, ChessPiece.PieceType.KNIGHT));
        }
    }

    private void addValidMoves(ChessPosition start, Collection<ChessMove> validMoves, int row, int col) {
        if (ChessPiece.onBoard(row, col)) {
            ChessPosition newPosition = new ChessPosition(row, col);
            validMoves.add(new ChessMove(start, newPosition, null));
        }
    }

    private void addValidPromotionMoves(ChessPosition start, Collection<ChessMove> validMoves, int row, int col) {
        if (ChessPiece.onBoard(row, col)) {
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessPiece.PieceType[] promotionPieces = {ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT};
            for (int i = 0; i < 4; i++) {
                validMoves.add(new ChessMove(start, newPosition, promotionPieces[i]));
            }
        }
    }

    private boolean isEmpty(ChessBoard board, int row, int col){
            if (ChessPiece.onBoard(row, col)) {
                ChessPosition position = new ChessPosition(row, col);
                return board.getPiece(position) == null;
            }
            return false;
        }
}
