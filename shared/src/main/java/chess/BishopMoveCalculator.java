package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoveCalculator implements ChessPieceMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        // Bishop potential moves: diagonals
        int[] rowMoves = {1, 1, -1, -1};
        int[] columnMoves = {1, -1, -1, 1};

        // Iterate through all 4 diagonal directions
        for (int i = 0; i < 4; i++) {
            int row = position.getRow();
            int column = position.getColumn();

            while (true) {
                row += rowMoves[i];
                column += columnMoves[i];

                if (!onBoard(row, column)) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, column);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                // If new position empty, valid, continue
                if (pieceAtNewPosition == null) {
                    validMoves.add(new ChessMove(position, newPosition, null));
                }
                // If new position has opposing color piece, valid but stop
                else if (pieceAtNewPosition.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPosition, null));
                    break;
                }

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

