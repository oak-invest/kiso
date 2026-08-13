@param com.oakinvest.kiso.core.model.html.util.PageMetadata pageMetadata

<div class="navbar bg-base-100 shadow-sm">

    <!-- Buttons =================================================================================================== -->
    <div class="flex-none flex items-center gap-1">
        <!-- Home link ============================================================================================= -->
        <a href="${pageMetadata.assetBasePath()}"
           class="btn btn-square btn-ghost"
           aria-label="Home"
           data-i18n-aria-label="navigation.home">
            @template.components.icons.home()
        </a>

        <!-- Navigation button ===================================================================================== -->
        <label for="kiso-navigation-drawer"
               class="btn btn-square btn-ghost"
               aria-label="Open navigation"
               data-i18n-aria-label="navigation.openNavigation">
            @template.components.icons.hamburger()
        </label>

        <!-- Download bundle button ===============================================================================  -->
        <a href="bundle.zip"
           download
           class="btn btn-square btn-ghost"
           aria-label="Download bundle"
           data-i18n-aria-label="navigation.downloadBundle">
            @template.components.icons.download()
        </a>
    </div>

    <div class="flex-1"></div>

    <!-- Search engine ============================================================================================= -->
    <div class="kiso-search hidden flex-none md:block"
         data-search-index-url="${pageMetadata.assetBasePath()}search-index.json"
         data-search-result-base-url="${pageMetadata.assetBasePath()}">

        <label class="input input-bordered flex items-center gap-2">
            @template.components.icons.glass(
            cssClass = "h-[1em] w-[1em] opacity-50"
            )

            <input class="kiso-search-input grow"
                   type="search"
                   placeholder="Search"
                   data-i18n-placeholder="search.placeholder"
                   autocomplete="off"
                   aria-label="Search"
                   data-i18n-aria-label="search.label"/>

        </label>

        <div class="kiso-search-results menu rounded-box mt-2 hidden w-80 max-w-[calc(100vw-1.5rem)] bg-base-100 p-2 shadow-lg"></div>

    </div>

</div>
