package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements ChessMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        int[][] directions = {
                {-1, 1},
                {1, -1},
                {1, 1},
                {-1, -1}
        };
        return SlidingMovesHelper.calculate(board, position, directions);
    }
}
