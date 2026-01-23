package chess;

import java.util.Collection;

public class QueenMovesCalculator implements ChessMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        int[][] directions = {
                {1, 1},
                {-1, -1},
                {-1, 1},
                {1, -1},
                {0, 1},
                {0, -1},
                {1, 0},
                {-1, 0}
        };
        return SlidingMovesHelper.calculate(board, position, directions);
    }
}
