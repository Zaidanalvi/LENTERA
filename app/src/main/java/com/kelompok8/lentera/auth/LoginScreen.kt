package com.kelompok8.lentera.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kelompok8.lentera.R
import com.kelompok8.lentera.core.ui.LinkText
import com.kelompok8.lentera.core.ui.PinkButton
import com.kelompok8.lentera.core.ui.avatarEmojis
import com.kelompok8.lentera.ui.theme.AvatarPink
import com.kelompok8.lentera.ui.theme.AvatarPurple
import com.kelompok8.lentera.ui.theme.FieldBlue
import com.kelompok8.lentera.ui.theme.FieldBorder
import com.kelompok8.lentera.ui.theme.LenteraLime
import com.kelompok8.lentera.ui.theme.TextLight
import com.kelompok8.lentera.ui.theme.mPlus1p

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToRegister: () -> Unit,
    onBackToMenu: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val state by viewModel.uiState.collectAsState()

    var savedAccounts by remember { mutableStateOf(SavedAccountsStore.getAccounts(context)) }
    var rememberMe by rememberSaveable { mutableStateOf(SavedAccountsStore.isRememberMeEnabled(context)) }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var showAccountSuggestions by remember { mutableStateOf(false) }
    var showForgotDialog by rememberSaveable { mutableStateOf(false) }

    // Filter akun tersimpan sesuai ketikan email (atau tampilkan semua jika email masih kosong / cocok)
    val matchingAccounts = remember(savedAccounts, email) {
        val q = email.trim()
        if (q.isEmpty()) {
            savedAccounts
        } else {
            val filtered = savedAccounts.filter {
                it.email.contains(q, ignoreCase = true) || it.name.contains(q, ignoreCase = true)
            }
            // Jika sudah sama persis dengan 1 akun yang dipilih, tetap izinkan buka semua lewat tombol dropdown
            if (filtered.size == 1 && filtered[0].email.equals(q, ignoreCase = true)) savedAccounts else filtered
        }
    }

    // Simpan akun otomatis ke perangkat saat login berhasil (jika "Ingat akun saya" aktif) dan pindah ke Beranda
    LaunchedEffect(state.loggedInUser) {
        val loggedIn = state.loggedInUser
        if (loggedIn != null) {
            if (rememberMe && email.isNotBlank() && password.isNotEmpty()) {
                SavedAccountsStore.saveAccount(
                    context = context,
                    email = email.trim(),
                    password = password,
                    name = loggedIn.name,
                    avatarId = loggedIn.avatarId
                )
            }
            onLoginSuccess()
        }
    }

    AuthLayout(heading = "Selamat Datang Kembali!") {
        Spacer(Modifier.height(10.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            LenteraTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.clearError()
                    // Jika email yang diketik cocok dengan akun tersimpan, otomatis isi password-nya juga
                    val exact = savedAccounts.find { acc -> acc.email.equals(it.trim(), ignoreCase = true) }
                    if (exact != null && password.isEmpty()) {
                        password = exact.password
                    }
                    if (savedAccounts.isNotEmpty()) {
                        showAccountSuggestions = true
                    }
                },
                placeholder = "Email",
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_email_24),
                        contentDescription = "Email",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = if (savedAccounts.isNotEmpty()) {
                    {
                        IconButton(onClick = { showAccountSuggestions = !showAccountSuggestions }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
                                contentDescription = "Pilih email tersimpan",
                                tint = LenteraLime,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                } else null,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(
                    onNext = {
                        showAccountSuggestions = false
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                modifier = Modifier.onFocusChanged { focusState ->
                    if (focusState.isFocused && savedAccounts.isNotEmpty()) {
                        showAccountSuggestions = true
                    }
                }
            )

            // Daftar saran email & password sebelumnya (muncul otomatis saat kolom Email diklik)
            AnimatedVisibility(visible = showAccountSuggestions && matchingAccounts.isNotEmpty()) {
                SavedAccountsDropdownCard(
                    accounts = matchingAccounts,
                    onSelectAccount = { account ->
                        email = account.email
                        password = account.password
                        viewModel.clearError()
                        showAccountSuggestions = false
                        focusManager.clearFocus()
                    },
                    onRemoveAccount = { account ->
                        SavedAccountsStore.removeAccount(context, account.email)
                        savedAccounts = SavedAccountsStore.getAccounts(context)
                        if (savedAccounts.isEmpty()) showAccountSuggestions = false
                    },
                    onDismiss = { showAccountSuggestions = false }
                )
            }
        }

        LenteraPasswordField(
            value = password,
            onValueChange = { password = it; viewModel.clearError() },
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(
                onDone = {
                    showAccountSuggestions = false
                    focusManager.clearFocus()
                    viewModel.login(email, password)
                }
            ),
            modifier = Modifier.onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    showAccountSuggestions = false
                }
            }
        )

        // Baris "Ingat akun saya" & "Lupa Password?" ala aplikasi modern
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                        rememberMe = !rememberMe
                        SavedAccountsStore.setRememberMe(context, rememberMe)
                    }
                    .padding(vertical = 4.dp, horizontal = 2.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (rememberMe) LenteraLime else FieldBlue)
                        .border(1.dp, if (rememberMe) LenteraLime else FieldBorder, RoundedCornerShape(4.dp))
                ) {
                    if (rememberMe) {
                        Text("✓", color = Color(0xFF0A1059), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Ingat akun saya",
                    color = TextLight,
                    fontFamily = mPlus1p,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "Lupa Password?",
                color = LenteraLime,
                fontFamily = mPlus1p,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                        viewModel.clearResetPasswordState()
                        showForgotDialog = true
                    }
                    .padding(vertical = 4.dp, horizontal = 4.dp)
            )
        }

        ErrorText(state.errorMessage)
        Spacer(Modifier.height(10.dp))
        PinkButton(
            text = "Masuk",
            isLoading = state.isLoading,
            onClick = {
                showAccountSuggestions = false
                focusManager.clearFocus()
                viewModel.login(email, password)
            }
        )
        Spacer(Modifier.height(36.dp))
        LinkText(text = "Belum punya akun? Daftar", onClick = onGoToRegister)
        LinkText(text = "Kembali ke Menu Utama", onClick = onBackToMenu)
    }

    if (showForgotDialog) {
        val defaultResetEmail = email.ifBlank { savedAccounts.firstOrNull()?.email.orEmpty() }
        ForgotPasswordDialog(
            initialEmail = defaultResetEmail,
            isLoading = state.isResettingPassword,
            errorMessage = state.resetPasswordError,
            successMessage = state.resetPasswordSuccess,
            onSendReset = { targetEmail -> viewModel.sendPasswordReset(targetEmail) },
            onClearError = { viewModel.clearResetPasswordState() },
            onDismiss = {
                showForgotDialog = false
                viewModel.clearResetPasswordState()
            }
        )
    }
}

@Composable
private fun SavedAccountsDropdownCard(
    accounts: List<SavedAccount>,
    onSelectAccount: (SavedAccount) -> Unit,
    onRemoveAccount: (SavedAccount) -> Unit,
    onDismiss: () -> Unit
) {
    val cardShape = RoundedCornerShape(12.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .clip(cardShape)
            .background(Color(0xFF101766))
            .border(1.dp, LenteraLime.copy(alpha = 0.65f), cardShape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF192282))
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Akun Tersimpan (Ketuk untuk isi otomatis)",
                color = LenteraLime,
                fontFamily = mPlus1p,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tutup",
                color = TextLight.copy(alpha = 0.8f),
                fontFamily = mPlus1p,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        accounts.forEachIndexed { index, account ->
            if (index > 0) {
                HorizontalDivider(color = Color(0x26FFFFFF))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectAccount(account) }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Brush.verticalGradient(listOf(AvatarPink, AvatarPurple)))
                        .border(1.dp, Color(0x80FFFFFF), CircleShape)
                ) {
                    Image(
                        painter = painterResource(avatarEmojis.getOrElse(account.avatarId) { avatarEmojis[0] }),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    if (account.name.isNotBlank()) {
                        Text(
                            text = account.name,
                            color = Color.White,
                            fontFamily = mPlus1p,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = account.email,
                        color = if (account.name.isNotBlank()) Color(0xFFD0D6FF) else Color.White,
                        fontFamily = mPlus1p,
                        fontSize = 13.sp,
                        fontWeight = if (account.name.isNotBlank()) FontWeight.Medium else FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (account.password.isNotEmpty()) {
                        Text(
                            text = "Password tersimpan ••••••••",
                            color = LenteraLime.copy(alpha = 0.85f),
                            fontFamily = mPlus1p,
                            fontSize = 10.sp
                        )
                    }
                }
                IconButton(
                    onClick = { onRemoveAccount(account) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Text("✕", color = Color(0xFFB9BEF2), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ForgotPasswordDialog(
    initialEmail: String,
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onSendReset: (String) -> Unit,
    onClearError: () -> Unit,
    onDismiss: () -> Unit
) {
    var resetEmail by rememberSaveable { mutableStateOf(initialEmail) }
    val dialogShape = RoundedCornerShape(22.dp)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(dialogShape)
                .background(Color(0xFF0A1059))
                .border(1.5.dp, Color(0xFF3B45DB), dialogShape)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Lupa Password?",
                color = LenteraLime,
                fontFamily = mPlus1p,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Masukkan email akun LENTERA kamu. Kami akan mengirimkan link untuk mereset password ke email tersebut.",
                color = TextLight,
                fontFamily = mPlus1p,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            LenteraTextField(
                value = resetEmail,
                onValueChange = {
                    resetEmail = it
                    onClearError()
                },
                placeholder = "Email akunmu",
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_email_24),
                        contentDescription = "Email",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                },
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(
                    onDone = { onSendReset(resetEmail) }
                )
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF6E7F),
                    fontFamily = mPlus1p,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (successMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F382B))
                        .border(1.dp, Color(0xFF4CC38A), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = successMessage,
                        color = Color(0xFF98F5C8),
                        fontFamily = mPlus1p,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                }
            }

            if (successMessage == null) {
                PinkButton(
                    text = "Kirim Link Reset",
                    isLoading = isLoading,
                    onClick = { onSendReset(resetEmail) },
                    modifier = Modifier.height(50.dp)
                )
            }

            TextButton(onClick = onDismiss) {
                Text(
                    text = if (successMessage != null) "Selesai & Kembali Login" else "Batal",
                    color = if (successMessage != null) LenteraLime else TextLight,
                    fontFamily = mPlus1p,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
