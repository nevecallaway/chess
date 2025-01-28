package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods
 */
public class ChessPosition {
    /* Implementation of permanent objects to store current position */
    private final int row;
    private final int column;

    public ChessPosition(int row, int col) {
        while (row < 1 || row > 8 || col < 1 || col > 8) {
            throw new IllegalArgumentException("Invalid position entered.");
        }
        this.row = row;
        this.column = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ChessPosition)) return false;
        ChessPosition other = (ChessPosition) obj;
        return this.row == other.row && this.column == other.column;
    }

    @Override
    public int hashCode() {
        return 31 * row + column;
    }
}
