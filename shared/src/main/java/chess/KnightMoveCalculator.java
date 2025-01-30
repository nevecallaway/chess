package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoveCalculator implements ChessPieceMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        int[] rowMoves = {-2, -1, 1, 2, 2, 1, -1, -2};
        int[] columnMoves = {1, 2, 2, 1, -1, -2, -2, -1};

        for (int i = 0; i < 8; i++) {
            int newRow = position.getRow() + rowMoves[i];
            int newColumn = position.getColumn() + columnMoves[i];

            if (onBoard(newRow, newColumn)) {
                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null || pieceAtNewPosition.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPosition, null));
                }
            }
        }
        return validMoves;
    }

    private boolean onBoard(int row, int column) {
        return row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }
}
