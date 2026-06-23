@param com.oakinvest.kiso.core.renderer.model.PackageTree packageTree
@param String assetBasePath
@param String currentHtmlPath

@if(packageTree != null)
    <ul class="menu menu-sm w-full gap-1 p-0">
        <%--        <li class="menu-title px-2">Pages</li>--%>
        <li>
            <a class="@if(packageTree.href().equals(currentHtmlPath))font-semibold@endif"
               href="${assetBasePath}${packageTree.href()}">
                ${packageTree.name()}
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
@endif
