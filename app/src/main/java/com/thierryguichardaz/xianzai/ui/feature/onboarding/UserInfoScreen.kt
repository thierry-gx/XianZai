package com.thierryguichardaz.xianzai.ui.feature.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Necessario se non usi DI per il repo
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thierryguichardaz.xianzai.data.preferences.UserPreferencesRepository // Importa repo
import com.thierryguichardaz.xianzai.ui.theme.XianZaiTheme
import kotlinx.coroutines.launch // Importa launch
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfoScreen(
    chronotype: String, // Il cronotipo determinato dal quiz
    userPreferencesRepository: UserPreferencesRepository,
    onUserInfoComplete: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    val isFormValid by remember {
        derivedStateOf {
            name.isNotBlank() && age.isNotBlank() && age.toIntOrNull() != null && (age.toIntOrNull() ?: 0) > 0
        }
    }
    val scope = rememberCoroutineScope()

    Scaffold( /* ... (TopAppBar invariata) ... */ ) { paddingValues ->
        Column( /* ... (Layout invariato) ... */ ) {
            // ... (Text, OutlinedTextFields, Spacer invariati) ...

            Button(
                onClick = {
                    scope.launch {
                        // --- MODIFICHE QUI ---
                        // 1. SALVA IL CRONOTIPO EFFETTIVO
                        userPreferencesRepository.saveChronotype(chronotype)

                        // 2. SALVA IL COMPLETAMENTO DELL'ONBOARDING
                        userPreferencesRepository.setOnboardingComplete(true)

                        // 3. (Opzionale) Salva nome/età se implementato nel repo
                        // userPreferencesRepository.saveUserName(name)
                        // userPreferencesRepository.saveUserAge(age.toInt())
                        // --- FINE MODIFICHE ---

                        // 4. Chiama la callback DOPO che il salvataggio è avvenuto (o almeno avviato)
                        onUserInfoComplete()
                    }
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Save & Start Using XianZai")
            }
        }
    }
}

// Preview non può fornire facilmente un Repository, quindi commentala o usa un mock
// @Preview(showBackground = true, widthDp = 360, heightDp = 640)
// @Composable
// fun UserInfoScreenPreview() {
//     XianZaiTheme {
//         // Per la preview, potresti dover creare un'istanza fittizia del repository
//         val context = LocalContext.current
//         val dummyRepo = UserPreferencesRepository(context)
//         UserInfoScreen(
//              chronotype = "Intermediate",
//              userPreferencesRepository = dummyRepo, // Usa repo fittizio
//              onUserInfoComplete = {}
//          )
//      }
// }