package com.thierryguichardaz.xianzai.ui.feature.main // o ui.feature.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.thierryguichardaz.xianzai.ui.theme.XianZaiTheme

@Composable
fun CalendarScreen() {
    // Contenuto verrà aggiunto dopo
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Calendar Screen Placeholder")
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    XianZaiTheme {
        CalendarScreen()
    }
}