@param com.oakinvest.kiso.core.renderer.model.navigation.BundleTree bundleTree
@param String assetBasePath
@param String currentHtmlPath

@if(bundleTree != null)
    <ul class="menu menu-sm w-full gap-1 p-0">
        <li>
            <a class="@if(bundleTree.indexHtmlPath().equals(currentHtmlPath))font-semibold@endif"
               href="${assetBasePath}${bundleTree.indexHtmlPath()}">
                ${bundleTree.name()}
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
@endif
