package com.bigdeal.podcast.core.opml

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber
import java.io.IOException
import java.io.InputStream

// We don't use namespaces.
private val ns: String? = null

/**
 * import podcasts from opml xml
 */
class OpmlImporter {
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<FeedEntry> {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readOpml(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readOpml(parser: XmlPullParser): List<FeedEntry> {
        val entries = mutableListOf<FeedEntry>()
        parser.require(XmlPullParser.START_TAG, ns, "opml")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag.
            Timber.d("parser.name = ${parser.name}")
            if (parser.name == "body") {
                continue
            }
            if (parser.name == "outline") {
                Timber.d("parser.name is outline")
                val text = parser.getAttributeValue(null, "text")
                if (text == "feeds") {
                    Timber.d("readEntry")
                    return readEntry(parser)
                } else {
                    Timber.d("outline tag without feeds attr")
                }
            } else {
                skip(parser)
            }
        }
        return entries
    }

    data class Entry(val title: String?, val summary: String?, val link: String?)

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser): List<FeedEntry> {
        parser.require(XmlPullParser.START_TAG, ns, "outline")
        val outlines: MutableList<FeedEntry> = mutableListOf()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "outline") {
               outlines.add(readOutline(parser))
            } else {
                skip(parser)
            }
        }
        return outlines
    }
}

// Processes link tags in the feed.
@Throws(IOException::class, XmlPullParserException::class)
private fun readOutline(parser: XmlPullParser): FeedEntry {
    Timber.d("readOutline")
    parser.require(XmlPullParser.START_TAG, ns, "outline")
    val url = parser.getAttributeValue(null, "xmlUrl")
    val title = parser.getAttributeValue(null, "text")
    val type = parser.getAttributeValue(null, "type")
    parser.nextTag()
    parser.require(XmlPullParser.END_TAG, ns, "outline")
    return FeedEntry(url, title, type)
}

// Processes summary tags in the feed.
@Throws(IOException::class, XmlPullParserException::class)
private fun readSummary(parser: XmlPullParser): String {
    parser.require(XmlPullParser.START_TAG, ns, "summary")
    val summary = readText(parser)
    parser.require(XmlPullParser.END_TAG, ns, "summary")
    return summary
}

// For the tags title and summary, extracts their text values.
@Throws(IOException::class, XmlPullParserException::class)
private fun readText(parser: XmlPullParser): String {
    var result = ""
    if (parser.next() == XmlPullParser.TEXT) {
        result = parser.text
        parser.nextTag()
    }
    return result
}

@Throws(XmlPullParserException::class, IOException::class)
private fun skip(parser: XmlPullParser) {
    if (parser.eventType != XmlPullParser.START_TAG) {
        throw IllegalStateException()
    }
    var depth = 1
    while (depth != 0) {
        when (parser.next()) {
            XmlPullParser.END_TAG -> depth--
            XmlPullParser.START_TAG -> depth++
        }
    }
}