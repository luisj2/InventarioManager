package com.xluis.inventarioefa.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleReturn
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleTaked
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID

fun Context.navigateTo(destinationClass: Class<out Activity>, bundle: Bundle = bundleOf()) {
    val activityContext = this as? Activity

    if (activityContext != null) {
        val intent = Intent(this, destinationClass).apply {
            putExtras(bundle)
        }
        activityContext.startActivity(intent)
    } else {
        val intent = Intent(this, destinationClass).apply {
            putExtras(bundle)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
}


fun String.parseDate () : Date{
    return try {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.parse(this) ?: Date(0)
    } catch (e: Exception) {
        Date(0)
    }
}

fun ArticleTaked.toArticleReturned(): ArticleReturn {
    return ArticleReturn(
        userName = this.userName,
        articleCategory = this.articleCategory,
        articleName = this.articleName,
        date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
        articleId = this.articleId,
        userId = this.userId,
        articlesReturnCount = this.articlesTakedCount
    )
}

fun Array<out SuspendResult<*>>.handleErrors(showMessage: (String) -> Unit) {
    this.forEach { result ->
        if (result is SuspendResult.Error) {
            showMessage(result.message)
        }
    }
}


fun Context.toast(message: String, lenght: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, lenght).show()
}

fun List<MutableState<*>>.cleanFields() {
    forEach { state ->
        when (state.value) {
            is String -> (state as MutableState<String>).value = ""
            is Int -> (state as MutableState<Int>).value = 1
            else -> (state as MutableState<Any?>).value = null
        }
    }
}
fun isValidEmail(email: String?): Boolean {
    if (email.isNullOrBlank()) return false
    val emailRegex = "^[A-Za-z](.*)([@]{1})(.+)(\\.)(.+)"
    return Regex(emailRegex).matches(email)
}
fun isValidPassword(password: String?): Boolean {
    if (password.isNullOrBlank()) return false
    val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
    return Regex(passwordRegex).matches(password)
}


