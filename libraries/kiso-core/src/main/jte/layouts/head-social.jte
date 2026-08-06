@param com.oakinvest.kiso.core.model.html.util.PageContext pageContext
@param com.oakinvest.kiso.core.model.html.util.PageMetadata pageMetadata

<!-- Social preview ================================================================================================ -->
@if(pageMetadata.assetBasePath() != null)
    <meta property="og:locale" content="${pageContext.siteConfiguration().languageTag()}">
    <meta property="og:site_name" content="${pageContext.siteConfiguration().name()}">
    <meta property="og:url" content="${pageMetadata.assetBasePath()}${pageMetadata.htmlPath()}">
    <meta property="og:type" content="website">
    <meta property="og:title" content="${pageMetadata.title()}">
    <meta property="og:description" content="${pageMetadata.description()}">
    <meta property="og:image" content="${pageMetadata.assetBasePath()}${pageMetadata.socialPreviewImagePath()}">
    <meta property="og:image:width" content="1200">
    <meta property="og:image:height" content="630">

    <meta name="twitter:card" content="summary_large_image">
    <meta name="twitter:title" content="${pageMetadata.title()}">
    <meta name="twitter:description" content="${pageMetadata.description()}">
    <meta name="twitter:image" content="${pageMetadata.assetBasePath()}${pageMetadata.socialPreviewImagePath()}">
@endif
