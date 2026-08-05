<%-- Page parameters ============================================================================================== --%>
@param com.oakinvest.kiso.core.configuration.SiteConfiguration siteConfiguration
@param com.oakinvest.kiso.core.configuration.ThemeConfiguration themeConfiguration
@param com.oakinvest.kiso.core.model.html.page.PageMetadata pageMetadata
@param com.oakinvest.kiso.core.model.html.navigation.BundleTree bundleTree
@param gg.jte.Content content

<!DOCTYPE html>
<html data-theme="${themeConfiguration.effectiveName()}" lang="${siteConfiguration.languageTag()}">

<head>

    <!-- Meta information ========================================================================================== -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageMetadata.title()}</title>
    <meta name="description" content="${pageMetadata.description()}">

    <!-- CSS, JS, favicons and social ============================================================================== -->
    @template.layouts.head-css(assetBasePath = pageMetadata.assetBasePath())
    @template.layouts.head-js(assetBasePath = pageMetadata.assetBasePath())
    @template.layouts.head-favicons(assetBasePath = pageMetadata.assetBasePath())
    @template.layouts.head-social(pageMetadata = pageMetadata, siteConfiguration = siteConfiguration)

    <!-- AI content ================================================================================================ -->
    <link rel="alternate" type="text/markdown" title="Markdown source" href="${pageMetadata.markdownFilename()}">

</head>

<body>

<!--
Create a container that covers at least the full browser height, uses the secondary page background color from the
current DaisyUI theme, and displays text using the default readable text color for that background.
    min-h-screen - Set the minimum height of the element to the full height of the viewport
-->
<div class="drawer min-h-screen bg-base-200 text-base-content">

    <!-- Drawer status ============================================================================================= -->
    <input id="kiso-navigation-drawer" type="checkbox" class="drawer-toggle"/>

    <!-- Drawer content ============================================================================================ -->
    <div class="drawer-content min-h-screen">

        <!-- Navigation bar ======================================================================================== -->
        @template.layouts.navigation-bar(siteConfiguration = siteConfiguration, pageMetadata = pageMetadata, bundleTree = bundleTree)

        <!-- Content =============================================================================================== -->
        <!--
            Create a centered main container, limit its width to about 1152px, add horizontal padding so content never
            touches screen edges, and increase spacing on larger screens for better readability.
                container - Create a centered layout wrapper
                mx-auto - Center a block element horizontally
                max-w-6xl - Limit the maximum width of the container
        -->
        <main class="container mx-auto max-w-6xl px-4 py-8 sm:px-6 lg:py-12">
            ${content}
        </main>

        <!-- Back to top =========================================================================================== -->
        <button type="button"
                class="kiso-back-to-top btn btn-primary btn-square fixed bottom-4 right-4 z-50 hidden shadow-lg"
                aria-label="Back to top"
                data-i18n-aria-label="navigation.backToTop">
            @template.components.icons.arrowUp()
        </button>

    </div>

    <!-- Drawer side menu ===========================================================================================-->
    <div class="drawer-side z-80">

        <!-- This is used to close the drawer - It's invisible to the user ========================================= -->
        <label for="kiso-navigation-drawer"
               aria-label="Close navigation"
               data-i18n-aria-label="navigation.closeNavigation"
               class="drawer-overlay"></label>

        <!-- Tree menu ============================================================================================= -->
        @template.layouts.side-menu(bundleTree = bundleTree, pageMetadata = pageMetadata)

    </div>

</div>

</body>

</html>
