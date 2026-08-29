@import java.util.Objects
@import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.INDEX

@param com.oakinvest.kiso.cli.model.navigation.BundleTree bundleTree
@param com.oakinvest.kiso.cli.model.util.PageMetadata pageMetadata

!{var currentBundleTree = Objects.requireNonNull(bundleTree);}
!{var currentPageMetadata = Objects.requireNonNull(pageMetadata);}

<li>
    <details open="${currentBundleTree.containsHtmlPath(currentPageMetadata.htmlPath())}">

        <!-- Bundle name =========================================================================================== -->
        <summary class="@if(currentBundleTree.containsHtmlPath(currentPageMetadata.htmlPath()))menu-active@endif">
            <span class="kiso-navigation-menu-label">${currentBundleTree.name()}</span>
        </summary>
        <ul>

            <!-- Index page (if present) =========================================================================== -->
            @if(currentBundleTree.indexPage().isPresent())
                !{var indexPage = currentBundleTree.indexPage().orElseThrow();}
                <li>
                    <a class="@if(indexPage.htmlPath().equals(currentPageMetadata.htmlPath()))font-semibold@endif"
                       href="${currentPageMetadata.assetBasePath()}${indexPage.htmlPath()}">
                        <span class="kiso-navigation-menu-label" data-i18n="pageKind.index">index</span>
                    </a>
                </li>
            @endif

            <!-- All bundles ======================================================================================= -->
            @for(var childBundle : currentBundleTree.childBundles())
                !{var childBundleTree = Objects.requireNonNull(childBundle);}
                @template.layouts.side-menu-node(bundleTree = childBundleTree, pageMetadata = currentPageMetadata)
            @endfor

            <!-- All pages inside the bundle ======================================================================= -->
            @for(var page : currentBundleTree.pages())
                @if(page.kind() != INDEX)
                    <li>
                        <a class="@if(page.htmlPath().equals(currentPageMetadata.htmlPath()))font-semibold@endif"
                           href="${currentPageMetadata.assetBasePath()}${page.htmlPath()}">
                            <span class="kiso-navigation-menu-label">${page.title()}</span>
                        </a>
                    </li>
                @endif
            @endfor

        </ul>
    </details>
</li>
