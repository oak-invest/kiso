@import java.time.LocalDate
@import java.time.format.DateTimeFormatter

@param String assetBasePath

!{var build = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);}

<!-- JS Librairies ================================================================================================= -->
<script src="${assetBasePath}assets/js/browser.js?build=${build}"></script>
<script src="${assetBasePath}assets/js/i18next.js?build=${build}"></script>
<script src="${assetBasePath}assets/js/minisearch.js?build=${build}"></script>

<!-- JS Kiso code ================================================================================================== -->
<script src="${assetBasePath}assets/js/kiso-i18n.js?build=${build}"
        data-i18n-base-url="${assetBasePath}assets/i18n/"
        data-i18n-languages="en,fr"
        defer></script>
<script src="${assetBasePath}assets/js/kiso-search.js?build=${build}"
        defer></script>
