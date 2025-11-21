package com.harm.api.engine

import com.harm.api.engine.model.AnalyzedChord
import com.harm.api.engine.model.AnalyzedProgression
import com.harm.api.engine.model.ParsedProgression
import org.springframework.stereotype.Service

@Service
class ProgressionAnalyzerService {
    val sharpNotes = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    val flatNotes = listOf("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B")

    fun analyze(parsedProgression: ParsedProgression, key: String): AnalyzedProgression {
        val scaleMap = getScaleMap(key)
        val analyzedMeasures  = mutableListOf<MutableList<AnalyzedChord>>()
        for (measure in parsedProgression.measures) {
            val analyzedChords = mutableListOf<AnalyzedChord>()

            for (chord in measure.chords) {
                val normalizedChord = rawChord(chord.rawChord)

                val roman = scaleMap[normalizedChord] ?: "NON_DIATONIC"
                analyzedChords.add(AnalyzedChord(chord.rawChord, roman))
            }
            analyzedMeasures.add(analyzedChords)
        }
        return AnalyzedProgression(analyzedMeasures)
    }

    private fun getScaleMap(key: String): Map<String, String> {

        val majorPattern = listOf(2, 2, 1, 2, 2, 2, 1)
        val minorNaturalPattern = listOf(2, 1, 2, 2, 1, 2, 2)
        val minorHarmonicPattern = listOf(2, 1, 2, 2, 1, 3, 1)
        val minorMelodicPattern = listOf(2, 1, 2, 2, 2, 2, 1)

        val majorRN = listOf("I", "ii", "iii", "IV", "V", "vi", "vii°")
        val minorNatRN = listOf("i", "ii°", "III", "iv", "v", "VI", "VII")
        val minorHarRN = listOf("i", "ii°", "III+", "iv", "V", "VI", "vii°")
        val minorMelRN = listOf("i", "ii", "III+", "IV", "V", "vi°", "vii°")

        val (tonic, mode) = parseKey(key)

        val pattern = when (mode) {
            "MAJOR" -> majorPattern
            "MINOR_NAT" -> minorNaturalPattern
            "MINOR_HARM" -> minorHarmonicPattern
            "MINOR_MELO" -> minorMelodicPattern
            else -> error("Invalid mode: $mode")
        }

        val roman = when (mode) {
            "MAJOR" -> majorRN
            "MINOR_NAT" -> minorNatRN
            "MINOR_HARM" -> minorHarRN
            "MINOR_MELO" -> minorMelRN
            else -> error("Invalid mode: $mode")
        }

        val scale = buildScale(tonic, pattern)
        val flats = useFlats(tonic)

        return scale.mapIndexed { i, note ->
            buildTriad(note, scale, flats) to roman[i]
        }.toMap()

    }

    private fun buildScale(key: String, pattern: List<Int>): List<String> {
        val flats = useFlats(key)
        val notes = if (flats) flatNotes else sharpNotes

        val startIdx = notes.indexOf(key)
        var index = startIdx
        val scale = mutableListOf(key)

        for (step in pattern) {
            index = (index + step) % 12
            scale += notes[index]
        }
        return scale
    }

    private fun useFlats(key: String): Boolean {
        return key.contains("b") || key in listOf("F", "Bb", "Eb", "Ab", "Db", "Gb", "Cb")
    }

    private fun buildTriad(root: String, scale: List<String>, flats: Boolean): String {
        val notes = if (flats) flatNotes else sharpNotes

        val rootIndex = notes.indexOf(root)
        val thirdIndex = notes.indexOf(scale[(scale.indexOf(root) + 2) % 7])
        val fifthIndex = notes.indexOf(scale[(scale.indexOf(root) + 4) % 7])

        fun semitones(a: Int, b: Int) = (b - a + 12) % 12

        val intervals = listOf(
            0,
            semitones(rootIndex, thirdIndex),
            semitones(rootIndex, fifthIndex)
        )

        val t = triadType(intervals)
        return root + t
    }

    private fun triadType(intervals: List<Int>): String =
        when (intervals) {
            listOf(0, 3, 7) -> "m"
            listOf(0, 4, 7) -> ""
            listOf(0, 3, 6) -> "dim"
            listOf(0, 4, 8) -> "+"
            else -> "?"
        }

    private fun parseKey(key: String): Pair<String, String> {
        val parts = key.split("_")
        return parts[0] to parts[1]
    }

    private fun rawChord(chord: String): String {
        return chord.split(Regex("[0-9/]+"))[0]
    }
}
