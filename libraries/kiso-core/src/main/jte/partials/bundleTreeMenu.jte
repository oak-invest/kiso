@param com.oakinvest.kiso.core.model.html.navigation.BundleTree bundleTree
@param String assetBasePath
@param String currentHtmlPath

@if(bundleTree != null)
    <ul class="menu menu-sm w-full gap-1 p-0">
        @if(bundleTree.hasIndexPage())
            <li>
                <a class="@if(bundleTree.indexHtmlPath().equals(currentHtmlPath))font-semibold@endif"
                   href="${assetBasePath}${bundleTree.indexHtmlPath()}">
                    <span class="kiso-navigation-menu-label">${bundleTree.name()}</span>
                </a>
            </li>
        @endif
        @for(var childBundle : bundleTree.childBundles())
            @if(childBundle != null)
                @template.partials.bundleTreeMenuNode(bundleTree = childBundle, assetBasePath = assetBasePath, currentHtmlPath = currentHtmlPath)
            @endif
        @endfor
        @for(var page : bundleTree.pages())
            @if(page.kind() != com.oakinvest.kiso.core.model.okf.markdown.MarkdownFileKind.INDEX)
                <li>
                    <a class="@if(page.href().equals(currentHtmlPath))font-semibold@endif"
                       href="${assetBasePath}${page.href()}">
                        <span class="kiso-navigation-menu-label">${page.title()}</span>
                    </a>
                </li>
            @endif
        @endfor
    </ul>
@endif
