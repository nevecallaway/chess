package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.onBoard;

public class SlidingMovesHelper {
    public static Collection<ChessMove> calculate(
            ChessBoard board,
            ChessPosition position,
            int[][] directions
    ) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        int startRow = position.getRow();
        int startCol = position.getColumn();
        ChessPiece oldPiece = board.getPiece(position);

        for (int i = 0; i < directions.length; i++) {
            int row = startRow + directions[i][0];
            int col = startCol + directions[i][1];
            while (onBoard(row, col)) {
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece newPiece = board.getPiece(newPosition);
                if (newPiece == null) {
                    validMoves.add(new ChessMove(position, newPosition, null));
                } else {
                    if (newPiece.getTeamColor() != oldPiece.getTeamColor()) {
                        validMoves.add(new ChessMove(position, newPosition, null));
                    }
                    break;
                }
                row += directions[i][0];
                col += directions[i][1];
            }
        }
        return validMoves;
    }
}
