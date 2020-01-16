import java.util.*

class Game(private val api: Api) {
    private var board: Board? = null
    private var playerO: Player? = null
    private var playerX: Player? = null
    private var gameId: String? = null

    init {
        var playerXStarts: Boolean
        while (true) {
            print("Type S to start a new game or J to join a game: ")
            val input = readLine()!!.trim()
            when (input) {
                "S", "s" -> playerXStarts = startNewGame()
                "J", "j" -> playerXStarts = joinGame()
                else -> println("Game type not recognized. Please input S or J")
            }
        }
        loop(playerXStarts)
    }

    fun startNewGame(): Boolean {
        board = Board()

        print("\nInput player names\nPlayer O: ")
        var playerOName = readLine()!!.trim()
        print("\nPlayer X: ")
        var playerXName = readLine()!!.trim()

        println("\nInput the type of player for both players (H - Human, C - CPU, R - Remote)")
        val playerOClass = getPlayerClass("Player O", playerOName)
        val playerXClass = getPlayerClass("Player X", playerXName)

        var startingPlayer = Piece.Empty
        while (startingPlayer == Piece.Empty) {
            print("\nInput starting player (O or X): ")
            val input = readLine()!!.trim().toUpperCase()
            when (input) {
                "Q" -> startingPlayer = Piece.PlayerO
                "X" -> startingPlayer = Piece.PlayerX
                else -> println("\nStarting player not recognized")
            }
        }

        playerO = setNewPlayer(playerOName, playerOClass, Piece.PlayerO, board)
        playerX = setNewPlayer(playerXName, playerXClass, Piece.PlayerX, board)
        board.setPlayers(playerO, playerX, startingPlayer)
        board.show()
        setupRemoteGame(null, startingPlayer)
        return startingPlayer == Piece.PlayerX
    }

    private fun startExistingGame(gamePojo: GamePojo, playerOClass: String, playerXClass: String): Boolean {
        board = new Board (gamePojo)
        playerO = setNewPlayer(gamePojo.playerO, playerOClass, Piece.PlayerO, board)
        playerX = setNewPlayer(gamePojo.playerX, playerXClass, Piece.PlayerX, board)
        board.setPlayers(playerO, playerX, gamePojo.startingPlayer)
        board.setPreviousMoves(gamePojo)
        board.show()
        setupRemoteGame(gamePojo.id, gamePojo.startingPlayer)
        return gamePojo.move == Piece.PlayerX
    }

    private fun setNewPlayer(playerName: String, playerClass: String, playerPiece: Piece, board: Board) {
        return when (playerClass) {
            "H" -> Human(playerName, playerPiece, board)
            "C" -> Cpu5(playerName, playerPiece, board)
            "R" -> Remote(playerName, playerPiece, board)
            else -> throw IndexOutOfBoundsException("Wrong player class")
        }
    }

    private fun joinGame(): Boolean {
        val gamesPojo = api.games
        val validGames = mutableListOf<GamePojo>()

        println("Here's a list of games to join:")
        println("Game ID         | Player O name | Player X name | Player turn")
        for (gamePojo in gamesPojo.games.values()) {
            if (gamePojo.winner == null) {
                validGames.add(gamePojo)
                println(gamePojo.id + " | "
                        + center(gamePojo.playerO, 13, false) + " | "
                        + center(gamePojo.playerX, 13, false) + " |      "
                        + gamePojo.move.mark
                )
            }
        }

        while (true) {
            print("\nInput the last ${getMinimumGameIdDigits(validGames)} digits of the game ID: ")
            val input = readLine()!!.trim()
            for (gamePojo in validGames) {
                if (gamePojo.id.endsWith(input)) {
                    println("\nInput the type of player for both players (H - Human, C - CPU, R - Remote)")
                    val playerOClass = getPlayerClass("Player O", gamePojo.playerO)
                    val playerXClass = getPlayerClass("Player X", gamePojo.playerX)
                    return startExistingGame(gamePojo, playerOClass, playerXClass)
                }
            }

            println("Game with ID ending with $input not identified.")
        }
    }

    private fun getPlayerClass(playerMark: String, playerName: String): String {
        while (true) {
            print("Player $playerMark ($playerName): ")
            val input = readLine()!!.trim().toUpperCase()

            when (input) {
                "H", "C", "R" -> return input
            }
            println("Type of player not recognized. Please input H, C or R")
        }
    }

    private fun getMinimumGameIdDigits(games: List<GamePojo>): String {
        var digits = 3

        while (true) {
            val ids = mutableSetOf<String>()
            var hasDuplicates = false

            for (gamePojo in games) {
                val id = gamePojo.id.substring(gamePojo.id.length - digits)
                if (ids.contains(id)) {
                    hasDuplicates = true
                    digits++
                    break
                }
                ids.add(id)
            }

            if (!hasDuplicates) {
                return digits.toString()
            }
        }
    }

    private fun loop(playerXStarts: Boolean) {
        var gameEnd = false
        var round = 1
        while (!gameEnd) {
            println("Round $round")
            if (playerXStarts) {
                gameEnd = playerTurn(playerX) || playerTurn(playerO)
            } else {
                gameEnd = playerTurn(playerO) || playerTurn(playerX)
            }
            round++
        }
        println("Game Over")
    }

    private fun playerTurn(player: Player): Boolean {
        player.MoveNeutron()
        if (checkNeutronInBackLine()) {
            return true
        }
        player.MovePlayerPiece()
        try {
            Thread.sleep(1000)
        } catch (InterruptedException e) {
            e.printStackTrace()
        }
        return checkNeutronBlocked(player.playerPiece)
    }

    private fun checkNeutronInBackLine(): Boolean {
        val loser = board.getNeutronBackLine()
        if (loser == Piece.PlayerX || loser == Piece.PlayerO) {
            val winner = loser.opponent()
            println("Neutron is on player ${loser.mark}'s back line.")
            println("Player ${winner.mark} wins!!!")
            return true
        }
        return false
    }

    private fun checkNeutronBlocked(player: Piece): Boolean {
        if (board.isNeutronBlocked) {
            val loser = player.opponent()
            println("Player ${loser.mark} cannot move the neutron.")
            println("Player ${player.mark} wins!!!")
            return true
        }
        return false
    }

    private fun setupRemoteGame(gameId: String?, startingPlayer: Piece) {
        if (gameId == null) {
            val gameStartPojo = GameStartPojo(playerO.name, playerX.name, startingPlayer.mark)
            gameId = api.startGame(gameStartPojo)
        }
        board.setApiGame(api, gameId)
        playerO.setApiGame(api, gameId)
        playerX.setApiGame(api, gameId)
        
    }
}
