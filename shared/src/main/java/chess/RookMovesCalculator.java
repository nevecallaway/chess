package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements ChessMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        int startRow = position.getRow();
        int startCol = position.getColumn();
        int[] rowMoves = {0, 1, 0, -1};
        int[] colMoves = {-1, 0, 1, 0};

        for (int i = 0; i < 4; i++) {
            int row = startRow;
            int col = startCol;
            while (true) {
                row += rowMoves[i];
                col += colMoves[i];

                if (!onBoard(row, col)) {
                    break;
                }
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece newPiece = board.getPiece(newPosition);
                if (newPiece == null || newPiece.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPosition, null));
                } else {
                    break;
                }
            }
        }
        return validMoves;
    }

    private boolean onBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
