package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoveCalculator implements ChessPieceMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        // Queen: rook & bishop
        int[] rowMoves = {1, -1, 0, 0, 1, -1, -1, 1};
        int[] columnMoves = {0, 0, -1, 1, -1, 1, 1, -1};

        // Iterate through all 8 directions
        for (int i = 0; i < 8; i++) {
            int row = position.getRow();
            int column = position.getColumn();

            // Move in the current direction until colliding with the edge of the board or another piece
            while (true) {
                row += rowMoves[i];
                column += columnMoves[i];

                // If the position is off board, stop
                if (!onBoard(row, column)) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, column);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                // If empty, valid, continue
                if (pieceAtNewPosition == null) {
                    validMoves.add(new ChessMove(position, newPosition, null));
                }
                // If new position has opposing color piece, valid but stop movement
                else if (pieceAtNewPosition.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPosition, null));
                    break;
                }
                // If new position has same color piece, end movement
                else {
                    break;
                }
            }
        }

        return validMoves;
    }

    private boolean onBoard(int row, int column) {
        return row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }
}
