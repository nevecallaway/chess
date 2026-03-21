package client;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import service.result.*;
import chess.ChessGame;
import ui.ChessBoardUI;

public class ChessClient {
    private String username = null;
    private String authToken = null;
    private final ServerFacade server;
    private State state = State.PRELOGIN;
    private List<GameInfo> gamesList = List.of();

    public ChessClient(String serverUrl) throws Exception {
        try {
            String[] parts = serverUrl.split(":");
            int port = Integer.parseInt(parts[parts.length - 1]);
            this.server = new ServerFacade(port);
        } catch (Exception e) {
            throw new Exception("Invalid server URL: " + serverUrl);
        }
    }

    public void run() {
        System.out.println("♕ Welcome to Chess");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                System.out.print(e.getMessage());
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n>>> ");
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play" -> playGame(params);
                case "observe" -> observeGame(params);
                case "back" -> "Returning to main menu.\n" + help();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws Exception {
        if (state != State.PRELOGIN) {
            throw new Exception("You must logout first.\n");
        }
        if (params.length < 3) {
            throw new Exception("Expected: register <username> <password> <email>\n");
        }

        String username = params[0];
        String password = params[1];
        String email = params[2];

        try {
            RegisterResult result = server.register(username, password, email);
            this.username = result.username();
            this.authToken = result.authToken();
            state = State.POSTLOGIN;
            return String.format("Account created! Welcome, %s!\n\n", username) + help();
        } catch (Exception e) {
            String errorMsg = e.getMessage().toLowerCase();
            if (errorMsg.contains("already exists")) {
                throw new Exception("Registration failed. Username or email already taken.\n");
            }
            throw new Exception("Registration failed.\n");
        }
    }

    public String login(String... params) throws Exception {
        if (state != State.PRELOGIN) {
            throw new Exception("You're already logged in. Logout first.\n");
        }
        if (params.length < 2) {
            throw new Exception("Expected: login <username> <password>\n");
        }

        String username = params[0];
        String password = params[1];

        try {
            LoginResult result = server.login(username, password);
            this.username = result.username();
            this.authToken = result.authToken();
            state = State.POSTLOGIN;
            return String.format("Welcome back, %s!\n\n", username) + help();
        } catch (Exception e) {
            throw new Exception("Login failed. Invalid username or password.\n");
        }
    }

    public String logout() throws Exception {
        assertLoggedIn();

        try {
            server.logout(authToken);
            username = null;
            authToken = null;
            state = State.PRELOGIN;
            return "You have been logged out.\n\n" + help();
        } catch (Exception e) {
            throw new Exception("Logout failed.\n");
        }
    }

    public String createGame(String... params) throws Exception {
        assertLoggedIn();
        if (params.length < 1) {
            throw new Exception("Expected: create <game_name>\n");
        }

        String gameName = String.join(" ", params);

        try {
            CreateGameResult result = server.createGame(gameName, authToken);
            return String.format("Game created! Game ID: %d\n", result.gameID());
        } catch (Exception e) {
            throw new Exception("Failed to create game.\n");
        }
    }

    public String listGames() throws Exception {
        assertLoggedIn();

        try {
            ListGamesResult result = server.listGames(authToken);
            gamesList = result.games();

            if (gamesList.isEmpty()) {
                return "No games available.\n";
            }

            StringBuilder sb = new StringBuilder("\nAvailable Games:\n");
            for (int i = 0; i < gamesList.size(); i++) {
                GameInfo game = gamesList.get(i);
                String white = (game.whiteUsername() != null) ? game.whiteUsername() : "[empty]";
                String black = (game.blackUsername() != null) ? game.blackUsername() : "[empty]";
                sb.append(String.format("%d. %s - White: %s, Black: %s\n", i + 1, game.gameName(), white, black));
            }
            sb.append("\nNow use 'play' or 'observe' with the game number.\n");
            return sb.toString();
        } catch (Exception e) {
            throw new Exception("Failed to list games.\n");
        }
    }

    public String playGame(String... params) throws Exception {
        assertLoggedIn();
        if (gamesList.isEmpty()) {
            throw new Exception("Please list games first using 'list'.\n");
        }
        if (params.length < 2) {
            throw new Exception("Expected: play <game_number> <WHITE|BLACK>\n");
        }

        try {
            int gameNumber = Integer.parseInt(params[0]) - 1;
            String color = params[1].toUpperCase();

            if (gameNumber < 0 || gameNumber >= gamesList.size()) {
                throw new Exception("Invalid game number.\n");
            }
            if (!color.equals("WHITE") && !color.equals("BLACK")) {
                throw new Exception("Color must be WHITE or BLACK.\n");
            }

            GameInfo game = gamesList.get(gameNumber);
            
            // Check if player is already in the game as the other color
            if (color.equals("WHITE") && game.blackUsername() != null && game.blackUsername().equals(username)) {
                throw new Exception("You are already in this game as BLACK.\n");
            }
            if (color.equals("BLACK") && game.whiteUsername() != null && game.whiteUsername().equals(username)) {
                throw new Exception("You are already in this game as WHITE.\n");
            }
            
            server.joinGame(authToken, color, game.gameID());
            
            // Display the initial board
            ChessGame chessGame = new ChessGame();
            System.out.println("\nGame: " + game.gameName());
            if (color.equals("WHITE")) {
                ChessBoardUI.displayBoardWhitePerspective(chessGame);
            } else {
                ChessBoardUI.displayBoardBlackPerspective(chessGame);
            }
            System.out.println("(Gameplay coming in Phase 6)\n");
            return String.format("Joined game %d as %s.\n\nType 'back' to return to menu.\n", game.gameID(), color);
        } catch (NumberFormatException ignored) {
            throw new Exception("Game number must be a valid integer.\n");
        } catch (Exception e) {
            if (e.getMessage().contains("Expected:") || e.getMessage().contains("Invalid") || e.getMessage().contains("Color") || e.getMessage().contains("already in this game")) {
                throw e;
            }
            throw new Exception("Failed to join game: " + e.getMessage() + "\n");
        }
    }

    public String observeGame(String... params) throws Exception {
        assertLoggedIn();
        if (gamesList.isEmpty()) {
            throw new Exception("Please list games first using 'list'.\n");
        }
        if (params.length < 1) {
            throw new Exception("Expected: observe <game_number>\n");
        }

        try {
            int gameNumber = Integer.parseInt(params[0]) - 1;

            if (gameNumber < 0 || gameNumber >= gamesList.size()) {
                throw new Exception("Invalid game number.\n");
            }

            GameInfo game = gamesList.get(gameNumber);
            
            // Display the board from white's perspective (observer view)
            ChessGame chessGame = new ChessGame();
            System.out.println("\nObserving: " + game.gameName());
            ChessBoardUI.displayBoardWhitePerspective(chessGame);
            System.out.println("(Gameplay coming in Phase 6)\n");
            return String.format("Observing game %d.\n\nType 'back' to return to menu.\n", game.gameID());
        } catch (NumberFormatException ignored) {
            throw new Exception("Game number must be a valid integer.\n");
        } catch (Exception e) {
            if (e.getMessage().contains("Expected:") || e.getMessage().contains("Invalid")) {
                throw e;
            }
            throw new Exception("Failed to observe game: " + e.getMessage() + "\n");
        }
    }

    public String help() {
        if (state == State.PRELOGIN) {
            return """
                    register <username> <password> <email>
                    login <username> <password>
                    quit
                    """;
        }
        return """
                create <game_name>
                list (do before play/observe to see game numbers)
                play <game_number> <WHITE|BLACK>
                observe <game_number>
                logout
                quit
                """;
    }

    private void assertLoggedIn() throws Exception {
        if (state == State.PRELOGIN) {
            throw new Exception("You must be logged in first.\n");
        }
    }
}
