package chess;

import java.util.Collection;

public class RookMovesCalculator implements ChessMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        int [][] directions = {
                {0, 1},
                {-1, 0},
                {1, 0},
                {0, -1}
        };
        return SlidingMovesHelper.calculate(board, position, directions);
    }
}
