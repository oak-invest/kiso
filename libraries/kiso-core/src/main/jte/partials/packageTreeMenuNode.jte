@param com.oakinvest.kiso.core.renderer.model.PackageTree packageTree
@param String assetBasePath
@param String currentHtmlPath

<li>
    <details open="${packageTree.containsHtmlPath(currentHtmlPath)}">
        <summary
                class="@if(packageTree.containsHtmlPath(currentHtmlPath))menu-active@endif">${packageTree.name()}</summary>
        <ul>
            <li>
                <a class="@if(packageTree.href().equals(currentHtmlPath))font-semibold@endif"
                   href="${assetBasePath}${packageTree.href()}">
                    Index
                </a>
            </li>
            @for(var childPackage : packageTree.childPackages())
                @template.partials.packageTreeMenuNode(packageTree = childPackage, assetBasePath = assetBasePath, currentHtmlPath = currentHtmlPath)
            @endfor
            @for(var page : packageTree.pages())
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
