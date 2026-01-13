package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements ChessMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        int[] rowMoves = {0, 1, 1, 1, 0, -1, -1, -1};
        int[] colMoves = {-1, -1, 0, 1, 1, 1, 0, -1};

        for (int i = 0; i < 8; i++) {
            int newRow = row + rowMoves[i];
            int newCol = col + colMoves[i];

            if (!ChessPiece.onBoard(newRow, newCol)) {
                continue;
            }

            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            ChessPiece newPiece = board.getPiece(newPosition);
            if (ChessPiece.validateMove(newPosition, newPiece, board.getPiece(position))) {
                validMoves.add(new ChessMove(position, newPosition, null));
            }
        }
        return validMoves;
    }
}
