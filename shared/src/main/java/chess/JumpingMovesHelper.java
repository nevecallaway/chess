package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.onBoard;

public class JumpingMovesHelper {
    public static Collection<ChessMove> calculate (
            ChessBoard board,
            ChessPosition position,
            int[][] directions
    ) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (int i = 0; i < directions.length; i++) {
            int oldRow = position.getRow();
            int oldCol = position.getColumn();
            ChessPiece oldPiece = board.getPiece(position);

            int newRow = oldRow + directions[i][0];
            int newCol = oldCol + directions[i][1];

            if (onBoard(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece newPiece = board.getPiece(newPosition);
                if (newPiece == null || newPiece.getTeamColor() != oldPiece.getTeamColor()) {
                    validMoves.add(new ChessMove(position, newPosition, null));
                }
            }
        }
        return validMoves;
    }
}
