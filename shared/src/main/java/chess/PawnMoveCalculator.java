package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

public class PawnMoveCalculator implements ChessPieceMoveCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Set<ChessMove> validMoves = new HashSet<>();
        ChessPiece piece = board.getPiece(position);
        ChessGame.TeamColor color = piece.getTeamColor();

        int direction = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (color == ChessGame.TeamColor.WHITE) ? 2 : 7;

        // Move forward one square
        if (isSquareEmpty(board, position.getRow() + direction, position.getColumn())) {
            addMoveIfValid(board, position, validMoves, position.getRow() + direction, position.getColumn(), null);

            // Initial double move
            if (position.getRow() == startRow && isSquareEmpty(board, position.getRow() + 2 * direction, position.getColumn())) {
                addMoveIfValid(board, position, validMoves, position.getRow() + 2 * direction, position.getColumn(), null);
            }
        }

        // Captures
        addCaptureMoveIfValid(board, position, validMoves, position.getRow() + direction, position.getColumn() - 1);
        addCaptureMoveIfValid(board, position, validMoves, position.getRow() + direction, position.getColumn() + 1);

        // Promotion
        int promotionRow = (color == ChessGame.TeamColor.WHITE) ? 8 : 1;
        if (position.getRow() + direction == promotionRow) {
            addPromotionMoves(board, position, validMoves, position.getRow() + direction, position.getColumn());
            addPromotionMoves(board, position, validMoves, position.getRow() + direction, position.getColumn() - 1);
            addPromotionMoves(board, position, validMoves, position.getRow() + direction, position.getColumn() + 1);
        }

        // Debug: Print generated moves
        System.out.println("Generated moves for pawn at " + position + ":");
        for (ChessMove move : validMoves) {
            System.out.println(move);
        }

        // Sort the moves
        List<ChessMove> sortedMoves = validMoves.stream()
                .sorted((m1, m2) -> m1.toString().compareTo(m2.toString()))
                .collect(Collectors.toList());

        return sortedMoves;
    }

    private void addMoveIfValid(ChessBoard board, ChessPosition start, Collection<ChessMove> validMoves, int row, int column, ChessPiece.PieceType promotion) {
        if (onBoard(row, column)) {
            ChessPosition newPosition = new ChessPosition(row, column);
            ChessMove move = new ChessMove(start, newPosition, promotion);
            System.out.println("Adding move: " + move);
            validMoves.add(move);
        }
    }

    private void addCaptureMoveIfValid(ChessBoard board, ChessPosition start, Collection<ChessMove> validMoves, int row, int column) {
        if (onBoard(row, column)) {
            ChessPosition newPosition = new ChessPosition(row, column);
            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
            if (pieceAtNewPosition != null && pieceAtNewPosition.getTeamColor() != board.getPiece(start).getTeamColor()) {
                ChessMove move = new ChessMove(start, newPosition, null);
                System.out.println("Adding capture move: " + move);
                validMoves.add(move);
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
                System.out.println("Adding promotion moves: " + moveQueen + ", " + moveRook + ", " + moveBishop + ", " + moveKnight);
                validMoves.add(moveQueen);
                validMoves.add(moveRook);
                validMoves.add(moveBishop);
                validMoves.add(moveKnight);
            }
        }
    }

    private boolean isSquareEmpty(ChessBoard board, int row, int column) {
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