object Format {
    fun center(text: String, length: Int, rightAligned: Boolean): String {
        val spaces = length - text.length
        return if (spaces <= 0) {
            text.substring(0 until length)
        } else {
            val spacesLeft = spaces / 2
            val spacesRight = spaces - spacesLeft

            if (rightAligned) {
                " ".repeat(spacesRight) + text + " ".repeat(spacesLeft)
            } else {
                " ".repeat(spacesLeft) + text + " ".repeat(spacesRight)
            }
        }
    }
}