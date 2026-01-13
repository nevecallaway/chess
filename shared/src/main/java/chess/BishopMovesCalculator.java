package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements ChessMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int startRow = position.getRow();
        int startCol = position.getColumn();
        int[] rowMoves = {-1, 1, -1, 1};
        int[] colMoves = {-1, 1, 1, -1};

        for (int i = 0; i < 4; i++) {

            int row = startRow + rowMoves[i];
            int col = startCol + colMoves[i];

            while (ChessPiece.onBoard(row, col)) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece newPiece = board.getPiece(newPosition);
                if (!ChessPiece.validateMove(newPosition, newPiece, board.getPiece(position))) {
                    break;
                }
                validMoves.add(new ChessMove(position, newPosition, null));
                if (newPiece != null) {
                    break;
                }
                row += rowMoves[i];
                col += colMoves[i];
            }
        }
        return validMoves;
    }
}
