package com.xluis.inventarioefa._domain.util

import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import org.w3c.dom.Element
import java.io.InputStream
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

fun parseArticlesFromSVG(inputStream: InputStream, zoneId: String): List<Article> {
    val articles = mutableListOf<Article>()
    val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
    val doc = docBuilder.parse(inputStream)

    val nodeList = doc.getElementsByTagName("rect")

    for (i in 0 until nodeList.length) {
        val node = nodeList.item(i) as Element
        val name = node.getAttribute("data-name").takeIf { it.isNotBlank() } ?: "Artículo"
        val category = try {
            ArticleCategory.valueOf(node.getAttribute("data-category").uppercase())
        } catch (e: Exception) {
            ArticleCategory.OTHER
        }
        val count = node.getAttribute("data-count").toIntOrNull() ?: 1

        articles.add(
            Article(
                id = UUID.randomUUID().toString(),
                name = name,
                category = category,
                zoneId = zoneId,
                count = count
            )
        )
    }

    return articles
}
