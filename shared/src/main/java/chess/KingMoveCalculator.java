package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoveCalculator implements ChessPieceMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        //King potential moves: 1 square in any direction
        int[] rowMoves = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] columnMoves = {-1, -1, -1, 0, 0, 1, 1, 1};

        //Iterates through one square in all 8 directions
        for (int i = 0; i < 8; i++) {
            int potentialRow = position.getRow() + rowMoves[i];
            int potentialColumn = position.getColumn() + columnMoves[i];
        }
        return validMoves;
    }

    //Function onBoard checks if the row
    private boolean onBoard(int row, int column) {
        return row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }
}
