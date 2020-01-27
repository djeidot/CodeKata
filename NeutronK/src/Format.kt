object Format {
    fun center(text: String, length: Int, rightAligned: Boolean): String {
        val spaces = length - text.length
        if (spaces <= 0) {
            return text.substring(0 until length)
        } else {
            val spacesLeft = spaces / 2
            val spacesRight = spaces - spacesLeft

            if (rightAligned) {
                return " ".repeat(spacesRight) + text + " ".repeat(spacesLeft)
            } else {
                return " ".repeat(spacesLeft) + text + " ".repeat(spacesRight)
            }
        }
    }
}