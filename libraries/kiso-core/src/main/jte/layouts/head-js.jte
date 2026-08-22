@import java.time.LocalDate
@import java.time.format.DateTimeFormatter

@param com.oakinvest.kiso.core.model.html.util.PageMetadata pageMetadata

!{var build = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);}

<!-- JS libraries ================================================================================================== -->
<script src="${pageMetadata.assetBasePath()}assets/js/browser.js?build=${build}"></script>
<script src="${pageMetadata.assetBasePath()}assets/js/i18next.js?build=${build}"></script>
<script src="${pageMetadata.assetBasePath()}assets/js/minisearch.js?build=${build}"></script>

<!-- JS Kiso code ================================================================================================== -->
<script src="${pageMetadata.assetBasePath()}assets/js/kiso-i18n.js?build=${build}"
        data-i18n-base-url="${pageMetadata.assetBasePath()}assets/i18n/"
        data-i18n-languages="en,fr,de,es,it,pt,nl,pl,ru,zh,ja,ko,ar,hi"
        defer></script>
<script src="${pageMetadata.assetBasePath()}assets/js/kiso-search.js?build=${build}"
        defer></script>
<script src="${pageMetadata.assetBasePath()}assets/js/kiso-back-to-top.js?build=${build}"
        defer></script>
