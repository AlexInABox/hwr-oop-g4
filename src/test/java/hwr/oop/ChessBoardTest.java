package hwr.oop;

import hwr.oop.board.ChessBoard;
import hwr.oop.board.ChessBoardException;
import hwr.oop.pieces.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static hwr.oop.board.ChessBoard.convertInputToPosition;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
  void convertInputToPosition_Valid() {
    SoftAssertions.assertSoftly(
            softly -> {
              try {
                softly.assertThat(convertInputToPosition("a8")).isEqualTo(new Position(7, 0));
                softly.assertThat(convertInputToPosition("h1")).isEqualTo(new Position(0, 7));
                softly.assertThat(convertInputToPosition("e5")).isEqualTo(new Position(4, 4));
              } catch (ChessBoardException e) {
                throw new RuntimeException(e);
              }
            });
  }

  @Test
  void convertInputToPosition_InvalidFormat() {
    assertThrows(ChessBoardException.class, () -> convertInputToPosition(""));
    assertThrows(ChessBoardException.class, () -> convertInputToPosition("a"));
    assertThrows(ChessBoardException.class, () -> convertInputToPosition("abc"));
    assertThrows(ChessBoardException.class, () -> convertInputToPosition("a12"));
    assertThrows(ChessBoardException.class, () -> convertInputToPosition("12"));
    assertThrows(ChessBoardException.class, () -> convertInputToPosition("abc12"));
  }

  @Test
  void testInvalidPosition() {
    assertThrows(ChessBoardException.class, () -> convertInputToPosition("i1"));
    assertThrows(ChessBoardException.class, () -> convertInputToPosition("a0"));
    assertThrows(ChessBoardException.class, () -> convertInputToPosition("a9"));
    assertThrows(ChessBoardException.class, () -> convertInputToPosition("h9"));
  }

  @Test
  void testIsPositionValid() {
    ChessBoard board = new ChessBoard();

    Position position_inBounds = new Position(2, 2);
    Position position_tooLowRow = new Position(-1, 2);
    Position position_tooHighRow = new Position(8, 2);
    Position position_tooLowColumn = new Position(2, -1);
    Position position_tooHighColumn = new Position(2, 8);
    Position position_tooLowAll = new Position(-1, -1);
    Position position_tooHighAll = new Position(8, 8);

    assertThat(board.isValidPosition(position_inBounds.row(), position_inBounds.column())).isTrue();
    assertThat(board.isValidPosition(position_tooLowRow.row(), position_tooLowRow.column())).isFalse();
    assertThat(board.isValidPosition(position_tooHighRow.row(), position_tooHighRow.column())).isFalse();
    assertThat(board.isValidPosition(position_tooLowColumn.row(), position_tooLowColumn.column())).isFalse();
    assertThat(board.isValidPosition(position_tooHighColumn.row(), position_tooHighColumn.column())).isFalse();
    assertThat(board.isValidPosition(position_tooLowAll.row(), position_tooLowAll.column())).isFalse();
    assertThat(board.isValidPosition(position_tooHighAll.row(), position_tooHighAll.column())).isFalse();

    // Edge cases: testing upper bounds
    assertThat(board.isValidPosition(7, 7)).isTrue(); // Upper-right corner
    assertThat(board.isValidPosition(7, 0)).isTrue(); // Lower-right corner
    assertThat(board.isValidPosition(0, 7)).isTrue(); // Upper-left corner
    assertThat(board.isValidPosition(0, 0)).isTrue(); // Lower-left corner

    // Testing positions one step outside bounds
    assertThat(board.isValidPosition(8, 7)).isFalse(); // Row out of bounds
    assertThat(board.isValidPosition(7, 8)).isFalse(); // Column out of bounds
    assertThat(board.isValidPosition(8, 8)).isFalse(); // Both row and column out of bounds
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
}
