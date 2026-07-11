@param com.oakinvest.kiso.core.renderer.model.navigation.BundleTree bundleTree
@param String assetBasePath
@param String currentHtmlPath

<li>
    <details open="${bundleTree.containsHtmlPath(currentHtmlPath)}">
        <summary
                class="@if(bundleTree.containsHtmlPath(currentHtmlPath))menu-active@endif"><span class="kiso-navigation-menu-label">${bundleTree.name()}</span></summary>
        <ul>
            @if(bundleTree.hasIndexPage())
                <li>
                    <a class="@if(bundleTree.indexHtmlPath().equals(currentHtmlPath))font-semibold@endif"
                       href="${assetBasePath}${bundleTree.indexHtmlPath()}">
                        <span class="kiso-navigation-menu-label">Index</span>
                    </a>
                </li>
            @endif
            @for(var childBundle : bundleTree.childBundles())
                @if(childBundle != null)
                    @template.partials.bundleTreeMenuNode(bundleTree = childBundle, assetBasePath = assetBasePath, currentHtmlPath = currentHtmlPath)
                @endif
            @endfor
            @for(var page : bundleTree.pages())
                @if(page.kind() != com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.INDEX)
                    <li>
                        <a class="@if(page.href().equals(currentHtmlPath))font-semibold@endif"
                           href="${assetBasePath}${page.href()}">
                            <span class="kiso-navigation-menu-label">${page.title()}</span>
                        </a>
                    </li>
                @endif
            @endfor
        </ul>
    </details>
</li>
