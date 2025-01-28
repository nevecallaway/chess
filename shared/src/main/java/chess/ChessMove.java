package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final int startRow;
    private final int startColumn;
    private final int endRow;
    private final int endColumn;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startRow = startPosition.getRow();
        this.startColumn = startPosition.getColumn();
        this.endRow = endPosition.getRow();
        this.endColumn = endPosition.getColumn();
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return new ChessPosition(startRow, startColumn); // Return ChessPosition with startRow and startColumn
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return new ChessPosition(endRow, endColumn); // Return ChessPosition with endRow and endColumn
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Check if comparing the same object
        if (o == null || getClass() != o.getClass()) return false; // Ensure the classes match
        ChessMove chessMove = (ChessMove) o; // Cast 'o' to ChessMove
        return startRow == chessMove.startRow &&
                startColumn == chessMove.startColumn &&
                endRow == chessMove.endRow &&
                endColumn == chessMove.endColumn &&
                Objects.equals(promotionPiece, chessMove.promotionPiece); // Compare fields
    }

    @Override
    public int hashCode() {
        // Generate a hash code using all relevant fields
        return Objects.hash(startRow, startColumn, endRow, endColumn, promotionPiece);
    }
}
