@param com.oakinvest.kiso.core.model.html.navigation.BundleTree bundleTree
@param com.oakinvest.kiso.core.model.html.page.PageMetadata pageMetadata

<!-- Left tree menu ================================================================================================ -->
<aside class="flex min-h-full w-80 max-w-[85vw] flex-col bg-base-100 p-4 text-base-content">

    <!-- Menu header =============================================================================================== -->
    <div class="mb-6 px-2 pt-2">
        <div class="mt-1 text-lg font-semibold" data-i18n="navigation.bundleContent">Bundle content</div>
    </div>

    <!-- Tree menu ================================================================================================= -->
    <nav class="kiso-navigation-menu flex-1">
        <ul class="menu menu-sm w-full gap-1 p-0">

            <!-- Index page (if present) =========================================================================== -->
            @if(bundleTree.hasIndexPage())
                <li>
                    <a class="@if(bundleTree.indexHtmlPath().equals(pageMetadata.htmlPath()))font-semibold@endif"
                       href="${pageMetadata.assetBasePath()}${bundleTree.indexHtmlPath()}">
                        <span class="kiso-navigation-menu-label">${bundleTree.name()}</span>
                    </a>
                </li>
            @endif

            <!-- All bundles ======================================================================================= -->
            @for(var childBundle : bundleTree.childBundles())
                @template.layouts.side-menu-node(bundleTree = childBundle, pageMetadata = pageMetadata)
            @endfor

            <!-- All pages inside the bundle ======================================================================= -->
            @for(var page : bundleTree.pages())
                @if(page.kind() != com.oakinvest.kiso.core.model.okf.markdown.MarkdownFileKind.INDEX)
                    <li>
                        <a class="@if(page.href().equals(pageMetadata.htmlPath()))font-semibold@endif"
                           href="${pageMetadata.assetBasePath()}${page.href()}">
                            <span class="kiso-navigation-menu-label">${page.title()}</span>
                        </a>
                    </li>
                @endif
            @endfor

        </ul>
    </nav>

    <!-- Menu footer =============================================================================================== -->
    <div class="mt-6 border-t border-base-300 px-2 pt-4 text-xs text-base-content/60">
        <span data-i18n="footer.generatedWith">Generated with</span>
        <a class="link link-hover font-semibold"
           href="https://oak-invest.github.io/kiso"
           target="kiso">
            Kiso
        </a>
    </div>

</aside>
