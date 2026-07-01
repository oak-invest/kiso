@param com.oakinvest.kiso.core.renderer.model.navigation.BundleTree bundleTree
@param String assetBasePath
@param String currentHtmlPath

<li>
    <details open="${bundleTree.containsHtmlPath(currentHtmlPath)}">
        <summary
                class="@if(bundleTree.containsHtmlPath(currentHtmlPath))menu-active@endif">${bundleTree.name()}</summary>
        <ul>
            <li>
                <a class="@if(bundleTree.indexHtmlPath().equals(currentHtmlPath))font-semibold@endif"
                   href="${assetBasePath}${bundleTree.indexHtmlPath()}">
                    Index
                </a>
            </li>
            @for(var childBundle : bundleTree.childBundles())
                @template.partials.bundleTreeMenuNode(bundleTree = childBundle, assetBasePath = assetBasePath, currentHtmlPath = currentHtmlPath)
            @endfor
            @for(var page : bundleTree.pages())
                @if(page.kind() != com.oakinvest.kiso.core.model.markdown.MarkdownFileKind.INDEX)
                    <li>
                        <a class="@if(page.href().equals(currentHtmlPath))font-semibold@endif"
                           href="${assetBasePath}${page.href()}">
                            ${page.title()}
                        </a>
                    </li>
                @endif
            @endfor
        </ul>
    </details>
</li>
