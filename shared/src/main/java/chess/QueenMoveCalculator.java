package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoveCalculator implements ChessPieceMoveCalculator {
    private final RookMoveCalculator rookMoveCalculator = new RookMoveCalculator();
    private final BishopMoveCalculator bishopMoveCalculator = new BishopMoveCalculator();

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        validMoves.addAll(rookMoveCalculator.calculateMoves(board, position));
        validMoves.addAll(bishopMoveCalculator.calculateMoves(board, position));

        return validMoves;
    }
}