package com.kickstarter.ui.compose.designsystem

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.kickstarter.ui.compose.designsystem.KSTheme.colors
import com.kickstarter.ui.compose.designsystem.KSTheme.dimensions
import com.kickstarter.ui.compose.designsystem.KSTheme.typography

@Composable
@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
fun KSBadgesPreview() {
    KSTheme {
        Column(
            Modifier
                .background(color = colors.kds_white)
                .fillMaxWidth()
                .padding(dimensions.paddingSmall)
        ) {

            KSGreenBadge(text = "Add-ons available")

            Spacer(modifier = Modifier.height(dimensions.listItemSpacingSmall))

            KSCoralBadge(text = "3 days left")
        }
    }
}

@Composable
fun KSGreenBadge(
    leadingIcon: @Composable () -> Unit = {},
    text: String,
    textColor: Color = colors.textAccentGreen
) {
    Row(
        modifier = Modifier
            .background(
                color = colors.backgroundAccentGreenSubtle,
                shape = shapes.small
            )
            .padding(
                start = dimensions.paddingMediumSmall,
                top = dimensions.paddingSmall,
                bottom = dimensions.paddingSmall,
                end = dimensions.paddingMediumSmall
            )
    ) {
        leadingIcon()

        Text(
            text = text,
            color = textColor,
            style = typography.footnoteMedium
        )
    }
}

@Composable
fun KSCoralBadge(
    leadingIcon: @Composable () -> Unit = {},
    text: String,
    textColor: Color = colors.textSecondary
) {
    Row(
        modifier = Modifier
            .background(
                color = colors.backgroundDangerSubtle,
                shape = shapes.small
            )
            .padding(
                start = dimensions.paddingMediumSmall,
                top = dimensions.paddingSmall,
                bottom = dimensions.paddingSmall,
                end = dimensions.paddingMediumSmall
            )
    ) {
        leadingIcon()

        Text(
            text = text,
            color = textColor,
            style = typography.footnoteMedium
        )
    }
}
