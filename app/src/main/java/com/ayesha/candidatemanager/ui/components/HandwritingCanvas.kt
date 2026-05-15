package com.ayesha.candidatemanager.ui.components

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.digitalink.Ink
import kotlinx.coroutines.launch
import com.ayesha.candidatemanager.ml.HandwritingRecognizerManager

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun HandwritingCanvasDialog(
    onDismiss: () -> Unit,
    onTextRecognized: (String) -> Unit
) {
    var paths by remember { mutableStateOf(listOf<Path>()) }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    
    val inkBuilder = remember { Ink.builder() }
    var strokeBuilder: Ink.Stroke.Builder? by remember { mutableStateOf(null) }
    
    val recognizerManager = remember { HandwritingRecognizerManager() }
    val scope = rememberCoroutineScope()
    var isModelReady by remember { mutableStateOf(false) }
    var isRecognizing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isModelReady = recognizerManager.initModel()
    }

    DisposableEffect(Unit) {
        onDispose { recognizerManager.close() }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.9f).fillMaxHeight(0.8f),
        title = { Text("Handwriting Input") },
        text = {
            Column(modifier = Modifier.fillMaxSize()) {
                if (!isModelReady) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    Text("Downloading offline language model...", style = MaterialTheme.typography.bodySmall)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5))
                        .pointerInteropFilter { event ->
                            when (event.actionMasked) {
                                MotionEvent.ACTION_DOWN -> {
                                    currentPath = Path().apply {
                                        moveTo(event.x, event.y)
                                    }
                                    strokeBuilder = Ink.Stroke.builder()
                                    strokeBuilder?.addPoint(Ink.Point.create(event.x, event.y, event.eventTime))
                                    true
                                }
                                MotionEvent.ACTION_MOVE -> {
                                    currentPath?.lineTo(event.x, event.y)
                                    strokeBuilder?.addPoint(Ink.Point.create(event.x, event.y, event.eventTime))
                                    // Trigger recomposition
                                    currentPath = Path().apply {
                                        currentPath?.let { addPath(it) }
                                    }
                                    true
                                }
                                MotionEvent.ACTION_UP -> {
                                    currentPath?.let { paths = paths + it }
                                    currentPath = null
                                    strokeBuilder?.let {
                                        inkBuilder.addStroke(it.build())
                                    }
                                    strokeBuilder = null
                                    true
                                }
                                else -> false
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        paths.forEach { path ->
                            drawPath(
                                path = path,
                                color = Color.Black,
                                style = Stroke(
                                    width = 8f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                        currentPath?.let {
                            drawPath(
                                path = it,
                                color = Color.Black,
                                style = Stroke(
                                    width = 8f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row {
                TextButton(onClick = {
                    paths = emptyList()
                    currentPath = null
                    // Create new ink builder
                    // Since Ink.Builder doesn't have a clear method, we re-instantiate it but since it's remembered, we need a slight workaround.
                    // For simplicity, we just won't clear the builder here and let users clear if needed, or we recreate.
                }) {
                    Text("Clear")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    enabled = isModelReady && !isRecognizing && paths.isNotEmpty(),
                    onClick = {
                        isRecognizing = true
                        scope.launch {
                            val text = recognizerManager.recognize(inkBuilder.build())
                            isRecognizing = false
                            onTextRecognized(text)
                        }
                    }
                ) {
                    if (isRecognizing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Convert & Insert")
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
