@import java.util.Objects
@import static com.oakinvest.kiso.core.util.types.MarkdownFileKind.INDEX

@param com.oakinvest.kiso.cli.model.util.PageContext pageContext
@param com.oakinvest.kiso.cli.model.util.PageMetadata pageMetadata

!{var currentPageContext = Objects.requireNonNull(pageContext);}
!{var currentPageMetadata = Objects.requireNonNull(pageMetadata);}

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
            @if(currentPageContext.bundleTree().indexPage().isPresent())
                !{var indexPage = currentPageContext.bundleTree().indexPage().orElseThrow();}
                <li>
                    <a class="@if(indexPage.htmlPath().equals(currentPageMetadata.htmlPath()))font-semibold@endif"
                       href="${currentPageMetadata.assetBasePath()}${indexPage.htmlPath()}">
                        <span class="kiso-navigation-menu-label">${currentPageContext.bundleTree().name()}</span>
                    </a>
                </li>
            @endif

            <!-- All bundles ======================================================================================= -->
            @for(var childBundle : currentPageContext.bundleTree().childBundles())
                !{var childBundleTree = Objects.requireNonNull(childBundle);}
                @template.layouts.side-menu-node(bundleTree = childBundleTree, pageMetadata = currentPageMetadata)
            @endfor

            <!-- All pages inside the bundle ======================================================================= -->
            @for(var page : currentPageContext.bundleTree().pages())
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
