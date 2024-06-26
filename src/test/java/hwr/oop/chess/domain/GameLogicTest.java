package hwr.oop.chess.domain;

import static hwr.oop.chess.domain.GameLogic.convertInputToPosition;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.*;

import hwr.oop.chess.Color;
import hwr.oop.chess.GameOutcome;
import hwr.oop.chess.Position;
import hwr.oop.chess.board.ChessBoard;
import hwr.oop.chess.game.Game;
import hwr.oop.chess.persistence.FileBasedPersistence;
import hwr.oop.chess.persistence.Persistence;
import hwr.oop.chess.pieces.*;
import hwr.oop.chess.player.Player;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameLogicTest {

  private GameLogic gameLogic;
  private Persistence persistence;
  private static final String TEST_FILE_PATH_GAMES = "target/GameLogicTestGames.txt";
  private static final String TEST_FILE_PATH_PLAYERS = "target/GameLogicTestPlayers.txt";

  @BeforeEach
  void setUp() {
    File fileGames = new File(TEST_FILE_PATH_GAMES);
    Path pathGames = fileGames.toPath();
    File filePlayers = new File(TEST_FILE_PATH_PLAYERS);
    Path pathPlayers = filePlayers.toPath();
    persistence = new FileBasedPersistence(pathGames, pathPlayers);
    gameLogic = new GameLogic(persistence);
  }

  @AfterEach
  void tearDown() {
    File fileGames = new File(TEST_FILE_PATH_GAMES);
    if (fileGames.exists() && !fileGames.delete()) {
      throw new RuntimeException("Deleting the file was unsuccessful.");
    }

    File filePlayers = new File(TEST_FILE_PATH_PLAYERS);
    if (filePlayers.exists() && !filePlayers.delete()) {
      throw new RuntimeException("Deleting the file was unsuccessful.");
    }
  }

  @Test
  void testLoadGame_GameFound() {
    // Arrange
    String gameId = "shortestGame";
    Player playerWhite = gameLogic.loadPlayer("Player1");
    Player playerBlack = gameLogic.loadPlayer("Player2");
    List<Game> games = new ArrayList<>();
    Game expectedGame = new Game(playerWhite, playerBlack, gameId);

    games.add(expectedGame);
    persistence.saveGames(games);

    // Act
    Game loadedGame = gameLogic.loadGame("shortestGame");

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat((loadedGame)).isNotNull();
          softly.assertThat(expectedGame).isEqualTo(loadedGame);
          softly
              .assertThat(expectedGame.getPlayerWhite().getElo())
              .isEqualTo(gameLogic.loadPlayer("Player1").getElo());
        });
  }

  @Test
  void testLoadGame_GameNotFound() {
    // Act & Assert
    GameNotFoundException exception =
        assertThrows(GameNotFoundException.class, () -> gameLogic.loadGame("123"));
    String expectedMessage = "Game with ID '123' not found.";
    assertThat(exception.getMessage()).contains(expectedMessage);
  }

  @Test
  void testSaveGame_NewGame() {
    // Arrange
    List<Game> games = new ArrayList<>();
    persistence.saveGames(games);
    Game newGame = new Game(new Player("Alice"), new Player("Bob"), "67");
    // Act
    gameLogic.saveGame(newGame);

    // Assert
    List<Game> loadedGames = persistence.loadGames();
    assertTrue(loadedGames.contains(newGame));
  }

  @Test
  void testSaveGame_OverwriteExistingGame() {
    // Arrange
    Player playerWhite = gameLogic.loadPlayer("Player1");
    Player playerBlack = gameLogic.loadPlayer("Player2");
    Player playerWhite2 = gameLogic.loadPlayer("Player3");
    Game existingGame = new Game(playerWhite, playerBlack, "123");
    Game updatedGame = new Game(playerWhite2, playerBlack, "123");
    List<Game> games = new ArrayList<>();
    games.add(existingGame);
    persistence.saveGames(games);

    // Act
    gameLogic.saveGame(updatedGame);

    // Assert
    List<Game> loadedGames = persistence.loadGames();
    assertTrue(loadedGames.contains(updatedGame));
  }

  @Test
  void testCreateGame_Successful() {
    // Arrange
    String gameId = "456";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");

    // Act
    gameLogic.createGame(playerWhite, playerBlack, gameId);

    // Assert
    List<Game> loadedGames = persistence.loadGames();
    assertTrue(loadedGames.contains(new Game(playerWhite, playerBlack, gameId)));
  }

  @Test
  void testCreateGame_GameAlreadyExists() {
    // Arrange
    String gameId = "456";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    List<Game> games = new ArrayList<>();
    games.add(new Game(playerWhite, playerBlack, gameId));
    persistence.saveGames(games);

    // Act & Assert
    assertThrows(
        GameAlreadyExistsException.class,
        () -> gameLogic.createGame(playerWhite, playerBlack, gameId));
  }

  @Test
  void testLoadPlayer_PlayerFound() {
    // Arrange
    Player expectedPlayer = new Player("Player1");
    List<Player> players = new ArrayList<>();
    players.add(expectedPlayer);
    persistence.savePlayers(players);

    // Act
    Player loadedPlayer = gameLogic.loadPlayer("Player1");

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(loadedPlayer).isNotNull();
          softly.assertThat(expectedPlayer).isEqualTo(loadedPlayer);
        });
  }

  @Test
  void testSavePlayer_NewPlayer() {
    // Arrange
    Player newPlayer = new Player("Alice");

    // Act
    gameLogic.savePlayer(newPlayer);

    // Assert
    List<Player> loadedPlayers = persistence.loadPlayers();
    assertTrue(loadedPlayers.contains(newPlayer));
  }

  @Test
  void testSavePlayer_OverwriteExistingPlayer() {
    // Arrange
    Player existingPlayer = new Player("Alice");
    Player updatedPlayer = new Player("Alice");
    updatedPlayer.setElo((short) 110);
    List<Player> players = new ArrayList<>();
    players.add(existingPlayer);
    persistence.savePlayers(players);

    // Act
    gameLogic.savePlayer(updatedPlayer);

    // Assert
    List<Player> loadedPlayers = persistence.loadPlayers();
    assertTrue(loadedPlayers.contains(updatedPlayer));
  }

  @Test
  void testLoadPlayer_PlayerWithSameName() {
    // Arrange
    Player existingPlayer = new Player("Alice");
    List<Player> players = new ArrayList<>();
    players.add(existingPlayer);
    persistence.savePlayers(players);

    // Act
    Player loadedPlayer = gameLogic.loadPlayer("Alice");

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(loadedPlayer).isNotNull();
          softly.assertThat(existingPlayer).isEqualTo(loadedPlayer);
        });
  }

  @Test
  void testSavePlayer_ExistingPlayerOverwritten() {
    // Arrange
    Player existingPlayer = new Player("Alice");
    Player updatedPlayer = new Player("Alice");
    updatedPlayer.setElo((short) 110);
    List<Player> players = new ArrayList<>();
    players.add(existingPlayer);
    persistence.savePlayers(players);

    // Act
    gameLogic.savePlayer(updatedPlayer);

    // Assert
    List<Player> loadedPlayers = persistence.loadPlayers();
    assertTrue(loadedPlayers.contains(updatedPlayer));
  }

  @Test
  void testMoveTo_ValidMove1() {
    // Arrange
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    String initialPosition = "e2";
    String targetPosition = "e4";

    // Act
    gameLogic.moveTo(initialPosition, targetPosition, game);
    gameLogic.saveGame(game);
    Piece movedPiece = game.getBoard().getPieceAtPosition(convertInputToPosition(targetPosition));
    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(movedPiece).isNotNull();
          softly
              .assertThat(convertInputToPosition(targetPosition))
              .isEqualTo(movedPiece.getPosition());
        });
  }

  @Test
  void testMoveTo_InvalidMove_NoPieceAtStartPosition() {
    // Arrange
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    String initialPosition = "d3";
    String targetPosition = "d4";

    // Act & Assert
    IllegalMoveException exception =
        assertThrows(
            IllegalMoveException.class,
            () -> gameLogic.moveTo(initialPosition, targetPosition, game));
    assertThat(exception.getMessage())
        .isEqualTo("No piece at the specified position: Position[row=2, column=3]");
  }

  @Test
  void testMoveTo_InvalidMove_WrongPlayerTurn() {
    // Arrange
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    String initialPosition = "e7";
    String targetPosition = "e6";

    // Act & Assert
    IllegalMoveException exception =
        assertThrows(
            IllegalMoveException.class,
            () -> gameLogic.moveTo(initialPosition, targetPosition, game));
    assertEquals("It's not your turn. Expected: WHITE, but got: BLACK", exception.getMessage());
  }

  @Test
  void testMoveTo_InvalidMove_TargetPositionOutOfBounds() {
    // Arrange
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    String initialPosition = "e2";
    String targetPosition = "e9";

    // Act & Assert
    ConvertInputToPositionException exception =
        assertThrows(
            ConvertInputToPositionException.class,
            () -> gameLogic.moveTo(initialPosition, targetPosition, game));
    assertEquals(
        "Invalid position. Position must be within the chessboard.", exception.getMessage());
  }

  @Test
  void testMoveTo_InvalidMove_InvalidDirectionForPiece() {
    // Arrange
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    String initialPosition = "b1";
    String targetPosition = "b5";

    // Act & Assert
    IllegalMoveException exception =
        assertThrows(
            IllegalMoveException.class,
            () -> gameLogic.moveTo(initialPosition, targetPosition, game));
    assertEquals(
        "Illegal move to position: Position[row=4, column=1]. Possible possible moves are for example: Position[row=2, column=2], Position[row=2, column=0]",
        exception.getMessage());
  }

  @Test
  void testMoveTo_InvalidMove_BlockingPieceInPath() {
    // Arrange
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    game.getBoard().clearChessboard();
    game.getBoard()
        .setPieceAtPosition(
            new Position(0, 0), new King(Color.WHITE, new Position(0, 0), game.getBoard()));
    game.getBoard()
        .setPieceAtPosition(
            new Position(2, 4),
            new Rook(Color.WHITE, new Position(2, 4), game.getBoard())); // Rook blocking the path
    game.getBoard()
        .setPieceAtPosition(
            new Position(3, 4),
            new Pawn(Color.BLACK, new Position(3, 4), game.getBoard())); // Pawn trying to move here
    String initialPosition = "e3";
    String targetPosition = "e5";

    // Act & Assert
    IllegalMoveException exception =
        assertThrows(
            IllegalMoveException.class,
            () -> gameLogic.moveTo(initialPosition, targetPosition, game));
    assertEquals(
        "Illegal move to position: Position[row=4, column=4]. Possible possible moves are for example: Position[row=3, column=4], Position[row=1, column=4]",
        exception.getMessage());
  }

  @Test
  void testSaveGame_AddNewGameWhenNotExists() {
    // Arrange
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    Game newGame = new Game(playerWhite, playerBlack, gameId);

    // Act
    gameLogic.saveGame(newGame);

    // Assert
    List<Game> loadedGames = persistence.loadGames();
    assertTrue(loadedGames.contains(newGame));
  }

  @Test
  void testSaveGame_onlyOverwritesOneGame() {
    // Arrange
    String gameId2 = "game2";
    Player playerWhite2 = gameLogic.loadPlayer("Luisa");
    Player playerBlack2 = gameLogic.loadPlayer("Jake");
    gameLogic.createGame(playerWhite2, playerBlack2, gameId2);
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    Game newGame = new Game(playerWhite, playerBlack, gameId);

    // Act
    gameLogic.saveGame(newGame);

    // Assert
    List<Game> loadedGames = persistence.loadGames();
    assertSoftly(
        softly -> {
          softly.assertThat(loadedGames.contains(newGame)).isTrue();
          softly.assertThat(loadedGames.getFirst().getPlayerWhite().getName()).isEqualTo("Luisa");
          softly.assertThat(loadedGames.get(1).getPlayerWhite().getName()).isEqualTo("Alice");
        });
  }

  @Test
  void testMoveTo_InvalidMove_SameStartAndEndPosition() {
    // Arrange
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    String startPosition = "b5";
    String endPosition = "b5";

    Piece piece = new Pawn(Color.WHITE, convertInputToPosition(startPosition), game.getBoard());
    game.getBoard().setPieceAtPosition(convertInputToPosition(startPosition), piece);

    // Act & Assert
    IllegalMoveException exception =
        assertThrows(
            IllegalMoveException.class, () -> gameLogic.moveTo(startPosition, endPosition, game));

    assertEquals(
        "Illegal move to position: "
            + convertInputToPosition(endPosition)
            + ". The start and end positions are the same.",
        exception.getMessage());
  }

  @Test
  void testAcceptRemi() {
    // Arrange
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    playerWhite.setElo((short) 2950);
    playerBlack.setElo((short) 2630);
    gameLogic.savePlayer(playerWhite);
    gameLogic.savePlayer(playerBlack);
    playerWhite = gameLogic.loadPlayer("Alice");
    playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);

    // Act
    gameLogic.offerRemi(game);
    gameLogic.acceptRemi(game);
    gameLogic.endGame(game);

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(game.getWinner()).isEqualTo(GameOutcome.REMI);
          softly.assertThat(game.isGameEnded()).isTrue();
          softly.assertThat(gameLogic.loadPlayer("Alice").getElo()).isEqualTo((short) 2943);
          softly.assertThat(gameLogic.loadPlayer("Bob").getElo()).isEqualTo((short) 2637);
        });
  }

  @Test
  void testResign_WhitePlayerResigns() {
    // Arrange
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);

    // Act
    gameLogic.resign(game);

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(game.getWinner()).isEqualTo(GameOutcome.BLACK);
          softly.assertThat(game.isGameEnded()).isTrue();
        });
  }

  @Test
  void testResign_BlackPlayerResigns() {
    // Arrange
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);

    game.toggleNextToMove();

    // Act
    gameLogic.resign(game);

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(game.getWinner()).isEqualTo(GameOutcome.WHITE);
          softly.assertThat(game.isGameEnded()).isTrue();
        });
  }

  @Test
  void testShortestGame() {
    // Arrange
    String gameId = "shortestGame";

    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);

    // Act
    Game game = gameLogic.loadGame(gameId);
    gameLogic.moveTo("f2", "f3", game);
    gameLogic.moveTo("e7", "e5", game);
    gameLogic.moveTo("g2", "g4", game);
    boolean isCheckMate = gameLogic.moveTo("d8", "h4", game);

    if (isCheckMate) {
      gameLogic.endGame(game);
    }
    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(game.isGameEnded()).isTrue();
          softly.assertThat(game.isRemiOffered()).isFalse();
          softly.assertThat(game.getWinner()).isEqualTo(GameOutcome.BLACK);
          softly.assertThat(game.getMoveCount()).isEqualTo((short) 4);
          softly.assertThat(gameLogic.loadPlayer("Alice").getElo()).isEqualTo((short) 1190);
          softly.assertThat(gameLogic.loadPlayer("Bob").getElo()).isEqualTo((short) 1210);
        });
  }

  @Test
  void testOfferRemi_TakeBackAfterMoveTo() {
    // Arrange
    String gameId = "shortestGame";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);

    // Act
    Game game = gameLogic.loadGame(gameId);
    gameLogic.offerRemi(game);
    gameLogic.moveTo("f2", "f3", game);
    Game gameLoadedAgain = gameLogic.loadGame(gameId);

    // Assert
    assertThat(gameLoadedAgain.isRemiOffered()).isFalse();
  }

  @Test
  void testShortGameWhiteWins() {
    // Arrange
    String gameId = "shortGame";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);

    // Act
    Game game = gameLogic.loadGame(gameId);
    gameLogic.moveTo("b2", "b3", game);
    gameLogic.moveTo("f7", "f6", game);
    gameLogic.moveTo("e2", "e4", game);
    gameLogic.moveTo("g7", "g5", game);
    boolean isCheckMate = gameLogic.moveTo("d1", "h5", game);
    if (isCheckMate) {
      gameLogic.endGame(game);
    }

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(game.isGameEnded()).isTrue();
          softly.assertThat(game.getWinner()).isEqualTo(GameOutcome.WHITE);
          softly.assertThat(game.getMoveCount()).isEqualTo((short) 5);
          softly.assertThat(gameLogic.loadPlayer("Alice").getElo()).isEqualTo((short) 1210);
          softly.assertThat(gameLogic.loadPlayer("Bob").getElo()).isEqualTo((short) 1190);
        });
  }

  @Test
  void testMoveToNotCheckMate() {
    // Arrange
    String gameId = "testGame";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);

    // Act
    Game game = gameLogic.loadGame(gameId);
    boolean isCheckMate = gameLogic.moveTo("f2", "f3", game);

    // Assert
    assertThat(isCheckMate).isFalse();
  }

  @Test
  void testIllegalMoveBecauseKingInCheck() {
    // Arrange
    String gameId = "shortGame";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);

    // Act
    Game game = gameLogic.loadGame(gameId);
    gameLogic.moveTo("d2", "d4", game);
    gameLogic.moveTo("e7", "e5", game);
    gameLogic.moveTo("e1", "d2", game);
    gameLogic.moveTo("f8", "b4", game);

    // Assert
    IllegalMoveBecauseKingIsInCheckException exception =
        assertThrows(
            IllegalMoveBecauseKingIsInCheckException.class,
            () -> gameLogic.moveTo("h2", "h3", game));
    assertEquals(
        "You can not move your piece to this position because your king is in check.",
        exception.getMessage());
  }

  @Test
  void testLoadGame_UpdatePlayers() {
    // Arrange
    String gameId = "testGame";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    List<Game> games = new ArrayList<>();
    Game game = new Game(playerWhite, playerBlack, gameId);
    games.add(game);
    persistence.saveGames(games);
    playerWhite.setElo((short) 1299);
    playerBlack.setElo((short) 1266);
    gameLogic.savePlayer(playerWhite);
    gameLogic.savePlayer(playerBlack);

    // Act
    Game loadedGame = gameLogic.loadGame(gameId);

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(loadedGame).isNotNull();
          softly.assertThat(loadedGame.getPlayerWhite().getName()).isEqualTo("Alice");
          softly.assertThat(loadedGame.getPlayerBlack().getName()).isEqualTo("Bob");
          softly.assertThat(loadedGame.getPlayerWhite()).isEqualTo(playerWhite);
          softly.assertThat(loadedGame.getPlayerBlack()).isEqualTo(playerBlack);
        });
  }

  @Test
  void testEndGame_DeclareWinnerRemi() {
    // Arrange
    String gameId = "gameToEnd";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);

    // Act
    gameLogic.offerRemi(game);
    gameLogic.acceptRemi(game);
    String result = gameLogic.endGame(game);

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(game.isRemiOffered()).isTrue();
          softly.assertThat(game.isGameEnded()).isTrue();
          softly.assertThat(game.getWinner()).isEqualTo(GameOutcome.REMI);
          softly.assertThat(result).contains("The game ended in Remi.");
        });
  }

  @Test
  void testEndGame_DeclareOfferRemi() {
    // Arrange
    String gameId = "gameToEnd";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);

    // Act
    gameLogic.offerRemi(game);
    Game gameLoadedAfterOfferRemi = gameLogic.loadGame(gameId);

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(gameLoadedAfterOfferRemi.isRemiOffered()).isTrue();
          softly.assertThat(gameLoadedAfterOfferRemi.isGameEnded()).isFalse();
          softly.assertThat(game.getWinner()).isEqualTo(GameOutcome.NOT_FINISHED_YET);
        });
  }

  @Test
  void testAcceptRemiWithoutOffering() {
    String gameId = "gameToEnd";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);

    // Act
    RemiWasNotOfferedException exception =
        assertThrows(RemiWasNotOfferedException.class, () -> gameLogic.acceptRemi(game));

    assertEquals("You have to offer remi first before you can accept it.", exception.getMessage());
  }

  @Test
  void testEndGame_DeclareWinnerWhite() {
    // Arrange
    String gameId = "gameToEnd";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    game.declareWinner(GameOutcome.WHITE);

    // Act
    String result = gameLogic.endGame(game);

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(game.isGameEnded()).isTrue();
          softly.assertThat(game.getWinner()).isEqualTo(GameOutcome.WHITE);
          softly.assertThat(result).contains("WHITE won this game. Congrats Alice (new ELO: 1210)");
          softly.assertThat(gameLogic.loadPlayer("Alice").getElo()).isEqualTo((short) 1210);
          softly.assertThat(gameLogic.loadPlayer("Bob").getElo()).isEqualTo((short) 1190);
        });
  }

  @Test
  void testEndGame_DeclareWinnerBlack() {
    // Arrange
    String gameId = "gameToEnd";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    game.declareWinner(GameOutcome.BLACK);

    // Act
    String result = gameLogic.endGame(game);

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(game.isGameEnded()).isTrue();
          softly.assertThat(game.getWinner()).isEqualTo(GameOutcome.BLACK);
          softly.assertThat(result).contains("BLACK won this game. Congrats Bob (new ELO: 1210)");
        });
  }

  @Test
  void testDeleteGame_GameExists() {
    // Arrange
    String gameId = "gameToDelete";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);

    // Act & Assert
    GameHasNotEndedException exception =
        assertThrows(GameHasNotEndedException.class, () -> gameLogic.endGame(game));

    assertEquals("The game has not ended yet", exception.getMessage());
  }

  @Test
  void testDeleteGame_GameDoesNotExist() {
    // Arrange
    String gameId = "nonExistentGame";

    // Act
    List<Game> loadedGames = persistence.loadGames();
    int initialSize = loadedGames.size();

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(initialSize).isEqualTo(loadedGames.size());
          softly.assertThat(loadedGames.stream().noneMatch(m -> m.getId().equals(gameId))).isTrue();
        });
  }

  @Test
  void testEndGame_DeleteGameAfterEnding() {
    // Arrange
    String gameId = "gameToEnd";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    game.declareWinner(GameOutcome.BLACK);

    // Act
    String result = gameLogic.endGame(game);

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(game.isGameEnded()).isTrue();
          softly.assertThat(game.getWinner()).isEqualTo(GameOutcome.BLACK);
          softly.assertThat(result).contains("BLACK won this game. Congrats Bob (new ELO: 1210)");
          softly
              .assertThat(persistence.loadGames().stream().anyMatch(m -> m.getId().equals(gameId)))
              .isFalse();
        });
  }

  @Test
  void testDeleteGame_OnlyDeletesOneGame() {
    // Arrange
    String gameId = "gameToDelete";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    gameLogic.createGame(playerWhite, playerBlack, "gameNotToDelete");
    Game game = gameLogic.loadGame(gameId);
    game.declareWinner(GameOutcome.BLACK);

    // Act
    gameLogic.endGame(game);
    List<Game> loadedGames = persistence.loadGames();

    // Act & Assert

    assertThat(loadedGames.getFirst().getId()).isEqualTo("gameNotToDelete");
  }

  @Test
  void testPromoteBlackPawnToQueen() throws IllegalMoveException {
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    game.getBoard().clearChessboard();
    Position blackPawnPosition = new Position(0, 0);
    Piece blackPawn = new Pawn(Color.BLACK, blackPawnPosition, game.getBoard());
    Piece promotedPiece = new Queen(Color.BLACK, blackPawnPosition, game.getBoard());
    game.getBoard().setPieceAtPosition(blackPawnPosition, blackPawn);

    gameLogic.promotePiece(game, "a1", "q");

    Assertions.assertThat(game.getBoard().getPieceAtPosition(blackPawnPosition))
        .isEqualTo(promotedPiece);
    Game loadedGame = gameLogic.loadGame(gameId);
    assertThat(loadedGame).isEqualTo(game);
  }

  @Test
  void testPromoteBlackPawnToBishop() throws IllegalMoveException {
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    game.getBoard().clearChessboard();
    Position blackPawnPosition = new Position(0, 0);
    Piece blackPawn = new Pawn(Color.BLACK, blackPawnPosition, game.getBoard());
    Piece promotedPiece = new Bishop(Color.BLACK, blackPawnPosition, game.getBoard());
    game.getBoard().setPieceAtPosition(blackPawnPosition, blackPawn);

    gameLogic.promotePiece(game, "a1", "b");

    Assertions.assertThat(game.getBoard().getPieceAtPosition(blackPawnPosition))
        .isEqualTo(promotedPiece);
  }

  @Test
  void testPromoteBlackPawnToRook() throws IllegalMoveException {
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    game.getBoard().clearChessboard();
    Position blackPawnPosition = new Position(0, 0);
    Piece blackPawn = new Pawn(Color.BLACK, blackPawnPosition, game.getBoard());
    Piece promotedPiece = new Rook(Color.BLACK, blackPawnPosition, game.getBoard());
    game.getBoard().setPieceAtPosition(blackPawnPosition, blackPawn);

    gameLogic.promotePiece(game, "a1", "r");

    Assertions.assertThat(game.getBoard().getPieceAtPosition(blackPawnPosition))
        .isEqualTo(promotedPiece);
  }

  @Test
  void testPromoteBlackPawnToKnight() throws IllegalMoveException {
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    game.getBoard().clearChessboard();
    Position blackPawnPosition = new Position(0, 0);
    Piece blackPawn = new Pawn(Color.BLACK, blackPawnPosition, game.getBoard());
    Piece promotedPiece = new Knight(Color.BLACK, blackPawnPosition, game.getBoard());
    game.getBoard().setPieceAtPosition(blackPawnPosition, blackPawn);

    gameLogic.promotePiece(game, "a1", "n");

    Assertions.assertThat(game.getBoard().getPieceAtPosition(blackPawnPosition))
        .isEqualTo(promotedPiece);
  }

  @Test
  void testPromoteBlackPawnToKingInvalid() throws IllegalMoveException {
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    game.getBoard().clearChessboard();
    Position blackPawnPosition = new Position(0, 0);
    Piece blackPawn = new Pawn(Color.BLACK, blackPawnPosition, game.getBoard());
    game.getBoard().setPieceAtPosition(blackPawnPosition, blackPawn);
    IllegalPromotionException exception =
        assertThrows(
            IllegalPromotionException.class, () -> gameLogic.promotePiece(game, "a1", "k"));
    String expectedMessage =
        "Promotion is not allowed: The specified type is invalid. Valid promotion types are 'Queen', 'Rook', 'Bishop', or 'Knight'.";
    Assertions.assertThat(exception.getMessage()).contains(expectedMessage);
  }

  @Test
  void testPromoteFailNoPiece() {
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    game.getBoard().clearChessboard();

    IllegalPromotionException exception =
        assertThrows(
            IllegalPromotionException.class, () -> gameLogic.promotePiece(game, "a4", "q"));
    String expectedMessage = "Promotion is not allowed. No piece at given position.";
    Assertions.assertThat(exception.getMessage()).contains(expectedMessage);
  }

  @Test
  void testPromoteFailNoPawnAtPosition() {
    String gameId = "game1";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    game.getBoard().clearChessboard();

    Position whiteKingPosition = new Position(0, 0);
    Piece whiteKing = new King(Color.WHITE, whiteKingPosition, game.getBoard());
    game.getBoard().setPieceAtPosition(whiteKingPosition, whiteKing);

    IllegalPromotionException exception =
        assertThrows(
            IllegalPromotionException.class, () -> gameLogic.promotePiece(game, "a1", "q"));
    String expectedMessage = "Promotion is not allowed. You can only promote pawns";
    Assertions.assertThat(exception.getMessage()).contains(expectedMessage);
  }

  @Test
  void convertInputToPosition_Valid() {
    assertSoftly(
        softly -> {
          softly.assertThat(convertInputToPosition("a8")).isEqualTo(new Position(7, 0));
          softly.assertThat(convertInputToPosition("h1")).isEqualTo(new Position(0, 7));
          softly.assertThat(convertInputToPosition("e5")).isEqualTo(new Position(4, 4));
        });
  }

  @Test
  void convertInputToPosition_InvalidFormat() {
    assertThrows(ConvertInputToPositionException.class, () -> convertInputToPosition(""));
    assertThrows(ConvertInputToPositionException.class, () -> convertInputToPosition("a"));
    assertThrows(ConvertInputToPositionException.class, () -> convertInputToPosition("abc"));
    assertThrows(ConvertInputToPositionException.class, () -> convertInputToPosition("a12"));
    assertThrows(ConvertInputToPositionException.class, () -> convertInputToPosition("12"));
    assertThrows(ConvertInputToPositionException.class, () -> convertInputToPosition("abc12"));
  }

  @Test
  void testInvalidPosition() {
    assertThrows(ConvertInputToPositionException.class, () -> convertInputToPosition("i1"));
    assertThrows(ConvertInputToPositionException.class, () -> convertInputToPosition("a0"));
    assertThrows(ConvertInputToPositionException.class, () -> convertInputToPosition("a9"));
    assertThrows(ConvertInputToPositionException.class, () -> convertInputToPosition("h9"));
  }

  @Test
  void testGetPossibleMoves_PieceAtPosition() {
    // Arrange
    String gameId = "testGame";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    Position initialPosition = convertInputToPosition("e2");
    Piece piece = new Pawn(Color.WHITE, initialPosition, game.getBoard());
    game.getBoard().setPieceAtPosition(initialPosition, piece);

    // Act
    List<Position> possibleMoves = gameLogic.getPossibleMoves("e2", game);

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(possibleMoves).isNotNull();
          softly.assertThat(possibleMoves.isEmpty()).isFalse();
        });
  }

  @Test
  void testGetPossibleMoves_NoPieceAtPosition() {
    // Arrange
    String gameId = "testGame";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);

    // Act
    List<Position> possibleMoves = gameLogic.getPossibleMoves("e3", game);

    // Assert
    assertSoftly(
        softly -> {
          softly.assertThat(possibleMoves).isNotNull();
          softly.assertThat(possibleMoves.isEmpty()).isTrue();
        });

    // Test, return value can not be changed to Collections.emptyList
    possibleMoves.add(new Position(2, 3));
  }

  @Test
  void testGetFenNotation() {
    // Arrange
    String gameId = "testGame";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);

    // Act
    String fen = gameLogic.getFENNotation(game);

    // Assert
    assertThat(fen).isEqualTo("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w 0");
  }

  @Test
  void testGetFenNotationAfterOneMove() {
    // Arrange
    String gameId = "testGame";
    Player playerWhite = gameLogic.loadPlayer("Alice");
    Player playerBlack = gameLogic.loadPlayer("Bob");
    gameLogic.createGame(playerWhite, playerBlack, gameId);
    Game game = gameLogic.loadGame(gameId);
    gameLogic.moveTo("f2", "f3", game);

    // Act
    String fen = gameLogic.getFENNotation(game);

    // Assert
    assertThat(fen).isEqualTo("rnbqkbnr/pppppppp/8/8/8/5P2/PPPPP1PP/RNBQKBNR b 1");
  }

  @Test
  void testIsEnemyPiece() {
    // Create two pieces with different colors
    Piece whitePiece = new Bishop(Color.WHITE, new Position(5, 3), new ChessBoard());
    Piece blackPiece = new Bishop(Color.BLACK, new Position(3, 5), new ChessBoard());

    // Create two pieces with the same color
    Piece whitePiece2 = new Bishop(Color.WHITE, new Position(2, 2), new ChessBoard());
    Piece blackPiece2 = new Bishop(Color.BLACK, new Position(4, 4), new ChessBoard());

    // Test when both pieces are not null and have different colors
    assertTrue(gameLogic.isEnemyPiece(whitePiece, blackPiece));
    assertTrue(gameLogic.isEnemyPiece(blackPiece, whitePiece));

    // Test when one of the pieces is null
    assertFalse(gameLogic.isEnemyPiece(null, whitePiece));
    assertFalse(gameLogic.isEnemyPiece(blackPiece, null));

    // Test when both pieces are null
    assertFalse(gameLogic.isEnemyPiece(null, null));

    // Test when both pieces are not null but have the same color
    assertFalse(gameLogic.isEnemyPiece(whitePiece, whitePiece2));
    assertFalse(gameLogic.isEnemyPiece(blackPiece, blackPiece2));
  }

  @Test
  void testShowCaptureMovesCommand() throws GameNotFoundException {
    // Arrange
    String gameId = "123";
    Player playerWhite = new Player("Alice");
    Player playerBlack = new Player("Bob");
    Game game = new Game(playerWhite, playerBlack, gameId);
    game.getBoard().clearChessboard();
    Position whiteKingPosition = new Position(0, 0);
    Position blackKingPosition = new Position(7, 7);
    Position blackPawnPosition = new Position(2, 2);
    Position whitePawnPosition = new Position(1, 1);
    List<Position> expectedPossibleMoves = new ArrayList<>();
    expectedPossibleMoves.add(new Position(2, 1));
    expectedPossibleMoves.add(new Position(3, 1));
    expectedPossibleMoves.add(new Position(2, 2));
    List<Position> expectedCaptureMoves = new ArrayList<>();
    expectedCaptureMoves.add(new Position(2, 2));

    Piece whiteKing = new King(Color.WHITE, whiteKingPosition, game.getBoard());
    Piece blackKing = new King(Color.BLACK, blackKingPosition, game.getBoard());
    Piece blackPawn = new Pawn(Color.BLACK, blackPawnPosition, game.getBoard());
    Piece whitePawn = new Pawn(Color.WHITE, whitePawnPosition, game.getBoard());

    game.getBoard().setPieceAtPosition(whiteKingPosition, whiteKing);
    game.getBoard().setPieceAtPosition(blackKingPosition, blackKing);
    game.getBoard().setPieceAtPosition(blackPawnPosition, blackPawn);
    game.getBoard().setPieceAtPosition(whitePawnPosition, whitePawn);

    // Act
    List<Position> actualPossibleMoves = gameLogic.getPossibleMoves("b2", game);
    List<Position> actualCapturedMoves =
        gameLogic.getCaptureMoves("b2", expectedPossibleMoves, game);
    // Assert
    assertThat(actualPossibleMoves).isEqualTo(expectedPossibleMoves);
    assertThat(actualCapturedMoves).isEqualTo(expectedCaptureMoves);
  }
}
