import Enums.Piece
import Players.Cpu5
import Players.Human
import Players.Player

class Game {
    private var board: Board = Board()
    private var playerO: Player? = null
    private var playerX: Player? = null

    init {
        val playerXStarts = startNewGame()
        loop(playerXStarts)
    }

    fun startNewGame(): Boolean {
        board = Board()

        print("\nInput player names\nPlayer O: ")
        val playerOName = readLine()!!.trim()
        print("\nPlayer X: ")
        val playerXName = readLine()!!.trim()

        println("\nInput the type of player for both players (H - Human, C - CPU)")
        val playerOClass = getPlayerClass("Player O", playerOName)
        val playerXClass = getPlayerClass("Player X", playerXName)

        var startingPlayer = Piece.Empty
        while (startingPlayer == Piece.Empty) {
            print("\nInput starting player (O or X): ")
            val input = readLine()!!.trim().toUpperCase()
            when (input) {
                "O" -> startingPlayer = Piece.PlayerO
                "X" -> startingPlayer = Piece.PlayerX
                else -> println("\nStarting player not recognized")
            }
        }

        playerO = setNewPlayer(playerOName, playerOClass, Piece.PlayerO, board)
        playerX = setNewPlayer(playerXName, playerXClass, Piece.PlayerX, board)
        board.setPlayers(playerO!!, playerX!!, startingPlayer)
        board.show()
        return startingPlayer == Piece.PlayerX
    }

    private fun setNewPlayer(playerName: String, playerClass: String, playerPiece: Piece, board: Board): Player {
        return when (playerClass) {
            "H" -> Human(playerName, playerPiece, board)
            "C" -> Cpu5(playerName, playerPiece, board)
            else -> throw IndexOutOfBoundsException("Wrong player class")
        }
    }
    
    private fun getPlayerClass(playerMark: String, playerName: String): String {
        while (true) {
            print("Player $playerMark ($playerName): ")
            val input = readLine()!!.trim().toUpperCase()

            when (input) {
                "H", "C" -> return input
            }
            println("Type of player not recognized. Please input H or C")
        }
    }

    private fun loop(playerXStarts: Boolean) {
        var gameEnd = false
        var round = 1
        while (!gameEnd) {
            println("Round $round")
            if (playerXStarts) {
                gameEnd = playerTurn(playerX!!) || playerTurn(playerO!!)
            } else {
                gameEnd = playerTurn(playerO!!) || playerTurn(playerX!!)
            }
            round++
        }
        println("Game Over")
    }

    private fun playerTurn(player: Player): Boolean {
        player.moveNeutron()
        if (checkNeutronInBackLine()) {
            return true
        }
        player.movePlayerPiece()
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
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
        if (board.isNeutronBlocked()) {
            val loser = player.opponent()
            println("Player ${loser.mark} cannot move the neutron.")
            println("Player ${player.mark} wins!!!")
            return true
        }
        return false
    }
}
