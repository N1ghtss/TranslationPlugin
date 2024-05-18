package cn.yiiguxing.plugin.translate.trans.microsoft

import cn.yiiguxing.plugin.translate.trans.BaseLanguageAdapter
import cn.yiiguxing.plugin.translate.trans.Lang

/**
 * Language adapter for Microsoft Translator.
 */
internal object MicrosoftLanguageAdapter : BaseLanguageAdapter() {

    private val UNSUPPORTED_LANGUAGES: Set<Lang> = setOf(
        Lang.BELARUSIAN,
        Lang.CEBUANO,
        Lang.CHICHEWA,
        Lang.CORSICAN,
        Lang.ENGLISH_AMERICAN,
        Lang.ENGLISH_BRITISH,
        Lang.ESPERANTO,
        Lang.FRISIAN,
        Lang.HAUSA,
        Lang.HAWAIIAN,
        Lang.IGBO,
        Lang.JAVANESE,
        Lang.KINYARWANDA,
        Lang.KURDISH, // 己俱体分为 `库尔德语（库尔曼吉语）` 和 `库尔德语（索拉尼）`
        Lang.KYRGYZ,
        Lang.LATIN,
        Lang.LUXEMBOURGISH,
        Lang.MALAGASY,
        Lang.PORTUGUESE,
        Lang.SCOTS_GAELIC,
        Lang.SERBIAN,
        Lang.SESOTHO,
        Lang.SHONA,
        Lang.SINDHI,
        Lang.SINHALA,
        Lang.SUNDANESE,
        Lang.TAJIK,
        Lang.XHOSA,
        Lang.YIDDISH,
    )

    @Suppress("SpellCheckingInspection")
    override fun getAdaptedLanguages(): Map<String, Lang> = mapOf(
        "yue" to Lang.CHINESE_CANTONESE,
        "lzh" to Lang.CHINESE_CLASSICAL,
        "zh-Hans" to Lang.CHINESE,
        "zh-Hant" to Lang.CHINESE_TRADITIONAL,
        "fil" to Lang.FILIPINO,
        "he" to Lang.HEBREW,
        "mww" to Lang.HMONG,
        "mn-Mong" to Lang.MONGOLIAN,
        "nb" to Lang.NORWEGIAN,
        "kmr" to Lang.KURDISH_KURMANJI,
        "ku" to Lang.KURDISH_SORANI,
        "pt" to Lang.PORTUGUESE_BRAZILIAN,
    )

    override val supportedSourceLanguages: List<Lang> = (Lang.sortedLanguages - UNSUPPORTED_LANGUAGES).toList()

    override val supportedTargetLanguages: List<Lang> =
        (Lang.sortedLanguages - UNSUPPORTED_LANGUAGES - Lang.AUTO).toList()
}


/**
 * Language code for Microsoft Translator.
 */
internal val Lang.microsoftLanguageCode: String
    get() = MicrosoftLanguageAdapter.getLanguageCode(this)

/**
 * Returns the [language][Lang] for the specified Microsoft Translator language [code].
 */
internal fun Lang.Companion.fromMicrosoftLanguageCode(code: String): Lang {
    return MicrosoftLanguageAdapter.getLanguage(code)
}