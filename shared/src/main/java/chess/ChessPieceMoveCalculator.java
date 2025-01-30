package chess;

import java.util.Collection;

public interface ChessPieceMoveCalculator {
    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position);
}

