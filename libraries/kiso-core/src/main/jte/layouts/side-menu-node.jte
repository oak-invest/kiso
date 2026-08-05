@param com.oakinvest.kiso.core.model.html.navigation.BundleTree bundleTree
@param com.oakinvest.kiso.core.model.html.page.PageMetadata pageMetadata

<li>
    <details open="${bundleTree.containsHtmlPath(pageMetadata.htmlPath())}">
        <!-- Bundle name =========================================================================================== -->
        <summary class="@if(bundleTree.containsHtmlPath(pageMetadata.htmlPath()))menu-active@endif">
            <span class="kiso-navigation-menu-label">${bundleTree.name()}</span>
        </summary>
        <ul>

            <!-- Index page (if present) =========================================================================== -->
            @if(bundleTree.hasIndexPage())
                <li>
                    <a class="@if(bundleTree.indexHtmlPath().equals(pageMetadata.htmlPath()))font-semibold@endif"
                       href="${pageMetadata.assetBasePath()}${bundleTree.indexHtmlPath()}">
                        <span class="kiso-navigation-menu-label" data-i18n="pageKind.index">index</span>
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
    </details>
</li>
