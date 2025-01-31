package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMoveCalculator implements ChessPieceMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(position);
        ChessGame.TeamColor color = piece.getTeamColor();

        int direction = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (color == ChessGame.TeamColor.WHITE) ? 2 : 7;

        // Promotion both moving forward and capturing diagonally forward
        int promotionRow = (color == ChessGame.TeamColor.WHITE) ? 8 : 1;
        if (position.getRow() + direction == promotionRow) {
            // Forward move promotion
            if (positionEmpty(board, position.getRow() + direction, position.getColumn())) {
                addPromotionMoves(board, position, validMoves, position.getRow() + direction, position.getColumn());
            }
            // Diagonal capture promotions
            addCapturePromotionMoveIfValid(board, position, validMoves, position.getRow() + direction, position.getColumn() - 1);
            addCapturePromotionMoveIfValid(board, position, validMoves, position.getRow() + direction, position.getColumn() + 1);
        } else {
            // Move forward one square
            if (positionEmpty(board, position.getRow() + direction, position.getColumn())) {
                addMoveIfValid(position, validMoves, position.getRow() + direction, position.getColumn(), null);

                // Initial double move
                if (position.getRow() == startRow && positionEmpty(board, position.getRow() + 2 * direction, position.getColumn())) {
                    addMoveIfValid(position, validMoves, position.getRow() + 2 * direction, position.getColumn(), null);
                }
            }

            // Captures, forward diagonal in both directions
            addCaptureMoveIfValid(board, position, validMoves, position.getRow() + direction, position.getColumn() - 1);
            addCaptureMoveIfValid(board, position, validMoves, position.getRow() + direction, position.getColumn() + 1);
        }

        return validMoves;
    }

    private void addMoveIfValid(ChessPosition start, Collection<ChessMove> validMoves, int row, int column, ChessPiece.PieceType promotion) {
        if (onBoard(row, column)) {
            ChessPosition newPosition = new ChessPosition(row, column);
            ChessMove move = new ChessMove(start, newPosition, promotion);
            validMoves.add(move);
        }
    }

    private void addCaptureMoveIfValid(ChessBoard board, ChessPosition start, Collection<ChessMove> validMoves, int row, int column) {
        if (onBoard(row, column)) {
            ChessPosition newPosition = new ChessPosition(row, column);
            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
            if (pieceAtNewPosition != null && pieceAtNewPosition.getTeamColor() != board.getPiece(start).getTeamColor()) {
                ChessMove move = new ChessMove(start, newPosition, null);
                validMoves.add(move);
            }
        }
    }

    private void addCapturePromotionMoveIfValid(ChessBoard board, ChessPosition start, Collection<ChessMove> validMoves, int row, int column) {
        if (onBoard(row, column)) {
            ChessPosition newPosition = new ChessPosition(row, column);
            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
            if (pieceAtNewPosition != null && pieceAtNewPosition.getTeamColor() != board.getPiece(start).getTeamColor()) {
                ChessMove moveQueen = new ChessMove(start, newPosition, ChessPiece.PieceType.QUEEN);
                ChessMove moveRook = new ChessMove(start, newPosition, ChessPiece.PieceType.ROOK);
                ChessMove moveBishop = new ChessMove(start, newPosition, ChessPiece.PieceType.BISHOP);
                ChessMove moveKnight = new ChessMove(start, newPosition, ChessPiece.PieceType.KNIGHT);
                validMoves.add(moveQueen);
                validMoves.add(moveRook);
                validMoves.add(moveBishop);
                validMoves.add(moveKnight);
            }
        }
    }

    private void addPromotionMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> validMoves, int row, int column) {
        if (onBoard(row, column)) {
            ChessPosition newPosition = new ChessPosition(row, column);
            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
            if (pieceAtNewPosition == null || pieceAtNewPosition.getTeamColor() != board.getPiece(start).getTeamColor()) {
                ChessMove moveQueen = new ChessMove(start, newPosition, ChessPiece.PieceType.QUEEN);
                ChessMove moveRook = new ChessMove(start, newPosition, ChessPiece.PieceType.ROOK);
                ChessMove moveBishop = new ChessMove(start, newPosition, ChessPiece.PieceType.BISHOP);
                ChessMove moveKnight = new ChessMove(start, newPosition, ChessPiece.PieceType.KNIGHT);
                validMoves.add(moveQueen);
                validMoves.add(moveRook);
                validMoves.add(moveBishop);
                validMoves.add(moveKnight);
            }
        }
    }
    // Helper function, tells if square is empty
    private boolean positionEmpty(ChessBoard board, int row, int column) {
        if (onBoard(row, column)) {
            ChessPosition position = new ChessPosition(row, column);
            return board.getPiece(position) == null;
        }
        return false;
    }

    private boolean onBoard(int row, int column) {
        return row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }
}