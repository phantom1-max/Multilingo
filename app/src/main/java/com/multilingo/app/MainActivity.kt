package com.multilingo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF172033)
private val Blue = Color(0xFF4263EB)
private val Lime = Color(0xFFC8F169)
private val Canvas = Color(0xFFF6F7FB)
private val Muted = Color(0xFF788196)

data class Course(val name: String, val level: String, val symbol: String, val tint: Color, val progress: Int)
data class Lesson(val title: String, val prompt: String, val code: String, val choices: List<String>, val answer: Int, val explanation: String)

private val courses = listOf(
    Course("Python", "Beginner · 24 lessons", "Py", Color(0xFFE9B44C), 18),
    Course("Java", "Beginner · 30 lessons", "Ja", Color(0xFFE87554), 0),
    Course("JavaScript", "Web · 26 lessons", "JS", Color(0xFFF2D35E), 0),
    Course("HTML & CSS", "Web design · 20 lessons", "</>", Color(0xFF65B8D5), 0),
    Course("SQL", "Databases · 18 lessons", "DB", Color(0xFF9B8AE8), 0),
    Course("C++", "Programming · 28 lessons", "C+", Color(0xFF78B7A2), 0)
)

private val pythonLessons = listOf(
    Lesson("Your first output", "Which Python command prints Hello, world?", "___('Hello, world!')", listOf("echo", "print", "say", "write"), 1, "print() displays text in the output. It is one of the first tools you'll use in Python."),
    Lesson("Store a value", "Choose the correct way to store 7 in a variable named score.", "score ___ 7", listOf("==", ":", "=","->"), 2, "A single equals sign assigns a value. Double equals compares two values."),
    Lesson("Make a decision", "Which keyword starts a conditional statement?", "___ score > 5:", listOf("when", "if", "check", "for"), 1, "if runs a block of code only when its condition is true.")
)

private fun lessonsFor(course: String): List<Lesson> = when (course) {
    "Java" -> listOf(
        Lesson("Print a message", "Which Java statement prints text?", "___ .out.println(\"Hello\");", listOf("System", "Console", "print", "echo"), 0, "System.out.println() writes a line to the console."),
        Lesson("Declare a variable", "Which type stores whole numbers?", "___ age = 14;", listOf("String", "int", "boolean", "double"), 1, "int stores whole-number values."),
        Lesson("Start a condition", "Which keyword begins a conditional?", "___ (age >= 13) {", listOf("when", "if", "for", "case"), 1, "if checks whether a condition is true.")
    )
    "JavaScript" -> listOf(
        Lesson("Log a message", "Which method writes a message to the console?", "console.___(\"Hello\");", listOf("print", "log", "write", "echo"), 1, "console.log() is commonly used to display debugging output."),
        Lesson("Declare a value", "Which keyword declares a block-scoped variable?", "___ score = 10;", listOf("let", "echo", "dim", "varies"), 0, "let declares a block-scoped variable that can be reassigned."),
        Lesson("Compare values", "Which operator checks strict equality?", "5 ___ \"5\"", listOf("=", "===", "=>", "!="), 1, "=== compares values without converting their types.")
    )
    "HTML & CSS" -> listOf(
        Lesson("Create a heading", "Which HTML tag creates the main heading?", "<___>Hello</h1>", listOf("p", "h1", "head", "title"), 1, "h1 represents the top-level heading in a page."),
        Lesson("Add a paragraph", "Which tag marks a paragraph?", "<___>Hello</p>", listOf("div", "span", "p", "br"), 2, "The p element represents a paragraph."),
        Lesson("Change text colour", "Which CSS property changes text colour?", "___: blue;", listOf("font", "color", "background", "text"), 1, "The color property sets the foreground colour of text.")
    )
    "SQL" -> listOf(
        Lesson("Read table data", "Which SQL keyword retrieves rows?", "___ * FROM students;", listOf("GET", "SELECT", "READ", "OPEN"), 1, "SELECT retrieves data from one or more tables."),
        Lesson("Filter rows", "Which keyword adds a condition?", "SELECT * FROM users ___ age > 12;", listOf("WHERE", "ORDER", "GROUP", "LIMIT BY"), 0, "WHERE filters rows using a condition."),
        Lesson("Sort results", "Which clause sorts query results?", "SELECT name FROM users ___ name;", listOf("SORT", "ORDER BY", "ARRANGE", "FILTER"), 1, "ORDER BY sorts rows by one or more columns.")
    )
    "C++" -> listOf(
        Lesson("Print text", "Which stream prints to the console?", "std::___ << \"Hello\";", listOf("cin", "cout", "input", "print"), 1, "std::cout is the standard output stream."),
        Lesson("Declare an integer", "Which type stores whole numbers?", "___ score = 10;", listOf("string", "int", "bool", "char[]"), 1, "int stores whole-number values."),
        Lesson("Include a library", "Which directive includes a header?", "___ <iostream>", listOf("import", "#include", "using", "package"), 1, "#include makes declarations from a header available.")
    )
    else -> pythonLessons
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("multilingo", MODE_PRIVATE)
        setContent {
            var screen by rememberSaveable { mutableStateOf(if (prefs.getBoolean("welcome_done", false)) "home" else "welcome") }
            var selectedCourse by rememberSaveable { mutableStateOf(prefs.getString("course", "Python") ?: "Python") }
            var lessonIndex by rememberSaveable { mutableIntStateOf(0) }
            var xp by rememberSaveable { mutableIntStateOf(prefs.getInt("xp", 0)) }
            var streak by rememberSaveable { mutableIntStateOf(prefs.getInt("streak", 1)) }
            var selectedAnswer by rememberSaveable { mutableIntStateOf(-1) }
            var checked by rememberSaveable { mutableStateOf(false) }
            var dark by rememberSaveable { mutableStateOf(prefs.getBoolean("dark", false)) }
            var rewardedLessons by remember { mutableStateOf(emptySet<String>()) }
            MaterialTheme(colorScheme = if (dark) darkColorScheme(primary = Color(0xFF9AAEFF), background = Color(0xFF111827), surface = Color(0xFF1F2937), surfaceVariant = Color(0xFF263349), onSurface = Color(0xFFF3F4F6), onSurfaceVariant = Color(0xFFB8C1D1)) else lightColorScheme(primary = Blue, background = Canvas, surface = Color.White, surfaceVariant = Color(0xFFF0F2F8), onSurface = Ink, onSurfaceVariant = Muted)) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when (screen) {
                        "welcome" -> WelcomeScreen(
                            onContinue = { prefs.edit().putBoolean("welcome_done", true).apply(); screen = "home" },
                            onSignIn = { prefs.edit().putBoolean("welcome_done", true).apply(); screen = "home" }
                        )
                        "home" -> HomeScreen(xp, streak, selectedCourse, onStart = { lessonIndex = 0; screen = "lesson" }, onCourses = { screen = "courses" }, onProfile = { screen = "profile" })
                        "courses" -> CoursesScreen(onBack = { screen = "home" }, onSelect = { selectedCourse = it; prefs.edit().putString("course", it).apply(); lessonIndex = 0; screen = "lesson" })
                        "lesson" -> {
                            val lessons = lessonsFor(selectedCourse)
                            val lesson = lessons[lessonIndex % lessons.size]
                            LessonScreen(selectedCourse, lesson, selectedAnswer, checked,
                                onSelect = { selectedAnswer = it; checked = false },
                                onCheck = { checked = true; val rewardKey = "$selectedCourse-$lessonIndex"; if (selectedAnswer == lesson.answer && rewardKey !in rewardedLessons) { rewardedLessons = rewardedLessons + rewardKey; xp += 10; prefs.edit().putInt("xp", xp).apply() } },
                                onContinue = { lessonIndex++; selectedAnswer = -1; checked = false; if (lessonIndex % lessons.size == 0) screen = "home" },
                                onExit = { screen = "home" })
                        }
                        else -> ProfileScreen(xp, streak, dark, onDark = { dark = it; prefs.edit().putBoolean("dark", it).apply() }, onBack = { screen = "home" })
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(onContinue: () -> Unit, onSignIn: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        Spacer(Modifier.height(12.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ByteMascot()
            Text("multilingo", fontSize = 34.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            Text("A little code every day.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
            Spacer(Modifier.height(28.dp))
            Text("Learn by doing.", fontSize = 27.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("Tiny lessons. Real skills. Your next big idea.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) { Text("Start learning") }
            OutlinedButton(onClick = onSignIn, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) { Text("Continue with Google") }
            OutlinedButton(onClick = onSignIn, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) { Text("Continue with email") }
            TextButton(onClick = onSignIn, modifier = Modifier.fillMaxWidth()) { Text("Continue with GitHub") }
            Text("Sign-in buttons are UI placeholders in this first build.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ByteMascot() {
    Box(
        Modifier.size(172.dp).background(Color(0xFF111A48), RoundedCornerShape(34.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(
                Modifier.size(width = 116.dp, height = 94.dp)
                    .background(Color(0xFF762BEA), RoundedCornerShape(34.dp))
                    .padding(9.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier.fillMaxSize().background(Color(0xFF080D25), RoundedCornerShape(25.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⌒   ⌒", color = Color(0xFF22E6FF), fontSize = 25.sp, fontWeight = FontWeight.Black, lineHeight = 26.sp)
                        Text("  ﹀  ", color = Color(0xFF22E6FF), fontSize = 24.sp, fontWeight = FontWeight.Black, lineHeight = 22.sp)
                    }
                }
            }
            Spacer(Modifier.height(5.dp))
            Box(
                Modifier.width(104.dp).height(43.dp).background(Color(0xFF762BEA), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("M", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Black)
            }
            Text("BYTE", color = Color(0xFF22E6FF), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        }
        Text("✦", color = Color(0xFFFFD45E), fontSize = 22.sp, modifier = Modifier.align(Alignment.TopEnd).padding(13.dp))
        Text("✦", color = Color(0xFF22E6FF), fontSize = 15.sp, modifier = Modifier.align(Alignment.BottomStart).padding(15.dp))
    }
}

@Composable
fun HomeScreen(xp: Int, streak: Int, selectedCourse: String, onStart: () -> Unit, onCourses: () -> Unit, onProfile: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 18.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column { Text("Hey, coder 👋", color = MaterialTheme.colorScheme.onSurfaceVariant); Text("Ready for a tiny win?", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) }
            Text("🔥 $streak", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatPill("⚡", "$xp XP"); StatPill("🎯", "Daily goal"); StatPill("💎", "0 gems")
        }
        Column(Modifier.weight(1f).padding(20.dp)) {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF202D4D))) {
                Column(Modifier.fillMaxWidth().padding(22.dp)) {
                    Text("YOUR LEARNING PATH", color = Lime, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                    Spacer(Modifier.height(10.dp))
                    Text("$selectedCourse fundamentals", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    Text("Pick up where you left off and keep building your skills.", color = Color(0xFFD6DCEC))
                    Spacer(Modifier.height(18.dp))
                    Button(onClick = onStart, colors = ButtonDefaults.buttonColors(containerColor = Lime, contentColor = Ink), modifier = Modifier.fillMaxWidth()) { Text("Continue lesson  →", fontWeight = FontWeight.Bold) }
                }
            }
            Spacer(Modifier.height(22.dp))
            Text("Today's mission", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 19.sp)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(18.dp)).padding(16.dp)) {
                Text("🧩", fontSize = 28.sp); Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) { Text("Complete one lesson", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold); Text("A small step counts.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) }
                Text("0/1", color = Blue, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(22.dp))
            Text("Explore languages", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 19.sp)
            Spacer(Modifier.height(10.dp))
            courses.take(3).forEach { CourseRow(it, onCourses) }
        }
        BottomBar("Home", onHome = {}, onCourses = onCourses, onProfile = onProfile)
    }
}

@Composable
fun StatPill(icon: String, label: String) {
    Row(Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape).padding(horizontal = 13.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) { Text(icon); Spacer(Modifier.width(5.dp)); Text(label, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
}

@Composable
fun CourseRow(course: Course, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(17.dp)).clickable { onClick() }.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(44.dp).background(course.tint, RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) { Text(course.symbol, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Black) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) { Text(course.name, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold); Text(course.level, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) }
        Icon(Icons.Default.ChevronRight, contentDescription = "Open course", tint = Muted)
    }
}

@Composable
fun CoursesScreen(onBack: () -> Unit, onSelect: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }; Text("Choose a language", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) }
        Text("Pick a path. You can learn more than one.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(14.dp))
        LazyColumn { items(courses) { course -> CourseRow(course) { onSelect(course.name) } } }
    }
}

@Composable
fun LessonScreen(course: String, lesson: Lesson, selected: Int, checked: Boolean, onSelect: (Int) -> Unit, onCheck: () -> Unit, onContinue: () -> Unit, onExit: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onExit) { Icon(Icons.Default.Close, contentDescription = "Exit lesson") }
            LinearProgressIndicator(progress = { 0.45f }, modifier = Modifier.weight(1f).height(8.dp), color = Blue, trackColor = Color(0xFFE2E6EF))
            Text("  3/8", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
        Spacer(Modifier.height(24.dp))
        Text(course.uppercase() + " · QUICK LESSON", color = Blue, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.2.sp)
        Text(lesson.title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(12.dp))
        Text(lesson.prompt, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(20.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF202D4D)), shape = RoundedCornerShape(18.dp)) { Text(lesson.code, modifier = Modifier.fillMaxWidth().padding(22.dp), color = Lime, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(18.dp))
        Text("CHOOSE YOUR ANSWER", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Spacer(Modifier.height(10.dp))
        lesson.choices.forEachIndexed { index, choice ->
            val active = selected == index
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp)).clickable { onSelect(index) }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("${'A' + index}", color = Blue, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(14.dp)); Text(choice, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                if (active) Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = Blue)
            }
        }
        if (checked) {
            Spacer(Modifier.height(12.dp))
            Text(if (selected == lesson.answer) "Correct! +10 XP 🎉" else "Not quite — try again.", color = if (selected == lesson.answer) Color(0xFF26845B) else Color(0xFFB65C43), fontWeight = FontWeight.Bold)
            Text(lesson.explanation, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = if (checked && selected == lesson.answer) onContinue else onCheck, enabled = selected >= 0, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(16.dp)) { Text(if (checked && selected == lesson.answer) "Continue" else "Check answer") }
    }
}

@Composable
fun BottomBar(active: String, onHome: () -> Unit, onCourses: () -> Unit, onProfile: () -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(selected = active == "Home", onClick = onHome, icon = { Icon(Icons.Default.Home, contentDescription = "Home") }, label = { Text("Home") })
        NavigationBarItem(selected = false, onClick = onCourses, icon = { Icon(Icons.Default.Code, contentDescription = "Courses") }, label = { Text("Learn") })
        NavigationBarItem(selected = false, onClick = onProfile, icon = { Icon(Icons.Default.Person, contentDescription = "Profile") }, label = { Text("Profile") })
    }
}

@Composable
fun ProfileScreen(xp: Int, streak: Int, dark: Boolean, onDark: (Boolean) -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(22.dp)) {
        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
        ByteMascot()
        Spacer(Modifier.height(12.dp))
        Text("Your progress", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text("$xp XP earned · $streak day streak", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Dark theme", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
            Switch(checked = dark, onCheckedChange = onDark)
        }
        Spacer(Modifier.height(12.dp))
        Text("Account providers (connect in a later release): Google · Email · GitHub", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
    }
}
