package com.wildanimalpuzzles.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

private data class Animal(
    val id: Int,
    val name: String,
    val emoji: String,
    val color: Color
)

private data class Piece(
    val id: Int,
    val animalId: Int,
    val correctSlot: Int,
    var rotation: Int = 0
)

private val animals = listOf(
    Animal(1, "Fox", "🦊", Color(0xFFE65100)),
    Animal(2, "Whale", "🐳", Color(0xFF1565C0)),
    Animal(3, "Turtle", "🐢", Color(0xFF2E7D32)),
    Animal(4, "Owl", "🦉", Color(0xFF6A1B9A)),
    Animal(5, "Bear", "🐻", Color(0xFF4E342E)),
    Animal(6, "Lion", "🦁", Color(0xFFF57F17)),
    Animal(7, "Hippo", "🦛", Color(0xFF00838F)),
    Animal(8, "Deer", "🦌", Color(0xFFD81B60))
)

private fun getLevelCount(level: Int): Int = (6 + ((level - 1) * (24 / 19))).coerceAtMost(30)

private fun createPieces(levelCount: Int): List<Piece> = List(levelCount) { index ->
    val animal = animals[index % animals.size]
    Piece(
        id = index,
        animalId = animal.id,
        correctSlot = index,
        rotation = Random.nextInt(0, 4) * 90
    )
}

@Composable
fun AppContent() {
    val level = rememberSaveable { mutableIntStateOf(1) }
    val currentCount = remember(level.value) { getLevelCount(level.value) }
    val columns = remember(currentCount) { ((Math.sqrt(currentCount.toDouble()))).toInt().coerceAtLeast(2) }
    val rows = remember(currentCount, columns) { ((currentCount.toDouble() / columns).toDouble()).toInt().coerceAtLeast(1) }

    val boardSlots = remember(level.value) {
        mutableStateListOf<Piece?>().apply {
            repeat(currentCount) { add(null) }
        }
    }

    val bankPieces = remember(level.value) {
        mutableStateListOf<Piece>().apply {
            addAll(createPieces(currentCount).shuffled())
        }
    }

    var selectedPiece by remember { mutableStateOf<Piece?>(null) }
    var timeSeconds by remember { mutableIntStateOf(0) }
    var won by remember { mutableStateOf(false) }

    LaunchedEffect(level.value) {
        timeSeconds = 0
        won = false
        selectedPiece = null
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            timeSeconds += 1
        }
    }

    fun rotatePiece(piece: Piece) {
        piece.rotation = (piece.rotation + 90) % 360
        selectedPiece = piece
        checkWin(boardSlots, currentCount) { won = true }
    }

    fun placeSelectedInSlot(slotIndex: Int) {
        val piece = selectedPiece ?: return
        if (boardSlots[slotIndex] != null) return
        boardSlots[slotIndex] = piece
        bankPieces.remove(piece)
        selectedPiece = null
        checkWin(boardSlots, currentCount) { won = true }
    }

    fun movePieceBackToBank(piece: Piece) {
        val slotIndex = boardSlots.indexOf(piece)
        if (slotIndex >= 0) boardSlots[slotIndex] = null
        if (!bankPieces.contains(piece)) bankPieces.add(piece)
        selectedPiece = null
    }

    fun resetLevel() {
        selectedPiece = null
        boardSlots.clear()
        repeat(currentCount) { boardSlots.add(null) }
        bankPieces.clear()
        bankPieces.addAll(createPieces(currentCount).shuffled())
        timeSeconds = 0
        won = false
    }

    fun nextLevel() {
        val nextLevel = (level.value % 20) + 1
        level.intValue = nextLevel
        resetLevel()
    }

    Surface(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF4E8D1)),
        color = Color(0xFFF4E8D1)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Wild Animal Puzzles",
                color = Color(0xFF5C3A21),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Level: ${level.value}", color = Color(0xFF5C3A21), fontWeight = FontWeight.Bold)
                Text("Time: ${formatTime(timeSeconds)}", color = Color(0xFF5C3A21), fontWeight = FontWeight.Bold)
                Button(onClick = { resetLevel() }) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Restart")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { if (level.value > 1) level.intValue -= 1 }) {
                    Text("Previous")
                }
                Button(onClick = { if (level.value < 20) level.intValue += 1 }) {
                    Text("Next")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier
                        .weight(1f)
                        .height((rows * 84).dp)
                        .background(Color(0xFFD7A15C), RoundedCornerShape(18.dp))
                        .border(4.dp, Color(0xFF8B5A2B), RoundedCornerShape(18.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(boardSlots.size) { index ->
                        val piece = boardSlots[index]
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(Color(0xFFB8860B), RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFF663300), RoundedCornerShape(10.dp))
                                .clickable {
                                    if (selectedPiece != null && piece == null) {
                                        placeSelectedInSlot(index)
                                    } else if (piece != null) {
                                        selectedPiece = piece
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (piece != null) {
                                PieceTile(
                                    piece = piece,
                                    selected = selectedPiece == piece,
                                    onSelect = { selectedPiece = piece },
                                    onRotate = { rotatePiece(piece) },
                                    onReturn = { movePieceBackToBank(piece) }
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .background(Color(0xFFE6C594), RoundedCornerShape(18.dp))
                        .border(3.dp, Color(0xFF8B5A2B), RoundedCornerShape(18.dp))
                        .padding(10.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(bankPieces) { piece ->
                            PieceTile(
                                piece = piece,
                                selected = selectedPiece == piece,
                                onSelect = { selectedPiece = piece },
                                onRotate = { rotatePiece(piece) },
                                onReturn = { movePieceBackToBank(piece) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (won) {
        AlertDialog(
            onDismissRequest = { won = false },
            title = { Text("🎉 Level Complete!") },
            text = { Text("You solved level ${level.value} in ${formatTime(timeSeconds)}.") },
            confirmButton = {
                Button(onClick = { nextLevel() }) {
                    Text("Next Level")
                }
            }
        )
    }
}

@Composable
private fun PieceTile(
    piece: Piece,
    selected: Boolean,
    onSelect: () -> Unit,
    onRotate: () -> Unit,
    onReturn: () -> Unit
) {
    val animal = animals.first { it.id == piece.animalId }
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(animal.color, RoundedCornerShape(12.dp))
            .border(
                width = if (selected) 3.dp else 0.dp,
                color = if (selected) Color.White else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onSelect() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = animal.emoji,
            fontSize = 28.sp,
            modifier = Modifier.rotatePiece(piece.rotation)
        )

        IconButton(
            onClick = onRotate,
            modifier = Modifier.align(Alignment.TopEnd).size(22.dp)
        ) {
            Icon(Icons.Default.RotateRight, contentDescription = "Rotate", tint = Color.White)
        }

        IconButton(
            onClick = onReturn,
            modifier = Modifier.align(Alignment.BottomEnd).size(22.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Return", tint = Color.White)
        }
    }
}

private fun checkWin(boardSlots: List<Piece?>, total: Int, callback: () -> Unit) {
    val solved = boardSlots.withIndex().count { (index, piece) ->
        piece != null && piece.correctSlot == index && piece.rotation == 0
    }

    if (solved == total) {
        callback()
    }
}

private fun formatTime(totalSeconds: Int): String {
    val minute = totalSeconds / 60
    val second = totalSeconds % 60
    return "%02d:%02d".format(minute, second)
}

private fun Modifier.rotatePiece(rotation: Int): Modifier = this.then(
    androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.5f)
)

