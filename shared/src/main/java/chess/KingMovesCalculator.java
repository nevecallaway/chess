package chess;

import java.util.ArrayList;
import java.util.Collection;

import static chess.ChessPiece.onBoard;

public class KingMovesCalculator implements ChessMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        int [][] directions = {
                {1, 1},
                {-1, -1},
                {-1, 1},
                {1, -1},
                {0, 1},
                {0, -1},
                {1, 0},
                {-1, 0}
        };
        return JumpingMovesHelper.calculate(board, position, directions);
    }
}
