package com.example.pdfreader.model

class TableCell(
    val row: Int,
    val column: Int,
    val value: String,
    val style: String = "border: 1px solid black;"
) {

    fun toHtml(): String {
        return """
            <td style="$style">
                $value
            </td>
        """.trimIndent()
    }
}

class Table {

    val cells = mutableListOf<TableCell>()

    fun addCell(cell: TableCell) {
        cells.add(cell)
    }

    fun toHtml(): String {
        return """
            <table border="1" style="border-collapse: collapse;">
                <tbody>
                    ${cells.joinToString("\n")}
                </tbody>
            </table>
        """.trimIndent()
    }
}