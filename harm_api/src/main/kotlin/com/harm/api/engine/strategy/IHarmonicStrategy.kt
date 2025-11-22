package com.harm.api.engine.strategy

import com.harm.api.engine.model.AnalyzedProgression
import com.harm.api.engine.model.ProcessedProgression

interface IHarmonicStrategy {
    fun getStyleName(): String
    fun reharmonize(analyzedProgression: AnalyzedProgression, key: String): ProcessedProgression
}