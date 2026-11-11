package br.com.fiap.climaalerta.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.FormatColorReset
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import br.com.fiap.climaalerta.domain.model.AlertType
import br.com.fiap.climaalerta.domain.model.RiskLevel
import br.com.fiap.climaalerta.domain.model.WeatherCondition
import br.com.fiap.climaalerta.ui.theme.OnRiskDark
import br.com.fiap.climaalerta.ui.theme.OnRiskLight
import br.com.fiap.climaalerta.ui.theme.RiskDanger
import br.com.fiap.climaalerta.ui.theme.RiskGreatDanger
import br.com.fiap.climaalerta.ui.theme.RiskNone
import br.com.fiap.climaalerta.ui.theme.RiskPotentialDanger

fun WeatherCondition.icon(isDay: Boolean = true): ImageVector = when (this) {
    WeatherCondition.CLEAR -> if (isDay) Icons.Filled.WbSunny else Icons.Filled.NightsStay
    WeatherCondition.PARTLY_CLOUDY -> Icons.Filled.WbCloudy
    WeatherCondition.CLOUDY -> Icons.Filled.Cloud
    WeatherCondition.FOG -> Icons.Filled.Dehaze
    WeatherCondition.DRIZZLE -> Icons.Filled.Grain
    WeatherCondition.RAIN -> Icons.Filled.Umbrella
    WeatherCondition.HEAVY_RAIN -> Icons.Filled.WaterDrop
    WeatherCondition.SNOW -> Icons.Filled.AcUnit
    WeatherCondition.THUNDERSTORM,
    WeatherCondition.THUNDERSTORM_WITH_HAIL -> Icons.Filled.Thunderstorm
    WeatherCondition.UNKNOWN -> Icons.Filled.Cloud
}

fun AlertType.icon(): ImageVector = when (this) {
    AlertType.HEAVY_RAIN -> Icons.Filled.Umbrella
    AlertType.STORM -> Icons.Filled.Thunderstorm
    AlertType.EXTREME_HEAT -> Icons.Filled.Thermostat
    AlertType.INTENSE_COLD -> Icons.Filled.AcUnit
    AlertType.STRONG_WIND -> Icons.Filled.Air
    AlertType.EXTREME_UV -> Icons.Filled.WbSunny
    AlertType.LOW_HUMIDITY -> Icons.Filled.FormatColorReset
}

fun RiskLevel.icon(): ImageVector = when (this) {
    RiskLevel.NONE -> Icons.Filled.CheckCircle
    RiskLevel.POTENTIAL_DANGER -> Icons.Filled.Info
    RiskLevel.DANGER -> Icons.Filled.Warning
    RiskLevel.GREAT_DANGER -> Icons.Filled.Report
}

val RiskLevel.color: Color
    get() = when (this) {
        RiskLevel.NONE -> RiskNone
        RiskLevel.POTENTIAL_DANGER -> RiskPotentialDanger
        RiskLevel.DANGER -> RiskDanger
        RiskLevel.GREAT_DANGER -> RiskGreatDanger
    }

val RiskLevel.onColor: Color
    get() = if (this == RiskLevel.POTENTIAL_DANGER) OnRiskDark else OnRiskLight
