package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMoveCalculator implements ChessPieceMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        // Rook potential moves: horizontally and vertically
        int[] rowMoves = {1, -1, 0, 0};
        int[] columnMoves = {0, 0, -1, 1};

        // Up, down, left, right
        for (int i = 0; i < 4; i++) {
            int row = position.getRow();
            int column = position.getColumn();

            // Move in direction until colliding w/ board edge or another pawn
            while (true) {
                row += rowMoves[i];
                column += columnMoves[i];

                // On board?
                if (!onBoard(row, column)) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, column);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                // If new position empty, valid, continue
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

    // On board?
    private boolean onBoard(int row, int column) {
        return row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }
}
