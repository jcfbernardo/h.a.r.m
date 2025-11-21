package com.harm.api.engine

import com.harm.api.engine.model.Chord
import com.harm.api.engine.model.Measure
import com.harm.api.engine.model.ParsedProgression
import org.springframework.stereotype.Service

@Service
class ProgressionParserService {

    fun parse(
        rawProgressionText: String, timeSignature: String
    ): ParsedProgression {
        var allMeasures = mutableListOf<Measure>()
        var currentSection: String? = null
        val pattern = """\[(.*?)]""".toRegex()

        rawProgressionText.lines().forEach { line ->
            if (pattern.containsMatchIn(line)) {
                val updatedLine: String = line.replace(pattern, "")
                currentSection = updatedLine
                return@forEach
            } else if (line.contains("|")) {
                val compass = line
                    .trim()
                    .split("|")
                    .map { line.trim() }
                val chordList = mutableListOf<Chord>()
                for (comp in compass) {
                    comp
                        .split(" ")
                        .map { chordList.add(Chord(comp)) }
                }
                val measure = Measure(currentSection, chordList)
                allMeasures.add(measure)
                return@forEach
            } else {
                val chordList = line
                    .split(" ")
                    .map { chord -> Chord(chord) }
                for (chord in chordList) {
                    val measure = Measure(currentSection, listOf(chord))
                    allMeasures.add(measure)
                }
            }
        }
        return ParsedProgression(allMeasures)
    }
}

