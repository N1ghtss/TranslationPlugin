package cn.yiiguxing.plugin.translate.trans.microsoft

import cn.yiiguxing.plugin.translate.message
import cn.yiiguxing.plugin.translate.trans.*
import cn.yiiguxing.plugin.translate.trans.Lang.Companion.isExplicit
import cn.yiiguxing.plugin.translate.trans.Lang.Companion.toExplicit
import cn.yiiguxing.plugin.translate.trans.microsoft.models.TextType
import cn.yiiguxing.plugin.translate.trans.microsoft.models.presentableError
import cn.yiiguxing.plugin.translate.trans.text.NamedTranslationDocument
import cn.yiiguxing.plugin.translate.ui.settings.TranslationEngine.MICROSOFT
import org.jsoup.nodes.Document
import javax.swing.Icon

/**
 * Microsoft translator.
 */
object MicrosoftTranslator : AbstractTranslator(), DocumentationTranslator {

    override val id: String = MICROSOFT.id
    override val name: String = MICROSOFT.translatorName
    override val icon: Icon = MICROSOFT.icon
    override val intervalLimit: Int = MICROSOFT.intervalLimit
    override val contentLengthLimit: Int = MICROSOFT.contentLengthLimit
    override val primaryLanguage: Lang get() = MICROSOFT.primaryLanguage
    override val supportedSourceLanguages: List<Lang> = MicrosoftLanguageAdapter.supportedSourceLanguages
    override val supportedTargetLanguages: List<Lang> = MicrosoftLanguageAdapter.supportedTargetLanguages

    override fun doTranslate(text: String, srcLang: Lang, targetLang: Lang): Translation {
        if (!targetLang.isExplicit()) {
            throw UnsupportedLanguageException(targetLang, "Unsupported target language: ${targetLang.langName}")
        }

        val msTranslation = MicrosoftTranslatorService.translate(text, srcLang, targetLang, TextType.PLAIN)
            ?: return Translation(text, text, srcLang.toExplicit(), targetLang)

        val translation = msTranslation.translations.first()
        val sourceLang = msTranslation.detectedLanguage?.language
            ?.let { Lang.fromMicrosoftLanguageCode(it) }
            ?: srcLang.toExplicit()

        val dictionaryLookup = if (
            sourceLang.isExplicit() &&
            sourceLang != targetLang &&
            MicrosoftTranslatorService.canLookupDictionary(text)
        ) {
            MicrosoftTranslatorService.dictionaryLookup(text, sourceLang, targetLang)
        } else null

        val extraDocuments = dictionaryLookup
            ?.let { MicrosoftTranslatorService.dictionaryExamples(it, sourceLang, targetLang) }
            ?.let { MicrosoftExampleDocumentFactory.getDocument(it) }
            ?.let { listOf(NamedTranslationDocument(message("examples.document.name"), it)) }
            ?: emptyList()

        return Translation(
            text,
            translation.text,
            sourceLang,
            Lang.fromMicrosoftLanguageCode(translation.to),
            dictDocument = dictionaryLookup?.let(MicrosoftDictionaryDocumentFactory::getDocument),
            extraDocuments = extraDocuments
        )
    }

    override fun translateDocumentation(
        documentation: Document,
        srcLang: Lang,
        targetLang: Lang
    ): Document = checkError {
        documentation.translateBody { bodyHTML ->
            translateDocumentation(bodyHTML, srcLang, targetLang)
        }
    }

    private fun translateDocumentation(documentation: String, srcLang: Lang, targetLang: Lang): String {
        return MicrosoftTranslatorService.translate(documentation, srcLang, targetLang, TextType.HTML)
            ?.translations
            ?.firstOrNull()
            ?.text
            ?: documentation
    }

    override fun createErrorInfo(throwable: Throwable): ErrorInfo? {
        // https://learn.microsoft.com/zh-cn/azure/cognitive-services/translator/reference/v3-0-reference#errors
        when (throwable) {
            is MicrosoftAuthenticationException -> return ErrorInfo(throwable.message ?: "Authentication failed")
            is MicrosoftStatusException -> return if (throwable.error?.code == 400050) {
                onError(ContentLengthLimitException())
            } else {
                ErrorInfo(throwable.error?.presentableError ?: throwable.message ?: message("error.unknown"))
            }
        }
        return super.createErrorInfo(throwable)
    }


}