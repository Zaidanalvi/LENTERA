package com.kelompok8.lentera.auth

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kelompok8.lentera.R
import com.kelompok8.lentera.core.ui.AvatarImage
import com.kelompok8.lentera.core.ui.LenteraLogo
import com.kelompok8.lentera.core.ui.avatarEmojis
import com.kelompok8.lentera.ui.theme.FieldBlue
import com.kelompok8.lentera.ui.theme.FieldBorder
import com.kelompok8.lentera.ui.theme.LenteraLime
import com.kelompok8.lentera.ui.theme.TextLight
import com.kelompok8.lentera.ui.theme.mPlus1p

// Komponen form yang dipakai bareng oleh halaman Login dan Register

val genderOptions = listOf("Pria", "Wanita")

val provinceOptions = listOf(
    "Aceh", "Sumatera Utara", "Sumatera Barat", "Riau", "Kepulauan Riau", "Jambi",
    "Sumatera Selatan", "Kepulauan Bangka Belitung", "Bengkulu", "Lampung",
    "DKI Jakarta", "Jawa Barat", "Banten", "Jawa Tengah", "DI Yogyakarta", "Jawa Timur",
    "Bali", "Nusa Tenggara Barat", "Nusa Tenggara Timur",
    "Kalimantan Barat", "Kalimantan Tengah", "Kalimantan Selatan", "Kalimantan Timur", "Kalimantan Utara",
    "Sulawesi Utara", "Gorontalo", "Sulawesi Tengah", "Sulawesi Barat", "Sulawesi Selatan", "Sulawesi Tenggara",
    "Maluku", "Maluku Utara",
    "Papua", "Papua Barat", "Papua Barat Daya", "Papua Tengah", "Papua Pegunungan", "Papua Selatan"
)

private val fieldShape = RoundedCornerShape(10.dp)
private val fieldTextStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

@Composable
fun AuthLayout(heading: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        LenteraLogo()
        Spacer(Modifier.height(20.dp))
        Text(heading, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextLight)
        content()
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = FieldBlue,
    unfocusedContainerColor = FieldBlue,
    focusedBorderColor = LenteraLime,
    unfocusedBorderColor = FieldBorder,
    focusedTextColor = TextLight,
    unfocusedTextColor = TextLight,
    focusedPlaceholderColor = TextLight.copy(alpha = 0.7f),
    unfocusedPlaceholderColor = TextLight.copy(alpha = 0.7f),
    cursorColor = LenteraLime
)

@Composable
fun LenteraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: @Composable () -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, style = fieldTextStyle) },
        leadingIcon = icon as @Composable (() -> Unit)?,
        singleLine = true,
        textStyle = fieldTextStyle,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = fieldShape,
        colors = fieldColors(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun LenteraPasswordField(value: String, onValueChange: (String) -> Unit) {
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Password", style = fieldTextStyle) },
        leadingIcon = { Icon(
            painter = painterResource(R.drawable.outline_passkey_24),
            contentDescription = "Password",
            tint = Color(0xFFFFFFFF),
            modifier = Modifier.size(20.dp))
        },
        trailingIcon = {
            TextButton(onClick = { visible = !visible }) {
                Text(if (visible) "Sembunyi" else "Lihat", color = TextLight)
            }
        },
        singleLine = true,
        textStyle = fieldTextStyle,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = fieldShape,
        colors = fieldColors(),
        modifier = Modifier.fillMaxWidth()
    )
}

// Dropdown dibuat sendiri (bukan ExposedDropdownMenuBox) supaya tampilannya bisa persis Figma
@Composable
fun LenteraDropdown(
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit,
    placeholder: String,
    icon: Painter,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(fieldShape)
                .background(FieldBlue)
                .border(1.dp, FieldBorder, fieldShape)
                .clickable { expanded = true }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(14.dp))
            Text(
                text = selected.ifEmpty { placeholder },
                style = fieldTextStyle,
                color = if (selected.isEmpty()) TextLight.copy(alpha = 0.7f) else TextLight,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color(0xFF1E1B4B))
                .width(347.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color(0xFFFFFFFF), fontFamily = mPlus1p) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AvatarPicker(selectedId: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        avatarEmojis.indices.forEach { id ->
            AvatarImage(
                avatarId = id,
                highlighted = id == selectedId,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clickable { onSelect(id) }
            )
        }
    }
}

@Composable
fun ErrorText(message: String?) {
    if (message != null) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
