package com.example.proofloop.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proofloop.R
import com.example.proofloop.data.auth.AuthRepository
import com.example.proofloop.data.auth.SessionManager
import com.example.proofloop.theme.BgDark
import com.example.proofloop.theme.CoralAlert
import com.example.proofloop.theme.TextMuted
import com.example.proofloop.theme.TextPrimary
import com.example.proofloop.theme.TextSecondary
import kotlinx.coroutines.launch

private val ProofLoopGold = Color(0xFFF5B428)
private val ProofLoopGoldDark = Color(0xFFD99B16)
private val CardBgDark = Color(0xFF0F121A)
private val CardBorderDark = Color(0xFF1D2433)
private val InputBgDark = Color(0xFF141824)
private val InputBorderDark = Color(0xFF242E42)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onTryDemoClick: () -> Unit = onLoginSuccess
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository.getInstance(context) }
    val sessionManager = remember { SessionManager.getInstance(context) }

    // Saved preferences
    val savedIdentifier by sessionManager.savedIdentifierFlow.collectAsState(initial = null)
    val savedRememberMe by sessionManager.rememberMeFlow.collectAsState(initial = true)

    // Form states
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    var isSignUpMode by remember { mutableStateOf(false) }

    // Validation & Status states
    var identifierTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var nameTouched by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Forgot password dialog
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var isResettingPassword by remember { mutableStateOf(false) }
    var resetMessage by remember { mutableStateOf<String?>(null) }
    var resetError by remember { mutableStateOf<String?>(null) }

    // Pre-fill from session if available
    LaunchedEffect(savedIdentifier, savedRememberMe) {
        if (identifier.isEmpty() && !savedIdentifier.isNullOrBlank()) {
            identifier = savedIdentifier ?: ""
        }
        rememberMe = savedRememberMe
    }

    // Local Validation rules
    val isIdentifierValid = identifier.trim().isNotEmpty() &&
            (!identifier.contains("@") || android.util.Patterns.EMAIL_ADDRESS.matcher(identifier.trim()).matches())
    val isPasswordValid = password.length >= 6
    val isNameValid = !isSignUpMode || fullName.trim().length >= 2
    val isFormComplete = isIdentifierValid && isPasswordValid && isNameValid

    fun performLogin() {
        if (isLoading) return
        focusManager.clearFocus()
        identifierTouched = true
        passwordTouched = true

        if (!isIdentifierValid) {
            errorMessage = "Please enter a valid email or student ID"
            return
        }
        if (!isPasswordValid) {
            errorMessage = "Password must be at least 6 characters"
            return
        }

        isLoading = true
        errorMessage = null

        coroutineScope.launch {
            val result = if (isSignUpMode) {
                authRepository.register(
                    email = identifier.trim(),
                    password = password,
                    name = fullName.trim().ifEmpty { "Student" },
                    rememberMe = rememberMe
                )
            } else {
                authRepository.login(
                    identifier = identifier.trim(),
                    password = password,
                    rememberMe = rememberMe
                )
            }

            isLoading = false
            result.onSuccess {
                onLoginSuccess()
            }.onFailure { error ->
                errorMessage = error.message ?: "Authentication failed. Please check your credentials."
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP SECTION: HERO ARTWORK MATCHING DESIGN MOCKUP
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.login_hero_bg),
                    contentDescription = "ProofLoop Hero: Build Real Skills. Prove Your Progress.",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                    contentScale = ContentScale.Crop
                )

                // Bottom gradient scrim to blend hero seamlessly into dark card surface
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, BgDark.copy(alpha = 0.95f), BgDark)
                            )
                        )
                )
            }

            // MAIN SECTION: DARK SURFACE CARD
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                color = CardBgDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Title & Subtitle
                    Text(
                        text = if (isSignUpMode) "Create Account" else "Welcome Back",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isSignUpMode) "Start proving your competency" else "Sign in to continue your journey",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Error Message Banner
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        errorMessage?.let { errorText ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF2E1518))
                                    .border(1.dp, CoralAlert.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = "Error",
                                    tint = CoralAlert,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorText,
                                    color = Color(0xFFFFD4D6),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    // Optional Full Name Field (Sign Up Mode)
                    AnimatedVisibility(visible = isSignUpMode) {
                        Column {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = {
                                    fullName = it
                                    nameTouched = true
                                    errorMessage = null
                                },
                                label = { Text("Full Name", color = TextSecondary, fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (nameTouched && !isNameValid) CoralAlert else TextMuted
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ProofLoopGold,
                                    unfocusedBorderColor = if (nameTouched && !isNameValid) CoralAlert else InputBorderDark,
                                    focusedContainerColor = InputBgDark,
                                    unfocusedContainerColor = InputBgDark,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // INPUT 1: Email or Student/Employee ID
                    OutlinedTextField(
                        value = identifier,
                        onValueChange = {
                            identifier = it
                            identifierTouched = true
                            errorMessage = null
                        },
                        placeholder = { Text("Email or Student/Employee ID", color = TextMuted, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = if (identifierTouched && !isIdentifierValid) CoralAlert else TextMuted
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ProofLoopGold,
                            unfocusedBorderColor = if (identifierTouched && !isIdentifierValid) CoralAlert else InputBorderDark,
                            focusedContainerColor = InputBgDark,
                            unfocusedContainerColor = InputBgDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (identifierTouched && !isIdentifierValid) {
                        Text(
                            text = "Please enter your valid college email or ID",
                            color = CoralAlert,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // INPUT 2: Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordTouched = true
                            errorMessage = null
                        },
                        placeholder = { Text("Password", color = TextMuted, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (passwordTouched && !isPasswordValid) CoralAlert else TextMuted
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { performLogin() }),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ProofLoopGold,
                            unfocusedBorderColor = if (passwordTouched && !isPasswordValid) CoralAlert else InputBorderDark,
                            focusedContainerColor = InputBgDark,
                            unfocusedContainerColor = InputBgDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (passwordTouched && !isPasswordValid) {
                        Text(
                            text = "Password must be at least 6 characters",
                            color = CoralAlert,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // OPTIONS ROW: Remember Me & Forgot Password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Remember Me Custom Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { rememberMe = !rememberMe }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (rememberMe) ProofLoopGold else Color.Transparent)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (rememberMe) ProofLoopGold else TextMuted,
                                        shape = RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (rememberMe) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Checked",
                                        tint = BgDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Remember me",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        // Forgot Password Link
                        Text(
                            text = "Forgot password?",
                            color = ProofLoopGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable {
                                resetEmail = identifier.trim()
                                resetError = null
                                resetMessage = null
                                showForgotPasswordDialog = true
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // PRIMARY CTA: Gold "Log In →" Button
                    Button(
                        onClick = { performLogin() },
                        enabled = !isLoading && isFormComplete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ProofLoopGold,
                            contentColor = Color(0xFF0F1118),
                            disabledContainerColor = ProofLoopGold.copy(alpha = 0.35f),
                            disabledContentColor = Color(0xFF0F1118).copy(alpha = 0.5f)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF0F1118),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isSignUpMode) "CREATING ACCOUNT..." else "AUTHENTICATING...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (isSignUpMode) "Create Account" else "Log In",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Pre-fill Demo Shortcut for Hackathon Judges
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                identifier = "murali.student@iqoo.edu"
                                password = "proofloop2026"
                                isSignUpMode = false
                                errorMessage = null
                                performLogin()
                            }
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⚡ Hackathon Judge: One-Tap Sign In with Demo Credentials",
                            color = ProofLoopGold.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Reviewer Bypass Login (Direct Hackathon Evaluation Access)
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(23.dp))
                            .background(Color(0xFF162032))
                            .border(1.dp, ProofLoopGold.copy(alpha = 0.8f), RoundedCornerShape(23.dp))
                            .clickable {
                                coroutineScope.launch {
                                    sessionManager.saveSession(
                                        token = "reviewer_token_proofloop_2026",
                                        user = com.example.proofloop.domain.models.UserSession(
                                            userId = "reviewer_demo",
                                            email = "reviewer@proofloop.demo",
                                            name = "Hackathon Reviewer",
                                            isDemoUser = true
                                        ),
                                        rememberMe = true,
                                        identifier = "reviewer@proofloop.demo"
                                    )
                                    onLoginSuccess()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Continue as Reviewer",
                            color = ProofLoopGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // "or" Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFF1E2638))
                        )
                        Text(
                            text = "  or  ",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFF1E2638))
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Social Auth: Continue with Google
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(23.dp))
                            .background(Color(0xFF141824))
                            .border(1.dp, Color(0xFF263044), RoundedCornerShape(23.dp))
                            .clickable {
                                Toast.makeText(
                                    context,
                                    "Google Sign-In requires active OAuth credentials. Please use your Student Email/ID.",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Google "G" representation
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                color = Color(0xFF4285F4),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with Google",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Social Auth: Continue with Apple
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(23.dp))
                            .background(Color(0xFF141824))
                            .border(1.dp, Color(0xFF263044), RoundedCornerShape(23.dp))
                            .clickable {
                                Toast.makeText(
                                    context,
                                    "Apple Sign-In is configured for iOS. Please use your Student Email/ID on Android.",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Continue with Apple",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // BOTTOM: Sign In / Sign Up Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isSignUpMode = !isSignUpMode
                                errorMessage = null
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSignUpMode) "Already have an account? " else "Don't have an account? ",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isSignUpMode) "Sign In" else "Sign Up",
                            color = ProofLoopGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // DECORATIVE BOTTOM GOLDEN SWOOSH (From Design Reference)
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
            ) {
                val width = size.width
                val height = size.height
                val path = Path().apply {
                    moveTo(0f, height)
                    cubicTo(
                        width * 0.25f, height * 0.4f,
                        width * 0.75f, height * 0.1f,
                        width, height
                    )
                    close()
                }
                drawPath(
                    path = path,
                    color = ProofLoopGold
                )
            }
        }

        // FORGOT PASSWORD MODAL DIALOG
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = {
                    if (!isResettingPassword) showForgotPasswordDialog = false
                },
                containerColor = CardBgDark,
                shape = RoundedCornerShape(20.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reset Password",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { showForgotPasswordDialog = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextMuted
                            )
                        }
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Enter your registered student email address. We'll send instructions and a verification code.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = {
                                resetEmail = it
                                resetError = null
                            },
                            placeholder = { Text("Student Email Address", color = TextMuted, fontSize = 13.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ProofLoopGold,
                                unfocusedBorderColor = InputBorderDark,
                                focusedContainerColor = InputBgDark,
                                unfocusedContainerColor = InputBgDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        resetError?.let { err ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = err,
                                color = CoralAlert,
                                fontSize = 12.sp
                            )
                        }

                        resetMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = ProofLoopGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = msg,
                                    color = ProofLoopGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (resetEmail.isBlank() || !resetEmail.contains("@")) {
                                resetError = "Please enter a valid email address"
                                return@Button
                            }
                            isResettingPassword = true
                            resetError = null
                            resetMessage = null

                            coroutineScope.launch {
                                val res = authRepository.requestPasswordReset(resetEmail.trim())
                                isResettingPassword = false
                                res.onSuccess { msg ->
                                    resetMessage = msg
                                }.onFailure { err ->
                                    resetError = err.message ?: "Failed to send reset instructions"
                                }
                            }
                        },
                        enabled = !isResettingPassword,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ProofLoopGold,
                            contentColor = Color(0xFF0F1118)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isResettingPassword) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color(0xFF0F1118),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = "Send Instructions", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showForgotPasswordDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = TextSecondary
                        )
                    ) {
                        Text(text = "Cancel", fontSize = 13.sp)
                    }
                }
            )
        }
    }
}
