package hwr.oop.chess.board;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import hwr.oop.chess.Color;
import hwr.oop.chess.Position;
import hwr.oop.chess.domain.IllegalPromotionException;
import hwr.oop.chess.pieces.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChessBoardTest {
  private ChessBoard board;

  @BeforeEach
  void setup() {
    board = new ChessBoard();
  }

  @Test
  void testInitialBoardSetup() {
    SoftAssertions.assertSoftly(
        softly -> {
          // Test for Rooks
          softly
              .assertThat(board.getPieceAtPosition(new Position(0, 0)))
              .isEqualTo(new Rook(Color.WHITE, new Position(0, 0), board));
          softly
              .assertThat(board.getPieceAtPosition(new Position(0, 7)))
              .isEqualTo(new Rook(Color.WHITE, new Position(0, 7), board));
          softly
              .assertThat(board.getPieceAtPosition(new Position(7, 0)))
              .isEqualTo(new Rook(Color.BLACK, new Position(7, 0), board));
          softly
              .assertThat(board.getPieceAtPosition(new Position(7, 7)))
              .isEqualTo(new Rook(Color.BLACK, new Position(7, 7), board));

          // Test for Knights
          softly
              .assertThat(board.getPieceAtPosition(new Position(0, 1)))
              .isEqualTo(new Knight(Color.WHITE, new Position(0, 1), board));
          softly
              .assertThat(board.getPieceAtPosition(new Position(0, 6)))
              .isEqualTo(new Knight(Color.WHITE, new Position(0, 6), board));
          softly
              .assertThat(board.getPieceAtPosition(new Position(7, 1)))
              .isEqualTo(new Knight(Color.BLACK, new Position(7, 1), board));
          softly
              .assertThat(board.getPieceAtPosition(new Position(7, 6)))
              .isEqualTo(new Knight(Color.BLACK, new Position(7, 6), board));

          // Test for Bishops
          softly
              .assertThat(board.getPieceAtPosition(new Position(0, 2)))
              .isEqualTo(new Bishop(Color.WHITE, new Position(0, 2), board));
          softly
              .assertThat(board.getPieceAtPosition(new Position(0, 5)))
              .isEqualTo(new Bishop(Color.WHITE, new Position(0, 5), board));
          softly
              .assertThat(board.getPieceAtPosition(new Position(7, 2)))
              .isEqualTo(new Bishop(Color.BLACK, new Position(7, 2), board));
          softly
              .assertThat(board.getPieceAtPosition(new Position(7, 5)))
              .isEqualTo(new Bishop(Color.BLACK, new Position(7, 5), board));

          // Test for Queens
          softly
              .assertThat(board.getPieceAtPosition(new Position(0, 3)))
              .isEqualTo(new Queen(Color.WHITE, new Position(0, 3), board));
          softly
              .assertThat(board.getPieceAtPosition(new Position(7, 3)))
              .isEqualTo(new Queen(Color.BLACK, new Position(7, 3), board));

          // Test for Kings
          softly
              .assertThat(board.getPieceAtPosition(new Position(0, 4)))
              .isEqualTo(new King(Color.WHITE, new Position(0, 4), board));
          softly
              .assertThat(board.getPieceAtPosition(new Position(7, 4)))
              .isEqualTo(new King(Color.BLACK, new Position(7, 4), board));

          // Test for Pawns
          for (int i = 0; i < 8; i++) {
            softly
                .assertThat(board.getPieceAtPosition(new Position(1, i)))
                .isEqualTo(new Pawn(Color.WHITE, new Position(1, i), board));
            softly
                .assertThat(board.getPieceAtPosition(new Position(6, i)))
                .isEqualTo(new Pawn(Color.BLACK, new Position(6, i), board));
          }
        });
  }

  @Test
  void testIsPositionValid() {
    ChessBoard testBoard = new ChessBoard();

    Position position_inBounds = new Position(2, 2);
    Position position_tooLowRow = new Position(-1, 2);
    Position position_tooHighRow = new Position(8, 2);
    Position position_tooLowColumn = new Position(2, -1);
    Position position_tooHighColumn = new Position(2, 8);
    Position position_tooLowAll = new Position(-1, -1);
    Position position_tooHighAll = new Position(8, 8);

    assertThat(testBoard.isValidPosition(position_inBounds.row(), position_inBounds.column()))
        .isTrue();
    assertThat(testBoard.isValidPosition(position_tooLowRow.row(), position_tooLowRow.column()))
        .isFalse();
    assertThat(testBoard.isValidPosition(position_tooHighRow.row(), position_tooHighRow.column()))
        .isFalse();
    assertThat(
            testBoard.isValidPosition(position_tooLowColumn.row(), position_tooLowColumn.column()))
        .isFalse();
    assertThat(
            testBoard.isValidPosition(
                position_tooHighColumn.row(), position_tooHighColumn.column()))
        .isFalse();
    assertThat(testBoard.isValidPosition(position_tooLowAll.row(), position_tooLowAll.column()))
        .isFalse();
    assertThat(testBoard.isValidPosition(position_tooHighAll.row(), position_tooHighAll.column()))
        .isFalse();

    // Edge cases: testing upper bounds
    assertThat(testBoard.isValidPosition(7, 7)).isTrue(); // Upper-right corner
    assertThat(testBoard.isValidPosition(7, 0)).isTrue(); // Lower-right corner
    assertThat(testBoard.isValidPosition(0, 7)).isTrue(); // Upper-left corner
    assertThat(testBoard.isValidPosition(0, 0)).isTrue(); // Lower-left corner

    // Testing positions one step outside bounds
    assertThat(testBoard.isValidPosition(8, 7)).isFalse(); // Row out of bounds
    assertThat(testBoard.isValidPosition(7, 8)).isFalse(); // Column out of bounds
    assertThat(testBoard.isValidPosition(8, 8)).isFalse(); // Both row and column out of bounds
  }

  @Test
  void equals_IdenticalInstances() {
    ChessBoard board2 = new ChessBoard();
    assertThat(board.equals(board2)).isTrue();
  }

  @Test
  @SuppressWarnings("EqualsWithItself")
  void equals_sameInstance() {
    assertThat(board.equals(board)).isTrue();
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void equals_InstanceNull() {
    assertThat(board.equals(null)).isFalse();
  }

  @Test
  @SuppressWarnings("EqualsBetweenInconvertibleTypes")
  void equals_DifferentClass() {
    Piece piece = new Bishop(Color.BLACK, new Position(7, 5), board);
    assertThat(board.equals(piece)).isFalse();
  }

  @Test
  void equals_DifferentInstances() {
    board.setPieceAtPosition(
        new Position(4, 4), new Bishop(Color.BLACK, new Position(4, 4), board));
    ChessBoard board2 = new ChessBoard();
    assertThat(board.equals(board2)).isFalse();
  }

  @Test
  void hashCode_IdenticalHashCode() {
    ChessBoard board2 = new ChessBoard();
    assertThat(board.hashCode()).isEqualTo(board2.hashCode());
  }

  @Test
  void hashCode_DifferentHashCode() {
    board.setPieceAtPosition(
        new Position(4, 4), new Bishop(Color.BLACK, new Position(4, 4), board));
    ChessBoard board2 = new ChessBoard();
    assertThat(board.hashCode()).isNotEqualTo(board2.hashCode());
  }

  @Test
  void whiteInCheckMate() {
    board.clearChessboard();

    Position whiteKingPosition = new Position(0, 0);
    Position blackKingPosition = new Position(7, 7);
    Position blackRook1Position = new Position(0, 2);
    Position blackRook2Position = new Position(1, 2);

    Piece whiteKing = new King(Color.WHITE, whiteKingPosition, board);
    Piece blackKing = new King(Color.BLACK, blackKingPosition, board);
    Piece blackRook1 = new Rook(Color.BLACK, blackRook1Position, board);
    Piece blackRook2 = new Rook(Color.BLACK, blackRook2Position, board);

    board.setPieceAtPosition(whiteKingPosition, whiteKing);
    board.setPieceAtPosition(blackKingPosition, blackKing);
    board.setPieceAtPosition(blackRook1Position, blackRook1);
    board.setPieceAtPosition(blackRook2Position, blackRook2);

    assertThat(board.isCheckMate()).isTrue();
  }

  @Test
  void blackInCheckMate() {
    board.clearChessboard();

    Position whiteKingPosition = new Position(7, 7);
    Position blackKingPosition = new Position(0, 0);
    Position whiteRook1Position = new Position(0, 2);
    Position whiteRook2Position = new Position(1, 2);

    Piece whiteKing = new King(Color.WHITE, whiteKingPosition, board);
    Piece blackKing = new King(Color.BLACK, blackKingPosition, board);
    Piece whiteRook1 = new Rook(Color.WHITE, whiteRook1Position, board);
    Piece whiteRook2 = new Rook(Color.WHITE, whiteRook2Position, board);

    board.setPieceAtPosition(whiteKingPosition, whiteKing);
    board.setPieceAtPosition(blackKingPosition, blackKing);
    board.setPieceAtPosition(whiteRook1Position, whiteRook1);
    board.setPieceAtPosition(whiteRook2Position, whiteRook2);

    assertThat(board.isCheckMate()).isTrue();
  }

  @Test
  void whiteInCheckButNotMate() {
    board.clearChessboard();

    Position whiteKingPosition = new Position(0, 0);
    Position blackKingPosition = new Position(7, 7);
    Position blackRook1Position = new Position(0, 2);

    Piece whiteKing = new King(Color.WHITE, whiteKingPosition, board);
    Piece blackKing = new King(Color.BLACK, blackKingPosition, board);
    Piece blackRook1 = new Rook(Color.BLACK, blackRook1Position, board);

    board.setPieceAtPosition(whiteKingPosition, whiteKing);
    board.setPieceAtPosition(blackKingPosition, blackKing);
    board.setPieceAtPosition(blackRook1Position, blackRook1);

    assertThat(board.isCheckMate()).isFalse();
  }

  @Test
  void noCheckMate() {
    board.clearChessboard();

    Position whiteKingPosition = new Position(0, 0);
    Position blackKingPosition = new Position(7, 7);
    Position blackRook2Position = new Position(1, 2);

    Piece whiteKing = new King(Color.WHITE, whiteKingPosition, board);
    Piece blackKing = new King(Color.BLACK, blackKingPosition, board);
    Piece blackRook2 = new Rook(Color.BLACK, blackRook2Position, board);

    board.setPieceAtPosition(whiteKingPosition, whiteKing);
    board.setPieceAtPosition(blackKingPosition, blackKing);
    board.setPieceAtPosition(blackRook2Position, blackRook2);

    assertThat(board.isCheckMate()).isFalse();
  }

  @Test
  void getKingOfColorReturnsNull() {
    board.clearChessboard();
    King king = board.getKingOfColor(Color.WHITE);
    assertThat(king).isEqualTo(null);
  }

  @Test
  void testPromoteWhitePawnToQueen() throws IllegalMoveException {
    board.clearChessboard();

    Position whiteKingPosition = new Position(0, 0);
    Position blackKingPosition = new Position(7, 7);
    Position whitePawnPosition = new Position(7, 0);

    Piece whiteKing = new King(Color.WHITE, whiteKingPosition, board);
    Piece blackKing = new King(Color.BLACK, blackKingPosition, board);
    Piece whitePawn = new Pawn(Color.WHITE, whitePawnPosition, board);
    Piece promotedPiece = new Queen(Color.WHITE, whitePawnPosition, board);

    board.setPieceAtPosition(whiteKingPosition, whiteKing);
    board.setPieceAtPosition(blackKingPosition, blackKing);
    board.setPieceAtPosition(whitePawnPosition, whitePawn);

    board.promoteTo(whitePawnPosition, promotedPiece);

    assertThat(board.getPieceAtPosition(whitePawnPosition)).isEqualTo(promotedPiece);
  }

  @Test
  void testPromoteBlackPawnToQueen() throws IllegalMoveException {
    board.clearChessboard();

    Position whiteKingPosition = new Position(0, 0);
    Position blackKingPosition = new Position(7, 7);
    Position blackPawnPosition = new Position(0, 0);

    Piece whiteKing = new King(Color.WHITE, whiteKingPosition, board);
    Piece blackKing = new King(Color.BLACK, blackKingPosition, board);
    Piece blackPawn = new Pawn(Color.BLACK, blackPawnPosition, board);
    Piece promotedPiece = new Queen(Color.BLACK, blackPawnPosition, board);

    board.setPieceAtPosition(whiteKingPosition, whiteKing);
    board.setPieceAtPosition(blackKingPosition, blackKing);
    board.setPieceAtPosition(blackPawnPosition, blackPawn);

    board.promoteTo(blackPawnPosition, promotedPiece);

    assertThat(board.getPieceAtPosition(blackPawnPosition)).isEqualTo(promotedPiece);
  }

  @Test
  void testPromoteFailWrongRow() {
    board.clearChessboard();

    Position whiteKingPosition = new Position(0, 0);
    Position blackKingPosition = new Position(7, 7);
    Position blackPawnPosition = new Position(6, 0);

    Piece whiteKing = new King(Color.WHITE, whiteKingPosition, board);
    Piece blackKing = new King(Color.BLACK, blackKingPosition, board);
    Piece blackPawn = new Pawn(Color.BLACK, blackPawnPosition, board);
    Piece promotedPiece = new Queen(Color.BLACK, blackPawnPosition, board);

    board.setPieceAtPosition(whiteKingPosition, whiteKing);
    board.setPieceAtPosition(blackKingPosition, blackKing);
    board.setPieceAtPosition(blackPawnPosition, blackPawn);

    Position toBePromotedPosition = new Position(6, 0);
    IllegalPromotionException exception =
        assertThrows(
            IllegalPromotionException.class,
            () -> board.promoteTo(toBePromotedPosition, promotedPiece));
    String expectedMessage = "You can't promote on that row!";
    assertThat(exception.getMessage()).contains(expectedMessage);
  }

  @Test
  void testPromoteFailNoPiece() {
    board.clearChessboard();

    Position whiteKingPosition = new Position(0, 0);
    Position blackKingPosition = new Position(7, 7);
    Position blackPawnPosition = new Position(0, 0);

    Piece whiteKing = new King(Color.WHITE, whiteKingPosition, board);
    Piece blackKing = new King(Color.BLACK, blackKingPosition, board);
    Piece blackPawn = new Pawn(Color.BLACK, blackPawnPosition, board);
    Piece promotedPiece = new Queen(Color.BLACK, blackPawnPosition, board);

    board.setPieceAtPosition(whiteKingPosition, whiteKing);
    board.setPieceAtPosition(blackKingPosition, blackKing);
    board.setPieceAtPosition(blackPawnPosition, blackPawn);

    Position toBePromotedPosition = new Position(6, 0);
    IllegalPromotionException exception =
        assertThrows(
            IllegalPromotionException.class,
            () -> board.promoteTo(toBePromotedPosition, promotedPiece));
    String expectedMessage = "No piece at position!";
    assertThat(exception.getMessage()).contains(expectedMessage);
  }

  @Test
  void testPromoteFailNotPawn() {
    board.clearChessboard();

    Position whiteKingPosition = new Position(0, 0);
    Position blackKingPosition = new Position(7, 7);
    Position whiteKnightPosition = new Position(7, 0);

    Piece whiteKing = new King(Color.WHITE, whiteKingPosition, board);
    Piece blackKing = new King(Color.BLACK, blackKingPosition, board);
    Piece whiteKnight = new Knight(Color.WHITE, whiteKnightPosition, board);

    Piece promotedPiece = new Queen(Color.BLACK, whiteKingPosition, board);

    board.setPieceAtPosition(whiteKingPosition, whiteKing);
    board.setPieceAtPosition(blackKingPosition, blackKing);
    board.setPieceAtPosition(whiteKnightPosition, whiteKnight);

    IllegalPromotionException exception =
        assertThrows(
            IllegalPromotionException.class,
            () -> board.promoteTo(whiteKnightPosition, promotedPiece));
    String expectedMessage = "You can only promote pawns!";
    assertThat(exception.getMessage()).contains(expectedMessage);
  }
}
