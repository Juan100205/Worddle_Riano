package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.random.Random
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var boardGrid: GridLayout
    private val rows = 5
    private val cols = 4

    private var currentRow = 0
    private var currentCol = 0

    private var targetWord: String = ""
    private var gameOver = false   // ✅ bandera para bloquear el juego

    // 📌 Lista de palabras de 4 letras
    private val wordList = listOf(
        "CASA", "LUNA", "ROSA", "PERA", "MESA",
        "GATO", "NUBE", "FLOR", "RATA", "PATO",
        "LEON", "VIDA", "CIEG", "FUEG", "SOLI"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardGrid = findViewById(R.id.boardGrid)

        setupBoard()
        setupKeyboard()
        chooseRandomWord()

        // ✅ Botón restart
        findViewById<Button>(R.id.restart).setOnClickListener {
            restartGame()
        }
    }

    /** Elegir palabra aleatoria */
    private fun chooseRandomWord() {
        targetWord = wordList[Random.nextInt(wordList.size)]
        println("Palabra objetivo: $targetWord")
    }

    /** Crear el tablero vacío */
    private fun setupBoard() {
        boardGrid.removeAllViews() // 🔄 limpiar antes de volver a llenar
        val totalCells = rows * cols
        for (i in 0 until totalCells) {
            val cell = layoutInflater.inflate(R.layout.cell_item, boardGrid, false)
            boardGrid.addView(cell)
        }
    }

    /** Obtener una celda específica */
    private fun getCell(row: Int, col: Int): TextView {
        val index = row * cols + col
        val cell = boardGrid.getChildAt(index)
        return cell.findViewById(R.id.cell_text)
    }

    /** Configurar el teclado */
    private fun setupKeyboard() {
        val letters = listOf(
            R.id.q, R.id.w, R.id.e, R.id.r, R.id.t,
            R.id.y, R.id.u, R.id.i, R.id.o, R.id.p,
            R.id.a, R.id.s, R.id.d, R.id.f, R.id.g,
            R.id.h, R.id.j, R.id.k, R.id.l, R.id.ñ,
            R.id.z, R.id.x, R.id.c, R.id.v, R.id.b,
            R.id.n, R.id.m
        )

        for (id in letters) {
            val btn = findViewById<Button>(id)
            btn.setOnClickListener {
                addLetter(btn.text.toString())
            }
        }

        findViewById<Button>(R.id.delete)?.setOnClickListener { deleteLetter() }
        findViewById<Button>(R.id.enter)?.setOnClickListener { checkWord() }
    }

    /** Agregar letra en la fila actual */
    private fun addLetter(letter: String) {
        if (gameOver) return
        if (currentCol < cols) {
            getCell(currentRow, currentCol).text = letter
            currentCol++
        }
    }

    /** Borrar última letra */
    private fun deleteLetter() {
        if (gameOver) return
        if (currentCol > 0) {
            currentCol--
            getCell(currentRow, currentCol).text = ""
        }
    }

    /** Validar palabra escrita en la fila actual */
    /** Validar palabra escrita en la fila actual */
    private fun checkWord() {
        if (gameOver) return
        if (currentCol == cols) {
            val word = (0 until cols).joinToString("") { getCell(currentRow, it).text.toString() }

            // ✅ Contamos cuántas veces aparece cada letra en la palabra objetivo
            val letterCount = mutableMapOf<Char, Int>()
            for (c in targetWord) {
                letterCount[c] = letterCount.getOrDefault(c, 0) + 1
            }

            // ✅ Primero marcamos los verdes (exactos)
            val resultColors = Array(cols) { R.color.gray }
            for (i in 0 until cols) {
                val cell = getCell(currentRow, i)
                val letter = word[i]

                if (targetWord[i] == letter) {
                    resultColors[i] = R.color.green
                    letterCount[letter] = letterCount[letter]!! - 1 // gastamos esa letra
                }
            }

            // ✅ Luego marcamos los naranjas (presentes pero en otra posición)
            for (i in 0 until cols) {
                val letter = word[i]
                if (resultColors[i] == R.color.gray && letterCount.getOrDefault(letter, 0) > 0) {
                    resultColors[i] = R.color.orange
                    letterCount[letter] = letterCount[letter]!! - 1 // gastamos una ocurrencia
                }
            }

            // ✅ Aplicamos los colores a las celdas
            for (i in 0 until cols) {
                val cell = getCell(currentRow, i)
                cell.setBackgroundColor(ContextCompat.getColor(this, resultColors[i]))
            }

            // ✅ Revisar victoria
            if (word.equals(targetWord, ignoreCase = true)) {
                Toast.makeText(this, "🎉 GANASTE! La palabra era: $word", Toast.LENGTH_LONG).show()
                gameOver = true
                return
            }

            currentRow++
            currentCol = 0

            if (currentRow >= rows) {
                Toast.makeText(this, "😢 PERDISTE. La palabra era: $targetWord", Toast.LENGTH_LONG).show()

                gameOver = true
            }
        }
    }


    /** 🔄 Reiniciar el juego */
    private fun restartGame() {
        currentRow = 0
        currentCol = 0
        gameOver = false
        setupBoard()
        chooseRandomWord()
    }
}
