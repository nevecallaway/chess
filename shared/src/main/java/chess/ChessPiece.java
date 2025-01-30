    package chess;

    import java.util.Collection;

    /**
     * Represents a single chess piece
     * <p>
     * Note: You can add to this class, but you may not alter
     * signature of the existing methods.
     */
    public class ChessPiece {
        private final ChessGame.TeamColor teamColor;
        private final PieceType pieceType;

        public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
            this.teamColor = pieceColor;
            this.pieceType = type;
        }

        /**
         * The various different chess piece options
         */
        public enum PieceType {
            KING,
            QUEEN,
            BISHOP,
            KNIGHT,
            ROOK,
            PAWN
        }

        /**
         * @return Which team this chess piece belongs to
         */
        public ChessGame.TeamColor getTeamColor() {
            return teamColor;
        }

        /**
         * @return which type of chess piece this piece is
         */
        public PieceType getPieceType() {
            return pieceType;
        }

        /**
         * Calculates all the positions a chess piece can move to
         * Does not take into account moves that are illegal due to leaving the king in
         * danger
         *
         * @return Collection of valid moves
         */
        public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
            ChessPieceMoveCalculator moves = switch(getPieceType()) {
                case KING -> new KingMoveCalculator();
                case ROOK -> new RookMoveCalculator();
                case BISHOP -> new BishopMoveCalculator();
                case QUEEN -> new QueenMoveCalculator();
                case KNIGHT -> new KnightMoveCalculator();
                case PAWN -> new PawnMoveCalculator();
            };
            return moves.calculateMoves(board, myPosition);
        }
    }
