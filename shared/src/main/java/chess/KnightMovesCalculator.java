package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements ChessMovesCalculator {
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        int [][] directions = {
                {1, 2},
                {1, -2},
                {-1, -2},
                {-1, 2},
                {2, 1},
                {2, -1},
                {-2, -1},
                {-2, 1}
        };
        return JumpingMovesHelper.calculate(board, position, directions);
    }
}
