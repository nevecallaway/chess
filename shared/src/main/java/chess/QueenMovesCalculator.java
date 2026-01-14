package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements ChessMovesCalculator {
    private final RookMovesCalculator rookMoves = new RookMovesCalculator();
    private final BishopMovesCalculator bishopMoves = new BishopMovesCalculator();
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        validMoves.addAll(rookMoves.calculateMoves(board, position));
        validMoves.addAll(bishopMoves.calculateMoves(board, position));

        return validMoves;
    }
}