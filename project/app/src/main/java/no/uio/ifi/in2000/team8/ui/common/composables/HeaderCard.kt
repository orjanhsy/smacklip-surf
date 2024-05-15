package no.uio.ifi.in2000.team8.ui.common.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team8.model.surfareas.SurfArea
import no.uio.ifi.in2000.team8.ui.surfarea.calculateFontSizeForText
import no.uio.ifi.in2000.team8.ui.theme.AppTypography
import no.uio.ifi.in2000.team8.utils.ResourceUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HeaderCard(surfArea: SurfArea, icon : String, date: LocalDateTime) {
    val formatter1 = DateTimeFormatter.ofPattern("E d. MMM", Locale("no", "NO"))
    val formattedDate1 = formatter1.format(date)

    //to get icon
    val resourceUtils = ResourceUtils()


    Box(
        modifier = Modifier
            .width(317.dp)
            .height(150.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
            shape = RoundedCornerShape(size = 12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                ) {
                    Row {
                        Text(
                            text = surfArea.locationName + "," + "\n " + surfArea.areaName, //+surfArea.areaName //hadde vært fint med Stadt
                            style = TextStyle(
                                fontSize = calculateFontSizeForText(surfArea.locationName),
                                fontWeight = FontWeight(500),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier
                                .padding(16.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = formattedDate1,
                            style = AppTypography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,

                            modifier = Modifier
                                .padding(5.dp)
                                .width(73.dp)
                                .height(16.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .shadow(
                            elevation = 37.425743103027344.dp,
                            spotColor = Color(0x0D000000),
                            ambientColor = Color(0x0D000000)
                        )
                        .padding(1.24752.dp)
                ) {
                    Image(
                        painter = painterResource(id = resourceUtils.findWeatherSymbol(icon)),
                        contentDescription = "image description",
                        modifier = Modifier
                            .width(126.dp)
                            .height(126.dp)
                    )
                }
            }
        }
    }
}