package hwr.oop;

import hwr.oop.exceptions.ChessBoardException;
import hwr.oop.exceptions.MovePieceException;
import hwr.oop.pieces.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static hwr.oop.ChessBoard.convertInputToPosition;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class ChessBoardTest {
  private ChessBoard board;
  List<List<Piece>> actualBoard;

  @BeforeEach
  void setup(){
    board = new ChessBoard();
    actualBoard = board.getBoard();
  }

  @Test
  void testInitialBoardSetup() {

    // Test for Rooks
    assertThat(actualBoard.get(0).get(0)).usingRecursiveComparison().isEqualTo(new Rook(Color.BLACK, new Position(0, 0)));
    assertThat(actualBoard.get(0).get(7)).usingRecursiveComparison().isEqualTo(new Rook(Color.BLACK, new Position(0, 7)));
    assertThat(actualBoard.get(7).get(0)).usingRecursiveComparison().isEqualTo(new Rook(Color.WHITE, new Position(7, 0)));
    assertThat(actualBoard.get(7).get(7)).usingRecursiveComparison().isEqualTo(new Rook(Color.WHITE, new Position(7, 7)));

    // Test for Knights
    assertThat(actualBoard.get(0).get(1)).usingRecursiveComparison().isEqualTo(new Knight(Color.BLACK, new Position(0, 1)));
    assertThat(actualBoard.get(0).get(6)).usingRecursiveComparison().isEqualTo(new Knight(Color.BLACK, new Position(0, 6)));
    assertThat(actualBoard.get(7).get(1)).usingRecursiveComparison().isEqualTo(new Knight(Color.WHITE, new Position(7, 1)));
    assertThat(actualBoard.get(7).get(6)).usingRecursiveComparison().isEqualTo(new Knight(Color.WHITE, new Position(7, 6)));

    // Test for Bishops
    assertThat(actualBoard.get(0).get(2)).usingRecursiveComparison().isEqualTo(new Bishop(Color.BLACK, new Position(0, 2)));
    assertThat(actualBoard.get(0).get(5)).usingRecursiveComparison().isEqualTo(new Bishop(Color.BLACK, new Position(0, 5)));
    assertThat(actualBoard.get(7).get(2)).usingRecursiveComparison().isEqualTo(new Bishop(Color.WHITE, new Position(7, 2)));
    assertThat(actualBoard.get(7).get(5)).usingRecursiveComparison().isEqualTo(new Bishop(Color.WHITE, new Position(7, 5)));

    // Test for Queens
    assertThat(actualBoard.get(0).get(3)).usingRecursiveComparison().isEqualTo(new Queen(Color.BLACK, new Position(0, 3)));
    assertThat(actualBoard.get(7).get(3)).usingRecursiveComparison().isEqualTo(new Queen(Color.WHITE, new Position(7, 3)));

    // Test for Kings
    assertThat(actualBoard.get(0).get(4)).usingRecursiveComparison().isEqualTo(new King(Color.BLACK, new Position(0, 4)));
    assertThat(actualBoard.get(7).get(4)).usingRecursiveComparison().isEqualTo(new King(Color.WHITE, new Position(7, 4)));

    // Test for Pawns
    for (int i = 0; i < 8; i++) {
      assertThat(actualBoard.get(1).get(i)).usingRecursiveComparison().isEqualTo(new Pawn(Color.BLACK, new Position(1, i)));
      assertThat(actualBoard.get(6).get(i)).usingRecursiveComparison().isEqualTo(new Pawn(Color.WHITE, new Position(6, i)));
    }
  }


//  @Test
//  void testPrintChessBoard() {
//    board = new ChessBoard();
//    printChessBoard(board.getBoard());
//    ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
//    System.setOut(new PrintStream(outputStreamCaptor));
//    printChessBoard(board.getBoard());
//    String expectedOutput =
//            """
//                  a b c d e f g h\r
//                +-----------------+\r
//               8| r n b q k b n r |\r
//               7| p p p p p p p p |\r
//               6| . . . . . . . . |\r
//               5| . . . . . . . . |\r
//               4| . . . . . . . . |\r
//               3| . . . . . . . . |\r
//               2| P P P P P P P P |\r
//               1| R N B Q K B N R |\r
//                +-----------------+\r
//                """;
//    assertThat(outputStreamCaptor.toString()).hasToString(expectedOutput);
//  }


  @Test
  void convertInputToPosition_Valid() throws ChessBoardException {
    assertThat(convertInputToPosition("a8")).isEqualTo(new Position(0, 0));
    assertThat(convertInputToPosition("h1")).isEqualTo(new Position(7, 7));
    assertThat(convertInputToPosition("e5")).isEqualTo(new Position(3, 4));
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
  void movePiece_Successful() throws MovePieceException {
    Position from = new Position(6,0);
    Position to = new Position(5,0);
    assertThat(board.movePiece(from, to)).isTrue();
    assertThat(actualBoard.get(6).getFirst()).isNull();
    assertThat(actualBoard.get(5).getFirst()).usingRecursiveComparison().isEqualTo(new Pawn(Color.WHITE, new Position(5,0)));
  }

  @Test
  void movePiece_Fail_OutsideBoardNegativeColumn(){
    Position from = new Position(6,0); //a2
    Position to = new Position(-1,0); //Outside Board
    MovePieceException exception = assertThrows(MovePieceException.class, () -> board.movePiece(from, to));
    String expectedMessage = "Invalid destination position!";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(actualBoard.get(6).getFirst()).usingRecursiveComparison().isEqualTo(new Pawn(Color.WHITE, new Position(6,0)));
  }

  @Test
  void movePiece_Fail_OutsideBoardNegativeRow(){
    Position from = new Position(6,0); //a2
    Position to = new Position(0,-1); //Outside Board
    MovePieceException exception = assertThrows(MovePieceException.class, () -> board.movePiece(from, to));
    String expectedMessage = "Invalid destination position!";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(actualBoard.get(6).getFirst()).usingRecursiveComparison().isEqualTo(new Pawn(Color.WHITE, new Position(6,0)));
  }

  @Test
  void movePiece_Fail_ColumnOutOfBounds(){
    Position from = new Position(6,0); //a2
    Position to = new Position(0,8); //Outside Board
    MovePieceException exception = assertThrows(MovePieceException.class, () -> board.movePiece(from, to));
    String expectedMessage = "Invalid destination position!";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(actualBoard.get(6).getFirst()).usingRecursiveComparison().isEqualTo(new Pawn(Color.WHITE, new Position(6,0)));
  }

  @Test
  void movePiece_Fail_RowOutOfBounds(){
    Position from = new Position(6,0); //a2
    Position to = new Position(8,0); //Outside Board
    MovePieceException exception = assertThrows(MovePieceException.class, () -> board.movePiece(from, to));
    String expectedMessage = "Invalid destination position!";
    assertThat(exception.getMessage()).contains(expectedMessage);
    assertThat(actualBoard.get(6).getFirst()).usingRecursiveComparison().isEqualTo(new Pawn(Color.WHITE, new Position(6,0)));
  }

  @Test
  void movePiece_Fail_OccupiedPositionByOwnPiece(){
    Position from = new Position(7,0); //a1
    Position to = new Position(6,0); //a2
    MovePieceException exception = assertThrows(MovePieceException.class, () -> board.movePiece(from, to));
    String expectedMessage = "Destination position occupied by own piece!";
    assertThat(exception.getMessage()).contains(expectedMessage);
  }

  @Test
  void movePiece_Fail_NoPieceOnFromPosition(){
    Position from = new Position(3,3); //d5
    Position to = new Position(4,3); //d4
    MovePieceException exception = assertThrows(MovePieceException.class, () -> board.movePiece(from, to));
    String expectedMessage = "No piece at the specified position!";
    assertThat(exception.getMessage()).contains(expectedMessage);
  }

}