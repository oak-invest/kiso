@param String dataTheme
@param String language
@param String siteName
@param String title
@param String description
@param String markdownFileName
@param String assetBasePath
@param com.oakinvest.kiso.core.renderer.model.navigation.BundleTree bundleTree
@param String currentHtmlPath
@param gg.jte.Content content

<!DOCTYPE html>
<html data-theme="${dataTheme}" lang="${language}">

<head>

    <!-- Meta information ========================================================================================== -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    @if(description != null)
        <meta name="description" content="${description}">
    @endif

    <!-- CSS ======================================================================================================= -->
    <link href="${assetBasePath}assets/css/daisyui.css" rel="stylesheet" type="text/css"/>
    <link href="${assetBasePath}assets/css/themes.css" rel="stylesheet" type="text/css"/>
    <link href="${assetBasePath}assets/css/application.css" rel="stylesheet" type="text/css"/>

    <!-- JS ======================================================================================================== -->
    <script src="${assetBasePath}assets/js/browser.js"></script>

    <!-- Favicons ================================================================================================== -->
    <link href="${assetBasePath}assets/favicon/kiso_favicon_light_32x32.svg" rel="icon" type="image/svg+xml"/>
    <link href="${assetBasePath}assets/favicon/kiso_favicon_light_32x32.svg" rel="icon" type="image/svg+xml"/>
    <link href="${assetBasePath}assets/favicon/kiso_favicon_light_32x32.svg" media="(prefers-color-scheme: light)"
          rel="icon"
          sizes="32x32" type="image/svg+xml">
    <link href="${assetBasePath}assets/favicon/kiso_favicon_light_16x16.svg" media="(prefers-color-scheme: light)"
          rel="icon"
          sizes="16x16" type="image/svg+xml">
    <link href="${assetBasePath}assets/favicon/kiso_favicon_light_180x180.svg" rel="apple-touch-icon" sizes="180x180">

    <link href="${assetBasePath}assets/favicon/kiso_favicon_dark_32x32.svg" media="(prefers-color-scheme: dark)"
          rel="icon"
          sizes="32x32" type="image/svg+xml">
    <link href="${assetBasePath}assets/favicon/kiso_favicon_dark_16x16.svg" media="(prefers-color-scheme: dark)"
          rel="icon"
          sizes="16x16" type="image/svg+xml">
    <link href="${assetBasePath}assets/favicon/kiso_favicon_dark_180x180.svg" rel="apple-touch-icon" sizes="180x180">

    <!-- Social preview ============================================================================================ -->
    @if(assetBasePath != null)
        <meta property="og:locale" content="${language}">
        <meta property="og:site_name" content="${siteName}">
        <meta property="og:url" content="${assetBasePath}${currentHtmlPath}">
        <meta property="og:type" content="website">
        <meta property="og:title" content="${title}">
        <meta property="og:description" content="${description}">
        <meta property="og:image" content="${assetBasePath}${currentHtmlPath}">
        <meta property="og:image:width" content="1200">
        <meta property="og:image:height" content="630">

        <meta name="twitter:card" content="summary_large_image">
        <meta name="twitter:title" content="${title}">
        <meta name="twitter:description" content="${description}">
        <meta name="twitter:image" content="${assetBasePath}${currentHtmlPath}">
    @endif

    <!-- AI Specific =============================================================================================== -->
    @if(markdownFileName != null)
        <link rel="alternate" type="text/plain" href="${markdownFileName}">
    @endif

</head>

<body>
<!--
Create a container that covers at least the full browser height, uses the secondary page background color from the
current DaisyUI theme, and displays text using the default readable text color for that background.
    min-h-screen - Set the minimum height of the element to the full height of the viewport
    text-base-content - Apply DaisyUI’s default text color for base surfaces
-->
<div class="drawer min-h-screen bg-base-200 text-base-content">
    <input id="kiso-navigation-drawer" type="checkbox" class="drawer-toggle"/>

    <div class="drawer-content min-h-screen">
        <div class="fixed left-3 top-3 z-40 flex gap-2 sm:left-4 sm:top-4">
            <a href="${assetBasePath}index.html"
               aria-label="Home"
               class="btn btn-square btn-ghost bg-base-100/90 shadow-sm backdrop-blur">
                <span class="kiso-home-icon" aria-hidden="true"></span>
            </a>
            <label for="kiso-navigation-drawer"
                   aria-label="Open navigation"
                   class="btn btn-square btn-ghost bg-base-100/90 shadow-sm backdrop-blur">
                <span class="kiso-hamburger" aria-hidden="true"></span>
            </label>
        </div>

        <!--
            Create a centered main container, limit its width to about 1152px, add horizontal padding so content never
            touches screen edges, and increase spacing on larger screens for better readability.
                container - Create a centered layout wrapper
                mx-auto - Center a block element horizontally
                max-w-6xl - Limit the maximum width of the container to 6xl (1152px)
        -->
        <main class="container mx-auto max-w-6xl px-4 py-8 sm:px-6 lg:py-12">

            ${content}

        </main>
    </div>

    <div class="drawer-side z-50">
        <label for="kiso-navigation-drawer" aria-label="Close navigation" class="drawer-overlay"></label>
        <aside class="flex min-h-full w-80 max-w-[85vw] flex-col bg-base-100 p-4 text-base-content">
            <div class="mb-6 px-2 pt-2">
                <div class="mt-1 text-lg font-semibold">Bundle content</div>
            </div>
            <nav class="kiso-navigation-menu flex-1">
                @template.partials.bundleTreeMenu(bundleTree = bundleTree, assetBasePath = assetBasePath, currentHtmlPath = currentHtmlPath)
            </nav>
            <div class="mt-6 border-t border-base-300 px-2 pt-4 text-xs text-base-content/60">
                Generated with
                <a class="link link-hover font-semibold"
                   href="https://oak-invest.github.io/kiso"
                   target="kiso">
                    Kiso
                </a>
            </div>
        </aside>
    </div>
</div>

</body>

</html>