package com.kandera.notebook

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private var pendingSaveText: String = ""
    private var pendingSaveName: String = "Kandera-Note.txt"
    private var pendingSaveMime: String = "text/html"
    private var currentUri: Uri? = null

    companion object {
        private const val REQUEST_OPEN = 1001
        private const val REQUEST_SAVE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(20, 35, 59)
        window.navigationBarColor = Color.rgb(20, 35, 59)
        webView = WebView(this)
        webView.setBackgroundColor(Color.rgb(238, 242, 248))
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(NotebookBridge(), "AndroidNotebook")
        setContentView(webView)
        webView.loadUrl("file:///android_asset/index.html")
    }

    inner class NotebookBridge {
        @JavascriptInterface
        fun openFile() {
            runOnUiThread {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("text/plain", "text/html", "text/markdown", "application/json"))
                }
                startActivityForResult(intent, REQUEST_OPEN)
            }
        }

        @JavascriptInterface
        fun saveText(name: String, content: String) {
            runOnUiThread {
                pendingSaveName = if (name.isBlank()) "Kandera-Note.txt" else name
                pendingSaveText = content
                pendingSaveMime = if (pendingSaveName.endsWith(".html", true) || pendingSaveName.endsWith(".htm", true)) "text/html" else "text/plain"
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = pendingSaveMime
                    putExtra(Intent.EXTRA_TITLE, pendingSaveName)
                }
                startActivityForResult(intent, REQUEST_SAVE)
            }
        }

        @JavascriptInterface
        fun printPage() {
            runOnUiThread {
                val printManager = getSystemService(PRINT_SERVICE) as PrintManager
                val adapter = webView.createPrintDocumentAdapter("Kandera Notebook")
                printManager.print("Kandera Notebook", adapter, PrintAttributes.Builder().build())
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data?.data == null) return
        val uri = data.data!!
        when (requestCode) {
            REQUEST_OPEN -> {
                try {
                    contentResolver.openInputStream(uri)?.use { stream ->
                        val text = BufferedReader(InputStreamReader(stream, Charsets.UTF_8)).readText()
                        val name = queryDisplayName(uri) ?: "Opened.txt"
                        val escapedText = org.json.JSONObject.quote(text)
                        val escapedName = org.json.JSONObject.quote(name)
                        webView.evaluateJavascript("window.onNativeFileOpened($escapedText, $escapedName)", null)
                        currentUri = uri
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "फ़ाइल नहीं खुल सकी", Toast.LENGTH_LONG).show()
                }
            }
            REQUEST_SAVE -> {
                try {
                    contentResolver.openOutputStream(uri, "wt")?.use { it.write(pendingSaveText.toByteArray(Charsets.UTF_8)) }
                    currentUri = uri
                    Toast.makeText(this, "Kandera Notebook: फ़ाइल सेव हो गई", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "फ़ाइल सेव नहीं हो सकी", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun queryDisplayName(uri: Uri): String? {
        return try {
            contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
                if (c.moveToFirst()) c.getString(0) else null
            }
        } catch (_: Exception) { null }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}
