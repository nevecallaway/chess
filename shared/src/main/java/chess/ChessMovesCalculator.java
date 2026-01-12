package chess;

import java.util.Collection;

public interface ChessMovesCalculator {
    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position);
}
